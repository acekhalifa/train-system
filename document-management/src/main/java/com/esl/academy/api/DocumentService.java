package com.esl.academy.api;

import com.esl.academy.api.core.exceptions.InternalServerException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.criteria.Predicate;
import com.esl.academy.api.DocumentDto.AddUpdateDocumentDto;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository repository;
    private final FileStorageUtil fileStorageUtil;

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private String determineExtensionGroup(String fileName) {
        String ext = getExtension(fileName);
        return switch (ext) {
            case "pdf", "doc", "docx", "txt" -> "document";
            case "jpg", "jpeg", "png", "gif" -> "image";
            case "mp4", "mov", "avi" -> "video";
            default -> "other";
        };
    }

    public DocumentDto addDocument(AddUpdateDocumentDto dto, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String path = fileStorageUtil.saveFile(file, fileName);
        Document document = Document.builder()
            .name(dto.name())
            .fileType(dto.fileType())
            .documentPath(path)
            .byteSize(file.getSize())
            .attachment(dto.attachment())
            .extension(getExtension(file.getOriginalFilename()))
            .extensionGroup(determineExtensionGroup(file.getOriginalFilename()))
            .isDeleted(false)
            .createdAt(OffsetDateTime.now())
            .createdBy("{}")
            .build();

        Document saved = repository.save(document);
        return DocumentMapper.INSTANCE.toDto(saved);
    }

    public DocumentDto updateDocument(UUID id, AddUpdateDocumentDto dto, MultipartFile file) throws IOException {
        Document document = repository.findByDocumentIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Document not found"));

        if (dto.name() != null) document.setName(dto.name());
        if (dto.fileType() != null) document.setFileType(dto.fileType());
        if (dto.attachment() != null) document.setAttachment(dto.attachment());
        if (file != null) {
            String newPath = fileStorageUtil.saveFile(file, file.getOriginalFilename());
            document.setDocumentPath(newPath);
            document.setByteSize(file.getSize());
            document.setExtension(getExtension(file.getOriginalFilename()));
            document.setExtensionGroup(determineExtensionGroup(file.getOriginalFilename()));
        }

        return DocumentMapper.INSTANCE.toDto(repository.save(document));
    }

    public Optional<DocumentDto> getById(UUID id) {
        Optional<Document> document = repository.findByDocumentIdAndIsDeletedFalse(id);
        return document.map(DocumentMapper.INSTANCE::toDto);
    }

    public Resource getDocumentAsResource(UUID id) {
        var document = repository.findByDocumentIdAndIsDeletedFalse(id).orElseThrow(
            () -> new NotFoundException("Document not found with ID: " + id));
        try {
            Path filePath = Paths.get(document.getDocumentPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("File not found or not readable: " + document.getDocumentPath());
            }
            return resource;
        } catch (IOException e) {
            throw new InternalServerException("Error loading file: " + e.getMessage(), e);
        }
    }


    public List<DocumentDto> getAllDocuments() {
        return repository.findAllByIsDeletedFalse()
            .stream()
            .map(DocumentMapper.INSTANCE::toDto)
            .toList();
    }

    public void softDelete(UUID id) {
        Document document = repository.findByDocumentIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Document not found with ID: " + id));
        document.setDeleted(false);
        repository.save(document);
    }

    public List<DocumentDto> searchDocuments(
        Optional<String> name,
        Optional<FileType> fileType,
        Optional<String> extension,
        Optional<String> extensionGroup,
        Optional<Boolean> attachment
    ) {
        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("isDeleted")));

            name.ifPresent(n -> predicates.add(cb.like(cb.lower(root.get("name")), "%" + n.toLowerCase() + "%")));
            fileType.ifPresent(ft -> predicates.add(cb.equal(root.get("fileType"), ft)));
            extension.ifPresent(ext -> predicates.add(cb.equal(root.get("extension"), ext)));
            extensionGroup.ifPresent(extGrp -> predicates.add(cb.equal(root.get("extensionGroup"), extGrp)));
            attachment.ifPresent(att -> predicates.add(cb.equal(root.get("attachment"), att)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec)
            .stream()
            .map(DocumentMapper.INSTANCE::toDto)
            .toList();
    }
}

package com.esl.academy.api;

import com.esl.academy.api.core.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.esl.academy.api.DocumentDto.AddUpdateDocumentDto;

@Tag(name = "Document")
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class DocumentController {
    private final DocumentService service;

    @Operation(summary = "Create a document")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DocumentDto addDocument(
        @RequestPart("metadata") @Valid AddUpdateDocumentDto dto,
        @RequestPart("file")MultipartFile file
        ) throws IOException {
        return service.addDocument(dto, file);
    }

    @PutMapping(
        value = "/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DocumentDto updateDocument(
        @PathVariable UUID id,
        @RequestPart("metadata") @Valid AddUpdateDocumentDto dto,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        return service.updateDocument(id, dto, file);
    }


    @Operation(summary = "Fetch a document by id")
    @GetMapping("/{id}")
    public Optional<DocumentDto> getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @Operation(summary = "Download a document by ID")
    @GetMapping("{id}/download")
    public ResponseEntity<Resource> downloadDocumentById(@PathVariable UUID id) {
        var resource = service.getDocumentAsResource(id);
        var certificateDto = service.getById(id).orElseThrow(() ->
            new NotFoundException("Document not found with ID: " + id));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + certificateDto.name()+ "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(certificateDto.byteSize())
            .body(resource);
    }

    @Operation(summary = "Preview a document by id")
    @GetMapping("{id}/preview")
    public ResponseEntity<Resource> previewDocumentById(@PathVariable UUID id) {
        var resource = service.getDocumentAsResource(id);
        var certificateDto = service.getById(id).orElseThrow(() ->
            new NotFoundException("Document not found with ID: " + id));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + certificateDto.name() + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(certificateDto.byteSize())
            .body(resource);
    }

    @Operation(summary = "Fetch all documents")
    @GetMapping
    public List<DocumentDto> getAll() {
        return service.getAllDocuments();
    }

    @Operation(summary = "Soft delete a document")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable UUID id) {
        service.softDelete(id);
    }

    @Operation(summary = "Search documents")
    @GetMapping("/search")
    public List<DocumentDto> search(
        @RequestParam Optional<String> name,
        @RequestParam Optional<FileType> fileType,
        @RequestParam Optional<String> extension,
        @RequestParam Optional<String> extensionGroup,
        @RequestParam Optional<Boolean> attachment
        ) {
        return service.searchDocuments(name, fileType, extension, extensionGroup, attachment);
    }
}

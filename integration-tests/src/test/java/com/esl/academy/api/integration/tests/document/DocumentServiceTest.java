package com.esl.academy.api.integration.tests.document;

import com.esl.academy.api.*;
import com.esl.academy.api.DocumentDto.AddUpdateDocumentDto;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentServiceTest extends BaseIntegrationTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    // --------------------------------------------------
    // Helper: create mock file
    // --------------------------------------------------
    private MockMultipartFile mockFile(String name, String content) {
        return new MockMultipartFile(
            "file",
            name,
            "application/octet-stream",
            content.getBytes()
        );
    }

    // --------------------------------------------------
    // TEST: Add DOC
    // --------------------------------------------------
    @Test
    void addDocument_ShouldSaveSuccessfully() throws IOException {

        AddUpdateDocumentDto dto = new AddUpdateDocumentDto(
            "Test File",
            FileType.DOCUMENT,
            true
        );

        MockMultipartFile file = mockFile("hello.pdf", "PDF content here");

        var saved = documentService.addDocument(dto, file);

        forceFlush(); // ensure DB state is committed

        assertThat(saved).isNotNull();
        assertThat(saved.documentId()).isNotNull();
        assertThat(saved.name()).isEqualTo("Test File");
        assertThat(saved.extension()).isEqualTo("pdf");
        assertThat(saved.byteSize()).isGreaterThan(0);

        // verify DB actually contains it
        var entity = documentRepository.findById(saved.documentId()).orElse(null);
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Test File");
    }

    // --------------------------------------------------
    // TEST: Update DOC
    // --------------------------------------------------
    @Test
    void updateDocument_ShouldUpdateFieldsAndFile() throws IOException {

//        setAuthenticatedUser("admin@admin.com");

        // step 1: create initial DOC
        AddUpdateDocumentDto dto = new AddUpdateDocumentDto(
            "Initial Name",
            FileType.DOCUMENT,
            true
        );

        var initialFile = mockFile("old.txt", "old content");
        var saved = documentService.addDocument(dto, initialFile);

        forceFlush();

        // step 2: update it
        AddUpdateDocumentDto updateDto = new AddUpdateDocumentDto(
            "Updated Name",
            FileType.DOCUMENT,
            false
        );

        var newFile = mockFile("new.png", "new JPG content");
        var updated = documentService.updateDocument(saved.documentId(), updateDto, newFile);

        forceFlush();

        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Updated Name");
        assertThat(updated.fileType()).isEqualTo(FileType.DOCUMENT);
        assertThat(updated.attachment()).isFalse();
        assertThat(updated.extension()).isEqualTo("png");

        // verify DB state
        var entity = documentRepository.findById(saved.documentId()).orElse(null);
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Updated Name");
        assertThat(entity.getExtension()).isEqualTo("png");
    }

    // --------------------------------------------------
    // TEST: Get By ID
    // --------------------------------------------------
    @Test
    void getById_ShouldReturnDocument() throws IOException {

//        setAuthenticatedUser("admin@admin.com");

        AddUpdateDocumentDto dto = new AddUpdateDocumentDto(
            "DOC Sample",
            FileType.DOCUMENT,
            true
        );

        MockMultipartFile file = mockFile("doc.pdf", "file-content");
        var saved = documentService.addDocument(dto, file);

        forceFlush();

        var found = documentService.getById(saved.documentId());

        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("DOC Sample");
    }
}

package com.esl.academy.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentDto(
    UUID documentId,
    String name,
    FileType fileType,
    String documentPath,
    Long byteSize,
    Boolean attachment,
    String extension,
    String extensionGroup,
    Boolean isDeleted,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    String createdBy,
    String updatedBy
) {
    public record AddUpdateDocumentDto(
        @Size(min = 1, max = 256)
        @NotNull(message = "name is required")
        String name,

        @NotNull(message = "file type is required")
        FileType fileType,

        @NotNull(message = "file attachment is required")
        Boolean attachment
        ) {}
}

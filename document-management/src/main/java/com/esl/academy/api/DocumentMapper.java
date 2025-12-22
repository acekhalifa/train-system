package com.esl.academy.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DocumentMapper {

    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    DocumentDto toDto(Document document);


    @Mapping(target = "downloadUrl",
        expression = "java(\"/api/documents/\" + document.getDocumentId() + \"/download\")")
    DocumentDto.DocumentResponseDto mapToResponseDto(Document document);

    List<DocumentDto.DocumentResponseDto> toDtoList(List<Document> documents);
}

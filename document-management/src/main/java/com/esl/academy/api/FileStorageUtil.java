package com.esl.academy.api;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileStorageUtil {
    @Value("${application.storage.path}")
    private String storagePath;

    public String saveFile(MultipartFile file, String fileName) throws IOException {
        Path directory = Paths.get(storagePath);

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String uniqueFileName = UUID.randomUUID() + "_" + fileName;
        Path target = directory.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), target);
        return target.toString();
    }

    public void deleteFile(String path) throws IOException {
        Path p = Paths.get(path);
        if (Files.exists(p)) {
            Files.delete(p);
        }
    }
}

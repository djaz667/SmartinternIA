package com.smartintern.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir, "cv"));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier uploads/cv", e);
        }
    }

    public String storeFile(MultipartFile file, String subDir, String prefix) {
        try {
            Path dir = Paths.get(uploadDir, subDir);
            Files.createDirectories(dir);

            String filename = prefix + "_" + System.currentTimeMillis() + ".pdf";
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return subDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier", e);
        }
    }

    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            Path path = Paths.get(uploadDir).resolve(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log but don't fail
        }
    }

    public Resource loadFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir).resolve(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("Fichier introuvable : " + filePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Fichier introuvable : " + filePath, e);
        }
    }
}

package com.smartintern.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        fileStorageService = new FileStorageService();
        Field field = FileStorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(fileStorageService, tempDir.toString());
        fileStorageService.init();
    }

    @Test
    void storeFile_savesFileAndReturnsPath() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "PDF content".getBytes());

        String path = fileStorageService.storeFile(file, "cv", "cv_1");

        assertThat(path).startsWith("cv/cv_1_").endsWith(".pdf");
        assertThat(Files.exists(tempDir.resolve(path))).isTrue();
    }

    @Test
    void deleteFile_removesExistingFile() throws Exception {
        Path cvDir = tempDir.resolve("cv");
        Files.createDirectories(cvDir);
        Path file = cvDir.resolve("test.pdf");
        Files.writeString(file, "content");

        fileStorageService.deleteFile("cv/test.pdf");

        assertThat(Files.exists(file)).isFalse();
    }

    @Test
    void deleteFile_nullPath_doesNotThrow() {
        fileStorageService.deleteFile(null);
        fileStorageService.deleteFile("");
    }

    @Test
    void loadFile_existingFile_returnsResource() throws Exception {
        Path cvDir = tempDir.resolve("cv");
        Files.createDirectories(cvDir);
        Path file = cvDir.resolve("test.pdf");
        Files.writeString(file, "PDF content");

        Resource resource = fileStorageService.loadFile("cv/test.pdf");

        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    @Test
    void loadFile_nonExisting_throwsException() {
        assertThatThrownBy(() -> fileStorageService.loadFile("cv/nonexistent.pdf"))
                .isInstanceOf(RuntimeException.class);
    }
}

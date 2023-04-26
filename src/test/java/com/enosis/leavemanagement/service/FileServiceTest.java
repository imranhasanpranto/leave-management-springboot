package com.enosis.leavemanagement.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileServiceTest {
    @InjectMocks
    FileService service;

    static MockMultipartFile file;
    static private final Path rootLocation = Paths.get("uploads");
    static Long userId = 111L;

    @BeforeAll
    static void setUp() {
        file = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
        );
    }

    @AfterAll
    static void tearDown() {
        //delete directory
        Path directory = rootLocation.resolve(String.valueOf(userId));
        File directoryToBeDeleted = directory.toFile();
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                file.delete();
            }
        }
        directoryToBeDeleted.delete();
    }

    @Test
    @Order(1)
    void saveFile() {
        String fileName = service.saveFile(file, userId);
        Path path = rootLocation.resolve(fileName);
        assertTrue(Files.exists(path));
    }

    @Test
    @Order(2)
    void delete() {
        //save
        String fileName = service.saveFile(file, userId);

        //delete
        boolean isDeleted = service.delete(fileName);
        assertTrue(isDeleted);
        Path path = rootLocation.resolve(fileName);
        assertTrue(Files.notExists(path));
    }

    @Test
    @Order(3)
    void fileUpdate() {
        //save
        String fileName = service.saveFile(file, userId);

        //delete
        boolean isDeleted = service.delete(fileName);
        assertTrue(isDeleted);
        Path path = rootLocation.resolve(fileName);
        assertTrue(Files.notExists(path));

        //save
        fileName = service.saveFile(file, userId);
        path = rootLocation.resolve(fileName);
        assertTrue(Files.exists(path));
    }
}
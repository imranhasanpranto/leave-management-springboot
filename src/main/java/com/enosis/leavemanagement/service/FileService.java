package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.exceptions.FileSaveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
public class FileService {
    private final Path rootLocation = Paths.get("uploads");

    private Path getPath(Long employeeId, String fileName){
        Path path = this.rootLocation.resolve(fileName + String.valueOf(employeeId));
        return path;
    }
    public String saveFile(MultipartFile file, Long userId) throws FileSaveException {
        String fileName;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            try {
                fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
                fileName = String.valueOf(userId)+"/"+fileName;
                Path path = this.rootLocation.resolve(fileName);
                Files.createDirectories(this.rootLocation.resolve(String.valueOf(userId)));
                Files.copy(file.getInputStream(), path);
            } catch (Exception e) {
                throw new RuntimeException("FAIL!");
            }
            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new FileSaveException("file save failed!!!");
        }
    }

    public boolean delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}

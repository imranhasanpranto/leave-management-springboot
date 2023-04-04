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
    public String saveFile(MultipartFile file) throws FileSaveException {
        String pathStr, fileName;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            try {
                fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
                pathStr = "1/"+fileName;
                Path path = this.rootLocation.resolve(pathStr);
                Files.createDirectories(this.rootLocation.resolve("1"));
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

    public Path fetchProfilePhotoByUserId(String filePath) {
        Path imagePath = null;

        Path rootLocation = Paths.get("uploads/1");
        System.out.println("Fetching profile image from " + rootLocation.toString());

        try {
            if (rootLocation.toFile().exists()) {
                Iterator<Path> iterator = Files.newDirectoryStream(rootLocation).iterator();

                if (iterator.hasNext()) {
                    imagePath = iterator.next();
                }
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return imagePath;
    }
}

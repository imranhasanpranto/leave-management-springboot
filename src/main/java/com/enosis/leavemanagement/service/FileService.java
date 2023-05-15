package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.exceptions.FileSaveException;
import com.enosis.leavemanagement.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileService {
    private final Path rootLocation = Paths.get("uploads");

    public ByteArrayResource getAttachmentByFilePathAndId(String filePath, Long id){
        try {
            Path imagePath = Paths.get("uploads/"+id+"/"+filePath);
            if (imagePath != null) {

                return new ByteArrayResource(Files.readAllBytes(imagePath));
            } else {
                throw new NotFoundException("File not found.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveFile(MultipartFile file, Long userId) throws FileSaveException {
        String fileName;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new FileSaveException("directory creation failed!!!");
        }
        try {

            fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
            fileName = String.valueOf(userId)+"/"+fileName;
            Path path = this.rootLocation.resolve(fileName);
            Files.createDirectories(this.rootLocation.resolve(String.valueOf(userId)));
            Files.copy(file.getInputStream(), path);
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

    public String fileUpdate(LeaveApplicationDTO leaveApplicationDTO, String prevFilePath, Long userId) throws RuntimeException, FileSaveException{
        if(prevFilePath != null && !prevFilePath.equals("")){
            delete(prevFilePath);
        }

        String path = "";

        if(leaveApplicationDTO.getFile() != null) {
            path = saveFile(leaveApplicationDTO.getFile(), userId);
        }


        return path;
    }
}

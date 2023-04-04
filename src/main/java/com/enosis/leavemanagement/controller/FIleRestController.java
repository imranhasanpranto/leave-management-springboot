package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FIleRestController {
    private final FileService fileService;
    @GetMapping("/get-file/{filePath}")
    public ResponseEntity<?> getProfileImage(@PathVariable String filePath) {
        try {
            //Path imagePath = fileService.fetchProfilePhotoByUserId(filePath);
            Path imagePath = Paths.get("uploads/1/"+filePath);
            if (imagePath != null) {
                System.out.println("Getting image from " + imagePath.toString());

                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));

                return ResponseEntity
                        .ok()
                        .contentLength(imagePath.toFile().length())
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                System.out.println("Profile photo not found for user " + filePath);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

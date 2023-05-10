package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FIleRestController {
    private final FileService fileService;
    @GetMapping("/get-file/{id}/{filePath}")
    public ResponseEntity<?> getProfileImage(@PathVariable Long id, @PathVariable String filePath) {
        try {
            Path imagePath = Paths.get("uploads/"+String.valueOf(id)+"/"+filePath);
            if (imagePath != null) {

                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));

                return ResponseEntity
                        .ok()
                        .contentLength(imagePath.toFile().length())
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

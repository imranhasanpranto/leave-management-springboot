package com.enosis.leavemanagement.controller;

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
    public ResponseEntity<?> getAttachment(@PathVariable Long id, @PathVariable String filePath) {
        ByteArrayResource resource = fileService.getAttachmentByFilePathAndId(filePath, id);
        return ResponseEntity
                .ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}

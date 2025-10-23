package com.trace.controller;

import com.trace.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/vision")
public class VisionController {

    @Autowired
    private VisionService visionService;

    @Value("${API_ACCESS_PASSWORDS:}")
    private String passwordsEnv;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password
    ) {
        try {
            List<String> validPasswords = Arrays.stream(passwordsEnv.split(","))
                    .map(String::trim)
                    .toList();

            if (!validPasswords.contains(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("❌ كلمة السر غير صحيحة");
            }

            String result = visionService.searchImage(file);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
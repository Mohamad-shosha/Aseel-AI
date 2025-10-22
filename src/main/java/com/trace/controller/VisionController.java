package com.trace.controller;

import com.trace.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public String uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password
    ) {
        try {
            List<String> validPasswords = Arrays.stream(passwordsEnv.split(","))
                    .map(String::trim)
                    .toList();

            if (!validPasswords.contains(password)) {
                return "❌ Unauthorized: Incorrect password";
            }

            return visionService.searchImage(file);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}

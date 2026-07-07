package com.supplychain.ai.controller;

import com.supplychain.ai.model.User;
import com.supplychain.ai.service.CSVService;
import com.supplychain.ai.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

    private final CSVService csvService;
    private final UserService userService;

    public UploadController(CSVService csvService, UserService userService) {
        this.csvService = csvService;
        this.userService = userService;
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload/dashboard";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, 
                                   Authentication authentication, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload.");
            return "upload/dashboard";
        }

        try {
            User user = userService.findByUsername(authentication.getName());
            csvService.processCSV(file, user);
            return "redirect:/dashboard?uploadSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to process CSV: " + e.getMessage());
            return "upload/dashboard";
        }
    }
}

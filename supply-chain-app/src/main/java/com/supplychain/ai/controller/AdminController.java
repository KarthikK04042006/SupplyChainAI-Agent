package com.supplychain.ai.controller;

import com.supplychain.ai.repository.SupplyChainDataRepository;
import com.supplychain.ai.repository.UploadHistoryRepository;
import com.supplychain.ai.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository             userRepository;
    private final UploadHistoryRepository    uploadRepository;
    private final SupplyChainDataRepository  dataRepository;

    public AdminController(UserRepository userRepository,
                           UploadHistoryRepository uploadRepository,
                           SupplyChainDataRepository dataRepository) {
        this.userRepository   = userRepository;
        this.uploadRepository = uploadRepository;
        this.dataRepository   = dataRepository;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users",        userRepository.findAll());
        model.addAttribute("totalUploads", uploadRepository.count());
        model.addAttribute("totalRecords", dataRepository.count());
        return "admin/index";
    }

    /** Toggle user enabled/disabled — POST to prevent CSRF via link */
    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(user -> {
            // Prevent disabling the built-in admin account
            if (!"admin".equals(user.getUsername())) {
                user.setEnabled(!user.isEnabled());
                userRepository.save(user);
            }
        });
        return "redirect:/admin";
    }

    /** Delete user — POST to prevent CSRF via link */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(user -> {
            if (!"admin".equals(user.getUsername())) {
                userRepository.delete(user);
            }
        });
        return "redirect:/admin";
    }
}

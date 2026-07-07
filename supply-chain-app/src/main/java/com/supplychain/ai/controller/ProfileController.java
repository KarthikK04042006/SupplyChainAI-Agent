package com.supplychain.ai.controller;

import com.supplychain.ai.dto.ProfileUpdateDto;
import com.supplychain.ai.model.User;
import com.supplychain.ai.repository.UserRepository;
import com.supplychain.ai.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserService userService, UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.userService      = userService;
        this.userRepository   = userRepository;
        this.passwordEncoder  = passwordEncoder;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user",      user);
        model.addAttribute("updateDto", new ProfileUpdateDto());
        return "profile/index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute ProfileUpdateDto dto,
                                Model model) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) return "redirect:/login";

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank())
            user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null && !dto.getLastName().isBlank())
            user.setLastName(dto.getLastName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Only update email if not taken by another user
            if (!userRepository.existsByEmail(dto.getEmail()) ||
                    dto.getEmail().equals(user.getEmail())) {
                user.setEmail(dto.getEmail());
            } else {
                model.addAttribute("user",       user);
                model.addAttribute("updateDto",  dto);
                model.addAttribute("errorMessage", "Email is already in use by another account.");
                return "profile/index";
            }
        }
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                model.addAttribute("user",      user);
                model.addAttribute("updateDto", dto);
                model.addAttribute("errorMessage", "Passwords do not match.");
                return "profile/index";
            }
            if (dto.getNewPassword().length() < 6) {
                model.addAttribute("user",      user);
                model.addAttribute("updateDto", dto);
                model.addAttribute("errorMessage", "Password must be at least 6 characters.");
                return "profile/index";
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userRepository.save(user);
        return "redirect:/profile?saved";
    }
}

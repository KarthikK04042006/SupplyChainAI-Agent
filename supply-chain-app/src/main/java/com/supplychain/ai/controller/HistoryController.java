package com.supplychain.ai.controller;

import com.supplychain.ai.repository.UploadHistoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoryController {

    private final UploadHistoryRepository uploadHistoryRepository;

    public HistoryController(UploadHistoryRepository uploadHistoryRepository) {
        this.uploadHistoryRepository = uploadHistoryRepository;
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("uploads", uploadHistoryRepository.findAllByOrderByUploadTimeDesc());
        return "history/index";
    }
}

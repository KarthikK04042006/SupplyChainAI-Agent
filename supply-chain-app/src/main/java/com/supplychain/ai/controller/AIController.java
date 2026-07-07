package com.supplychain.ai.controller;

import com.supplychain.ai.service.AIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ai")
    public String aiDashboard(Model model) {
        model.addAttribute("aiData", aiService.getAIPredictions());
        return "ai/index";
    }
}

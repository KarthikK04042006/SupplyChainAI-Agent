package com.supplychain.ai.controller;

import com.supplychain.ai.repository.UploadHistoryRepository;
import com.supplychain.ai.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final AnalyticsService analyticsService;
    private final UploadHistoryRepository uploadRepository;

    public DashboardController(AnalyticsService analyticsService, UploadHistoryRepository uploadRepository) {
        this.analyticsService = analyticsService;
        this.uploadRepository = uploadRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @RequestParam(required = false) String uploadSuccess) {
        model.addAttribute("summary", analyticsService.getSummary());
        model.addAttribute("recentUploads", uploadRepository.findAllByOrderByUploadTimeDesc()
                .stream().limit(5).toList());
        if (uploadSuccess != null) {
            model.addAttribute("successMessage", "Data uploaded and processed successfully!");
        }
        return "dashboard/index";
    }
}

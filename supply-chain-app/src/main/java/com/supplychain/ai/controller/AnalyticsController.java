package com.supplychain.ai.controller;

import com.supplychain.ai.repository.SupplyChainDataRepository;
import com.supplychain.ai.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SupplyChainDataRepository dataRepository;

    public AnalyticsController(AnalyticsService analyticsService,
                                SupplyChainDataRepository dataRepository) {
        this.analyticsService = analyticsService;
        this.dataRepository   = dataRepository;
    }

    @GetMapping("/analytics")
    public String analyticsPage(Model model) {
        model.addAttribute("summary",    analyticsService.getSummary());
        model.addAttribute("regions",    dataRepository.findDistinctRegions());
        model.addAttribute("categories", dataRepository.findDistinctCategories());
        return "analytics/index";
    }
}

package com.supplychain.ai.service;

import com.supplychain.ai.model.SupplyChainData;
import com.supplychain.ai.repository.SupplyChainDataRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final SupplyChainDataRepository dataRepository;

    public AIService(SupplyChainDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public Map<String, Object> getAIPredictions() {
        List<SupplyChainData> data = dataRepository.findAll();
        Map<String, Object> predictions = new HashMap<>();

        if (data.isEmpty()) {
            return predictions;
        }

        // Demand Forecast (Simple Linear Projection Simulation)
        double currentSales = data.stream().mapToDouble(SupplyChainData::getSales).sum();
        predictions.put("demandForecast", currentSales * 1.15); // Predicted 15% growth

        // Inventory Prediction
        long lowStockCount = data.stream().filter(d -> d.getQuantity() < 5).count();
        predictions.put("lowStockItems", lowStockCount);

        // Risk Analysis
        double shippingCosts = data.stream().mapToDouble(SupplyChainData::getShippingCost).sum();
        double riskScore = (shippingCosts / currentSales) * 100;
        predictions.put("riskScore", Math.min(100, riskScore));

        // Supply Chain Health Score
        double profitMargin = (data.stream().mapToDouble(SupplyChainData::getProfit).sum() / currentSales) * 100;
        predictions.put("healthScore", Math.min(100, 70 + profitMargin));

        // Smart Recommendations
        List<String> recommendations = new ArrayList<>();
        if (lowStockCount > 10) recommendations.add("Restock top 5 selling products immediately.");
        if (riskScore > 20) recommendations.add("Optimize shipping routes in the Central region to reduce costs.");
        recommendations.add("Consider expanding category 'Technology' based on recent trends.");
        predictions.put("recommendations", recommendations);

        // Additional Predictions
        predictions.put("revenuePrediction", currentSales * 1.25);
        predictions.put("anomalyCount", data.stream().filter(d -> d.getProfit() < 0).count());
        predictions.put("predictionConfidence", 92.5);

        return predictions;
    }
}

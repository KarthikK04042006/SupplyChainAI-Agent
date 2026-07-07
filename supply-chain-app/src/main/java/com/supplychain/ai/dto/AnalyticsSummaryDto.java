package com.supplychain.ai.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class AnalyticsSummaryDto {
    private long totalOrders;
    private double totalSales;
    private double avgSales;
    private double highestSale;
    private double lowestSale;
    private double totalProfit;
    private int totalQuantity;
    private double totalDiscount;
    private double totalShippingCost;
    private double avgProfit;
    private double profitMargin;
    private double salesGrowth;
    private double orderGrowth;
    
    private Map<String, Double> salesByRegion;
    private Map<String, Double> salesByCategory;
    private Map<String, Double> salesBySegment;
    private Map<String, Long> ordersByRegion;
    private Map<String, Long> ordersByCategory;
    private Map<String, Double> monthlySales;
    private Map<String, Double> topProducts;
    private Map<String, Double> topCustomers;
}

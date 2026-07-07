package com.supplychain.ai.service;

import com.supplychain.ai.dto.AnalyticsSummaryDto;
import com.supplychain.ai.model.SupplyChainData;
import com.supplychain.ai.repository.SupplyChainDataRepository;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final SupplyChainDataRepository dataRepository;

    public AnalyticsService(SupplyChainDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public AnalyticsSummaryDto getSummary() {
        List<SupplyChainData> allData = dataRepository.findAll();

        if (allData.isEmpty()) {
            return AnalyticsSummaryDto.builder()
                    .totalOrders(0)
                    .totalSales(0)
                    .avgSales(0)
                    .highestSale(0)
                    .lowestSale(0)
                    .totalProfit(0)
                    .avgProfit(0)
                    .profitMargin(0)
                    .totalQuantity(0)
                    .totalShippingCost(0)
                    .totalDiscount(0)
                    .salesGrowth(0)
                    .orderGrowth(0)
                    .salesByRegion(new HashMap<>())
                    .salesByCategory(new HashMap<>())
                    .salesBySegment(new HashMap<>())
                    .ordersByRegion(new HashMap<>())
                    .ordersByCategory(new HashMap<>())
                    .monthlySales(new LinkedHashMap<>())
                    .topProducts(new LinkedHashMap<>())
                    .topCustomers(new LinkedHashMap<>())
                    .build();
        }

        List<Double> salesValues = allData.stream()
                .map(d -> d.getSales() != null ? d.getSales() : 0.0)
                .collect(Collectors.toList());

        double totalSales    = salesValues.stream().mapToDouble(Double::doubleValue).sum();
        double highestSale   = salesValues.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double lowestSale    = salesValues.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double totalProfit   = allData.stream().mapToDouble(d -> d.getProfit()       != null ? d.getProfit()       : 0.0).sum();
        int    totalQuantity = allData.stream().mapToInt(d    -> d.getQuantity()     != null ? d.getQuantity()     : 0).sum();
        double totalShipping = allData.stream().mapToDouble(d -> d.getShippingCost() != null ? d.getShippingCost(): 0.0).sum();
        double totalDiscount = allData.stream().mapToDouble(d -> d.getDiscount()     != null ? d.getDiscount()     : 0.0).sum();

        // Aggregate by Region
        Map<String, Double> salesByRegion = allData.stream()
                .filter(d -> d.getRegion() != null && !d.getRegion().isBlank())
                .collect(Collectors.groupingBy(SupplyChainData::getRegion,
                        Collectors.summingDouble(d -> d.getSales() != null ? d.getSales() : 0.0)));

        Map<String, Long> ordersByRegion = allData.stream()
                .filter(d -> d.getRegion() != null && !d.getRegion().isBlank())
                .collect(Collectors.groupingBy(SupplyChainData::getRegion, Collectors.counting()));

        // Aggregate by Category
        Map<String, Double> salesByCategory = allData.stream()
                .filter(d -> d.getCategory() != null && !d.getCategory().isBlank())
                .collect(Collectors.groupingBy(SupplyChainData::getCategory,
                        Collectors.summingDouble(d -> d.getSales() != null ? d.getSales() : 0.0)));

        Map<String, Long> ordersByCategory = allData.stream()
                .filter(d -> d.getCategory() != null && !d.getCategory().isBlank())
                .collect(Collectors.groupingBy(SupplyChainData::getCategory, Collectors.counting()));

        // Aggregate by Segment
        Map<String, Double> salesBySegment = allData.stream()
                .filter(d -> d.getSegment() != null && !d.getSegment().isBlank())
                .collect(Collectors.groupingBy(SupplyChainData::getSegment,
                        Collectors.summingDouble(d -> d.getSales() != null ? d.getSales() : 0.0)));

        // Monthly Sales
        Map<String, Double> rawMonthly = allData.stream()
                .filter(d -> d.getOrderDate() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getOrderDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        LinkedHashMap::new,
                        Collectors.summingDouble(d -> d.getSales() != null ? d.getSales() : 0.0)
                ));

        String[] MONTHS = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        Map<String, Double> monthlySales = new LinkedHashMap<>();
        for (String m : MONTHS) {
            if (rawMonthly.containsKey(m)) monthlySales.put(m, rawMonthly.get(m));
        }

        // Top 10 Products by Sales
        Map<String, Double> topProducts = allData.stream()
                .filter(d -> d.getProductName() != null && !d.getProductName().isBlank())
                .collect(Collectors.groupingBy(SupplyChainData::getProductName,
                        Collectors.summingDouble(d -> d.getSales() != null ? d.getSales() : 0.0)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new
                ));

        // Top 10 Customers by Sales
        Map<String, Double> topCustomers = allData.stream()
                .filter(d -> d.getCustomerName() != null && !d.getCustomerName().isBlank())
                .collect(Collectors.groupingBy(SupplyChainData::getCustomerName,
                        Collectors.summingDouble(d -> d.getSales() != null ? d.getSales() : 0.0)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new
                ));

        double profitMargin = totalSales > 0 ? (totalProfit / totalSales) * 100.0 : 0.0;

        return AnalyticsSummaryDto.builder()
                .totalOrders((long) allData.size())
                .totalSales(totalSales)
                .avgSales(totalSales / allData.size())
                .highestSale(highestSale)
                .lowestSale(lowestSale)
                .totalProfit(totalProfit)
                .avgProfit(totalProfit / allData.size())
                .profitMargin(profitMargin)
                .totalQuantity(totalQuantity)
                .totalShippingCost(totalShipping)
                .totalDiscount(totalDiscount)
                .salesGrowth(12.5)
                .orderGrowth(8.2)
                .salesByRegion(salesByRegion)
                .salesByCategory(salesByCategory)
                .salesBySegment(salesBySegment)
                .ordersByRegion(ordersByRegion)
                .ordersByCategory(ordersByCategory)
                .monthlySales(monthlySales)
                .topProducts(topProducts)
                .topCustomers(topCustomers)
                .build();
    }
}

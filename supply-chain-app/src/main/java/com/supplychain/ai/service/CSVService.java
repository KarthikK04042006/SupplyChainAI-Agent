package com.supplychain.ai.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.supplychain.ai.model.SupplyChainData;
import com.supplychain.ai.model.UploadHistory;
import com.supplychain.ai.model.User;
import com.supplychain.ai.repository.SupplyChainDataRepository;
import com.supplychain.ai.repository.UploadHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class CSVService {

    private final SupplyChainDataRepository dataRepository;
    private final UploadHistoryRepository uploadRepository;

    public CSVService(SupplyChainDataRepository dataRepository, UploadHistoryRepository uploadRepository) {
        this.dataRepository = dataRepository;
        this.uploadRepository = uploadRepository;
    }

    @Transactional
    public UploadHistory processCSV(MultipartFile file, User user) throws Exception {
        UploadHistory upload = UploadHistory.builder()
                .fileName(file.getOriginalFilename())
                .uploadTime(LocalDateTime.now())
                .fileSize(file.getSize())
                .user(user)
                .status("PROCESSING")
                .build();
        upload = uploadRepository.save(upload);

        List<SupplyChainData> dataList = new ArrayList<>();
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yy")
        };

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).build()) {
            String[] header = reader.readNext();
            if (header == null) throw new RuntimeException("Empty CSV file");

            Map<String, Integer> colMap = mapHeaders(header);
            log.info("Mapped columns: {}", colMap);

            String[] line;
            while ((line = reader.readNext()) != null) {
                try {
                    SupplyChainData data = SupplyChainData.builder()
                            .orderId(getVal(line, colMap, "Order ID"))
                            .orderDate(parseDate(getVal(line, colMap, "Order Date"), formatters))
                            .shipDate(parseDate(getVal(line, colMap, "Ship Date"), formatters))
                            .shipMode(getVal(line, colMap, "Ship Mode"))
                            .customerId(getVal(line, colMap, "Customer ID"))
                            .customerName(getVal(line, colMap, "Customer Name"))
                            .segment(getVal(line, colMap, "Segment"))
                            .country(getVal(line, colMap, "Country"))
                            .city(getVal(line, colMap, "City"))
                            .state(getVal(line, colMap, "State"))
                            .region(getVal(line, colMap, "Region"))
                            .productId(getVal(line, colMap, "Product ID"))
                            .category(getVal(line, colMap, "Category"))
                            .subCategory(getVal(line, colMap, "Sub-Category"))
                            .productName(getVal(line, colMap, "Product Name"))
                            .sales(parseDouble(getVal(line, colMap, "Sales")))
                            .quantity(parseInt(getVal(line, colMap, "Quantity")))
                            .discount(parseDouble(getVal(line, colMap, "Discount")))
                            .profit(parseDouble(getVal(line, colMap, "Profit")))
                            .shippingCost(parseDouble(getVal(line, colMap, "Shipping Cost")))
                            .orderPriority(getVal(line, colMap, "Order Priority"))
                            .upload(upload)
                            .build();
                    dataList.add(data);
                } catch (Exception e) {
                    log.warn("Skipping invalid row: {}", Arrays.toString(line));
                }
            }
        }

        dataRepository.saveAll(dataList);
        upload.setRowCount(dataList.size());
        upload.setStatus("SUCCESS");
        return uploadRepository.save(upload);
    }

    private Map<String, Integer> mapHeaders(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            map.put(header[i].trim(), i);
        }
        return map;
    }

    private String getVal(String[] line, Map<String, Integer> map, String key) {
        Integer idx = map.get(key);
        if (idx != null && idx < line.length) return line[idx];
        return "";
    }

    private LocalDate parseDate(String dateStr, DateTimeFormatter[] formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception ignored) {}
        }
        return LocalDate.now();
    }

    private Double parseDouble(String val) {
        if (val == null || val.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(val.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer parseInt(String val) {
        if (val == null || val.isEmpty()) return 0;
        try {
            return (int) Double.parseDouble(val.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}

package com.supplychain.ai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "supply_chain_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyChainData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private LocalDate orderDate;
    private LocalDate shipDate;
    private String shipMode;
    private String customerId;
    private String customerName;
    private String segment;
    private String country;
    private String city;
    private String state;
    private String region;
    private String productId;
    private String category;
    private String subCategory;
    private String productName;
    private Double sales;
    private Integer quantity;
    private Double discount;
    private Double profit;
    private Double shippingCost;
    private String orderPriority;

    @ManyToOne
    @JoinColumn(name = "upload_id")
    private UploadHistory upload;
}

package com.microservicesprojectone.order_mservice.dto;


import com.microservicesprojectone.order_mservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private Double priceAtPurchase;
    private int quantity;
    private OrderStatus status;

}

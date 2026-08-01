package com.microservicesprojectone.order_mservice.dto;


import com.microservicesprojectone.order_mservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private Long productId;
    private Long userId;
    private int quantity;
    private OrderStatus status;
}

package com.microservicesprojectone.order_mservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private Double priceAtPurchase;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;


}

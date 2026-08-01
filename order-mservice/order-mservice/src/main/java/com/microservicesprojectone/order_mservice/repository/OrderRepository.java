package com.microservicesprojectone.order_mservice.repository;


import com.microservicesprojectone.order_mservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}

package com.microservicesprojectone.payment_mservice.repository;

import com.microservicesprojectone.payment_mservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Payment findByOrderId(Long orderId);
}

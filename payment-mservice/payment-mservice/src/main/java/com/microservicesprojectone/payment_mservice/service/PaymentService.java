package com.microservicesprojectone.payment_mservice.service;

import com.microservicesprojectone.common_module.events.OrderEvent;
import com.microservicesprojectone.payment_mservice.entity.Payment;
import com.microservicesprojectone.payment_mservice.entity.PaymentStatus;
import com.microservicesprojectone.payment_mservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "order-created",groupId = "payment-group")
    @Transactional
    public void createPayment(OrderEvent event){
        System.out.println("Received Order event: "+event.getOrderId());
        Payment payment=new Payment();
        payment.setAmount(event.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId(event.getUserId());
        payment.setOrderId(event.getOrderId());
        payment.setProductId(event.getProductId());
        Payment savedPayment=paymentRepository.save(payment);
        System.out.println("Processing payment");

        savedPayment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(savedPayment);
        System.out.println("Payment Completed for Order: "+event.getOrderId());


    }
}

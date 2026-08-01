package com.microservicesprojectone.order_mservice.service;

import com.microservicesprojectone.common_module.events.OrderEvent;
import com.microservicesprojectone.order_mservice.client.ProductClientInterface;
import com.microservicesprojectone.order_mservice.dto.OrderRequestDto;
import com.microservicesprojectone.order_mservice.dto.OrderResponseDto;
import com.microservicesprojectone.order_mservice.entity.Order;
import com.microservicesprojectone.order_mservice.entity.OrderStatus;
import com.microservicesprojectone.order_mservice.exceptions.ServiceUnavailableException;
import com.microservicesprojectone.order_mservice.external.ProductResponse;
import com.microservicesprojectone.order_mservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class OrderService {
    private final ProductClientInterface productClientInterface;
    private final KafkaTemplate<String, OrderEvent>kafkaTemplate;
    private  final OrderRepository orderRepository;

    @CircuitBreaker(name = "productService",fallbackMethod = "fallbackProduct")
    public ProductResponse getProductDetails(Long id){
        return productClientInterface.getById(id);
    }

    public ProductResponse fallbackProduct(Long id, Exception ex) {
        System.out.println("FALLBACK TRIGGERED: " + ex.getClass().getName());
        // send alert to monitoring system
        // differentiate between timeout vs service down vs bad request
        throw new ServiceUnavailableException("Product service is down, please try later");
    }

    public OrderResponseDto createOrder(OrderRequestDto requestDto){
        ProductResponse product= getProductDetails(requestDto.getProductId());
        Order order=new Order();
        order.setStatus(requestDto.getStatus());
        order.setQuantity(requestDto.getQuantity());
        order.setUserId(requestDto.getUserId());
        order.setProductId(requestDto.getProductId());
        order.setProductName(product.getName());
        order.setPriceAtPurchase(product.getPrice());
        Order savedOrder=orderRepository.save(order);
        OrderEvent event=new OrderEvent();
        event.setOrderId(savedOrder.getId());
        event.setProductId(savedOrder.getProductId());
        event.setUserId(savedOrder.getUserId());
        event.setStatus("PENDING");
        event.setAmount(savedOrder.getPriceAtPurchase());
        kafkaTemplate.send("order-created",event);
        return  new OrderResponseDto(savedOrder.getId(), savedOrder.getUserId(), savedOrder.getProductId(),
                savedOrder.getProductName(), savedOrder.getPriceAtPurchase(), savedOrder.getQuantity(), savedOrder.getStatus()
        );

    }

}

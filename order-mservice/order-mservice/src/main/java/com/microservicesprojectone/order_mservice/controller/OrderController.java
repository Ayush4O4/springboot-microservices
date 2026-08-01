package com.microservicesprojectone.order_mservice.controller;
import com.microservicesprojectone.order_mservice.dto.OrderRequestDto;
import com.microservicesprojectone.order_mservice.dto.OrderResponseDto;
import com.microservicesprojectone.order_mservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("order")
public class OrderController {



  private final OrderService orderService;

    @PostMapping("/create")

    public OrderResponseDto createOrder(@RequestBody OrderRequestDto requestDto){
        return orderService.createOrder(requestDto);
    }

}

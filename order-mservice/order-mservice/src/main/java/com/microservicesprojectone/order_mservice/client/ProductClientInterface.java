package com.microservicesprojectone.order_mservice.client;


import com.microservicesprojectone.order_mservice.external.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@org.springframework.cloud.openfeign.FeignClient(name = "product-mservice")
public interface ProductClientInterface {
    @GetMapping
    public List<ProductResponse> getAll() ;

    @GetMapping("/products/{id}")
    public ProductResponse getById(@PathVariable Long id);
//
//    @PostMapping
//    public ProductResponse create(@RequestBody ProductResponse product) ;
}

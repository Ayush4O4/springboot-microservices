package com.microservicesprojectone.product_mservice.repository;


import com.microservicesprojectone.product_mservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}

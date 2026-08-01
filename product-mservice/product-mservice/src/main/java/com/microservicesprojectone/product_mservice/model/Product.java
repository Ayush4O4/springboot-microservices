package com.microservicesprojectone.product_mservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

        @Id
        @GeneratedValue
        private Long id;
        private String name;
        private Double price;
        private Integer stock;
        // constructors, getters, setters

}

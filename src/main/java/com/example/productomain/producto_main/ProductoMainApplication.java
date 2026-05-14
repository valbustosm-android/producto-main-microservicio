package com.example.productomain.producto_main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.productomain")
@EntityScan(basePackages = "com.example.productomain.model")
@EnableJpaRepositories(basePackages = "com.example.productomain.repository")
public class ProductoMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductoMainApplication.class, args);
	}

}

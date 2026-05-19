package com.example.quote_service_eventstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuoteServiceEventstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuoteServiceEventstoreApplication.class, args);
	}

}

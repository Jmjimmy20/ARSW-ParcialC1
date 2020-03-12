package edu.eci.arsw.exams.moneylaunderingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"edu.eci.arsw.exams.moneylaunderingapi"})
public class MoneyLaunderingAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyLaunderingAPIApplication.class, args);
	}

}

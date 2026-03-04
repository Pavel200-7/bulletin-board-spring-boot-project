package com.example.bulletin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.config.EnableStateMachine;

@SpringBootApplication
public class BulletinApplication {

	public static void main(String[] args) {
		SpringApplication.run(BulletinApplication.class, args);
	}

}

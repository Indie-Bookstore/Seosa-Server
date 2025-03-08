package com.seosa.seosa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SeosaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeosaApplication.class, args);
	}

}

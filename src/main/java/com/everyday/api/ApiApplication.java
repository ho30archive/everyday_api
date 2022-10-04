package com.everyday.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //BaseTimeEntity를 만들었다면 꼭 Application에 @EnableJpaAuditing을 추가해줘야 한다.
@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(ApiApplication.class, args);
	}

}

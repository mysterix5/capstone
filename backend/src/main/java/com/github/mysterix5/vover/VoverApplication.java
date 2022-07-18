package com.github.mysterix5.vover;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VoverApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoverApplication.class, args);
	}

	@Bean
	public Sardine sardine(@Value("${app.webdav.username}") String username,
						   @Value("${app.webdav.password}") String password) {
		return SardineFactory.begin(username, password);
	}
}

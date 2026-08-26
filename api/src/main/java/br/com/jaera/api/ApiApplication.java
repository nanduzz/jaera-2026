package br.com.jaera.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

// TODO: Remove Security exclusions when Firebase Auth / Spring Security is implemented
@SpringBootApplication(exclude = {
	SecurityAutoConfiguration.class,
	UserDetailsServiceAutoConfiguration.class
})
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}

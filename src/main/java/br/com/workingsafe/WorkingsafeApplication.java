package br.com.workingsafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WorkingsafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkingsafeApplication.class, args);
	}
}

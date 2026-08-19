package br.com.lucas.alves.encurtador_url;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EncurtadorUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(EncurtadorUrlApplication.class, args);
	}

}

package io.github.manuelcarro97.reservas;

import org.springframework.boot.SpringApplication;

import io.github.manuelcarro97.reservas.support.TestcontainersConfiguration;

public class TestReservasApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ReservasApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

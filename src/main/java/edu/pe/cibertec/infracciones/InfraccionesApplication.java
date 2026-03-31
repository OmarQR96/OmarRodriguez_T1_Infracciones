package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InfraccionesApplication {

	static {
		new DotenvConfig();
	}

	public static void main(String[] args) {
		SpringApplication.run(InfraccionesApplication.class, args);
	}
}
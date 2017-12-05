package com.mkoteam;

import com.mkoteam.controller.JSJDataRecevier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ControllerApplication implements CommandLineRunner {

	@Autowired
	JSJDataRecevier jsjDataRecevier;

	@Override
	public void run(String... args){
		jsjDataRecevier.connect();
	}
	public static void main(String[] args) {
		SpringApplication.run(ControllerApplication.class, args);
	}
}

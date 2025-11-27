package com.esl.academy.api.integration.tests.base;

import com.esl.academy.api.TCMPApplication;
import org.springframework.boot.SpringApplication;

public class TestNdicBlmsApplication {

	public static void main(String[] args) {
		SpringApplication.from(TCMPApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
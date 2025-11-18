package com.barbearia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SuaBarbeariaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuaBarbeariaApplication.class, args);
    }

}

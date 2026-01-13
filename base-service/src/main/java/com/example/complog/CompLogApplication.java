package com.example.complog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.complog.mapper")
public class CompLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompLogApplication.class, args);
    }

}

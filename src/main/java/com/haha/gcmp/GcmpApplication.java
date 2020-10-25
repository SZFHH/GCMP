package com.haha.gcmp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.haha.gcmp.repository")
public class GcmpApplication {

    public static void main(String[] args) {
        SpringApplication.run(GcmpApplication.class, args);
    }

}

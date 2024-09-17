package com.miaoaotian.smallcar;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.miaoaotian.smallcar.mapper")
@SpringBootApplication
public class SmallcarApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmallcarApplication.class, args);
    }

}

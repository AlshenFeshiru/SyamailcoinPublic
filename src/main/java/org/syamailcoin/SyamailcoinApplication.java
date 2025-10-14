package org.syamailcoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyamailcoinApplication {
    public static void main(String[] args) {
        SpringApplication.run(SyamailcoinApplication.class, args);
        System.out.println("Syamailcoin Backend Started");
    }
}

package com.backbenchcoders.innspiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.backbenchcoders.innspiration.repository")
public class InnSpirationApplication {

    public static void main(String[] args) {
        SpringApplication.run(InnSpirationApplication.class, args);
    }

}

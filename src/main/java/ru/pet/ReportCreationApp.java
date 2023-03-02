package ru.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ReportCreationApp {
    public static void main(String[] args) {
        SpringApplication.run(ReportCreationApp.class, args);
    }
}

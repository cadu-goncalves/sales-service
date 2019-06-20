package com.viniland.sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {
        "com.viniland.sales"
})
public class SalesServiceApp {

    public static void main(String[] args) {
        // Make application work with UTC dates
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(SalesServiceApp.class, args);
    }
}

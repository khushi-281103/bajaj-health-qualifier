package com.bajaj.health;

import com.bajaj.health.service.QualifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class BajajHealthApplication {

    @Autowired
    private QualifierService qualifierService;

    public static void main(String[] args) {
        SpringApplication.run(BajajHealthApplication.class, args);
    }

    @PostConstruct
    public void init() {
        qualifierService.startProcess();
    }
}

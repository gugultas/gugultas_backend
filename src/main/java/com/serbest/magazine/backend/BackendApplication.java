package com.serbest.magazine.backend;

import com.serbest.magazine.backend.service.ImageModelService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BackendApplication{

    private final ImageModelService imageModelService;

    public BackendApplication(ImageModelService imageModelService) {
        this.imageModelService = imageModelService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
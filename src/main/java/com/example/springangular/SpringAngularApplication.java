package com.example.springangular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

import static com.example.springangular.constants.FileConstant.USER_FOLDER;

@SpringBootApplication
public class SpringAngularApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAngularApplication.class, args);

        // build the images directory
        new File(USER_FOLDER).mkdirs();
    }
}

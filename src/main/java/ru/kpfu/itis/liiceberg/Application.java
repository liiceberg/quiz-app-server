package ru.kpfu.itis.liiceberg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Collections;

@SpringBootApplication
@EnableScheduling
public class Application {

    public Application(FreeMarkerConfigurer configurer) {
        configurer.getTaglibFactory().setClasspathTlds(Collections.singletonList("/META-INF/security.tld"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

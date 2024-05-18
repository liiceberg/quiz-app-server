package ru.kpfu.itis.liiceberg;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Collections;

@SpringBootApplication
@EnableScheduling
@EnableWebMvc
public class Application {
    private final DispatcherServlet servlet;

    public Application(FreeMarkerConfigurer configurer, DispatcherServlet servlet) {
        configurer.getTaglibFactory().setClasspathTlds(Collections.singletonList("/META-INF/security.tld"));
        this.servlet = servlet;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner getCommandLineRunner() {
        servlet.setThrowExceptionIfNoHandlerFound(true);
        return args -> {};
    }
}

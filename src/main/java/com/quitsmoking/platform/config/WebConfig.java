package com.quitsmoking.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Chỉ forward những route không phải API, Swagger, Webjars, file tĩnh
        registry.addViewController("/{spring:^(?!api|swagger-ui|v3|swagger-resources|webjars|index\\.html|.*\\.(js|css|png|jpg|svg|ico)$).*$}")
                .setViewName("forward:/index.html");

        registry.addViewController("/**/{spring:^(?!api|swagger-ui|v3|swagger-resources|webjars|index\\.html|.*\\.(js|css|png|jpg|svg|ico)$).*$}")
                .setViewName("forward:/index.html");

        registry.addViewController("/{spring:^(?!api|swagger-ui|v3|swagger-resources|webjars|index\\.html|.*\\.(js|css|png|jpg|svg|ico)$).*$}/**{spring:^(?!\\..*$).*$}")
                .setViewName("forward:/index.html");
    }
}

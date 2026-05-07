package com.nami.webstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // serve arquivos de upload de produto (dinâmico)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);

        // recursos estáticos padrão
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/imagens/**")
                .addResourceLocations("classpath:/static/imagens/");
    }
}
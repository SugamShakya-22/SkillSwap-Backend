package com.skillswap.skillswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Serve uploaded files from uploads/messages/ directory
        registry.addResourceHandler("/uploads/messages/**")
                .addResourceLocations("file:uploads/messages/");

        System.out.println("✅ Static resource handler configured for /uploads/messages/");
    }
}
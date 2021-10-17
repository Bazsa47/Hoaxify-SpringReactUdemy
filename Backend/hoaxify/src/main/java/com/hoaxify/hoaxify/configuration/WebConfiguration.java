package com.hoaxify.hoaxify.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    AppConfiguration appConfiguration;

    @Bean
    CommandLineRunner createUploadFolder(){
        return (args) -> {
            createNonExistingFolder(appConfiguration.getUploadPath());
            createNonExistingFolder(appConfiguration.getFullProfileImagePath());
            createNonExistingFolder(appConfiguration.getFullAttachmentsPath());
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:"+ appConfiguration.getUploadPath()+"/");
    }

    private void createNonExistingFolder(String path) {
        File folder = new File(path);
        boolean folderExists = folder.exists() && folder.isDirectory();
        if (!folderExists){
            folder.mkdir();
        }
    }
}
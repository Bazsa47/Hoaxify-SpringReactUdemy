package com.hoaxify.hoaxify.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="hoaxify")
public class AppConfiguration {

    String uploadPath;
    String profileImagesFolder = "profile";
    String attachmentsFodler = "attachments";

    public String getFullProfileImagePath() {
        return this.uploadPath + "/" + this.profileImagesFolder;
    }

    public String getFullAttachmentsPath() {
        return this.uploadPath + "/" + this.attachmentsFodler;
    }
}

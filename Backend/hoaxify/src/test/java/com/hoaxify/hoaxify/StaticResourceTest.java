package com.hoaxify.hoaxify;

import com.hoaxify.hoaxify.configuration.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StaticResourceTest {

    @Autowired
    AppConfiguration appConfiguration;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void checkStaticFolder_whenAppIsInitialized_uploadFolderMustExist() {
        File uploadFolder = new File(appConfiguration.getUploadPath());
        boolean uploadFolderExist = uploadFolder.exists() && uploadFolder.isDirectory();
        assertThat(uploadFolderExist).isTrue();
    }

    @Test
    public void checkStaticFolder_whenAppIsInitialized_profileImageSubfolderMustExist() {
        String profileImageFolder = appConfiguration.getFullProfileImagePath();
        File ProfileImageFolder = new File(profileImageFolder);
        boolean uploadFolderExist = ProfileImageFolder.exists() && ProfileImageFolder.isDirectory();
        assertThat(uploadFolderExist).isTrue();
    }

    @Test
    public void checkStaticFolder_whenAppIsInitialized_AttatchmentsSubfolderMustExist() {
        String attachmentsFolderPath = appConfiguration.getFullAttachmentsPath();
        File attachmentsFolder = new File(attachmentsFolderPath);
        boolean attachmentsFolderExists = attachmentsFolder.exists() && attachmentsFolder.isDirectory();
        assertThat(attachmentsFolderExists).isTrue();
    }

    @Test
    public void getStaticFile_whenImageExistsInProfileFolder_receiveOK() throws Exception {
        String fileName = "profile-picture.png";
        File source = new ClassPathResource("profile.png").getFile();

        File target = new File(appConfiguration.getFullProfileImagePath()+"/"+fileName);
        FileUtils.copyFile(source,target);

        mockMvc.perform(get("/images/"+appConfiguration.getProfileImagesFolder()+"/"+fileName)).andExpect(status().isOk());
    }

    @Test
    public void getStaticFile_whenImageExistsInAttachmentsFolder_receiveOK() throws Exception {
        String fileName = "profile-picture.png";
        Resource resource = new ClassPathResource("profile.png");
        File source = resource.getFile();

        File target = new File(appConfiguration.getFullAttachmentsPath()+"/"+fileName);
        FileUtils.copyFile(source,target);

        mockMvc.perform(get("/images/"+appConfiguration.getFullAttachmentsPath()+"/"+fileName)).andExpect(status().isOk());
    }

    @Test
    public void getStaticFile_whenImageDoesNotExist_ReceiveNotFound() throws Exception {
        mockMvc.perform(get("/images/"+appConfiguration.getAttachmentsFodler()+"there-is-no-such-image.png")).andExpect(status().isNotFound());
    }

    @Test
    public void getStaticFile_whenImageExistsInAttachmentsFolder_receiveOKWithCacheHeaders() throws Exception {
        String fileName = "profile-picture.png";
        Resource resource = new ClassPathResource("profile.png");
        File source = resource.getFile();

        File target = new File(appConfiguration.getFullAttachmentsPath()+"/"+fileName);
        FileUtils.copyFile(source,target);

        MvcResult result = mockMvc.perform(get("/images/" + appConfiguration.getFullAttachmentsPath() + "/" + fileName)).andReturn();

        String cacheControl = result.getResponse().getHeaderValue("Cache-Control").toString();
        assertThat(cacheControl).containsIgnoringCase("max-age=31536000");
    }



    @After
    public void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImagePath()));
        FileUtils.cleanDirectory(new File(appConfiguration.getFullAttachmentsPath()));
    }
}

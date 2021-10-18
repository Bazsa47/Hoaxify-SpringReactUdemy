package com.hoaxify.hoaxify.file;

import com.hoaxify.hoaxify.configuration.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {

    public FileService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        this.tika = new Tika();
    }

    AppConfiguration appConfiguration;

    Tika tika;

    public String saveProfileImage(String base64Image) throws IOException {
        String imagename = UUID.randomUUID().toString().replaceAll("-","");

        byte[] decodedBytes= Base64.getDecoder().decode(base64Image);
        File target = new File(appConfiguration.getFullProfileImagePath()+"/"+imagename);
        FileUtils.writeByteArrayToFile(target,decodedBytes);
        return imagename;
    }

    public String detectType(byte[] fileArr) {
        Tika tika = new Tika();
        return tika.detect(fileArr);
    }

    public void deleteProfileImage(String image) {
        try {
            Files.deleteIfExists(Paths.get(appConfiguration.getFullProfileImagePath()+"/"+image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

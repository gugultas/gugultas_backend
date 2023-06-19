package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.service.ImageModelService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;


@Service
public class ImageModelServiceImpl implements ImageModelService {
    private final Path rootProd = Paths.get("./prod-uploads");
    private final Path rootDev = Paths.get("./dev-uploads");
    private final Path rootTest = Paths.get("./test-uploads");

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Profile("dev")
    @Override
    public void init() {
        try {
            File directoryDev = new File("uploads-dev");
            FileUtils.cleanDirectory(directoryDev);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resource load(String filename) {
        Path root = null;
        if (this.activeProfile.equals("prod")) {
            root = this.rootProd;
        } else if (this.activeProfile.equals("test")) {
            root = this.rootTest;
        } else {
            root = this.rootDev;
        }

        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void upload(InputStream inputStream, String filename) {
        Path root = null;
        if (this.activeProfile.equals("prod")) {
            root = this.rootProd;
        } else if (this.activeProfile.equals("test")) {
            root = this.rootTest;
        } else {
            root = this.rootDev;
        }

        try {
            Files.copy(inputStream, root.resolve(filename));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }


    }
}

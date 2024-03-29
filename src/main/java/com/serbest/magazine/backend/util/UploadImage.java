package com.serbest.magazine.backend.util;

import com.serbest.magazine.backend.entity.ImageModel;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UploadImage {

    public static String changeNameWithTimeStamp(String filename) {
        try {
            if (!filename.contains(".")){
                throw new IllegalArgumentException("Please provide a valid file.");
            }
            String[] names = filename.split("\\.");

            if (names.length == 1){
                throw new IllegalArgumentException("Please provide a valid file with proper extension.");
            }

            names[0] = names[0] + System.currentTimeMillis();
            return String.join(".", names);
        }catch (Exception e){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    public static ImageModel uploadImage(MultipartFile file) throws IOException {
        if (file == null) {
            return new ImageModel();
        }
        return new ImageModel(file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }
}

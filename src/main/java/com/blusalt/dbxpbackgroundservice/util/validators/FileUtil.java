package com.blusalt.dbxpbackgroundservice.util.validators;

import com.blusalt.commons.exceptions.DbxpApplicationException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static void createKeyFile(String key, String fileName) {
        try {
            File file = new File("src/main/resources/rsa_keys/" + fileName);
            Path path = Paths.get("src/main/resources/rsa_keys/" + fileName);
            Files.writeString(path, key,
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DbxpApplicationException("unable to create file " + e.getMessage());
        }
    }

    public static void deleteKeyFile(String fileName) {
        try {
            File file = new File("src/main/resources/rsa_keys/" + fileName);
            file.delete();
        } catch (Exception e) {
            throw new DbxpApplicationException("unable to delete file " + e.getMessage());
        }

    }
}

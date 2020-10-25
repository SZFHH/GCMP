package com.haha.gcmp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static void createDirectory(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");

        if (Files.notExists(path)) {
            Files.createDirectories(path);

            log.debug("Created directory: [{}]", path);
        }
    }

    public static void createFile(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");
        if (Files.notExists(path)) {
            Files.createFile(path);
            log.debug("Created file: [{}]", path);
        }
    }

    public static void writeFile(Path path, String content) throws IOException {
        Assert.notNull(path, "Path must not be null");
        createFile(path);
        BufferedWriter bfw = Files.newBufferedWriter(path);
        bfw.write(content);
        bfw.flush();
        bfw.close();
        log.debug("Write into file: [{}]", path);
    }

    public static void deleteFile(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");
        Files.deleteIfExists(path);
    }

    public static File[] listDir(Path path) {

        Assert.isTrue(Files.isDirectory(path), "Path must be a directory");
        File file = path.toFile();
        return file.listFiles();
    }
}

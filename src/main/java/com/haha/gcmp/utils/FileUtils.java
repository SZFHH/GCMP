package com.haha.gcmp.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static void saveMultiFile(String basePath, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return;
        }
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        for (MultipartFile file : files) {
            String filePath = basePath + "/" + file.getOriginalFilename();
            makeDir(filePath);
            File dest = new File(filePath);
            try {
                file.transferTo(dest);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 确保目录存在，不存在则创建
     *
     * @param filePath
     */
    private static void makeDir(String filePath) {
        if (filePath.lastIndexOf('/') > 0) {
            String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf('/');
        if (index == -1) {
            return path;
        }
        return path.substring(index + 1);
    }

    public static String joinPaths(String... arr) {
        return StringUtils.join(arr, '/');
    }

    public static String getDir(String path) {
        int index = path.lastIndexOf('/');
        return path.substring(0, index);
    }

    public static String readFile(Path path) throws IOException {

        return org.apache.commons.io.FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
    }

}

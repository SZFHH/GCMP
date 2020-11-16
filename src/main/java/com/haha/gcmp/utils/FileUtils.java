package com.haha.gcmp.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 如果文件夹不存在，递归创建文件夹
     *
     * @param path 文件夹路径
     * @throws IOException if an I/O error occurs
     */
    public static void createDirectory(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");

        if (Files.notExists(path)) {
            Files.createDirectories(path);

            log.debug("Created directory: [{}]", path);
        }
    }

    /**
     * 如果父文件夹不存在，创建父文件夹
     *
     * @param path must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void createParentDirectory(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");
        Path parentPath = path.getParent();
        createDirectory(parentPath);
    }

    /**
     * 如果文件夹为空，删除
     *
     * @param path must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void deleteDirIfEmpty(Path path) throws IOException {
        long count = Files.list(path).count();
        if (count == 0) {
            Files.delete(path);
        }
    }

    /**
     * 如果文件不存在，创建文件
     *
     * @param path 文件路径
     * @throws IOException if an I/O error occurs
     */
    public static void createFile(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");
        if (Files.notExists(path)) {
            createParentDirectory(path);
            Files.createFile(path);
            log.debug("Created file: [{}]", path);
        }
    }

    /**
     * 写文件
     *
     * @param path    文件路径
     * @param content 文件内容
     * @throws IOException if an I/O error occurs
     */
    public static void writeFile(Path path, String content) throws IOException {
        Assert.notNull(path, "Path must not be null");
        createFile(path);
        BufferedWriter bfw = Files.newBufferedWriter(path);
        bfw.write(content);
        bfw.flush();
        bfw.close();
        log.debug("Write into file: [{}]", path);
    }

    /**
     * 如果文件存在，删除文件
     *
     * @param path 文件路径
     * @throws IOException if an I/O error occurs
     */
    public static void deleteFile(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");
        Files.deleteIfExists(path);
    }

    /**
     * 如果文件夹存在，删除文件夹
     *
     * @param path 文件夹路径
     * @throws IOException if an I/O error occurs
     */
    public static void deleteDir(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");
        Files.walkFileTree(path, new DeleteDirectory());
    }

    /**
     * 获取文件夹下的所有文件
     *
     * @param path 文件夹路径
     * @return list of Files
     */
    public static File[] listDir(Path path) {
        Assert.isTrue(Files.isDirectory(path), "Path must be a directory");
        File file = path.toFile();
        return file.listFiles();
    }

    /**
     * 获取路径的文件名
     *
     * @param path 文件路径
     * @return 文件名
     */
    public static String getFileName(String path) {
        int index = path.lastIndexOf('/');
        if (index == -1) {
            return path;
        }
        return path.substring(index + 1);
    }

    /**
     * 合并路径
     *
     * @param arr 路径列表
     * @return 合并后的而路径
     */
    public static String joinPaths(String... arr) {
        return StringUtils.join(arr, '/');
    }

    /**
     * 获取父文件夹路径
     *
     * @param path 文件（夹）路径
     * @return 父文件夹路径
     */
    public static String getDir(String path) {
        int index = path.lastIndexOf('/');
        return path.substring(0, index);
    }

    /**
     * 读取文件
     *
     * @param path 文件路径
     * @return 文件内容
     * @throws IOException if an I/O error occurs
     */
    public static String readFile(Path path) throws IOException {

        return org.apache.commons.io.FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
    }

    public static class DeleteDirectory implements FileVisitor<Path> {

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.deleteIfExists(dir);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
            throws IOException {
            throw exc;
        }
    }

}

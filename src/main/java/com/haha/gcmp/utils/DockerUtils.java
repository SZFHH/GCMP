package com.haha.gcmp.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Image;

import java.io.File;
import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public class DockerUtils {

    public static List<Image> listImages(DockerClient dockerClient) {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        return listImagesCmd.exec();
    }

    public static void pullImage(DockerClient dockerClient, String tag) {
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(tag);
        try {
            pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void createImage(DockerClient dockerClient, File dockerFile) {
        String tag = dockerFile.getName();
        BuildImageCmd imgToCreate = dockerClient.buildImageCmd(dockerFile).withTag(tag);
        try {
            BuildImageResultCallback exec = imgToCreate.exec(new BuildImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void removeImage(DockerClient dockerClient, String tag) {
        RemoveImageCmd imgToRemove = dockerClient.removeImageCmd(tag);
        try {
            imgToRemove.exec();
        } catch (NotFoundException ignored) {

        }

    }
}

package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.params.ImageToAddParam;
import com.haha.gcmp.model.params.ImageToRemoveParam;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public interface DockerService {
    List<Image> listCommonImage();

    List<Image> listUserImage();

    Map<String, List<Image>> listAllImage();

    Image getImage(int imageId);

    void addImage(ImageToAddParam imageToAddParam);

    void addCommonImage(ImageToAddParam imageToAddParam);

    void removeCommonImage(ImageToRemoveParam imageToRemoveParam);

    boolean isSufficientDockerQuota();

    void removeUserImage(ImageToRemoveParam imageToRemoveParam);

    void addUserImage(ImageToAddParam imageToAddParam);

    Path getUserDockerFileDir();

    boolean imageExists(int serverId, String tag);

    void pullImage(int serverId, String tag);

    void createImage(int serverId, String tag);

    void removeImage(int serverId, String tag);

    int getDockerFileCount();

    int getUserDockerFileQuota();
}

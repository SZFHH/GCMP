package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.params.ImageToAddParam;
import com.haha.gcmp.model.params.ImageToRemoveParam;

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

    void addImage(ImageToAddParam imageToAddParam);

    void addCommonImage(ImageToAddParam imageToAddParam);

    void removeCommonImage(ImageToRemoveParam imageToRemoveParam);

    boolean isSufficientDockerQuota();

    void removeUserImage(ImageToRemoveParam imageToRemoveParam);

    void addUserImage(ImageToAddParam imageToAddParam);

    boolean imageExists(String hostName, String tag);

    void pullImage(String hostName, String tag);

    void createImage(String hostName, String tag);

    void removeImage(String hostName, String tag);

    int getDockerFileCount();

    int getUserDockerFileQuota();
}

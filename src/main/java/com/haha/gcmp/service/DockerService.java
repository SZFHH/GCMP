package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.params.ImageParam;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public interface DockerService {
    /**
     * 获取所有的公共镜像
     *
     * @return list of images
     */
    List<Image> listCommonImage();

    /**
     * 获取当前用户的镜像
     *
     * @return list of images
     */
    List<Image> listUserImage();

    /**
     * 获取所有的镜像，需要管理员权限
     *
     * @return map from user name to list of images
     */
    Map<String, List<Image>> listAllImage();

    /**
     * 根据镜像id获取镜像
     *
     * @param imageId image id
     * @return image
     */
    Image getImage(int imageId);

    /**
     * 添加镜像
     *
     * @param imageParam must not be null
     */
    void addImage(ImageParam imageParam);

    /**
     * 添加公共镜像
     *
     * @param imageParam must not be null
     */
    void addCommonImage(ImageParam imageParam);

    /**
     * 删除公共镜像
     *
     * @param imageId image id
     */
    void removeCommonImage(int imageId);

    /**
     * 当前用户是否有剩余的镜像限额
     *
     * @return true if a user has sufficient unused image quota
     */
    boolean isSufficientImageQuota();

    /**
     * 删除用户镜像
     *
     * @param imageId image id
     */
    void removeUserImage(int imageId);

    /**
     * 添加用户镜像
     *
     * @param imageParam must not be null
     */
    void addUserImage(ImageParam imageParam);

    /**
     * 获取存储用户docker file的绝对路径
     *
     * @return absolute path of user's dockerfile directory
     */
    Path getUserDockerFileDir();

    /**
     * 检查镜像是否存在
     *
     * @param serverId server id
     * @param tag      image tag
     * @return true if exists，false otherwise
     */
    boolean imageExists(int serverId, String tag);

    /**
     * 让服务器拉取镜像
     *
     * @param serverId server id
     * @param tag      image tag
     */
    void pullImage(int serverId, String tag);

    /**
     * 从用户的dockerfile，在服务器上创建镜像
     *
     * @param serverId server id
     * @param tag      image tag
     */
    void createImage(int serverId, String tag);

    /**
     * 在服务器上移除镜像
     *
     * @param serverId server id
     * @param tag      image tag
     */
    void removeImage(int serverId, String tag);

    /**
     * 获取用户自己上传的dockerfile总数
     *
     * @return user's dockerfile count
     */
    int getDockerFileCount();

    /**
     * 获取用户的私人镜像限额
     *
     * @return user's dockerfile quota
     */
    int getUserDockerFileQuota();
}

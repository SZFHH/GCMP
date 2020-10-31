package com.haha.gcmp.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.ImageToAddParam;
import com.haha.gcmp.model.params.ImageToRemoveParam;
import com.haha.gcmp.repository.DockerMapper;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.DockerService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.service.base.AbstractServerService;
import com.haha.gcmp.utils.CollectionUtils;
import com.haha.gcmp.utils.DockerUtils;
import com.haha.gcmp.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.haha.gcmp.config.GcmpConst.PROTOCOL_TCP;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
@Service
public class DockerServiceImpl extends AbstractServerService<DockerClient> implements DockerService {
    private final DockerMapper dockerMapper;
    private final AdminService adminService;
    private final UserService userService;

    public DockerServiceImpl(DockerMapper dockerMapper, AdminService adminService, UserService userService, GcmpProperties gcmpProperties) {
        super(gcmpProperties);
        this.dockerMapper = dockerMapper;
        this.adminService = adminService;
        this.userService = userService;
    }

    @Override
    public List<Image> listCommonImage() {
        return dockerMapper.findByOwner(adminService.getAdminId());
    }

    @Override
    public List<Image> listUserImage() {
        User user = userService.getCurrentUser();
        return dockerMapper.findByOwner(user.getId());
    }

    @Override
    public Map<String, List<Image>> listAllImage() {
        List<User> allUser = userService.getAllUser();
        Map<Integer, String> userMap = CollectionUtils.convertToMap(allUser, User::getId, User::getUserName);
        List<Image> allImages = dockerMapper.findAll();

        return CollectionUtils.convertToListMap(allImages, image -> userMap.get(image.getOwner()));
    }

    @Override
    public void addImage(ImageToAddParam imageToAddParam) {
        if (!isSufficientDockerQuota()) {
            throw new BadRequestException("镜像数量达到限额！");
        }
        if (imageToAddParam.isCommon()) {
            addCommonImage(imageToAddParam);
        } else {
            addUserImage(imageToAddParam);
        }
    }

    @Override
    public void addCommonImage(ImageToAddParam imageToAddParam) {
        Image image = new Image(imageToAddParam.getTag(), imageToAddParam.getDesc(), adminService.getAdminId(), imageToAddParam.getAlias());
        dockerMapper.insert(image);
    }

    public void removeAllPulledOrCreatedImages(String tag) {
        Set<String> allHostName = getAllHostName();
        for (String hostName : allHostName) {
            removeImage(hostName, tag);
        }
    }

    @Override
    public void removeCommonImage(ImageToRemoveParam imageParam) {
        removeAllPulledOrCreatedImages(imageParam.getTag());
        dockerMapper.deleteById(imageParam.getId());
    }

    @Override
    public void removeUserImage(ImageToRemoveParam imageParam) {
        Image image = dockerMapper.findById(imageParam.getId());
        if (image == null) {
            return;
        }
        User user = userService.getCurrentUser();
        if (image.getOwner() != user.getId() || !image.getTag().equals(imageParam.getTag())) {
            throw new BadRequestException("不能删除其他人的镜像");
        }
        Path dirPath = getUserDockerFileDir();
        Path filePath = dirPath.resolve(imageParam.getTag());
        try {
            FileUtils.deleteFile(filePath);
        } catch (IOException e) {
            throw new ServiceException("删除文件" + filePath.toString() + "异常", e);
        }
        removeAllPulledOrCreatedImages(imageParam.getTag());
        dockerMapper.deleteById(imageParam.getId());
    }

    @Override
    public boolean isSufficientDockerQuota() {
        User user = userService.getCurrentUser();
        if (user.getUserName().equals(gcmpProperties.getAdminName())) {
            return true;
        }
        return user.getDockerQuota() > getDockerFileCount();
    }

    @Override
    public int getDockerFileCount() {
        Path dirPath = getUserDockerFileDir();
        try {
            FileUtils.createDirectory(dirPath);
        } catch (IOException e) {
            throw new ServiceException("创建用户dockerFile文件夹(" + dirPath + ")异常", e);
        }
        return FileUtils.listDir(dirPath).length;
    }

    @Override
    public int getUserDockerFileQuota() {
        User user = userService.getCurrentUser();
        return user.getDockerQuota();
    }

    @Override
    public void addUserImage(ImageToAddParam imageToAddParam) {
        if (!isSufficientDockerQuota()) {
            throw new BadRequestException("镜像数量达到限额！");
        }
        User user = userService.getCurrentUser();
        String tag = user.getUserName() + "_" + imageToAddParam.getAlias();
        try {
            saveDockerFile(imageToAddParam.getDockerFile(), tag);
        } catch (IOException e) {
            throw new ServiceException("保存用户(" + user.getUserName() + ")镜像(" + imageToAddParam.getAlias() + ")失败！", e);
        }

        Image image = new Image(tag, imageToAddParam.getDesc(), user.getId(), imageToAddParam.getAlias());
        dockerMapper.insert(image);
    }

    @Override
    public Path getUserDockerFileDir() {
        User user = userService.getCurrentUser();
        return Paths.get(gcmpProperties.getDockerFileRoot(), user.getUserName());
    }

    private void saveDockerFile(String dockFile, String tag) throws IOException {
        Path dirPath = getUserDockerFileDir();
        FileUtils.createDirectory(dirPath);
        Path filePath = dirPath.resolve(tag);
        if (Files.exists(filePath)) {
            throw new BadRequestException("镜像已存在,请更换镜像名或删除已存在的镜像。");
        }
        FileUtils.writeFile(filePath, dockFile);
    }

    @Override
    public boolean imageExists(String hostName, String tag) {
        DockerClient dockerClient = getClient(hostName);
        List<com.github.dockerjava.api.model.Image> images = DockerUtils.listImages(dockerClient);
        for (com.github.dockerjava.api.model.Image image : images) {
            if (tag.equals(image.getRepoTags()[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void pullImage(String hostName, String tag) {
        DockerClient dockerClient = getClient(hostName);
        DockerUtils.pullImage(dockerClient, tag);
    }

    @Override
    public void createImage(String hostName, String tag) {
        DockerClient dockerClient = getClient(hostName);
        Path dirPath = getUserDockerFileDir();
        Path filePath = dirPath.resolve(tag);
        DockerUtils.createImage(dockerClient, filePath.toFile());
    }

    @Override
    public void removeImage(String hostName, String tag) {
        DockerClient dockerClient = getClient(hostName);
        DockerUtils.removeImage(dockerClient, tag);
    }


    @Override
    protected DockerClient doInitClientContainer(String hostName, String hostIp, String username, String password) {
        String uri = PROTOCOL_TCP + hostIp + ":" + gcmpProperties.getDockerClientPort();
        return DockerClientBuilder.getInstance(uri).build();
    }
}

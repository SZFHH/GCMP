package com.haha.gcmp.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.ForbiddenException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.ImageParam;
import com.haha.gcmp.repository.ImageMapper;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.DockerService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.service.base.AbstractServerService;
import com.haha.gcmp.utils.CollectionUtils;
import com.haha.gcmp.utils.DockerUtils;
import com.haha.gcmp.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.haha.gcmp.model.support.GcmpConst.PROTOCOL_TCP;

/**
 * Docker service implementation.
 *
 * @author SZFHH
 * @date 2020/10/24
 */
@Service
public class DockerServiceImpl extends AbstractServerService<DockerClient> implements DockerService {
    private final ImageMapper imageMapper;
    private AdminService adminService;
    private UserService userService;

    public DockerServiceImpl(ImageMapper imageMapper, GcmpProperties gcmpProperties) {
        super(gcmpProperties);
        this.imageMapper = imageMapper;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<Image> listCommonImage() {
        return imageMapper.listByOwner(adminService.getAdminId());
    }

    @Override
    public List<Image> listUserImage() {
        User user = userService.getCurrentUser();
        return imageMapper.listByOwner(user.getId());
    }

    @Override
    public Map<String, List<Image>> listAllImage() {
        List<User> allUser = userService.getAllUser();
        Map<Integer, String> userMap = CollectionUtils.convertToMap(allUser, User::getId, User::getUsername);
        List<Image> allImages = imageMapper.listAll();

        return CollectionUtils.convertToListMap(allImages, image -> userMap.get(image.getOwner()));
    }

    @Override
    public Image getImage(int imageId) {
        return imageMapper.getById(imageId);
    }

    @Override
    public void addImage(ImageParam imageParam) {
        if (!isSufficientImageQuota()) {
            throw new BadRequestException("镜像数量达到限额！");
        }
        if (imageParam.isCommon()) {
            addCommonImage(imageParam);
        } else {
            addUserImage(imageParam);
        }
    }

    @Override
    public void addCommonImage(ImageParam imageParam) {
        Image image = fromImageParam(imageParam, true);
        imageMapper.insert(image);
    }

    @Override
    public void removeAllPulledOrCreatedImages(String tag) {
        int serverCount = getServerCount();
        for (int i = 0; i < serverCount; i++) {
            removeImage(i, tag);
        }
    }

    @Override
    public void removeCommonImage(int imageId) {
        Image image = imageMapper.getById(imageId);
        removeAllPulledOrCreatedImages(image.getTag());
        imageMapper.removeById(image.getId());
    }

    @Override
    public void removeUserImage(int imageId) {
        Image image = imageMapper.getById(imageId);
        if (image == null) {
            return;
        }
        User user = userService.getCurrentUser();
        if (image.getOwner() != user.getId()) {
            throw new ForbiddenException("不能删除其他人的镜像");
        }
        Path dirPath = getUserDockerFileDir();
        Path filePath = dirPath.resolve(image.getTag());
        try {
            FileUtils.deleteFile(filePath);
        } catch (IOException e) {
            throw new ServiceException("删除文件" + filePath.toString() + "异常", e);
        }
        removeAllPulledOrCreatedImages(image.getTag());
        imageMapper.removeById(image.getId());
    }

    @Override
    public boolean isSufficientImageQuota() {
        User user = userService.getCurrentUser();
        if (user.getUsername().equals(gcmpProperties.getAdminName())) {
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
            throw new ServiceException("创建用户dockerFile文件夹:" + dirPath + "异常", e);
        }
        return FileUtils.listDir(dirPath).length;
    }

    @Override
    public int getUserDockerFileQuota() {
        User user = userService.getCurrentUser();
        return user.getDockerQuota();
    }

    @Override
    public String getDockerFile(int imageId) {
        Image image = imageMapper.getById(imageId);
        if (image == null) {
            throw new BadRequestException("该镜像不存在");
        }
        String tag = image.getTag();
        try {
            return getDockerFile(tag);
        } catch (IOException e) {
            throw new ServiceException("读取dockerFile异常, imageId:" + imageId + " tag: " + tag, e);
        }

    }

    private Image fromImageParam(ImageParam imageParam, boolean isCommon) {
        int ownerId = adminService.getAdminId();
        String tag = imageParam.getTag();
        if (!isCommon) {
            User user = userService.getCurrentUser();
            ownerId = user.getId();
            tag = user.getUsername() + "_" + imageParam.getAlias();
        }

        return new Image(imageParam.getId(), tag, imageParam.getDesc(), ownerId, imageParam.getAlias());
    }

    @Override
    public int updateUserImage(ImageParam imageParam) {
        Image newImage = fromImageParam(imageParam, false);
        String tag = imageParam.getTag();
        try {
            removeDockerFile(tag);
            tag = newImage.getTag();
            saveDockerFile(imageParam.getDockerFile(), tag);
        } catch (IOException e) {
            throw new ServiceException("更新用户镜像异常，tag: " + tag, e);
        }
        return imageMapper.update(newImage);

    }

    @Override
    public int updateCommonImage(ImageParam imageParam) {
        Image image = fromImageParam(imageParam, true);
        return imageMapper.update(image);
    }

    @Override
    public List<Image> listByOwnerId(int ownerId) {
        return imageMapper.listByOwner(ownerId);
    }

    @Override
    public void removeById(int id) {
        imageMapper.removeById(id);
    }

    @Override
    public void addUserImage(ImageParam imageParam) {
        if (!isSufficientImageQuota()) {
            throw new BadRequestException("镜像数量达到限额！");
        }
        Image image = fromImageParam(imageParam, false);
        String tag = image.getTag();
        try {
            saveDockerFile(imageParam.getDockerFile(), tag);
        } catch (IOException e) {
            throw new ServiceException("保存用户镜像:" + tag + "异常！", e);
        }
        imageMapper.insert(image);
    }

    @Override
    public Path getUserDockerFileDir() {
        User user = userService.getCurrentUser();
        return Paths.get(gcmpProperties.getDockerFileRoot(), user.getUsername());
    }

    private void removeDockerFile(String tag) throws IOException {
        Path dirPath = getUserDockerFileDir();
        Path filePath = dirPath.resolve(tag);
        FileUtils.deleteFile(filePath);
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

    private String getDockerFile(String tag) throws IOException {
        Path dirPath = getUserDockerFileDir();
        Path filePath = dirPath.resolve(tag);
        return FileUtils.readFile(filePath);
    }

    @Override
    public boolean imageExists(int serverId, String tag) {
        DockerClient dockerClient = getClient(serverId);
        List<com.github.dockerjava.api.model.Image> images = DockerUtils.listImages(dockerClient);
        for (com.github.dockerjava.api.model.Image image : images) {
            if (tag.equals(image.getRepoTags()[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void pullImage(int serverId, String tag) {
        DockerClient dockerClient = getClient(serverId);
        DockerUtils.pullImage(dockerClient, tag);
    }

    @Override
    public void createImage(int serverId, String tag) {
        DockerClient dockerClient = getClient(serverId);
        Path dirPath = getUserDockerFileDir();
        Path filePath = dirPath.resolve(tag);
        DockerUtils.createImage(dockerClient, filePath.toFile());
    }

    @Override
    public void removeImage(int serverId, String tag) {
        DockerClient dockerClient = getClient(serverId);
        DockerUtils.removeImage(dockerClient, tag);
    }

    @Override
    protected DockerClient doInitClientContainer(ServerProperty serverProperty) {
        String uri = PROTOCOL_TCP + serverProperty.getHostIp() + ":" + gcmpProperties.getDockerClientPort();
        return DockerClientBuilder.getInstance(uri).build();
    }
}

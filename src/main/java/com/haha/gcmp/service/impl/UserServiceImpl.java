package com.haha.gcmp.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.NotFoundException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.entity.Task;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.UserParam;
import com.haha.gcmp.repository.UserMapper;
import com.haha.gcmp.security.util.SecurityUtils;
import com.haha.gcmp.service.DataService;
import com.haha.gcmp.service.DockerService;
import com.haha.gcmp.service.TaskService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * User service implementation.
 *
 * @author SZFHH
 * @date 2020/10/23
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private TaskService taskService;
    private DataService dataService;
    private DockerService dockerService;
    private final GcmpProperties gcmpProperties;

    public UserServiceImpl(UserMapper userMapper, GcmpProperties gcmpProperties) {
        this.userMapper = userMapper;
        this.gcmpProperties = gcmpProperties;
    }

    @Autowired
    public void setDockerService(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public User getByIdOfNonNull(int id) {
        User user = userMapper.getById(id);
        if (user == null) {
            throw new NotFoundException("The id does not exist").setErrorData(id);
        }
        return user;
    }

    @Override
    public User getByIdOfNullable(int id) {
        return userMapper.getById(id);
    }

    @Override
    public User getByNameOfNonNull(String userName) {
        User user = userMapper.getByUsername(userName);
        if (user == null) {
            throw new NotFoundException("没找到用户：" + userName);
        }
        return user;
    }

    @Override
    public User getByNameOfNullable(String userName) {
        return userMapper.getByUsername(userName);
    }

    @Override
    public void createUser(UserParam registerParam) {
        User user = registerParam.toEntity();
        if (userMapper.getByUsername(user.getUsername()) != null) {
            throw new BadRequestException("用户名已存在");
        }
        setPassword(user);
        userMapper.insert(user);
    }

    @Override
    public User getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.listAll();
    }

    @Override
    public int updatePassword(User user) {
        setPassword(user);
        return userMapper.updatePassword(user);
    }

    @Override
    public int updateDockerQuota(User user) {
        return userMapper.updateDockerQuota(user);
    }

    @Override
    public void removeUser(int userId) {
        User user = userMapper.getById(userId);

        // 取消用户任务
        List<Task> tasks = taskService.listByUserId(userId);
        for (Task task : tasks) {
            taskService.cancelTask(task.getId());
        }

        // 删除用户文件，临时文件
        String dataAbsolutePath = FileUtils.joinPaths(gcmpProperties.getDataRoot(), user.getUsername());
        String tempAbsolutePath = FileUtils.joinPaths(gcmpProperties.getTempFileRoot(), user.getUsername());
        int size = gcmpProperties.getServerProperties().size();
        for (int i = 0; i < size; i++) {
            dataService.removeAbsolutePath(dataAbsolutePath, i);
            dataService.removeAbsolutePath(tempAbsolutePath, i);
        }
        dataService.removeTempFileByUserId(userId);

        // 删除用户镜像
        String dockerFilePath = FileUtils.joinPaths(gcmpProperties.getDockerFileRoot(), user.getUsername());
        try {
            FileUtils.deleteDir(Paths.get(dockerFilePath));
        } catch (IOException e) {
            throw new ServiceException("删除用户dockerFile异常,用户: " + user.getUsername(), e);
        }
        List<Image> images = dockerService.listByOwnerId(userId);
        for (Image image : images) {
            dockerService.removeAllPulledOrCreatedImages(image.getTag());
            dockerService.removeById(image.getId());
        }

        // 删除用户
        userMapper.removeById(userId);

    }


    private void setPassword(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
    }
}

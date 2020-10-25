package com.haha.gcmp.service.impl;

import com.haha.gcmp.config.GcmpProperties;
import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.service.DataService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.service.base.AbstractServerService;
import com.haha.gcmp.service.support.FileClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
@Service
public class DataServiceImpl extends AbstractServerService<FileClient> implements DataService {
    private final UserService userService;

    protected DataServiceImpl(GcmpProperties gcmpProperties, UserService userService) {
        super(gcmpProperties);
        this.userService = userService;
    }

    @Override
    protected FileClient doInitClientContainer(String hostName, String hostIp) {
        return new FileClient(hostName, hostIp, getHostUser(hostName), getHostPassword(hostName));
    }
    
    @Override
    public List<DataFile> listDataDir(String hostName, String dirPath) {
        Path rootPath = getUserDataDir();
        Path absolutePath = rootPath.resolve(dirPath);
        FileClient fileClient = getClient(hostName);
        return fileClient.listDir(absolutePath);
    }

    @Override
    public void upload(String hostName, MultipartFile[] files) {

    }

    @Override
    public void unzip(String hostName, String filePath) {

    }

    @Override
    public void remove(String hostName, String path) {

    }

    @Override
    public Path getUserDataDir() {
        User user = userService.getCurrentUser();
        return Paths.get(gcmpProperties.getDataRoot(), user.getUserName());
    }
}

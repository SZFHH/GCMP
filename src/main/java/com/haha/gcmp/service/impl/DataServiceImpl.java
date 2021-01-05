package com.haha.gcmp.service.impl;

import com.haha.gcmp.client.fileclient.FileClient;
import com.haha.gcmp.client.fileclient.FtpFileClientImpl;
import com.haha.gcmp.client.fileclient.SftpFileClientImpl;
import com.haha.gcmp.config.propertites.FileSshPoolConfig;
import com.haha.gcmp.config.propertites.FtpPoolConfig;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.config.propertites.SftpPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.Data;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.model.entity.TempFile;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import com.haha.gcmp.repository.CommonDataMapper;
import com.haha.gcmp.repository.TempFileMapper;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.DataService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.service.base.AbstractServerService;
import com.haha.gcmp.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.haha.gcmp.model.support.GcmpConst.FTP_TYPE_SFTP;

/**
 * Data service implementation.
 *
 * @author SZFHH
 * @date 2020/10/25
 */
@Service
public class DataServiceImpl extends AbstractServerService<FileClient> implements DataService {
    private UserService userService;
    private AdminService adminService;
    private final TempFileMapper tempfileMapper;
    private final SftpPoolConfig sftpPoolConfig;
    private final FileSshPoolConfig sshPoolConfig;
    private final FtpPoolConfig ftpPoolConfig;
    private final CommonDataMapper commonDataMapper;


    protected DataServiceImpl(GcmpProperties gcmpProperties, TempFileMapper tempfileMapper, SftpPoolConfig sftpPoolConfig, FileSshPoolConfig sshPoolConfig, FtpPoolConfig ftpPoolConfig, CommonDataMapper commonDataMapper) {
        super(gcmpProperties);
        this.tempfileMapper = tempfileMapper;
        this.sftpPoolConfig = sftpPoolConfig;
        this.sshPoolConfig = sshPoolConfig;
        this.ftpPoolConfig = ftpPoolConfig;
        this.commonDataMapper = commonDataMapper;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    protected FileClient doInitClientContainer(ServerProperty serverProperty) {
        String ftpType = gcmpProperties.getFtpType();
        if (FTP_TYPE_SFTP.equals(ftpType)) {
            return new SftpFileClientImpl(sftpPoolConfig, sshPoolConfig, serverProperty);
        }
        return new FtpFileClientImpl(ftpPoolConfig, sshPoolConfig, serverProperty);
    }

    @Override
    public List<Data> listDataDir(DataParam dataParam) {
        return doListDataDir(dataParam.getServerId(), getUserDataPath(""), dataParam.getRelativePath());
    }

    private List<Data> doListDataDir(int serverId, String rootPath, String relativePath) {
        String absolutePath = FileUtils.joinPaths(rootPath, relativePath);
        FileClient fileClient = getClient(serverId);
        List<Data> data = fileClient.listDir(absolutePath);
        data.forEach(dataFile -> dataFile.setPath(FileUtils.joinPaths(relativePath, dataFile.getName())));

        return data;
    }

    @Override
    public void unzip(DataParam dataParam) {
        FileClient fileClient = getClient(dataParam.getServerId());
        String absolutePath = getUserDataPath(dataParam.getRelativePath());
        fileClient.unzip(absolutePath);
    }

    @Override
    public void removeRelativePath(DataParam dataParam) {
        String absolutePath = getUserDataPath(dataParam.getRelativePath());
        removeAbsolutePath(absolutePath, dataParam.getServerId());
    }

    @Override
    public void removeAbsolutePath(String absolutePath, int serverId) {
        FileClient fileClient = getClient(serverId);
        fileClient.remove(absolutePath);
    }

    @Override
    public String getUserDataPath(String relativePath) {
        User user = userService.getCurrentUser();
        if (user.getId() == adminService.getAdminId()) {
            return FileUtils.joinPaths(gcmpProperties.getGcmpRoot(), relativePath);
        }
        return FileUtils.joinPaths(gcmpProperties.getDataRoot(), user.getUsername(), relativePath);
    }

    @Override
    public String getUserTempPath(String identifier) {
        User user = userService.getCurrentUser();
        return FileUtils.joinPaths(gcmpProperties.getTempFileRoot(), user.getUsername(), identifier);
    }

    @Override
    public CheckFileResult checkChunk(CheckFileQuery param) {
        String absolutePath = getUserDataPath(param.getRelativePath());
        FileClient client = getClient(param.getServerId());
        boolean existed = client.checkFileExists(absolutePath);
        int userId = userService.getCurrentUser().getId();
        if (existed) {
            Data fileInfo = client.getFileInfo(absolutePath);
            if (fileInfo.getSize() == param.getTotalSize()) {
                tempfileMapper.remove(new TempFile(param.getMd5(), param.getServerId(), userId, param.getRelativePath()));
                client.removeDirIfExists(getUserTempPath(param.getMd5()));
                return new CheckFileResult(null, CheckFileResult.EXISTED);
            }
        }
        TempFile tempFile = new TempFile(param.getMd5(), param.getServerId(), userId, param.getRelativePath());
        if (!tempfileMapper.exists(tempFile)) {
            tempfileMapper.insert(tempFile);
        }
        List<Data> tempFiles = listTempFiles(param.getServerId(), param.getMd5());
        List<String> existedChunk = validateTempFile(param.getServerId(), tempFiles, param.getTotalChunks(), param.getChunkSize(), param.getTotalSize());
        return new CheckFileResult(existedChunk, CheckFileResult.NON_EXISTED);

    }

    private List<String> validateTempFile(int serverId, List<Data> tempFiles, int totalChunks, long chunkSize, long totalSize) {
        List<String> rv = new ArrayList<>();
        long lastSize = totalSize - (long) (totalChunks - 1) * chunkSize;
        for (Data data : tempFiles) {
            String absolutePath = FileUtils.joinPaths(gcmpProperties.getTempFileRoot(), data.getPath());
            int fileName = Integer.parseInt(data.getName());
            if (fileName < totalChunks) {
                if (data.getSize() == chunkSize) {
                    rv.add(data.getName());
                } else {
                    getClient(serverId).remove(absolutePath);
                }
            } else {
                if (data.getSize() == lastSize) {
                    rv.add(data.getName());
                } else {
                    getClient(serverId).remove(absolutePath);
                }
            }
        }
        return rv;
    }

    private List<Data> listTempFiles(int serverId, String md5) {
        String tempDir = FileUtils.joinPaths(userService.getCurrentUser().getUsername(), md5);
        return doListDataDir(serverId, gcmpProperties.getTempFileRoot(), tempDir);
    }

    private List<String> listTempFilePaths(int serverId, String md5) {
        List<Data> data = listTempFiles(serverId, md5);
        data.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getName())));
        List<String> rv = new ArrayList<>();
        data.forEach(dataFile -> rv.add(getUserTempPath(FileUtils.joinPaths(md5, dataFile.getName()))));
        return rv;
    }

    @Override
    public void uploadChunk(UploadChunkParam param, MultipartFile file) {
        FileClient fileClient = getClient(param.getServerId());

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new ServiceException("获取multipartFile输入流异常:文件" + param.getRelativePath(), e);
        }
        if (param.getTotalChunks() <= 1) {
            int userId = userService.getCurrentUser().getId();
            String filePath = getUserDataPath(param.getRelativePath());
            fileClient.put(data, filePath);
            TempFile tempFile = new TempFile(param.getMd5(), param.getServerId(), userId, param.getRelativePath());
            tempfileMapper.remove(tempFile);
            fileClient.remove(getUserTempPath(param.getMd5()));
        } else {
            int chunkNumber = param.getChunkNumber();
            String userTempDir = getUserTempPath(param.getMd5());
            String chunkName = String.valueOf(chunkNumber);
            fileClient.put(data, FileUtils.joinPaths(userTempDir, chunkName));
        }
    }

    @Override
    public void mergeChunk(MergeChunkParam mergeChunkParam) {
        int userId = userService.getCurrentUser().getId();
        int serverId = mergeChunkParam.getServerId();
        String md5 = mergeChunkParam.getMd5();
        String relativePath = mergeChunkParam.getRelativePath();

        List<String> mergeFileList = listTempFilePaths(serverId, md5);
        FileClient fileClient = getClient(serverId);
        String filePath = getUserDataPath(relativePath);
        fileClient.mergeFiles(mergeFileList, filePath);
        TempFile tempFile = new TempFile(md5, serverId, userId, relativePath);
        tempfileMapper.remove(tempFile);
        fileClient.remove(getUserTempPath(md5));
    }

    @Override
    public void cancelUpload(TempFile tempFile) {
        tempfileMapper.remove(tempFile);
        FileClient fileClient = getClient(tempFile.getServerId());
        fileClient.remove(getUserTempPath(tempFile.getMd5()));
        fileClient.remove(getUserDataPath(tempFile.getRelativePath()));
    }

    @Override
    public void move(DataMoveParam dataMoveParam) {
        String absoluteSrcPath = getUserDataPath(dataMoveParam.getSrcRelativePath());
        String absoluteTargetPath = getUserDataPath(dataMoveParam.getTargetRelativePath());
        FileClient fileClient = getClient(dataMoveParam.getServerId());
        fileClient.move(absoluteSrcPath, absoluteTargetPath);
    }

    @Override
    public void copy(DataMoveParam dataMoveParam) {
        String absoluteSrcPath = getUserDataPath(dataMoveParam.getSrcRelativePath());
        String absoluteTargetPath = getUserDataPath(dataMoveParam.getTargetRelativePath());
        FileClient fileClient = getClient(dataMoveParam.getServerId());
        fileClient.copy(absoluteSrcPath, absoluteTargetPath);
    }

    @Override
    public void newRelativeDir(DataParam dataParam) {
        String absolutePath = getUserDataPath(dataParam.getRelativePath());
        FileClient fileClient = getClient(dataParam.getServerId());
        fileClient.mkdirIfNotExist(absolutePath, "777");
    }

    @Override
    public void newAbsoluteDir(String absolutePath, int serverId) {
        FileClient fileClient = getClient(serverId);
        fileClient.mkdirIfNotExist(absolutePath, "777");
    }

    @Override
    public byte[] getFile(DataParam dataParam) {
        String absolutePath = getUserDataPath(dataParam.getRelativePath());
        FileClient fileClient = getClient(dataParam.getServerId());

        return fileClient.get(absolutePath);

    }

    @Override
    public List<Data> listCommonDataset() {
        return commonDataMapper.listAll();
    }

    @Override
    public void syncCommonData() {
        String commonDataRoot = gcmpProperties.getCommonDataRoot();
        List<Data> datas = doListDataDir(0, "", commonDataRoot);
        commonDataMapper.removeAll();
        for (Data data : datas) {
            if (!data.isFile()) {
                commonDataMapper.insert(data);
            }
        }
    }

    @Override
    public void removeTempFileByUserId(int userId) {
        tempfileMapper.removeByUserId(userId);
    }

    @Override
    public Resource loadFileAsResource(DataParam dataParam) {
        String absolutePath = getUserDataPath(dataParam.getRelativePath());
        FileClient fileClient = getClient(dataParam.getServerId());
        if (gcmpProperties.getFtpType().equals(FTP_TYPE_SFTP)) {
            byte[] file = getFile(dataParam);
            return new ByteArrayResource(file);
        }
        InputStream inputStream = fileClient.getInputStream(absolutePath);
        return new InputStreamResource(inputStream);
    }
}

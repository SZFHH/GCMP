package com.haha.gcmp.service.impl;

import com.haha.gcmp.config.FTPClientConfigure;
import com.haha.gcmp.config.GcmpProperties;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.dto.CheckChunkDTO;
import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.TempFileInfo;
import com.haha.gcmp.repository.FileMapper;
import com.haha.gcmp.service.DataService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.service.base.AbstractServerService;
import com.haha.gcmp.service.support.FileClient;
import com.haha.gcmp.utils.FileUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
@Service
public class DataServiceImpl extends AbstractServerService<FileClient> implements DataService {
    private final UserService userService;
    private final FileMapper fileMapper;
    private final FTPClientConfigure ftpClientConfigure;
    private static final Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    protected DataServiceImpl(GcmpProperties gcmpProperties, UserService userService, FileMapper fileMapper, FTPClientConfigure ftpClientConfigure) {
        super(gcmpProperties);
        this.userService = userService;
        this.fileMapper = fileMapper;
        this.ftpClientConfigure = ftpClientConfigure;
    }

    @Override
    protected FileClient doInitClientContainer(String hostName, String hostIp) {
        return new FileClient(hostName, hostIp, getHostUser(hostName), getHostPassword(hostName), ftpClientConfigure);
    }

    @Override
    public List<DataFile> listDataDir(DataParam dataParam) {
        return doListDataDir(dataParam.getHostName(), gcmpProperties.getDataRoot(), dataParam.getRelativePath());
    }

    private List<DataFile> doListDataDir(String hostName, String rootPath, String relativePath) {
        String absolutePath = FileUtils.joinPaths(rootPath, relativePath);
        FileClient fileClient = getClient(hostName);
        FTPFile[] ftpFiles = fileClient.listDir(absolutePath);
        List<DataFile> dataFiles = new ArrayList<>();
        for (FTPFile ftpFile : ftpFiles) {
            dataFiles.add(new DataFile(ftpFile.isFile(), ftpFile.getName(), ftpFile.getSize(),
                FileUtils.joinPaths(relativePath, ftpFile.getName())));
        }
        return dataFiles;
    }

    @Override
    public void unzip(DataParam dataParam) {
        FileClient fileClient = getClient(dataParam.getHostName());
        String absolutePath = getUserDataPath(dataParam.getRelativePath());
        fileClient.unzip(absolutePath);
    }

    @Override
    public void remove(DataParam dataParam) {
        FileClient fileClient = getClient(dataParam.getHostName());
        if (dataParam.isFile()) {
            fileClient.removeFile(dataParam.getRelativePath());
        } else {
            fileClient.removeDir(dataParam.getRelativePath());
        }
    }

    @Override
    public String getUserDataPath(String relativePath) {
        User user = userService.getCurrentUser();
        return FileUtils.joinPaths(gcmpProperties.getDataRoot(), user.getUserName(), relativePath);
    }

    @Override
    public String getUserTempPath(String identifier) {
        User user = userService.getCurrentUser();
        return FileUtils.joinPaths(gcmpProperties.getTempFileRoot(), user.getUserName(), identifier);
    }

    @Override
    public CheckChunkDTO checkChunk(CheckChunkParam param) {
        String absolutePath = getUserDataPath(param.getRelativePath());
        FileClient client = getClient(param.getHostName());
        boolean existed = client.checkFileExists(absolutePath);
        if (existed) {
            FTPFile fileInfo = client.getFileInfo(absolutePath);
            if (fileInfo.getSize() == param.getTotalSize()) {
                fileMapper.remove(new TempFileInfo(param.getMd5(), param.getHostName(), param.getRelativePath()));
                client.removeDirIfExists(getUserTempPath(param.getMd5()));
                return new CheckChunkDTO(null, CheckChunkDTO.EXISTED);
            }
        }
        TempFileInfo tempFileInfo = new TempFileInfo(param.getMd5(), param.getHostName(), param.getRelativePath());
        if (fileMapper.find(tempFileInfo) == null) {
            fileMapper.insert(tempFileInfo);
        }
        List<DataFile> tempFiles = listTempFiles(param.getHostName(), param.getMd5());
        List<String> existedChunk = validateTempFile(param.getHostName(), tempFiles, param.getTotalChunks(), param.getChunkSize(), param.getTotalSize());
        return new CheckChunkDTO(existedChunk, CheckChunkDTO.NON_EXISTED);

    }

    private List<String> validateTempFile(String hostName, List<DataFile> tempFiles, int totalChunks, long chunkSize, long totalSize) {
        List<String> rv = new ArrayList<>();
        long lastSize = totalSize - (long) (totalChunks - 1) * chunkSize;
        for (DataFile dataFile : tempFiles) {
            String absolutePath = FileUtils.joinPaths(gcmpProperties.getTempFileRoot(), dataFile.getPath());
            int fileName = Integer.parseInt(dataFile.getName());
            if (fileName < totalChunks) {
                if (dataFile.getSize() == chunkSize) {
                    rv.add(dataFile.getName());
                } else {
                    getClient(hostName).removeFile(absolutePath);
                }
            } else {
                if (dataFile.getSize() == lastSize) {
                    rv.add(dataFile.getName());
                } else {
                    getClient(hostName).removeFile(absolutePath);
                }
            }
        }
        return rv;
    }

    private List<DataFile> listTempFiles(String hostName, String md5) {
        String tempDir = FileUtils.joinPaths(userService.getCurrentUser().getUserName(), md5);
        return doListDataDir(hostName, gcmpProperties.getTempFileRoot(), tempDir);
    }

    private List<String> listTempFilePaths(String hostName, String md5) {
        List<DataFile> dataFiles = listTempFiles(hostName, md5);
        List<String> rv = new ArrayList<>();
        dataFiles.forEach(dataFile -> rv.add(FileUtils.joinPaths(gcmpProperties.getTempFileRoot(), dataFile.getPath())));
        return rv;
    }

    @Override
    public void uploadChunk(UploadChunkParam param, MultipartFile file) {
        FileClient fileClient = getClient(param.getHostName());
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new ServiceException("获取multipartFile输入流异常:文件" + param.getRelativePath(), e);
        }
        if (param.getTotalChunks() <= 1) {
            String filePath = getUserDataPath(param.getRelativePath());
            fileClient.put(inputStream, filePath);
        } else {
            int chunkNumber = param.getChunkNumber();
            String userTempDir = getUserTempPath(param.getMd5());
            String chunkName = String.valueOf(chunkNumber);
            fileClient.put(inputStream, FileUtils.joinPaths(userTempDir, chunkName));
        }
    }

    @Override
    public void mergeChunk(MergeChunkParam mergeChunkParam) {
        String hostName = mergeChunkParam.getHostName();
        String md5 = mergeChunkParam.getMd5();
        String relativePath = mergeChunkParam.getRelativePath();
        List<String> mergeFileList = listTempFilePaths(hostName, md5);
        FileClient fileClient = getClient(hostName);

        String filePath = getUserDataPath(relativePath);
        fileClient.mergeFiles(mergeFileList, filePath);
        TempFileInfo tempFileInfo = new TempFileInfo(hostName, md5, relativePath);
        fileMapper.remove(tempFileInfo);
        fileClient.removeDir(getUserTempPath(md5));
    }

    @Override
    public void cancelUpload(TempFileInfo tempFileInfo) {
        fileMapper.remove(tempFileInfo);
        FileClient fileClient = getClient(tempFileInfo.getHostName());
        fileClient.removeDir(getUserTempPath(tempFileInfo.getMd5()));
    }

    @Override
    public void move(DataMoveParam dataMoveParam) {
        String absoluteSrcPath = getUserDataPath(dataMoveParam.getSrcRelativePath());
        String absoluteTargetPath = getUserDataPath(dataMoveParam.getTargetRelativePath());
        FileClient fileClient = getClient(dataMoveParam.getHostName());
        fileClient.move(absoluteSrcPath, absoluteTargetPath);
    }
}

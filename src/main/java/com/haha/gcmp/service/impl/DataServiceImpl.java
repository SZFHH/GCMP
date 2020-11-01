package com.haha.gcmp.service.impl;

import com.haha.gcmp.config.propertites.FtpPoolConfig;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.config.propertites.SftpPoolConfig;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.model.entity.TempFileInfo;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import com.haha.gcmp.repository.FileMapper;
import com.haha.gcmp.service.DataService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.service.base.AbstractServerService;
import com.haha.gcmp.service.support.fileclient.FileClient;
import com.haha.gcmp.service.support.fileclient.FtpFileClientImpl;
import com.haha.gcmp.service.support.fileclient.SftpFileClientImpl;
import com.haha.gcmp.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.haha.gcmp.config.GcmpConst.FTP_TYPE_SFTP;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
@Service
public class DataServiceImpl extends AbstractServerService<FileClient> implements DataService {
    private final UserService userService;
    private final FileMapper fileMapper;
    private final SftpPoolConfig sftpPoolConfig;
    private final SshPoolConfig sshPoolConfig;
    private final FtpPoolConfig ftpPoolConfig;
    private static final Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    protected DataServiceImpl(GcmpProperties gcmpProperties, UserService userService, FileMapper fileMapper, SftpPoolConfig sftpPoolConfig, SshPoolConfig sshPoolConfig, FtpPoolConfig ftpPoolConfig) {
        super(gcmpProperties);
        this.userService = userService;
        this.fileMapper = fileMapper;
        this.sftpPoolConfig = sftpPoolConfig;
        this.sshPoolConfig = sshPoolConfig;
        this.ftpPoolConfig = ftpPoolConfig;
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
    public List<DataFile> listDataDir(DataParam dataParam) {
        return doListDataDir(dataParam.getServerId(), gcmpProperties.getDataRoot(), dataParam.getRelativePath());
    }

    private List<DataFile> doListDataDir(int serverId, String rootPath, String relativePath) {
        String absolutePath = FileUtils.joinPaths(rootPath, relativePath);
        FileClient fileClient = getClient(serverId);
        List<DataFile> dataFiles = fileClient.listDir(absolutePath);
        dataFiles.forEach(dataFile -> dataFile.setPath(FileUtils.joinPaths(relativePath, dataFile.getName())));

        return dataFiles;
    }

    @Override
    public void unzip(DataParam dataParam) {
        FileClient fileClient = getClient(dataParam.getServerId());
        String absolutePath = getUserDataPath(dataParam.getRelativePath());
        fileClient.unzip(absolutePath);
    }

    @Override
    public void remove(DataParam dataParam) {
        FileClient fileClient = getClient(dataParam.getServerId());
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
    public CheckFileResult checkChunk(CheckFileQuery param) {
        String absolutePath = getUserDataPath(param.getRelativePath());
        FileClient client = getClient(param.getServerId());
        boolean existed = client.checkFileExists(absolutePath);
        if (existed) {
            DataFile fileInfo = client.getFileInfo(absolutePath);
            if (fileInfo.getSize() == param.getTotalSize()) {
                fileMapper.remove(new TempFileInfo(param.getMd5(), param.getServerId(), param.getRelativePath()));
                client.removeDirIfExists(getUserTempPath(param.getMd5()));
                return new CheckFileResult(null, CheckFileResult.EXISTED);
            }
        }
        TempFileInfo tempFileInfo = new TempFileInfo(param.getMd5(), param.getServerId(), param.getRelativePath());
        if (fileMapper.get(tempFileInfo) == null) {
            fileMapper.insert(tempFileInfo);
        }
        List<DataFile> tempFiles = listTempFiles(param.getServerId(), param.getMd5());
        List<String> existedChunk = validateTempFile(param.getServerId(), tempFiles, param.getTotalChunks(), param.getChunkSize(), param.getTotalSize());
        return new CheckFileResult(existedChunk, CheckFileResult.NON_EXISTED);

    }

    private List<String> validateTempFile(int serverId, List<DataFile> tempFiles, int totalChunks, long chunkSize, long totalSize) {
        List<String> rv = new ArrayList<>();
        long lastSize = totalSize - (long) (totalChunks - 1) * chunkSize;
        for (DataFile dataFile : tempFiles) {
            String absolutePath = FileUtils.joinPaths(gcmpProperties.getTempFileRoot(), dataFile.getPath());
            int fileName = Integer.parseInt(dataFile.getName());
            if (fileName < totalChunks) {
                if (dataFile.getSize() == chunkSize) {
                    rv.add(dataFile.getName());
                } else {
                    getClient(serverId).removeFile(absolutePath);
                }
            } else {
                if (dataFile.getSize() == lastSize) {
                    rv.add(dataFile.getName());
                } else {
                    getClient(serverId).removeFile(absolutePath);
                }
            }
        }
        return rv;
    }

    private List<DataFile> listTempFiles(int serverId, String md5) {
        String tempDir = FileUtils.joinPaths(userService.getCurrentUser().getUserName(), md5);
        return doListDataDir(serverId, gcmpProperties.getTempFileRoot(), tempDir);
    }

    private List<String> listTempFilePaths(int serverId, String md5) {
//        String tempDir = getUserTempPath(md5);
//        List<String> rv = new ArrayList<>();
//        for (int i = 1; i <= totalChunks; ++i) {
//            rv.add(FileUtils.joinPaths(tempDir, String.valueOf(i)));
//        }
        List<DataFile> dataFiles = listTempFiles(serverId, md5);
        dataFiles.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getName())));
        List<String> rv = new ArrayList<>();
        dataFiles.forEach(dataFile -> rv.add(getUserTempPath(FileUtils.joinPaths(md5, dataFile.getName()))));
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
            String filePath = getUserDataPath(param.getRelativePath());
            fileClient.put(data, filePath);
        } else {
            int chunkNumber = param.getChunkNumber();
            String userTempDir = getUserTempPath(param.getMd5());
            String chunkName = String.valueOf(chunkNumber);
            fileClient.put(data, FileUtils.joinPaths(userTempDir, chunkName));
        }
    }

    @Override
    public void mergeChunk(MergeChunkParam mergeChunkParam) {
        int serverId = mergeChunkParam.getServerId();
        String md5 = mergeChunkParam.getMd5();
        String relativePath = mergeChunkParam.getRelativePath();

        List<String> mergeFileList = listTempFilePaths(serverId, md5);
        FileClient fileClient = getClient(serverId);
        String filePath = getUserDataPath(relativePath);
        fileClient.mergeFiles(mergeFileList, filePath);
        TempFileInfo tempFileInfo = new TempFileInfo(md5, serverId, relativePath);
        fileMapper.remove(tempFileInfo);
        fileClient.removeDir(getUserTempPath(md5));
    }

    @Override
    public void cancelUpload(TempFileInfo tempFileInfo) {
        fileMapper.remove(tempFileInfo);
        FileClient fileClient = getClient(tempFileInfo.getServerId());
        fileClient.removeDir(getUserTempPath(tempFileInfo.getMd5()));
        fileClient.removeFile(getUserDataPath(tempFileInfo.getRelativePath()));
    }

    @Override
    public void move(DataMoveParam dataMoveParam) {
        String absoluteSrcPath = getUserDataPath(dataMoveParam.getSrcRelativePath());
        String absoluteTargetPath = getUserDataPath(dataMoveParam.getTargetRelativePath());
        FileClient fileClient = getClient(dataMoveParam.getServerId());
        fileClient.move(absoluteSrcPath, absoluteTargetPath);
    }
}

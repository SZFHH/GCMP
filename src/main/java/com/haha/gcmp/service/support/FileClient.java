package com.haha.gcmp.service.support;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.haha.gcmp.config.FTPClientConfigure;
import com.haha.gcmp.config.GcmpConst;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.haha.gcmp.config.GcmpConst.SSHPort;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public class FileClient {

    private final String hostName;
    private FTPClientPool ftpClientPool;
    private final Connection connection;
    private final Object lock;

    public FileClient(String hostName, String hostIp, String user, String password, FTPClientConfigure commonConfig) {
        this.hostName = hostName;
        FTPClientConfigure config = new FTPClientConfigure(hostIp, GcmpConst.FTPClientPort, user, password, commonConfig);
        FtpClientFactory ftpClientFactory = new FtpClientFactory(config);
        try {
            ftpClientPool = new FTPClientPool(ftpClientFactory);
        } catch (Exception e) {
            throw new ServiceException("FTPClientPool初始化异常。服务器：" + hostName, e);
        }
        boolean authenticated;
        connection = new Connection(hostIp, SSHPort);

        try {
            connection.connect();
            authenticated = connection.authenticateWithPassword(user, password);
        } catch (IOException e) {
            throw new ServiceException("SSH连接异常。服务器：" + hostName, e);
        }
        if (!authenticated) {
            throw new ServiceException("SSH连接验证异常。服务器：" + hostName);
        }
        lock = new Object();

    }

    private FTPClient getFtpClient() {
        FTPClient ftpClient;
        try {
            ftpClient = ftpClientPool.borrowObject();
        } catch (IOException | ServiceException e) {
            throw new ServiceException("从连接池获取ftpClient异常。服务器：" + hostName, e);
        }
        return ftpClient;
    }

    private void returnFtpClient(FTPClient ftpClient) {
        ftpClientPool.returnObject(ftpClient);
    }

    public FTPFile[] listDir(String dirPath) {
        FTPClient ftpClient = getFtpClient();
        FTPFile[] ftpFiles;
        try {
            ftpFiles = ftpClient.listFiles(dirPath);
        } catch (IOException e) {
            throw new ServiceException("获取文件列表异常。服务器：" + hostName + " 路径：" + dirPath, e);
        } finally {
            returnFtpClient(ftpClient);
        }

        return ftpFiles;

    }

    private void createParentDirIfNecessary(String remoteFilePath) {
        String dirPath = FileUtils.getDir(remoteFilePath);
        mkdirIfNotExist(dirPath);
    }

    public void put(InputStream data, String remoteFilePath) {
        createParentDirIfNecessary(remoteFilePath);
        FTPClient ftpClient = getFtpClient();
//        System.out.println("put  " + ftpClient);
        try {
            ftpClient.storeFile(remoteFilePath, data);
        } catch (IOException e) {
            throw new ServiceException("上传文件(" + remoteFilePath + ")至服务器(" + hostName + ")异常。", e);
        } finally {
            returnFtpClient(ftpClient);
        }

    }

    public void mergeFiles(List<String> fileList, String targetFilePath) {
        createParentDirIfNecessary(targetFilePath);
        String[] arr = new String[fileList.size() + 3];
        arr[0] = "cat";
        for (int i = 0; i < fileList.size(); ++i) {
            arr[i + 1] = fileList.get(i);
        }
        arr[fileList.size() + 1] = ">";
        arr[fileList.size() + 2] = targetFilePath;
        String cmd = StringUtils.join(arr, ' ');
        Session session = null;
        try {
            session = connection.openSession();
            session.execCommand(cmd);
            session.waitForCondition(ChannelCondition.EXIT_STATUS, Integer.MAX_VALUE);
        } catch (IOException e) {
            throw new ServiceException(String.format("合并文件异常。服务器:%s 文件: %s", hostName, targetFilePath), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void removeFile(String remotePath) {
        FTPClient ftpClient = getFtpClient();
        try {
            ftpClient.deleteFile(remotePath);
        } catch (IOException e) {
            throw new ServiceException(String.format("删除文件异常。服务器:%s 文件: %s", hostName, remotePath), e);
        } finally {
            returnFtpClient(ftpClient);
        }

    }


    public void removeDir(String remotePath) {
        String cmd = "rm -rf " + remotePath;
        Session session = null;
        try {
            session = connection.openSession();
            session.execCommand(cmd);

        } catch (IOException e) {
            throw new ServiceException(String.format("删除文件夹异常。服务器:%s 文件夹: %s", hostName, remotePath), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void unzip(String remoteFilePath) {
        String dirPath = FileUtils.getDir(remoteFilePath);
        String unzipCmd = "cd " + dirPath + " && ";
        if (remoteFilePath.endsWith(".tar")) {
            unzipCmd = "tar -xvf";
        } else if (remoteFilePath.endsWith(".gz")) {
            unzipCmd = "gzip -d";
        } else if (remoteFilePath.endsWith(".tar.gz") || remoteFilePath.endsWith(".tgz")) {
            unzipCmd = "tar -zxvf";
        } else if (remoteFilePath.endsWith(".zip")) {
            unzipCmd = "unzip";
        } else if (remoteFilePath.endsWith(".rar")) {
            throw new BadRequestException("不支持rar格式，请选择(zip,tar,gz,tar.gz,tgz)中的格式");
        }
        String cmd = unzipCmd + " " + remoteFilePath;
        Session session = null;
        try {
            session = connection.openSession();
            session.execCommand(cmd);
        } catch (IOException e) {
            throw new ServiceException(String.format("解压异常。服务器:%s 文件夹: %s", hostName, remoteFilePath), e);
        } finally {
            if (session != null) {
                session.close();
            }

        }

    }

    public void move(String srcPath, String targetPath) {
        createParentDirIfNecessary(targetPath);
        String cmd = "mv -f  " + srcPath + " " + targetPath;
        Session session = null;
        try {
            session = connection.openSession();
            session.execCommand(cmd);
        } catch (IOException e) {
            throw new ServiceException(String.format("移动文件异常。服务器:%s 源：%s目标: %s", hostName, srcPath, targetPath), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void mkdirIfNotExist(String dirPath) {
        if (!checkDirectoryExists(dirPath)) {
            mkdir(dirPath);
        }
    }

    private void mkdir(String dirPath) {
        String[] pathElements = dirPath.split("/");
        pathElements[0] = "/";
        FTPClient ftpClient = getFtpClient();
//        System.out.println("mkdir " + ftpClient);
        try {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);

                if (!existed) {
                    boolean result = ftpClient.makeDirectory(singleDir);
                    ftpClient.changeWorkingDirectory(singleDir);

                }
            }
        } catch (IOException e) {
            throw new ServiceException(String.format("创建文件夹异常。服务器:%s 文件夹：%s", hostName, dirPath), e);
        } finally {
            returnFtpClient(ftpClient);
        }
    }

    public boolean checkDirectoryExists(String dirPath) {
        FTPClient ftpClient = getFtpClient();
        boolean existed;
        try {
            existed = ftpClient.changeWorkingDirectory(dirPath);
        } catch (IOException e) {
            throw new ServiceException(String.format("检查路径异常。服务器:%s 文件夹：%s", hostName, dirPath), e);
        } finally {
            returnFtpClient(ftpClient);
        }
        return existed;
    }

    public boolean checkFileExists(String filePath) {
        InputStream inputStream;
        boolean existed;
        FTPClient ftpClient = getFtpClient();
        try {
            inputStream = ftpClient.retrieveFileStream(filePath);
            int replyCode = ftpClient.getReplyCode();
            existed = inputStream != null && replyCode != 550;
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new ServiceException(String.format("检查路径异常。服务器:%s 文件：%s", hostName, filePath), e);
        } finally {
            returnFtpClient(ftpClient);
        }
        return existed;
    }

    public FTPFile getFileInfo(String filePath) {
        FTPClient ftpClient = getFtpClient();
        String fileName = FileUtils.getFileName(filePath);
        FTPFile[] ftpFiles;
        try {
            ftpFiles = ftpClient.listFiles(FileUtils.getDir(filePath), file -> file.getName().equals(fileName));
        } catch (IOException e) {
            throw new ServiceException(String.format("获取文件属性异常。服务器:%s 文件：%s", hostName, filePath), e);
        }
        if (ftpFiles.length == 0) {
            return null;
        } else {
            return ftpFiles[0];
        }
    }

    public void removeDirIfExists(String dirPath) {
        if (checkDirectoryExists(dirPath)) {
            removeDir(dirPath);
        }
    }

}


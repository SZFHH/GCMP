package com.haha.gcmp.service.support.fileclient;

import ch.ethz.ssh2.Connection;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.service.support.fileclient.pool.SshClientFactory;
import com.haha.gcmp.service.support.fileclient.pool.SshClientPool;
import com.haha.gcmp.utils.FileUtils;
import com.haha.gcmp.utils.SshUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.io.IOException;
import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public abstract class AbstractFileClient<T> implements FileClient {
    private final SshClientPool sshClientPool;
    protected String hostName;
    private GenericObjectPool<T> ftpClientPool;


    protected AbstractFileClient(SshPoolConfig sshPoolConfig, ServerProperty serverProperty) {
        SshClientFactory sshClientFactory = new SshClientFactory(sshPoolConfig, serverProperty);
        sshClientPool = new SshClientPool(sshClientFactory);
        hostName = serverProperty.getHostName();
    }

    public void setFtpClientPool(GenericObjectPool<T> ftpClientPool) {
        this.ftpClientPool = ftpClientPool;
    }

    @Override
    public String execShellCmd(String cmd, String exceptionMsg) {
        Connection connection;
        try {
            connection = sshClientPool.borrowObject();
        } catch (Exception e) {
            throw new ServiceException("从ssh连接池获取连接异常", e);
        }
        try {
            return SshUtils.execCmd(connection, cmd);
        } catch (IOException e) {
            throw new ServiceException(exceptionMsg, e);
        } finally {
            sshClientPool.returnObject(connection);
        }
    }

    @Override
    public void removeDir(String remotePath) {
        String cmd = "rm -rf " + remotePath;
        String msg = String.format("删除文件夹异常。服务器:%s 文件夹: %s", hostName, remotePath);
        execShellCmd(cmd, msg);
    }

    @Override
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
        String msg = String.format("解压异常。服务器:%s 文件夹: %s", hostName, remoteFilePath);
        execShellCmd(cmd, msg);
    }

    @Override
    public void move(String srcPath, String targetPath) {
        createParentDirIfNecessary(targetPath, "777");
        String cmd = "mv -f  " + srcPath + " " + targetPath;
        String msg = String.format("移动文件异常。服务器:%s 源：%s目标: %s", hostName, srcPath, targetPath);
        execShellCmd(cmd, msg);
    }

    @Override
    public boolean checkDirectoryExists(String remotePath) {
        String cmd = "[ -d " + remotePath + " ] && echo y || echo n";
        String msg = String.format("检查路径异常。服务器:%s 文件夹：%s", hostName, remotePath);
        String rv = execShellCmd(cmd, msg);

        return "y".equals(rv.substring(0, 1));
    }

    @Override
    public boolean checkFileExists(String remotePath) {
        String cmd = "[ -f " + remotePath + " ] && echo y || echo n";
        String msg = String.format("检查路径异常。服务器:%s 文件：%s", hostName, remotePath);
        String rv = execShellCmd(cmd, msg);
        return "y".equals(rv.substring(0, 1));
    }

    @Override
    public void mergeFiles(List<String> fileList, String targetFilePath) {
        createParentDirIfNecessary(targetFilePath, "777");
        String[] arr = new String[fileList.size() + 3];
        arr[0] = "cat";
        for (int i = 0; i < fileList.size(); ++i) {
            arr[i + 1] = fileList.get(i);
        }
        arr[fileList.size() + 1] = ">";
        arr[fileList.size() + 2] = targetFilePath;
        String cmd = StringUtils.join(arr, ' ');
        String msg = String.format("合并文件异常。服务器:%s 文件: %s", hostName, targetFilePath);
        execShellCmd(cmd, msg);
    }

    @Override
    public void removeFile(String remotePath) {
//        System.out.println(remotePath);
        String cmd = "rm -f " + remotePath;
        String msg = String.format("删除文件异常。服务器:%s 文件: %s", hostName, remotePath);
        execShellCmd(cmd, msg);
    }

    @Override
    public void mkdirs(String dirPath, String permission) {
        String[] pathElements = dirPath.split("/");
        StringBuilder sb = new StringBuilder();
        String curPath;
        String msg = String.format("创建文件夹异常。服务器:%s 文件夹：%s", hostName, dirPath);
//        System.out.println("mkdir " + ftpClient);

        for (String singleDir : pathElements) {
            sb.append('/');
            sb.append(singleDir);
            curPath = sb.toString();
//            System.out.println(curPath);
            if (!checkDirectoryExists(curPath)) {
                execShellCmd("mkdir " + curPath, msg);
                chmod(permission, curPath);
            }
        }
    }

    @Override
    public void chmod(String permission, String remotePath) {
        String msg = String.format("更改权限异常。服务器:%s 文件(夹)：%s", hostName, remotePath);
        String cmd = "chmod " + permission + " " + remotePath;
        execShellCmd(cmd, msg);
    }

    @Override
    public void createParentDirIfNecessary(String remoteFilePath, String permission) {
        String dirPath = FileUtils.getDir(remoteFilePath);
        mkdirIfNotExist(dirPath, permission);
    }

    @Override
    public void mkdirIfNotExist(String dirPath, String permission) {
        if (!checkDirectoryExists(dirPath)) {
            mkdirs(dirPath, permission);
        }
    }

    @Override
    public void removeDirIfExists(String dirPath) {
        if (checkDirectoryExists(dirPath)) {
            removeDir(dirPath);
        }
    }

    @Override
    public DataFile getFileInfo(String filePath) {
        List<DataFile> dataFiles = listDir(FileUtils.getDir(filePath));
        for (DataFile dataFile : dataFiles) {
            if (dataFile.getName().equals(FileUtils.getFileName(filePath))) {
                return dataFile;
            }
        }

        throw new ServiceException("没找到对应的文件：" + filePath);
    }

    private T getFtpClient() {
        T sftpClient;
        try {
            sftpClient = ftpClientPool.borrowObject();
        } catch (Exception e) {
            throw new ServiceException("从连接池获取sftpClient异常。服务器：" + hostName, e);
        }
        return sftpClient;
    }

    private void returnFtpClient(T ftpClient) {
        ftpClientPool.returnObject(ftpClient);
    }

    protected abstract List<DataFile> doListDir(String dirPath, T ftpClient);

    @Override
    public List<DataFile> listDir(String dirPath) {
        mkdirIfNotExist(dirPath, "777");
        T ftpClient = getFtpClient();
        try {
            return doListDir(dirPath, ftpClient);
        } finally {
            returnFtpClient(ftpClient);
        }
    }

    @Override
    public void put(byte[] data, String remoteFilePath) {
        createParentDirIfNecessary(remoteFilePath, "777");
        T ftpClient = getFtpClient();
        try {
            doPut(data, remoteFilePath, ftpClient);
        } finally {
            returnFtpClient(ftpClient);
        }
    }

    protected abstract void doPut(byte[] data, String remoteFilePath, T ftpClient);
}

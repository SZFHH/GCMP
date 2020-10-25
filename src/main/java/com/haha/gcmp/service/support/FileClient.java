package com.haha.gcmp.service.support;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.DataFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.haha.gcmp.config.GcmpConst.SCPClientPort;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public class FileClient {
    private SFTPv3Client sftPv3Client;
    private SCPClient scpClient;
    private Connection connection;
    private String hostName;

    public FileClient(String hostName, String hostIp, String user, String password) {
        this.hostName = hostName;
        connection = new Connection(hostIp, SCPClientPort);
        boolean authenticated;
        try {
            authenticated = connection.authenticateWithPassword(user, password);
        } catch (IOException e) {
            throw new ServiceException("FileClient初始化IO异常.", e);
        }
        if (!authenticated) {
            throw new ServiceException("集群服务器：" + hostName + "密码错误。 " + user + " : " + password);
        }
        try {
            scpClient = connection.createSCPClient();
            sftPv3Client = new SFTPv3Client(connection);
        } catch (IOException e) {
            throw new ServiceException("FileClient初始化IO异常.", e);
        }
    }

    public List<DataFile> listDir(Path dirPath) {
        List<DataFile> dataFiles = new ArrayList<>();
        Vector<?> v;
        try {
            v = sftPv3Client.ls(dirPath.toString());

        } catch (IOException e) {
            throw new ServiceException("获取文件列表异常。服务器：" + hostName + " 路径：" + dirPath, e);
        }

        for (Object o : v) {
            SFTPv3DirectoryEntry s = (SFTPv3DirectoryEntry) o;
            dataFiles.add(new DataFile(s.attributes.isRegularFile(), s.filename, s.attributes.size));
        }
        return dataFiles;
    }
}

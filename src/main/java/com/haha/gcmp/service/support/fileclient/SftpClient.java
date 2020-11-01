package com.haha.gcmp.service.support.fileclient;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import com.haha.gcmp.utils.FileUtils;
import com.haha.gcmp.utils.SshUtils;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class SftpClient {
    private final SFTPv3Client sftpClient;
    private final SCPClient scpClient;
    private final Connection connection;

    public SftpClient(Connection connection) throws IOException {
        this.sftpClient = new SFTPv3Client(connection);
        this.scpClient = connection.createSCPClient();
        this.connection = connection;
    }

    public void destroy() {
        sftpClient.close();
        connection.close();
    }

    public boolean validate() {
        return SshUtils.validate(connection);
    }

    public List<?> ls(String dirPath) throws IOException {
        List<?> rv;
        Vector<?> sftpFiles = sftpClient.ls(dirPath);
        rv = sftpFiles.subList(2, sftpFiles.size());
        return rv;
    }

    public void put(byte[] data, String remoteFilePath) throws IOException {
        scpClient.put(data, FileUtils.getFileName(remoteFilePath), FileUtils.getDir(remoteFilePath), "0777");
    }
}

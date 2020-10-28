package com.haha.gcmp.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author SZFHH
 * @date 2020/10/27
 */
@Configuration
public class FTPClientConfigure {
    private String host;
    private int port;
    private String username;
    private String password;
    private String passiveMode;
    private String encoding;
    private int clientTimeout;
    private int threadNum;
    private int transferFileType;
    private boolean renameUploaded;
    private int retryTimes;

    public FTPClientConfigure() {

    }

    public FTPClientConfigure(String host, int port, String username, String password, FTPClientConfigure commonConfig) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.encoding = commonConfig.encoding;
        this.clientTimeout = commonConfig.clientTimeout;
        this.threadNum = commonConfig.threadNum;
        this.transferFileType = commonConfig.transferFileType;
        this.renameUploaded = commonConfig.renameUploaded;
        this.retryTimes = commonConfig.retryTimes;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(String passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getTransferFileType() {
        return transferFileType;
    }

    public void setTransferFileType(int transferFileType) {
        this.transferFileType = transferFileType;
    }

    public boolean isRenameUploaded() {
        return renameUploaded;
    }

    public void setRenameUploaded(boolean renameUploaded) {
        this.renameUploaded = renameUploaded;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Override
    public String toString() {
        return "FTPClientConfig [host=" + host + "\n port=" + port + "\n username=" + username + "\n password=" + password + "\n passiveMode=" + passiveMode
            + "\n encoding=" + encoding + "\n clientTimeout=" + clientTimeout + "\n threadNum=" + threadNum + "\n transferFileType="
            + transferFileType + "\n renameUploaded=" + renameUploaded + "\n retryTimes=" + retryTimes + "]";
    }

}

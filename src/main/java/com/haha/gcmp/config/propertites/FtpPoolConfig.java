package com.haha.gcmp.config.propertites;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ftp配置参数对象   继承自GenericObjectPoolConfig
 *
 * @author jelly
 */
@ConfigurationProperties("ftp-pool")
public class FtpPoolConfig extends GenericObjectPoolConfig<FTPClient> {

    //ftp 连接超时时间 毫秒
    private int connectTimeOut = 5000;

    private String controlEncoding = "utf-8";

    private int bufferSize = 4096;

    //  传输数据格式   2表binary二进制数据
    private int fileType = 2;

    private int dataTimeout = 120000;

    private boolean useEpsvWithIpv4 = false;

    private boolean passiveMode = true;


    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public String getControlEncoding() {
        return controlEncoding;
    }

    public void setControlEncoding(String controlEncoding) {
        this.controlEncoding = controlEncoding;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public boolean isUseEpsvWithIpv4() {
        return useEpsvWithIpv4;
    }

    public void setUseEpsvWithIpv4(boolean useEpsvWithIpv4) {
        this.useEpsvWithIpv4 = useEpsvWithIpv4;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }


}

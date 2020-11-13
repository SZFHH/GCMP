package com.haha.gcmp.config.propertites;

import com.haha.gcmp.model.entity.ServerProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


/**
 * Gcmp configuration properties.
 *
 * @author SZFHH
 * @date 2020/10/18
 */
@ConfigurationProperties("gcmp")
public class GcmpProperties {

    private String k8sConfigFilePath;

    private String pypiSource;

    private String taskLogRoot;

    private int taskLogLimitBytes;

    private int taskClearPeriod;

    private List<ServerProperty> serverProperties;

    private String ftpType = "sftp";

    private String dockerFileRoot;

    private String dataRoot;

    private String tempFileRoot;

    private String dockerClientPort;

    private boolean productionEnv = true;

    private String adminName;

    private String cache = "memory";

    private String gcmpRoot;

    private String commonDataRoot;

    public String getK8sConfigFilePath() {
        return k8sConfigFilePath;
    }

    public void setK8sConfigFilePath(String k8sConfigFilePath) {
        this.k8sConfigFilePath = k8sConfigFilePath;
    }

    public String getPypiSource() {
        return pypiSource;
    }

    public void setPypiSource(String pypiSource) {
        this.pypiSource = pypiSource;
    }

    public String getTaskLogRoot() {
        return taskLogRoot;
    }

    public void setTaskLogRoot(String taskLogRoot) {
        this.taskLogRoot = taskLogRoot;
    }

    public int getTaskLogLimitBytes() {
        return taskLogLimitBytes;
    }

    public void setTaskLogLimitBytes(int taskLogLimitBytes) {
        this.taskLogLimitBytes = taskLogLimitBytes;
    }

    public int getTaskClearPeriod() {
        return taskClearPeriod;
    }

    public void setTaskClearPeriod(int taskClearPeriod) {
        this.taskClearPeriod = taskClearPeriod;
    }

    public List<ServerProperty> getServerProperties() {
        return serverProperties;
    }

    public void setServerProperties(List<ServerProperty> serverProperties) {
        this.serverProperties = serverProperties;
    }

    public String getFtpType() {
        return ftpType;
    }

    public void setFtpType(String ftpType) {
        this.ftpType = ftpType;
    }

    public String getDockerFileRoot() {
        return dockerFileRoot;
    }

    public void setDockerFileRoot(String dockerFileRoot) {
        this.dockerFileRoot = dockerFileRoot;
    }

    public String getDataRoot() {
        return dataRoot;
    }

    public void setDataRoot(String dataRoot) {
        this.dataRoot = dataRoot;
    }

    public String getTempFileRoot() {
        return tempFileRoot;
    }

    public void setTempFileRoot(String tempFileRoot) {
        this.tempFileRoot = tempFileRoot;
    }

    public String getDockerClientPort() {
        return dockerClientPort;
    }

    public void setDockerClientPort(String dockerClientPort) {
        this.dockerClientPort = dockerClientPort;
    }

    public boolean isProductionEnv() {
        return productionEnv;
    }

    public void setProductionEnv(boolean productionEnv) {
        this.productionEnv = productionEnv;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getGcmpRoot() {
        return gcmpRoot;
    }

    public void setGcmpRoot(String gcmpRoot) {
        this.gcmpRoot = gcmpRoot;
    }

    public String getCommonDataRoot() {
        return commonDataRoot;
    }

    public void setCommonDataRoot(String commonDataRoot) {
        this.commonDataRoot = commonDataRoot;
    }
}

package com.haha.gcmp.config;

import com.haha.gcmp.model.enums.Mode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;

import static com.haha.gcmp.config.GcmpConst.*;
import static com.haha.gcmp.utils.GcmpUtils.ensureSuffix;


/**
 * Halo configuration properties.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-15
 */

@ConfigurationProperties("gcmp")
public class GcmpProperties {
    private String dockerFileRoot;
    private String dataRoot;

    public String getDataRoot() {
        return dataRoot;
    }

    public void setDataRoot(String dataRoot) {
        this.dataRoot = dataRoot;
    }

    /**
     * Doc api disabled. (Default is true)
     */
    private String dockerClientPort;
    private boolean docDisabled = true;

    public String getDockerClientPort() {
        return dockerClientPort;
    }

    public void setDockerClientPort(String dockerClientPort) {
        this.dockerClientPort = dockerClientPort;
    }

    private Map<String, String> hostIps;
    private Map<String, String> hostUsers;

    public Map<String, String> getHostUsers() {
        return hostUsers;
    }

    public void setHostUsers(Map<String, String> hostUsers) {
        this.hostUsers = hostUsers;
    }

    public Map<String, String> getHostPasswords() {
        return hostPasswords;
    }

    public void setHostPasswords(Map<String, String> hostPasswords) {
        this.hostPasswords = hostPasswords;
    }

    private Map<String, String> hostPasswords;

    public Map<String, String> getHostIps() {
        return hostIps;
    }

    public void setHostIps(Map<String, String> hostIps) {
        this.hostIps = hostIps;
    }

    /**
     * Production env. (Default is true)
     */
    private boolean productionEnv = true;

    public boolean isProductionEnv() {
        return productionEnv;
    }

    /**
     * Authentication enabled
     */
    private boolean authEnabled = true;
    private String adminName;

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * Halo startup mode.
     */
    private Mode mode = Mode.PRODUCTION;

    /**
     * Admin path.
     */
    private String adminPath = "admin";

    /**
     * Work directory.
     */
    private String workDir = ensureSuffix(USER_HOME, FILE_SEPARATOR) + ".halo" + FILE_SEPARATOR;

    /**
     * Halo backup directory.(Not recommended to modify this config);
     */
    private String backupDir = ensureSuffix(TEMP_DIR, FILE_SEPARATOR) + "halo-backup" + FILE_SEPARATOR;

    /**
     * Halo data export directory.
     */
    private String dataExportDir = ensureSuffix(TEMP_DIR, FILE_SEPARATOR) + "halo-data-export" + FILE_SEPARATOR;

    /**
     * Upload prefix.
     */
    private String uploadUrlPrefix = "upload";

    /**
     * Download Timeout.
     */
    private Duration downloadTimeout = Duration.ofSeconds(30);

    /**
     * cache store impl
     * memory
     * level
     */
    private String cache = "memory";

    public Mode getMode() {
        return mode;
    }

    private ArrayList<String> cacheRedisNodes = new ArrayList<>();

    private String cacheRedisPassword = "";


    public String getCache() {
        return cache;
    }

    public String getDockerFileRoot() {
        return dockerFileRoot;
    }

    public void setDockerFileRoot(String dockerFileRoot) {
        this.dockerFileRoot = dockerFileRoot;
    }
}

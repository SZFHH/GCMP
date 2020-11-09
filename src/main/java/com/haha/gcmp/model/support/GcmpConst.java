package com.haha.gcmp.model.support;

import org.springframework.http.HttpHeaders;

/**
 * GCMP const
 *
 * @author SZFHH
 * @date 2020/10/18
 */
public class GcmpConst {
    public final static String LOCAL_CHARSET = "UTF-8";

    public final static String SERVER_CHARSET = "ISO-8859-1";

    public final static String FTP_TYPE_FTP = "ftp";

    public final static String FTP_TYPE_SFTP = "sftp";

    public final static String REMEMBER_ME_COOKIE_NAME = "GcmpRememberMe";

    public final static int REMEMBER_ME_COOKIE_TIME = 10 * 365 * 24 * 60 * 60;

    public final static int DEFAULT_DOCKER_QUOTA = 2;

    public final static String POD_STATUS_SUCCEED = "Succeed";

    public final static String POD_STATUS_RUNNING = "Running";

    public final static String POD_STATUS_PENDING = "Pending";

    public final static String POD_STATUS_FAILED = "Failed";

    public final static String POD_STATUS_DELETED = "Deleted";

    public final static String POS_STATUS_UNKNOWN = "Unknown";

    public final static String PROTOCOL_HTTPS = "https://";

    public final static String PROTOCOL_HTTP = "http://";

    public final static String PROTOCOL_TCP = "tcp://";

    /**
     * Admin token header name.
     */
    public final static String TOKEN_HEADER_NAME = "Gcmp-" + HttpHeaders.AUTHORIZATION;
    /**
     * Admin token param name.
     */
    public final static String TOKEN_QUERY_NAME = "gcmp_token";

}

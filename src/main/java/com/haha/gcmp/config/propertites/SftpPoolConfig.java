package com.haha.gcmp.config.propertites;

import com.haha.gcmp.service.support.fileclient.SftpClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
@ConfigurationProperties("sftp-pool")
public class SftpPoolConfig extends GenericObjectPoolConfig<SftpClient> {


}

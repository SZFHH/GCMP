package com.haha.gcmp.config.propertites;

import com.haha.gcmp.client.fileclient.SftpClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SFTP pool config
 *
 * @author SZFHH
 * @date 2020/10/31
 */
@ConfigurationProperties("sftp-pool")
public class SftpPoolConfig extends GenericObjectPoolConfig<SftpClient> {

}

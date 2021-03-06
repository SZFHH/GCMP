package com.haha.gcmp.config.propertites;

import ch.ethz.ssh2.Connection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * File SSH pool config
 *
 * @author SZFHH
 * @date 2020/10/31
 */
@ConfigurationProperties("file-ssh-pool")
public class FileSshPoolConfig extends GenericObjectPoolConfig<Connection> {

}

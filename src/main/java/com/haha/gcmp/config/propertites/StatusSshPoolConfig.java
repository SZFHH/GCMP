package com.haha.gcmp.config.propertites;

import ch.ethz.ssh2.Connection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Status SSH pool config
 *
 * @author SZFHH
 * @date 2020/11/12
 */
@ConfigurationProperties("status-ssh-pool")
public class StatusSshPoolConfig extends GenericObjectPoolConfig<Connection> {
}

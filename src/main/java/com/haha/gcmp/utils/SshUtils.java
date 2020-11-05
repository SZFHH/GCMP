package com.haha.gcmp.utils;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class SshUtils {
    /**
     * 验证连接是否存活
     *
     * @param connection ssh连接
     * @return true if connection is alive，false otherwise
     */
    public static boolean validate(Connection connection) {
        Session session = null;
        boolean alive = false;
        try {
            session = connection.openSession();
            alive = true;
        } catch (IOException ignored) {

        } finally {
            if (session != null) {
                session.close();
            }
        }
        return alive;
    }

    /**
     * 执行shell 命令
     *
     * @param connection ssh连接
     * @param cmd        命令
     * @return 执行结果输出
     * @throws IOException
     */
    public static String execCmd(Connection connection, String cmd) throws IOException {
        Session session = null;
        String rv;
        try {
            session = connection.openSession();
            session.execCommand(cmd);
            session.waitForCondition(ChannelCondition.EXIT_STATUS, Integer.MAX_VALUE);
            rv = IOUtils.toString(session.getStdout(), Charset.defaultCharset());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return rv;
    }

    /**
     * 处理命令：free-b 输出的原始内存信息
     *
     * @param rawInfo 内存原始信息
     * @return long[2](内存总空间 ， 可用内存空间)
     */
    private static long[] processMemoryInfo(String rawInfo) {
        String[] lines = rawInfo.split("\\n");
        long[] rv = new long[2];
        for (String line : lines) {
            if (line.startsWith("Mem")) {
                String[] items = Arrays.stream(line.split("\\s")).filter(s -> !"".equals(s)).toArray(String[]::new);
                rv[0] = Long.parseLong(items[1]);
                rv[1] = Long.parseLong(items[6]);
                break;
            }
        }
        return rv;
    }

    /**
     * 获取内存信息
     *
     * @param connection ssh连接
     * @return long[2](内存总空间 ， 可用内存空间)
     * @throws IOException
     */
    public static long[] getMemoryInfo(Connection connection) throws IOException {
        String cmd = "free -b";
        String rawInfo = execCmd(connection, cmd);
        return processMemoryInfo(rawInfo);
    }

    /**
     * 获取磁盘信息
     *
     * @param connection ssh连接
     * @return long[2](磁盘总空间 ， 可用磁盘空间)
     * @throws IOException
     */
    public static long[] getDiskInfo(Connection connection) throws IOException {
        String cmd = "df";
        String rawInfo = execCmd(connection, cmd);
        return processDiskInfo(rawInfo);
    }

    /**
     * 处理命令：df 输出的原始磁盘信息
     *
     * @param rawInfo 磁盘原始信息
     * @return long[2](磁盘总空间 ， 可用磁盘空间)
     */
    private static long[] processDiskInfo(String rawInfo) {
        String[] lines = rawInfo.split("\\n");
        long[] rv = new long[2];
        for (String line : lines) {
            if (line.startsWith("Filesystem")) {
                continue;
            }
            String[] items = Arrays.stream(line.split("\\s")).filter(s -> !s.equals("")).toArray(String[]::new);
            if ("/".equals(items[5])) {
                rv[0] = Long.parseLong(items[1]) * 1024;
                rv[1] = Long.parseLong(items[3]) * 1024;
                break;
            }
        }
        return rv;
    }
}

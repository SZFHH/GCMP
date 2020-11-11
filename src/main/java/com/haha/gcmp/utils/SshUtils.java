package com.haha.gcmp.utils;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.haha.gcmp.model.support.GcmpConst.DISK_MOUNT_PATH;

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
        StringBuilder rv = new StringBuilder();
        try {
            session = connection.openSession();
            session.execCommand(cmd);
            InputStream stdout = session.getStdout();
            InputStream stderr = session.getStderr();

            byte[] buffer = new byte[100];

            while (true) {
                if ((stdout.available() == 0)) {
                    int conditions = session.waitForCondition(ChannelCondition.STDOUT_DATA |
                        ChannelCondition.STDERR_DATA | ChannelCondition.EOF, 1000 * 5);
                    if ((conditions & ChannelCondition.TIMEOUT) != 0) {
                        break;
                    }
                    if ((conditions & ChannelCondition.EOF) != 0) {
                        if ((conditions & (ChannelCondition.STDOUT_DATA |
                            ChannelCondition.STDERR_DATA)) == 0) {
                            break;
                        }
                    }
                }
                while (stdout.available() > 0) {
                    int len = stdout.read(buffer);
                    if (len > 0) {
                        rv.append(new String(buffer, 0, len));
                    }
                }
                while (stderr.available() > 0) {
                    int len = stderr.read(buffer);
                    if (len > 0) {
                        rv.append(new String(buffer, 0, len));
                    }
                }
            }

        } finally {
            if (session != null) {
                session.close();
            }
        }
        return rv.toString();
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
            if (DISK_MOUNT_PATH.equals(items[5])) {
                rv[0] = Long.parseLong(items[1]) * 1024;
                rv[1] = Long.parseLong(items[3]) * 1024;
                break;
            }
        }
        return rv;
    }
}

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

    public static long[] processMemoryInfo(String rawInfo) {
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

    public static long[] getMemoryInfo(Connection connection) throws IOException {
        String cmd = "free -b";
        String rawInfo = execCmd(connection, cmd);
        return processMemoryInfo(rawInfo);
    }

    public static long[] getDiskInfo(Connection connection) throws IOException {
        String cmd = "df";
        String rawInfo = execCmd(connection, cmd);
        return processDiskInfo(rawInfo);
    }

    public static long[] processDiskInfo(String rawInfo) {
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

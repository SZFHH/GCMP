package com.haha.gcmp.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import java.io.IOException;

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
}

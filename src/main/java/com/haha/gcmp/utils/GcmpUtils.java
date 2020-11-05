package com.haha.gcmp.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.UUID;


/**
 * @author SZFHH
 * @date 2020/10/18
 */

public class GcmpUtils {

    /**
     * Gets random uuid without dash.
     *
     * @return random uuid without dash
     */
    @NonNull
    public static String randomUUIDWithoutDash() {
        return StringUtils.remove(UUID.randomUUID().toString(), '-');
    }

}

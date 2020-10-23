package com.haha.gcmp.utils;

import javax.servlet.http.Cookie;

/**
 * @author SZFHH
 * @date 2020/10/23
 */

public class CookieUtil {
    
    public static Cookie findCookie(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}

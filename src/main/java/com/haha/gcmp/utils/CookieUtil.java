package com.haha.gcmp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.security.support.AuthenticationToken;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.haha.gcmp.model.support.GcmpConst.REMEMBER_ME_COOKIE_NAME;
import static com.haha.gcmp.model.support.GcmpConst.REMEMBER_ME_COOKIE_TIME;

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

    public static void setRememberMe(HttpServletResponse response, LoginParam loginParam) {
        AuthenticationToken authenticationToken = new AuthenticationToken(loginParam.getUserName(), loginParam.getPassword());
        String json;
        try {
            json = JsonUtils.objectToJson(authenticationToken);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to convert " + authenticationToken + " to json", e);
        }
        Cookie cookie = new Cookie(REMEMBER_ME_COOKIE_NAME, json);
        cookie.setMaxAge(REMEMBER_ME_COOKIE_TIME);
        response.addCookie(cookie);
    }

    public static void clearRememberMe(HttpServletResponse response) {
        Cookie cookie = new Cookie(REMEMBER_ME_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

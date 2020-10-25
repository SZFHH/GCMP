package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.security.support.AuthenticationToken;
import com.haha.gcmp.security.token.AuthToken;

import javax.servlet.http.HttpServletResponse;

/**
 * @author SZFHH
 * @date 2020/10/23
 */

public interface AuthService {
    int ACCESS_TOKEN_EXPIRED_SECONDS = 24 * 3600;

    AuthToken authenticate(LoginParam loginParam);

    User authCheck(AuthenticationToken authenticationToken);

    void setRememberMe(HttpServletResponse response, LoginParam loginParam);

    void logout(HttpServletResponse response);
}


package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.security.support.AuthenticationToken;
import com.haha.gcmp.security.token.AuthToken;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletResponse;

/**
 * @author SZFHH
 * @date 2020/10/23
 */

public interface AuthService {
    int ACCESS_TOKEN_EXPIRED_SECONDS = 48 * 3600;

    /**
     * 验证用户的登录信息
     *
     * @param loginParam must not be null.
     * @return a token
     */
    AuthToken authenticate(LoginParam loginParam);

    /**
     * 检查登录凭证
     *
     * @param authenticationToken must not be null.
     * @return user
     */
    User authCheck(AuthenticationToken authenticationToken);

    /**
     * 设置remember me
     *
     * @param response   must not be null.
     * @param loginParam must not be null.
     */
    void setRememberMe(HttpServletResponse response, LoginParam loginParam);

    /**
     * 退出登录
     *
     * @param response must not be null.
     */
    void logout(HttpServletResponse response);

    /**
     * 获取用户的token
     *
     * @param user must not be null
     * @return token
     */
    String getToken(User user);

    /**
     * 创建用户的token
     *
     * @param user must not be null
     * @return AuthToken
     */
    AuthToken buildAuthToken(@NonNull User user);
}


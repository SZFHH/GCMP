package com.haha.gcmp.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.haha.gcmp.cache.AbstractStringCacheStore;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.security.support.AuthenticationToken;
import com.haha.gcmp.security.token.AuthToken;
import com.haha.gcmp.security.util.SecurityUtils;
import com.haha.gcmp.service.AuthService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.utils.CookieUtil;
import com.haha.gcmp.utils.GcmpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * Authentication service implementation.
 *
 * @author SZFHH
 * @date 2020/10/23
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final AbstractStringCacheStore cacheStore;
    private final UserService userService;

    public AuthServiceImpl(AbstractStringCacheStore cacheStore, UserService userService) {
        this.cacheStore = cacheStore;
        this.userService = userService;
    }

    @Override
    public AuthToken authenticate(LoginParam loginParam) {
        String userName = loginParam.getUsername();
        String password = loginParam.getPassword();
        AuthenticationToken authenticationToken = new AuthenticationToken(userName, password);
        User user = authCheck(authenticationToken);
        return buildAuthToken(user);
    }

    @Override
    public User authCheck(AuthenticationToken authenticationToken) {
        String mismatchTip = "用户名或者密码不正确";
        String userName = authenticationToken.getUserName();
        String password = authenticationToken.getPassWord();
        User user = userService.getByNameOfNonNull(userName);
        if (!passwordMatch(user, password)) {
            throw new BadRequestException(mismatchTip);
        }
        return user;

    }

    @Override
    public void setRememberMe(HttpServletResponse response, LoginParam loginParam) {
        CookieUtil.setRememberMe(response, loginParam);
    }

    @Override
    public void logout(HttpServletResponse response) {
        CookieUtil.clearRememberMe(response);
        User user = userService.getCurrentUser();
        cacheStore.get(SecurityUtils.buildAccessTokenKey(user)).ifPresent(
            token -> cacheStore.delete(SecurityUtils.buildTokenAccessKey(token)));
        cacheStore.delete(SecurityUtils.buildAccessTokenKey(user));

    }

    private AuthToken buildAuthToken(@NonNull User user) {
        Assert.notNull(user, "User must not be null");

        // Generate new token
        AuthToken token = new AuthToken();

        token.setAccessToken(GcmpUtils.randomUUIDWithoutDash());
        token.setExpiredIn(ACCESS_TOKEN_EXPIRED_SECONDS);

        cacheStore.put(SecurityUtils.buildAccessTokenKey(user), token.getAccessToken(), ACCESS_TOKEN_EXPIRED_SECONDS, TimeUnit.SECONDS);
        // Cache those tokens with user id
        cacheStore.put(SecurityUtils.buildTokenAccessKey(token.getAccessToken()), user.getId(), ACCESS_TOKEN_EXPIRED_SECONDS, TimeUnit.SECONDS);

        return token;
    }

    private boolean passwordMatch(User user, String plainPassword) {
        Assert.notNull(user, "User must not be null");

        return !StringUtils.isBlank(plainPassword) && BCrypt.checkpw(plainPassword, user.getPassWord());
    }


}

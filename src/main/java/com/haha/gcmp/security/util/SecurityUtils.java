package com.haha.gcmp.security.util;

import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.security.context.SecurityContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;


/**
 * @author SZFHH
 * @date 2020/10/18
 */
public class SecurityUtils {

    private final static String TOKEN_ACCESS_CACHE_PREFIX = "gcmp.access.token.";

    private final static String ACCESS_TOKEN_CACHE_PREFIX = "gcmp.access_token.";


    private SecurityUtils() {
    }

    @NonNull
    public static String buildAccessTokenKey(@NonNull User user) {
        Assert.notNull(user, "User must not be null");

        return ACCESS_TOKEN_CACHE_PREFIX + user.getId();
    }


    @NonNull
    public static String buildTokenAccessKey(@NonNull String accessToken) {
        Assert.hasText(accessToken, "Access token must not be blank");

        return TOKEN_ACCESS_CACHE_PREFIX + accessToken;
    }


    public static User getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getDetail().getUser();
    }

}

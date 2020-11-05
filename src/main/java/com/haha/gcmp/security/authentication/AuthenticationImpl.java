package com.haha.gcmp.security.authentication;

import com.haha.gcmp.security.support.UserDetail;

/**
 * @author SZFHH
 * @date 2020/10/18
 */
public class AuthenticationImpl implements Authentication {

    private final UserDetail userDetail;

    public AuthenticationImpl(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    @Override
    public UserDetail getDetail() {
        return userDetail;
    }
}

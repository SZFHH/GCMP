package com.haha.gcmp.security.context;


import com.haha.gcmp.security.authentication.Authentication;

/**
 * @author SZFHH
 * @date 2020/10/18
 */
public class SecurityContextImpl implements SecurityContext {

    private Authentication authentication;

    public SecurityContextImpl(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

}

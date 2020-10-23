package com.haha.gcmp.security.context;


import com.haha.gcmp.security.authentication.Authentication;

/**
 * Security context implementation.
 *
 * @author johnniang
 */

public class SecurityContextImpl implements SecurityContext {

    private Authentication authentication;

    public SecurityContextImpl(Object o) {
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

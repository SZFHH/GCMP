package com.haha.gcmp.security.context;

import com.haha.gcmp.security.authentication.Authentication;
import org.springframework.lang.Nullable;


/**
 * @author SZFHH
 * @date 2020/10/18
 */
public interface SecurityContext {

    /**
     * Gets the currently authenticated principal.
     *
     * @return the Authentication or null if authentication information is unavailable
     */
    @Nullable
    Authentication getAuthentication();

    /**
     * Changes the currently authenticated principal, or removes the authentication information.
     *
     * @param authentication the new authentication or null if no further authentication should not be stored
     */
    void setAuthentication(@Nullable Authentication authentication);
}

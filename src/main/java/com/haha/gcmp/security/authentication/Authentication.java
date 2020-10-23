package com.haha.gcmp.security.authentication;

import com.haha.gcmp.security.support.UserDetail;
import org.springframework.lang.NonNull;


/**
 * Authentication.
 *
 * @author johnniang
 */
public interface Authentication {

    /**
     * Get user detail.
     *
     * @return user detail
     */
    @NonNull
    UserDetail getDetail();
}

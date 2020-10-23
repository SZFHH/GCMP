package com.haha.gcmp.security.support;

import com.haha.gcmp.model.entity.User;
import org.springframework.lang.NonNull;


/**
 * User detail.
 *
 * @author johnniang
 */

public class UserDetail {

    private User user;

    public UserDetail(User user) {
        this.user = user;
    }

    /**
     * Gets user info.
     *
     * @return user info
     */
    @NonNull
    public User getUser() {
        return user;
    }

    /**
     * Sets user info.
     *
     * @param user user info
     */
    public void setUser(User user) {
        this.user = user;
    }
}

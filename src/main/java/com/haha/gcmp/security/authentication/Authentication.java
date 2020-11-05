package com.haha.gcmp.security.authentication;

import com.haha.gcmp.security.support.UserDetail;
import org.springframework.lang.NonNull;


/**
 * @author SZFHH
 * @date 2020/10/18
 */
public interface Authentication {

    /**
     * 获取 user detail.
     *
     * @return user detail
     */
    @NonNull
    UserDetail getDetail();
}

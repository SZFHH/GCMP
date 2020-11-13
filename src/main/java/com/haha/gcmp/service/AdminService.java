package com.haha.gcmp.service;

import com.haha.gcmp.model.params.UserParam;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public interface AdminService {
    /**
     * 管理员密码是否进行过初始化
     *
     * @return true if admin password has been initialized.
     */
    boolean isInitialized();

    /**
     * 初始化管理员密码
     *
     * @param registerParam register param
     */
    void initialize(UserParam registerParam);

    /**
     * 获取管理员 id
     *
     * @return admin id
     */
    int getAdminId();
}

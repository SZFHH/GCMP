package com.haha.gcmp.service;

import com.haha.gcmp.model.params.RegisterParam;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public interface AdminService {

    boolean isInitialized();

    void initialize(RegisterParam registerParam);
}

package com.haha.gcmp.security.support;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public class AuthenticationToken {
    private String userName;
    private String passWord;

    public AuthenticationToken(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }
}

package com.haha.gcmp.security.support;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public class AuthenticationToken {
    private String username;
    private String password;

    public AuthenticationToken() {
    }

    public AuthenticationToken(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

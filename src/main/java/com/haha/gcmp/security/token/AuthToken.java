package com.haha.gcmp.security.token;

/**
 * @author SZFHH
 * @date 2020/10/18
 */
public class AuthToken {

    /**
     * Access token.
     */
    private String accessToken;

    /**
     * Expired in. (seconds)
     */
    private int expiredIn;


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiredIn() {
        return expiredIn;
    }

    public void setExpiredIn(int expiredIn) {
        this.expiredIn = expiredIn;
    }
}

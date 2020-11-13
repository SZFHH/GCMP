package com.haha.gcmp.model.dto;

/**
 * @author SZFHH
 * @date 2020/11/12
 */
public class UserDTO {
    String username;
    String accessToken;

    public UserDTO(String username, String accessToken) {
        this.username = username;
        this.accessToken = accessToken;
    }

    public UserDTO() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

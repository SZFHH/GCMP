package com.haha.gcmp.model.dto;

/**
 * @author SZFHH
 * @date 2020/11/12
 */
public class UserDTO {
    String username;

    public UserDTO() {
    }

    public UserDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

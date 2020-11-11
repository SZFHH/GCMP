package com.haha.gcmp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author SZFHH
 * @date 2020/11/6
 */
@Controller
public class PageController {
    @GetMapping(value = "/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/my_data")
    public String myData() {
        return "my-data";
    }

    @GetMapping(value = "/my_image")
    public String myImage() {
        return "my-image";
    }

    @GetMapping(value = "/my_task")
    public String myTask() {
        return "my-task";
    }

    @GetMapping(value = "/user_common_image")
    public String userCommonImage() {
        return "user-common-image";
    }

    @GetMapping(value = "/user_common_data")
    public String userCommonData() {
        return "user-common-data";
    }

    @GetMapping(value = "login")
    public String login() {
        return "login";
    }

}

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

    @GetMapping(value = "/mydata")
    public String myData() {
        return "my-data";
    }
}

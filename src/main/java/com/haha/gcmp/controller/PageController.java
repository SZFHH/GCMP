package com.haha.gcmp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author SZFHH
 * @date 2020/11/12
 */
@Controller
public class PageController {
    @RequestMapping("admin")
    public String admin() {
        return "admin";
    }
}

package com.sf.collecat.manager.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by 862911 on 2016/6/30.
 */
@Controller
@RequestMapping("/freeMarker")
public class BaseController {
    @RequestMapping("/hello")
    public String sayHello(ModelMap map) {
        System.out.println("say Hello ……");
        map.addAttribute("name", " World!");
        return "/hello.ftl";
    }
}

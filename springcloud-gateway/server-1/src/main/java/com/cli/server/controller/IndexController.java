package com.cli.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/server1")
public class IndexController {

    @GetMapping("/get")
    public String get() {
        return "我是服务1";
    }

    @GetMapping("/route/afterRoute")
    public String afterRoute() {
        return "This predicate matches requests that happen after the specified datetime. ";
    }

    @GetMapping("/cookie")
    public String cookie1() {
        return "我是tag1";
    }

}

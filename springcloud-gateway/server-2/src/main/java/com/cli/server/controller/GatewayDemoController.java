package com.cli.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc:
 * createBy: Ningjianjian
 */
@RestController
public class GatewayDemoController {

    @GetMapping("/cookie")
    public String cookie2() {
        return "我是tag2";
    }
}

package com.cli.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc:
 * createBy: Ningjianjian
 */
@RestController
public class GatewayDemoController {

    @Value("${server.port}")
    private int port;

    @GetMapping("/cookie")
    public String cookie1() {
        return "我是tag1, port = " + port;
    }

}

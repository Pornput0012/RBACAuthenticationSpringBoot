package com.pornput.rbactemplate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        log.debug("Ping endpoint called");
        return "pong";
    }
}

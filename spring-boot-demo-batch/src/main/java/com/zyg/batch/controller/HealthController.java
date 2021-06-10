package com.zyg.batch.controller;

import com.zyg.batch.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class HealthController {
    @Autowired
    private IUserService userService;

    @GetMapping("health")
    public String health(){
        return "目前总数为:" + userService.count();
    }

}

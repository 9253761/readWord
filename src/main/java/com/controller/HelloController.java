/*
 * Copyright (c) 2016-2018 fafa.com.cn. All Rights Reserved.
 */

package com.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 这是描述该项目是一个springboot项目
 *
 * @author Cll
 * @date 2018/11/11 13:52
 * @since 1.0.0
 */

@RestController
@RequestMapping("/HelloController")
public class HelloController {
    @RequestMapping("/Hello")
    public String Hello(){
        return "Hello cll";
    }
}

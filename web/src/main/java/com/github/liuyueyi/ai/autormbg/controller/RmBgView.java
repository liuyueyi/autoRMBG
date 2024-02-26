package com.github.liuyueyi.ai.autormbg.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author YiHui
 * @date 2024/2/22
 */
@Slf4j
@Controller
public class RmBgView {
    /**
     * 移除图片背景视图
     *
     * @return
     */
    @GetMapping(path = {"", "/", "/index"})
    public String index() {
        return "index";
    }

}

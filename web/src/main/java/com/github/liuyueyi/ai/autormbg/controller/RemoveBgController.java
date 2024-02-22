package com.github.liuyueyi.ai.autormbg.controller;

import com.github.liuyueyi.ai.autormbg.service.BgRemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YiHui
 * @date 2024/2/22
 */
@Controller
public class RemoveBgController {
    @Autowired
    private BgRemoveService bgRemoveService;

    /**
     * 移除图片背景视图
     *
     * @return
     */
    @GetMapping(path = {"", "/", "/index"})
    public String index() {
        return "index";
    }


    /**
     * 移除图片背景的功能接口实现
     *
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(path = "upload/rmbg")
    public Map<String, Object> rmBg(MultipartHttpServletRequest request) throws IOException {
        MultipartFile file = request.getFile("img");

        String rmBgImgUrl = bgRemoveService.removeBg(file);

        Map<String, Object> res = new HashMap<>();
        res.put("url", rmBgImgUrl);
        return res;
    }
}

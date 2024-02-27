package com.github.liuyueyi.ai.autormbg.controller;

import com.github.liuyueyi.ai.autormbg.model.ImgRemoveRes;
import com.github.liuyueyi.ai.autormbg.service.BgRemoveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

/**
 * webmvc 同步的方式
 *
 * @author YiHui
 * @date 2024/2/23
 */
@Slf4j
@RestController
public class RmBgSyncRest {
    @Autowired
    private BgRemoveService bgRemoveService;

    /**
     * 移除图片背景的功能接口实现
     *
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(path = "upload/rmbg")
    public ImgRemoveRes rmBg(MultipartHttpServletRequest request) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("抠图");
        MultipartFile file = request.getFile("img");

        String rmBgImgUrl = bgRemoveService.removeBg(file);
        stopWatch.stop();
        log.info("抠图耗时: {}ms", stopWatch.getTotalTimeMillis());
        return new ImgRemoveRes(rmBgImgUrl);
    }
}

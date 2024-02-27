package com.github.liuyueyi.ai.autormbg.controller;

import com.github.liuyueyi.ai.autormbg.model.ImgRemoveRes;
import com.github.liuyueyi.ai.autormbg.service.BgRemoveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * webclient 异步的方式
 *
 * @author YiHui
 * @date 2024/2/23
 */
@Slf4j
@RestController
public class RmBgAsyncRest {
    @Autowired
    private BgRemoveService bgRemoveService;

    @PostMapping(path = "upload/async/rmbg", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ImgRemoveRes> rmImgBg(@RequestPart("img") FilePart file) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("抠图");
        log.info("开始执行抠图!");
        Mono<ImgRemoveRes> res = bgRemoveService.removeBgAsync(file)
                .flatMap(s -> Mono.just(new ImgRemoveRes(s)))
                .onErrorResume(Mono::error);
        stopWatch.stop();
        log.info("执行完毕! 返回~, 耗时 = {}ms", stopWatch.getTotalTimeMillis());
        return res;
    }
}

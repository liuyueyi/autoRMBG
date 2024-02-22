package com.github.liuyueyi.ai.autormbg.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 图片相关配置属性
 *
 * @author YiHui
 * @date 2024/2/22
 */
@Data
@Component
@ConfigurationProperties(prefix = "image")
public class ImageProperties {

    /**
     * 存储在服务器上绝对路径
     */
    private String absTmpPath;

    /**
     * 对外访问的web相对路径
     */
    private String webImgPath;

    /**
     * 访问图片的host
     */
    private String cdnHost;

    /**
     * 处理后的图片后缀
     */
    private String processImgSuffix;

    public String buildImgUrl(String url) {
        if (!url.startsWith(cdnHost)) {
            return cdnHost + url;
        }
        return url;
    }
}

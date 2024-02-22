package com.github.liuyueyi.ai.autormbg.service;

import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.github.hui.quick.plugin.base.file.FileWriteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author YiHui
 * @date 2024/2/22
 */
@Slf4j
@Service
public class BgRemoveService {
    private final String DEFAULT_FILE_TYPE = "txt";
    private final Set<MediaType> STATIC_IMG_TYPE = new HashSet<>(Arrays.asList(MediaType.ImagePng, MediaType.ImageJpg, MediaType.ImageWebp, MediaType.ImageGif));

    private ImageProperties imageProperties;
    @Value("${ai.url:http://127.0.0.1:8000/rmbg}")
    private String aiUrl;

    private Random random;
    private RestTemplate restTemplate;

    public BgRemoveService(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
        this.random = new Random();
        this.restTemplate = new RestTemplate();
    }


    /**
     * 移除图片背景
     *
     * @param mFile
     * @return
     * @throws IOException
     */
    public String removeBg(MultipartFile mFile) throws IOException {
        FileWriteUtil.FileInfo file = saveToFile(mFile.getInputStream(), null);
        String path = file.getPath() + "/" + file.getFilename();

        String response = restTemplate.getForObject(aiUrl + "?name=" + path + "&type=" + file.getFileType() + "&outSuffix=" + imageProperties.getProcessImgSuffix(), String.class);
        String result = response.replaceAll("\"", "").replace(imageProperties.getAbsTmpPath(), imageProperties.getCdnHost());
        log.info("processImg: {} -> {}", file.getAbsFile(), result);
        return result;
    }

    /**
     * 图片上传
     *
     * @param input
     * @param fileType
     * @return
     */
    public String save(InputStream input, String fileType) {
        FileWriteUtil.FileInfo file = saveToFile(input, fileType);
        return imageProperties.buildImgUrl(imageProperties.getWebImgPath() + file.getFilename() + "." + file.getFileType());
    }

    /**
     * 保存图片到本地
     *
     * @param input
     * @param fileType
     * @return
     */
    public FileWriteUtil.FileInfo saveToFile(InputStream input, String fileType) {
        try {
            if (fileType == null) {
                // 根据魔数判断文件类型
                InputStream finalInput = input;
                byte[] bytes = StreamUtils.copyToByteArray(finalInput);
                input = new ByteArrayInputStream(bytes);
                fileType = getFileType((ByteArrayInputStream) input, fileType);
            }

            FileWriteUtil.FileInfo fileInfo = genTmpFileName(fileType);
            FileWriteUtil.FileInfo file = FileWriteUtil.saveFileByStream(input, fileInfo);
            return file;
        } catch (Exception e) {
            log.error("Parse img from httpRequest to BufferedImage error! e:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成临时文件
     *
     * @return
     */
    private FileWriteUtil.FileInfo genTmpFileName(String fileType) {
        FileWriteUtil.FileInfo file = new FileWriteUtil.FileInfo();
        file.setFileType(fileType);
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd/HHmmssSSS")) + "_" + random.nextInt(100);
        String path = imageProperties.getAbsTmpPath() + imageProperties.getWebImgPath() + fileName.substring(0, 8);
        file.setPath(path);
        file.setFilename(fileName.substring(9));
        return file;
    }


    /**
     * 获取文件类型
     *
     * @param input
     * @param fileType
     * @return
     */
    private String getFileType(ByteArrayInputStream input, String fileType) {
        if (StringUtils.isNotBlank(fileType)) {
            return fileType;
        }

        MediaType type = MediaType.typeOfMagicNum(FileReadUtil.getMagicNum(input));
        if (STATIC_IMG_TYPE.contains(type)) {
            return type.getExt();
        }
        return DEFAULT_FILE_TYPE;
    }


}

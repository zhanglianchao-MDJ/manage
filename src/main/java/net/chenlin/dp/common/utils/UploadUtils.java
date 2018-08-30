package net.chenlin.dp.common.utils;

import net.chenlin.dp.common.constant.SystemConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 * @author ZhouChenglin
 * @date 2018/1/29
 */
public class UploadUtils {

    private static Logger LOG = LoggerFactory.getLogger(UploadUtils.class);

    /** 上传文件处理(支持批量) */
    public static List<String> uploadFile(HttpServletRequest request) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        List<String> fileNames = new ArrayList<>();
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            File dirFile = new File(SystemConstant.UPLOAD_LOCATION_PATH);
            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext();) {
                String key = iterator.next();
                MultipartFile multipartFile = multiRequest.getFile(key);
                if (multipartFile != null) {
                    String name = multipartFile.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String postFix = name.substring(name.lastIndexOf(".")).toLowerCase();
                    String fileName = uuid + postFix;
                    String filePath = SystemConstant.UPLOAD_LOCATION_PATH + fileName;
                    File file = new File(filePath);
                    file.setWritable(true, false);
                    try {
                        multipartFile.transferTo(file);
                        fileNames.add(SystemConstant.UPLOAD_LOCATION_MAPPING.concat(fileName));
                    } catch (Exception e) {
                        LOG.error(name + "保存失败", e);
                    }
                }
            }
        }
        return fileNames;
    }

    /** 上传文件处理(支持批量) */
    public static List<String> uploadFile(HttpServletRequest request, String path) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        List<String> fileNames = new ArrayList<>();
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            File dirFile = new File(SystemConstant.UPLOAD_LOCATION_PATH.concat(path));
            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext();) {
                String key = iterator.next();
                MultipartFile multipartFile = multiRequest.getFile(key);
                if (multipartFile != null) {
                    String name = multipartFile.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String postFix = name.substring(name.lastIndexOf(".")).toLowerCase();
                    String fileName = uuid + postFix;
                    String filePath = SystemConstant.UPLOAD_LOCATION_PATH + path + fileName;
                    File file = new File(filePath);
                    file.setWritable(true, false);
                    try {
                        multipartFile.transferTo(file);
                        fileNames.add(SystemConstant.UPLOAD_LOCATION_MAPPING + path + fileName);
                    } catch (Exception e) {
                        LOG.error(name + "保存失败", e);
                    }
                }
            }
        }
        return fileNames;
    }

}

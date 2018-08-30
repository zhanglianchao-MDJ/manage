package net.chenlin.dp.common.support.config;

import net.chenlin.dp.common.constant.SystemConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;

/**
 * web配置
 * @author zcl<yczclcn@163.com>
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Value("${spring.http.multipart.location}")
    private String uploadDir;

    /**
     * 文件上传路径虚拟映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (StringUtils.isBlank(uploadDir)) {
            throw new RuntimeException("文件上传路径为空，请先在application.yml中配置{spring.http.multipart.location}路径！");
        }
        if (!uploadDir.endsWith("/")) {
            throw new RuntimeException("文件上传路径必须以 / 结束！");
        }
        File uploadDest = new File(uploadDir);
        if (!uploadDest.exists()) {
            throw new RuntimeException("配置的文件上传路径不存在，请配置已存在的路径！");
        }
        SystemConstant.UPLOAD_LOCATION_PATH = uploadDir;
        registry.addResourceHandler(SystemConstant.getResourceHandlerMapping())
                .addResourceLocations(SystemConstant.getResourceLocation());
    }

}

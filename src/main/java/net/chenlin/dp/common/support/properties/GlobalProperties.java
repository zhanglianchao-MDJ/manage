package net.chenlin.dp.common.support.properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统全局配置
 * @author zcl<yczclcn@163.com>
 */
@Component
@ConfigurationProperties(prefix = GlobalProperties.PREFIX)
public class GlobalProperties {

    static final String PREFIX = "global";

    /** 文件上传目录 **/
    private String uploadLocation;

    /** 文件上传目录访问路径 **/
    private String uploadMapping;

    /**
     * WebConfig注册上传路径
     * @return
     */
    public String getRegisterUploadLocation() {
        if (StringUtils.isEmpty(uploadLocation)) {
            return null;
        }
        return "file:".concat(uploadLocation);
    }

    /**
     * WebConfig注册访问路径
     * @return
     */
    public String getRegisterUploadMapping() {
        if (StringUtils.isEmpty(uploadMapping)) {
            return null;
        }
        return uploadMapping.concat("**");
    }

    public String getUploadLocation() {
        return uploadLocation;
    }

    public void setUploadLocation(String uploadLocation) {
        this.uploadLocation = uploadLocation;
    }

    public String getUploadMapping() {
        return uploadMapping;
    }

    public void setUploadMapping(String uploadMapping) {
        this.uploadMapping = uploadMapping;
    }
}

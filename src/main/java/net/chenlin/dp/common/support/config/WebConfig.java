package net.chenlin.dp.common.support.config;

import net.chenlin.dp.common.support.interceptor.RestApiInterceptor;
import net.chenlin.dp.common.support.properties.GlobalProperties;
import net.chenlin.dp.common.xss.XssFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import java.io.File;

/**
 * web配置
 * @author zcl<yczclcn@163.com>
 */
@DependsOn("springContextUtils")
@Configuration
public class WebConfig implements WebMvcConfigurer, ErrorPageRegistrar {

    @Autowired
    GlobalProperties globalProperties;

    /**
     * 文件上传路径虚拟映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (StringUtils.isBlank(globalProperties.getUploadLocation())) {
            throw new RuntimeException("文件上传路径为空，请先在application.yml中配置{global.upload-location}路径！");
        }
        if (!globalProperties.getUploadLocation().endsWith("/")) {
            throw new RuntimeException("文件上传路径必须以 / 结束！");
        }
        File uploadDest = new File(globalProperties.getUploadLocation());
        if (!uploadDest.exists()) {
            throw new RuntimeException("配置的文件上传路径不存在，请配置已存在的路径！");
        }
        registry.addResourceHandler(globalProperties.getRegisterUploadMapping())
                .addResourceLocations(globalProperties.getRegisterUploadLocation());
    }

    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册rest拦截器
        registry.addInterceptor(new RestApiInterceptor()).addPathPatterns("/rest/**");
    }

    /**
     * shiroFilter注册
     * @return
     */
    @Bean
    public FilterRegistrationBean shiroFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //该值缺省为false，表示生命周期由SpringApplicationContext管理，设置为true则表示由ServletContainer管理
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.setEnabled(true);
        registration.setOrder(Integer.MAX_VALUE - 1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * xssFilter注册
     * @return
     */
    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

    /**
     * 错误页面
     * @param registry
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage notFound = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
        ErrorPage sysError = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
        registry.addErrorPages(notFound, sysError);
    }
}

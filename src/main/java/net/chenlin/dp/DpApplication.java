package net.chenlin.dp;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * 应用启动器
 *
 * @author ZhouChenglin
 * @email yczclcn@163.com
 * @url www.chenlintech.com
 * @date 2017年9月3日 上午1:53:12
 */
@Configuration
@SpringBootApplication
public class DpApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DpApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

}

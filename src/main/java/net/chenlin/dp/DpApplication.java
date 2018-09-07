package net.chenlin.dp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动器
 * @author zcl<yczclcn@163.com>
 */
@SpringBootApplication
public class DpApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DpApplication.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DpApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
        LOGGER.info("The Dp application has been started successfully!");
    }

}

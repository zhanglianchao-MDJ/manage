package net.chenlin.dp.modules.test.quartz;

import org.springframework.stereotype.Component;

/**
 * 定时任务 实际动作 测试类
 */
@Component
public class TestQuartz {

    public void testQuartz(String id){
        System.out.println(System.currentTimeMillis()+"我是定时任务"+id);
    }
}

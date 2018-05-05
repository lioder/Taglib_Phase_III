package horizon.taglib.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * 多线程执行定时任务
 * 所有定时任务放在一个线程池中，启动定时任务时启用不同线程
 */

@Configuration
public class ScheduleConfigure implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar){
        //设置长度为8的定时任务线程池
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(8));
    }
}

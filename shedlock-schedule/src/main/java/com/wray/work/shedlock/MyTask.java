package com.wray.work.shedlock;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 任务
 *
 * @author wangfarui
 * @since 2024/1/9
 */
@Component
public class MyTask {

    @Scheduled(cron = "0/5 * * * * *")
    @SchedulerLock(name = "scheduledTaskName")
    public void scheduledTask() {
        // To assert that the lock is held (prevents misconfiguration errors)
        LockAssert.assertLocked();
        // do something
        System.out.println(LocalDateTime.now());
        // sleep 5s
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

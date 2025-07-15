package com.ibs.vi.scheduler;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Component
public class DynamicVIScheduler {

    private final TaskScheduler taskScheduler;
    private final VISchedulerTask viSchedulerTask;

    private ScheduledFuture<?> scheduledFuture;
    private long intervalInMinutes = 5; // default

    public DynamicVIScheduler(VISchedulerTask viSchedulerTask) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.initialize();
        this.taskScheduler = scheduler;
        this.viSchedulerTask = viSchedulerTask;
    }

    @PostConstruct
    public void init() {
        scheduleTask(intervalInMinutes);
    }

    public void scheduleTask(long intervalMinutes) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        this.intervalInMinutes = intervalMinutes;
        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                viSchedulerTask::run,
                Duration.ofMinutes(intervalMinutes)
        );
        System.out.println("Scheduled VI job every " + intervalMinutes + " minutes");
    }

    public long getCurrentInterval() {
        return this.intervalInMinutes;
    }
}


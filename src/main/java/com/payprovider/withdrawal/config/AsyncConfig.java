package com.payprovider.withdrawal.config;

import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for Async execution.
 * Params:
 * corePoolSize – the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
 * maxPoolSize – the maximum number of threads to allow in the pool
 * keepAliveTime – when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
 * queueCapacity - the capacity bound
 * <p>
 * Params can be changed by Spring using following keys:
 * corePoolSize – async.withdrawal.task.executor.poolSize.core
 * maxPoolSize – async.withdrawal.task.executor.poolSize
 * keepAliveTime – async.withdrawal.task.executor.keepAliveTime
 * queueCapacity - async.withdrawal.task.executor.queueCapacity
 */

@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String WITHDRAWAL_TASK_EXECUTOR = "withdrawalTaskExecutor";

    @Value("${async.withdrawal.task.executor.poolSize.core:10}")
    private int corePoolSize;

    @Value("${async.withdrawal.task.executor.poolSize.max:10}")
    private int maxPoolSize;

    @Value("${async.withdrawal.task.executor.keepAliveTime:60}")
    private int keepAliveTime;

    @Value("${async.withdrawal.task.executor.queueCapacity:500}")
    private int queueCapacity;

    @Bean
    public Executor taskExecutor() {
        TaskQueue workQueue = new TaskQueue(queueCapacity);
        TaskThreadFactory taskThreadFactory = new TaskThreadFactory(WITHDRAWAL_TASK_EXECUTOR, false, Thread.NORM_PRIORITY);
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MINUTES, workQueue, taskThreadFactory);
    }
}

package com.fizz.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/*@Configuration
@EnableScheduling
@ComponentScan
@EnableAsync(
	mode = AdviceMode.PROXY,
	proxyTargetClass = false,
	order = Ordered.HIGHEST_PRECEDENCE
)*/
@Deprecated
public class RootContextConfiguration implements AsyncConfigurer,SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		TaskScheduler scheduler = this.taskScheduler();
		taskRegistrar.setTaskScheduler(scheduler);
	}

	@Override
	public Executor getAsyncExecutor() {
		Executor executor = this.taskScheduler();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(100);
		//scheduler.setThreadNamePrefix("fizz-");
		scheduler.setAwaitTerminationSeconds(90);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		return scheduler;
	}

}

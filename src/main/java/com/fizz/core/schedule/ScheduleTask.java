package com.fizz.core.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

	@Scheduled(cron="0 0/10 * * * ?")
	public void task() {
		//TODO 执行的代码
		
	}
	
}

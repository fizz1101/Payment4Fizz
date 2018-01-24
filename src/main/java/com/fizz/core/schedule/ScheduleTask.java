package com.fizz.core.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

	/**
	 * 订单过期校验，并处理
	 */
	//@Scheduled(cron="0 0/10 * * * ?")
	@Scheduled(fixedDelay = 999999999, initialDelay = 1000)
	public void expireTradeCheck() {
		//TODO 执行的代码
		new ExpireTradeCheckSchedule().start();
	}
	
}

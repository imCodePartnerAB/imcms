package com.imcode.imcms.config;

import com.imcode.imcms.domain.exception.ImcmsAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
	@Value("${async.thread-pool.core-size}")
	private Integer corePoolSize;
	@Value("${async.thread-pool.max-size}")
	private Integer maxPoolSize;
	@Value("${async.thread-pool.queue-capacity}")
	private Integer queueCapacity;

	@Bean
	public ThreadPoolTaskExecutor mvcTaskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix("AsyncThreadExecutor-");
		executor.initialize();
		return executor;
	}

	@Override
	public Executor getAsyncExecutor() {
		return mvcTaskExecutor();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new ImcmsAsyncExceptionHandler();
	}
}

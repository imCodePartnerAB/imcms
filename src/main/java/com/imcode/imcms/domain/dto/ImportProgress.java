package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportProgress {
	private Integer totalSize = -1;
	private AtomicInteger progress = new AtomicInteger(-1);
	private AtomicBoolean error = new AtomicBoolean(false);

	public void increment() {
		if (progress.get() == -1)
			progress.set(0);

		progress.incrementAndGet();
	}

	public void setError() {
		error.set(true);
	}

	public void reset() {
		totalSize = -1;
		progress.set(-1);
	}
}

package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportProgress {
	private Integer totalSize = -1;
	private AtomicInteger progress = new AtomicInteger(-1);

	public void increment() {
		progress.incrementAndGet();
	}

	public void reset() {
		this.totalSize = -1;
		progress.set(-1);
	}
}

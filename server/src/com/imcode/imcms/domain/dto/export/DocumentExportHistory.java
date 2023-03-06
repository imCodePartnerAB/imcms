package com.imcode.imcms.domain.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.math.IntRange;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentExportHistory {
	private Integer start;
	private Integer end;
	private Map<Integer, ExportStatus> documents = new HashMap<>();

	public DocumentExportHistory(IntRange range) {
		this.start = range.getMinimumInteger();
		this.end = range.getMaximumInteger();
	}

	@JsonIgnore
	public IntRange getRange() {
		return new IntRange(start, end);
	}

	public ExportStatus getStatus(Integer id) {
		return documents.get(id);
	}

	public void add(Integer id, ExportStatus status) {
		documents.put(id, status);
	}

	public enum ExportStatus {
		SUCCESS,
		SKIPPED,
		FAILED;
	}
}

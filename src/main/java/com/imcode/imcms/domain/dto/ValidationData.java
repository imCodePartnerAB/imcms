package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Class holds errors and warnings data as text validation result.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Data
public class ValidationData {
	@JsonProperty("messages")
	private List<TextValidationDefect> defects;
}

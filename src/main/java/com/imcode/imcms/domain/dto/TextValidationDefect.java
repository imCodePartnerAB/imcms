package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Class is a wrapper for defect in text validation.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class TextValidationDefect {
	private String type;
	private int lastLine;
	private int firstLine;
	private int lastColumn;
	private int firstColumn;
	private String subType;
	private String message;
	private String extract;
	private int hiliteStart;
	private int hiliteLength;
}
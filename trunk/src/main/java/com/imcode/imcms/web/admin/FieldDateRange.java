package com.imcode.imcms.web.admin;

import java.util.Date;

public class FieldDateRange extends DateRange {
	private static final long serialVersionUID = -2536344095392448592L;
	
	private final String field;

	
	public FieldDateRange() {
		this(null, null, null);
	}
	
	public FieldDateRange(String field) {
		this(field, null, null);
	}

	public FieldDateRange(Date from, Date to) {
		this(null, from, to);
	}
	
	public FieldDateRange(String field, Date from, Date to) {
		super(from, to);
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
}

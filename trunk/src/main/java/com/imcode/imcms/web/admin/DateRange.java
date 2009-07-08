package com.imcode.imcms.web.admin;

import java.util.Date;

public class DateRange extends Range<Date> {
	private static final long serialVersionUID = -892323186659022028L;

	public DateRange() {
	}

	public DateRange(Date from, Date to) {
		super(from, to);
	}
}

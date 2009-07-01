package com.imcode.imcms.web.admin;

import java.io.Serializable;

public class NameValuePair implements Serializable {
	private static final long serialVersionUID = -6996949600694488760L;
	
	private final String  name;
	private final Object value;
	
	
	public NameValuePair(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	
	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}

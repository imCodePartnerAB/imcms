package com.imcode.imcms.web.admin;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Range<T> implements Serializable {
	private static final long serialVersionUID = -9003774059401929567L;
	
	protected T from;
	protected T to;
	
	
	public Range() {
	}
	
	public Range(T from, T to) {
		this.from = from;
		this.to = to;
	}
	
	
	public T getFrom() {
		return from;
	}

	public void setFrom(T from) {
		this.from = from;
	}

	public T getTo() {
		return to;
	}

	public void setTo(T to) {
		this.to = to;
	}
	
	public boolean isSet() {
		return (from != null || to != null);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(from)
				.append(to).hashCode();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Range)) {
			return false;
		}
		
		Range<T> other = (Range<T>) obj;
		
		return new EqualsBuilder().append(from, other.getFrom())
				.append(to, other.getTo())
				.isEquals();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%s - %s]", getClass().getName(), from, to);
	}
}

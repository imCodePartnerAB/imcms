package com.imcode.imcms.web.admin;


/**
 * Search parameters
 */
public class SearchParams {
	
	/**
	 * Documents range.
	 */
	static class Range {
		
		private Integer from;
		
		private Integer to;
		
		public Range() {}
		
		public Range(Integer from, Integer to) {
			this.from = from;
			this.to = to;
		}

		public Integer getFrom() {
			return from;
		}

		public void setFrom(Integer from) {
			this.from = from;
		}

		public Integer getTo() {
			return to;
		}

		public void setTo(Integer to) {
			this.to = to;
		}		
	}
	
	/**
	 * Document range.
	 */
	private Range range;

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}
}

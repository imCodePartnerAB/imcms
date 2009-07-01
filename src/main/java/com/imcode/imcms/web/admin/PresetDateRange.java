package com.imcode.imcms.web.admin;

import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PresetDateRange {
	private static final int MONTHS_PER_QUARTER = 3;
	
	private final String localeKey;
	private final DateRange dateRange;
	
	
	public PresetDateRange(String localeKey, DateRange dateRange) {
		this.localeKey = localeKey;
		this.dateRange = dateRange;
	}

	
	public static PresetDateRange[] getStandardPresets(Locale locale) {
		Calendar currentDate = getCurrentDate(locale);
		
		PresetDateRange yesterdayPreset = getYesterdayPreset(locale, currentDate);
		PresetDateRange lastWeekPreset = getLastWeekPreset(locale, currentDate);
		PresetDateRange lastMonthPreset = getLastMonthPreset(locale, currentDate);
		PresetDateRange lastQuarterPreset = getLastQuarterPreset(locale, currentDate);
		PresetDateRange lastYearPreset = getLastYearPreset(locale, currentDate);
		
		return new PresetDateRange[] { 
			yesterdayPreset, lastWeekPreset, lastMonthPreset, lastQuarterPreset, lastYearPreset
		};
	}
	
	private static Calendar getCurrentDate(Locale locale) {
		Calendar currentDate = Calendar.getInstance(locale);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MILLISECOND, 0);
		
		return currentDate;
	}
	
	
	public static PresetDateRange getYesterdayPreset(Locale locale) {
		return getYesterdayPreset(locale, getCurrentDate(locale));
	}
	
	private static PresetDateRange getYesterdayPreset(Locale locale, Calendar currentDate) {
		Calendar yesterday = (Calendar) currentDate.clone();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		
		return new PresetDateRange("yesterday", new DateRange(yesterday.getTime(), currentDate.getTime()));
	}
	
	
	public static PresetDateRange getLastWeekPreset(Locale locale) {
		return getLastWeekPreset(locale, getCurrentDate(locale));
	}
	
	private static PresetDateRange getLastWeekPreset(Locale locale, Calendar currentDate) {
		Calendar lastWeekFrom = (Calendar) currentDate.clone();
		lastWeekFrom.add(Calendar.WEEK_OF_YEAR, -1);
		lastWeekFrom.set(Calendar.DAY_OF_WEEK, lastWeekFrom.getFirstDayOfWeek());
		
		Calendar lastWeekTo = (Calendar) lastWeekFrom.clone();
		lastWeekTo.add(Calendar.DAY_OF_MONTH, 6);
		
		DateRange lastWeekRange = new DateRange(lastWeekFrom.getTime(), lastWeekTo.getTime());
		PresetDateRange lastWeekPreset = new PresetDateRange("last_week", lastWeekRange);
		
		return lastWeekPreset;
	}
	
	
	public static PresetDateRange getLastMonthPreset(Locale locale) {
		return getLastMonthPreset(locale, getCurrentDate(locale));
	}
	
	private static PresetDateRange getLastMonthPreset(Locale locale, Calendar currentDate) {
		Calendar lastMonthLower = (Calendar) currentDate.clone();
		lastMonthLower.add(Calendar.MONTH, -1);
		lastMonthLower.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar lastMonthUpper = (Calendar) lastMonthLower.clone();
		lastMonthUpper.set(Calendar.DAY_OF_MONTH, lastMonthUpper.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		DateRange lastMonthRange = new DateRange(lastMonthLower.getTime(), lastMonthUpper.getTime());
		PresetDateRange lastMonthPreset = new PresetDateRange("last_month", lastMonthRange);
		
		return lastMonthPreset;
	}
	
	
	public static PresetDateRange getLastQuarterPreset(Locale locale) {
		return getLastMonthPreset(locale, getCurrentDate(locale));
	}
	
	private static PresetDateRange getLastQuarterPreset(Locale locale, Calendar currentDate) {
		int currentQuarter = currentDate.get(Calendar.MONTH) / MONTHS_PER_QUARTER;
		Calendar lastQuarterLower = (Calendar) currentDate.clone();
		
		if (currentQuarter == 0) {
			lastQuarterLower.add(Calendar.YEAR, -1);
			lastQuarterLower.set(Calendar.MONTH, Calendar.OCTOBER);
		} else {
			lastQuarterLower.set(Calendar.MONTH, (currentQuarter - 1) * MONTHS_PER_QUARTER);
		}
		lastQuarterLower.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar lastQuarterUpper = (Calendar) lastQuarterLower.clone();
		lastQuarterUpper.add(Calendar.MONTH, 2);
		lastQuarterUpper.set(Calendar.DAY_OF_MONTH, lastQuarterUpper.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		DateRange lastQuarterRange = new DateRange(lastQuarterLower.getTime(), lastQuarterUpper.getTime());
		PresetDateRange lastQuarterPreset = new PresetDateRange("last_quarter", lastQuarterRange);
		
		return lastQuarterPreset;
	}
	
	
	public static PresetDateRange getLastYearPreset(Locale locale) {
		return getLastYearPreset(locale, getCurrentDate(locale));
	}
	
	private static PresetDateRange getLastYearPreset(Locale locale, Calendar currentDate) {
		Calendar lastYearLower = (Calendar) currentDate.clone();
		lastYearLower.set(Calendar.DAY_OF_MONTH, 1);
		lastYearLower.set(Calendar.MONTH, Calendar.JANUARY);
		lastYearLower.add(Calendar.YEAR, -1);
		
		Calendar lastYearUpper = (Calendar) lastYearLower.clone();
		lastYearUpper.set(Calendar.MONTH, Calendar.DECEMBER);
		lastYearUpper.set(Calendar.DAY_OF_MONTH, lastYearUpper.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		DateRange lastYearRange = new DateRange(lastYearLower.getTime(), lastYearUpper.getTime());
		PresetDateRange lastYearPreset = new PresetDateRange("last_year", lastYearRange);
		
		return lastYearPreset;
	}
	
	
	public String getLocaleKey() {
		return localeKey;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PresetDateRange)) {
			return false;
		}
		
		PresetDateRange other = (PresetDateRange) obj;
		
		return new EqualsBuilder().append(localeKey, other.getLocaleKey())
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(localeKey).hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s[localeKey: %s, dateRange: %s]", PresetDateRange.class.getName(), localeKey, dateRange);
	}
}

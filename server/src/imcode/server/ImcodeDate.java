package imcode.server ;
import java.util.Calendar ;

public class ImcodeDate {


	/**
	  get date : yymmdd
	  */
	public static String getDateToDay() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

			String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
		int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
		int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;

			String dateToDay  = year ;
		dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
		dateToDay += day < 10 ? "0" + Integer.toString(day) : Integer.toString(day) ;

		return dateToDay ;
	}



	/**
	  get date : yyyy-mm-dd
	  */
	public static String getDateToDayDelim() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

			String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
		int month  = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
		int day    = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
		int hour   = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY)))  ;
		int min    = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;

			String dateToDay  = year + "-" ;
		dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
		dateToDay += "-" ;
		dateToDay +=  day < 10 ? "0" + Integer.toString(day) : Integer.toString(day) ;

		return dateToDay ;
	}


	/** 
	  get time: hhmmss
	  */
	public static String getTimeNow() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY)))  ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;

			String timeNow  = "" ;
		timeNow += hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour) ;
		timeNow += min < 10 ? "0" + Integer.toString(min) : Integer.toString(min) ;

		return timeNow ;
	}



	/** 
	  get time: hh:mm
	  */
	public static String getTimeNowDelim() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY)))  ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;

			String timeNow  = "" ;
		timeNow += hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour) ;
		timeNow += ":" ;
		timeNow +=  + min < 10 ? "0" + Integer.toString(min) : Integer.toString(min) ;

		return timeNow ;
	}

	/**
	  get full date : yyyy-mm-dd hh:mm:ss.000
	  */
	public static String getFullDateDelim() {
		return getDateToDayDelim() + " " + getTimeNowDelim() ; 
	}	

	/**
	  get full date : yyyy-mm-dd hh:mm:ss.000
	  */
	public static String getFullDate() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

			String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
		int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
		int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;
		int sec   = Integer.parseInt(Integer.toString(cal.get(Calendar.SECOND))) ;	


			String date_to_day  = year + "-" ;
		date_to_day += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
		date_to_day += "-" ;
		date_to_day += day < 10 ? "0"   + Integer.toString(day)   : Integer.toString(day)  + " " ;
		date_to_day += " " ; 
		date_to_day += hour < 10 ? "0"  + Integer.toString(hour)  : Integer.toString(hour) ;
		date_to_day += ":" ;
		date_to_day += min < 10 ? "0"   + Integer.toString(min)   : Integer.toString(min) ;
		date_to_day += ":" ;
		date_to_day += sec < 10 ? "0"   + Integer.toString(min)   : Integer.toString(sec) ;
		date_to_day += ".000" ;

		return date_to_day ;
	}

	/**
	  get full time : hh:mm:ss.000
	  */
	public static String getFullTime() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;
		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;
		int sec   = Integer.parseInt(Integer.toString(cal.get(Calendar.SECOND))) ;	


			String time_now  = "" ;

			time_now += hour < 10 ? "0"  + Integer.toString(hour)  : Integer.toString(hour) ;
		time_now += ":" ;
		time_now += min < 10 ? "0"   + Integer.toString(min)   : Integer.toString(min) ;
		time_now += ":" ;
		time_now += sec < 10 ? "0"   + Integer.toString(min)   : Integer.toString(sec) ;
		time_now += ".000" ;

		return time_now ;
	}



}

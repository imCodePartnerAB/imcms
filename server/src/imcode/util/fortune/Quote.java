package imcode.util.fortune ;

import java.util.Date ;
import imcode.util.DateRange ;

public class Quote {
	private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private String text ;
    private DateRange dateRange ;

    public Quote(String text, DateRange dateRange) {
	this.text = text ;
	this.dateRange = dateRange ;
    }

    /**
       get-method for text

       @return the value of text
    **/
    public String getText() {
	return this.text;
    }

    /**
       set-method for text

       @param text Value for text
    **/
    public void setText(String text){
	this.text = text;
    }

    /**
       get-method for dateRange

       @return the value of dateRange
    **/
    public DateRange getDateRange() {
	return this.dateRange;
    }

    /**
       set-method for dateRange

       @param dateRange Value for dateRange
    **/
    public void setDateRange(DateRange dateRange){
	this.dateRange = dateRange;
    }

}

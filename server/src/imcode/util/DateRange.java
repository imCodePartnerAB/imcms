package imcode.util ;

import java.util.* ;

/**

This class represents a range of Dates.

**/
public class DateRange {

    private Date startDate ;
    private Date endDate ;

    public DateRange(Date startDate, Date endDate) {
	this.startDate = startDate ;
	this.endDate = endDate ;
    }

    /**
       get-method for startDate

       @return the value of startDate
    **/
    public Date getStartDate() {
	return this.startDate;
    }

    /**
       get-method for endDate

       @return the value of endDate
    **/
    public Date getEndDate() {
	return this.endDate;
    }

    /**
       Check whether this DateRange contains a date.
       Specifically, this DateRange contains a date
       if the date is equal to or greater than the startDate and
       less than the endDate.

       @return whether or not this DateRange contains the specified date.
    **/
    public boolean contains(Date date) {
	return (startDate.compareTo(date) <= 0) && endDate.after(date) ;
    }

}

package imcode.util.fortune;

import java.util.Date;

/**
 * This class represents a range of Dates.
 */
public class DateRange {

    private Date startDate;
    private Date endDate;

    public DateRange( Date startDate, Date endDate ) {
        if ( startDate.after( endDate ) ) {
            this.startDate = endDate;
            this.endDate = startDate;
        } else {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    /**
     * get-method for startDate
     *
     * @return the value of startDate
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * get-method for endDate
     *
     * @return the value of endDate
     */
    public Date getEndDate() {
        return this.endDate;
    }

    /**
     * Check whether this DateRange contains a date.
     * Specifically, this DateRange contains a date
     * if the date is equal to or greater than the startDate and
     * less than the endDate.
     *
     * @return whether or not this DateRange contains the specified date.
     */
    public boolean contains( Date date ) {
        return !startDate.after( date ) && endDate.after( date );
    }

    /**
     * Compares this DateRange to another object.
     *
     * @return true If and only if object is a DateRange, and both start-dates and end-dates match.
     */
    public boolean equals( Object object ) {
        if ( object instanceof DateRange ) {
            return equals( (DateRange)object );
        } else {
            return false;
        }
    }

    /**
     * Compares this DateRange to another.
     *
     * @return true If and only if both start-dates and end-dates match.
     */
    private boolean equals( DateRange dateRange ) {
        return this.startDate.equals( dateRange.startDate ) && this.endDate.equals( dateRange.endDate );
    }

    /**
     * Compares this DateRange to another.
     *
     * @return true If and only if the ranges overlap.
     */
    public boolean overlap( DateRange dateRange ) {
        return ( startDate.before( dateRange.endDate ) && endDate.after( dateRange.startDate ) );
    }

}

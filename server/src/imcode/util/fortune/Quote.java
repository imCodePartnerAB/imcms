package imcode.util.fortune ;

public class Quote {

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

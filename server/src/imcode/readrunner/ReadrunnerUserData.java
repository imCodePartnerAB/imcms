package imcode.readrunner ;

import java.util.Date ;

public class ReadrunnerUserData {

    private int     uses = 0 ;
    private int     maxUses = 0 ;
    private int     maxUsesWarningThreshold = 0 ;
    private Date    expiryDate = null ;
    private int     expiryDateWarningThreshold = 0 ;
    private boolean expiryDateWarningSent = false ;

    /**
       get-method for expiryDateWarningSent

       @return the value of expiryDateWarningSent
    **/
    public boolean getExpiryDateWarningSent()  {
	return this.expiryDateWarningSent;
    }

    /**
       set-method for expiryDateWarningSent

       @param expiryDateWarningSent Value for expiryDateWarningSent
    **/
    public void setExpiryDateWarningSent(boolean expiryDateWarningSent) {
	this.expiryDateWarningSent = expiryDateWarningSent;
    }

    /**
       Get the number of readrunner-uses for this user
    **/
    public int getUses() {
	return this.uses ;
    }

    /**
       Set the number of readrunner-uses for this user
    **/
    public void setUses(int uses) {
	this.uses = uses ;
    }

    /**
       Get the max number of readrunner-uses for this user
    **/
    public int getMaxUses() {
	return this.maxUses ;
    }

    /**
       Set the max number of readrunner-uses for this user
    **/
    public void setMaxUses(int maxUses) {
	this.maxUses = maxUses ;
    }

    /**
       Get the remaining percentage of the number of max-users at which
       the user will receive an expiry-warning.
    **/
    public int getMaxUsesWarningThreshold() {
	return this.maxUsesWarningThreshold ;
    }

    /**
       Set the remaining percentage of the number of max-users at which
       the user will receive an expiry-warning.
    **/
    public void setMaxUsesWarningThreshold(int maxUsesWarningThreshold) {
	this.maxUsesWarningThreshold = maxUsesWarningThreshold ;
    }

    /**
       Get the readrunner-use expiry-date for this user.
    **/
    public Date getExpiryDate() {
	return this.expiryDate ;
    }

    /**
       Set the readrunner-use expiry-date for this user.
    **/
    public void setExpiryDate(Date expiryDate) {
	this.expiryDate = expiryDate ;
    }

    /**
       Get the remaining number of days before
       the user will receive an expiry-warning.
    **/
    public int getExpiryDateWarningThreshold() {
	return this.expiryDateWarningThreshold ;
    }

    /**
       Set the remaining number of days before
       the user will receive an expiry-warning.
    **/
    public void setExpiryDateWarningThreshold(int expiryDateWarningThreshold) {
	this.expiryDateWarningThreshold = expiryDateWarningThreshold ;
    }
	
}

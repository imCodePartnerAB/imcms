package imcode.server ;

import java.util.* ;

public class User extends Hashtable {
    private final static String CVS_REV="$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /** Good stuff **/
    private int userId ;
    private String loginName ;
    private String password ;
    private String firstName = "" ;
    private String lastName  = "" ;
    private boolean active ;
    private String langPrefix ;
    private String emailAddress = "" ;
    private int template_group = -1 ;
    private String loginType ;

    /**
       get user-id
    **/
    public int getUserId() {
	return this.userId ;
    }

    /**
       get user-id
    **/
    public void setUserId(int userId) {
	this.userId = userId ;
    }

    /**
       get login name (username)
    **/
    public String getLoginName() {
	return this.loginName ;
    }

    /**
       set login name (username)
    **/
    public void setLoginName(String loginName) {
	this.loginName = loginName ;
    }

    /**
       get password
    **/
    public String getPassword() {
	return this.password ;
    }

    /**
       set password
    **/
    public void setPassword(String password) {
	this.password = password ;
    }

    /**
       get full name
    **/
    public String getFullName() {
	return getFirstName() + " " +
	    getLastName() ;
    }

    /**
       get first name
    **/
    public String getFirstName() {
	return this.firstName ;
    }

    /**
       set first name
    **/
    public void setFirstName(String firstName) {
	this.firstName = firstName ;
    }

    /**
       set last name
    **/
    public String getLastName() {
	return this.lastName ;
    }

    /**
       get last name
    **/
    public void setLastName(String lastName) {
	this.lastName = lastName ;
    }

    /**
       Check whether the user is allowed to log in
    **/
    public boolean isActive() {
	return this.active ;
    }

    /**
       Set whether the user is allowed to log in
    **/
    public void setActive(boolean active) {
	this.active = active ;
    }

    /**
       Return the users e-mail address
    **/
    public String getEmailAddress() {
	return this.emailAddress ;
    }

    /**
       Set the users e-mail address
    **/
    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress ;
    }

    /**
       set template group
    **/
    public void setTemplateGroup(int template_group) {
	this.template_group = template_group ;
    }

    /**
       get template group
    **/
    public int getTemplateGroup() {
	return template_group ;
    }


    /**
       Return the users lang_prefix
    **/
    public String getLangPrefix() {
	return this.langPrefix ;
    }

    /**
       Set the users lang_prefix
    **/
    public void setLangPrefix(String langPrefix) {
	this.langPrefix = langPrefix ;
    }

    /**
       Get the login-type.
    **/
    public String getLoginType() {
	return this.loginType ;
    }

    /**
       Set the login-type.
    **/
    public void setLoginType(String loginType) {
	this.loginType = loginType ;
    }
}

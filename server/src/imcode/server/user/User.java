package imcode.server.user;

import java.util.*;
import java.lang.reflect.Field;

import org.apache.log4j.*;
import imcode.server.IMCServiceInterface;

public class User extends Hashtable {

   User() {
   }

   private final static String CVS_REV = "$Revision$";
   private final static String CVS_DATE = "$Date$";

   private static Logger log = Logger.getLogger( User.class.getName() );

   /** Good stuff **/
   private int userId;
   private String loginName;		//varchar 50
   private String password;		//varchar 15
   private String firstName;		//varchar 25
   private String lastName;		//varchar 30
   private String title;		//varchar 30
   private String company;		//varchar 30
   private String address;		//varchar 40
   private String city;		//varchar 30
   private String zip;			//varchar 15
   private String country;		//varchar 30
   private String county_council;	//varchar 30
   private String emailAddress;	//varchar 50
   private String workPhone;           //varchar 25
   private String mobilePhone;         //varchar 25
   private String homePhone;           //varchar 25
   private int lang_id;		//int
   private int user_type;		//int
   private boolean active;		//int
   private String create_date;		//smalldatetime

   private String langPrefix;

   private int template_group = -1;
   private String loginType;

   private IMCServiceInterface serverObject;
   private boolean imcmsExternal = false ;

   /**
    get user-id
    **/
   public int getUserId() {
      return this.userId;
   }

   /**
    set user-id
    **/
   public void setUserId( int userId ) {
      this.userId = userId;
   }

   /**
    get login name (username)
    **/
   public String getLoginName() {
      return this.loginName;
   }

   /**
    set login name (username)
    **/
   public void setLoginName( String loginName ) {
      this.loginName = loginName;
   }

   /**
    get password
    **/
   public String getPassword() {
      return this.password;
   }

   /**
    set password
    **/
   public void setPassword( String password ) {
      this.password = password;
   }

   /**
    get full name
    **/
   public String getFullName() {
      return getFirstName() + " " + getLastName();
   }

   /**
    get first name
    **/
   public String getFirstName() {
      return this.firstName;
   }

   /**
    set first name
    **/
   public void setFirstName( String firstName ) {
      this.firstName = firstName;
   }

   /**
    get last name
    **/
   public String getLastName() {
      return this.lastName;
   }

   /**
    set last name
    **/
   public void setLastName( String lastName ) {
      this.lastName = lastName;
   }

   /**
    set title
    **/
   public void setTitle( String title ) {
      this.title = title;
   }

   /**
    get title
    **/
   public String getTitle() {
      return this.title;
   }

   /**
    set company
    **/
   public void setCompany( String company ) {
      this.company = company;
   }

   /**
    get company
    **/
   public String getCompany() {
      return this.company;
   }

   /**
    set address
    **/
   public void setAddress( String address ) {
      this.address = address;
   }

   /**
    get address
    **/
   public String getAddress() {
      return this.address;
   }


   /**
    set city
    **/
   public void setCity( String city ) {
      this.city = city;
   }

   /**
    get city
    **/
   public String getCity() {
      return this.city;
   }


   /**
    set zip
    **/
   public void setZip( String zip ) {
      this.zip = zip;
   }

   /**
    get zip
    **/
   public String getZip() {
      return this.zip;
   }

   /**
    set country
    **/
   public void setCountry( String country ) {
      this.country = country;
   }

   /**
    get country
    **/
   public String getCountry() {
      return this.country;
   }

   /**
    set county_council
    **/
   public void setCountyCouncil( String county_council ) {
      this.county_council = county_council;
   }

   /**
    get county_council
    **/
   public String getCountyCouncil() {
      return this.county_council;
   }

   /**
    Return the users e-mail address
    **/
   public String getEmailAddress() {
      return this.emailAddress;
   }

   /**
    Set the users e-mail address
    **/
   public void setEmailAddress( String emailAddress ) {
      this.emailAddress = emailAddress;
   }

   /**
    Get the users workphone
    **/
   public String getWorkPhone() {
      return this.workPhone;
   }

   /**
    Set the users workphone
    **/
   public void setWorkPhone( String workphone ) {
      this.workPhone = workphone;
   }

   /**
    Get the users mobilephone
    **/
   public String getMobilePhone() {
      return this.mobilePhone;
   }

   /**
    Set the users mobilephone
    **/
   public void setMobilePhone( String mobilephone ) {
      this.mobilePhone = mobilephone;
   }

   /**
    Get the users homephone
    **/
   public String getHomePhone() {
      return this.homePhone;
   }

   /**
    Set the users homepohne
    **/
   public void setHomePhone( String homephone ) {
      this.homePhone = homephone;
   }

   /**
    get lang_id
    **/
   public int getLangId() {
      return this.lang_id;
   }

   /**
    set lang_id
    **/
   public void setLangId( int lang_id ) {
      this.lang_id = lang_id;
   }


   /**
    set user_type
    **/
   public void setUserType( int user_type ) {
      this.user_type = user_type;
   }

   /**
    get user_type
    **/
   public int getUserType() {
      return this.user_type;
   }


   /**
    Set whether the user is allowed to log in
    **/
   public void setActive( boolean active ) {
      this.active = active;
   }

   /**
    Check whether the user is allowed to log in
    **/
   public boolean isActive() {
      return this.active;
   }

   /**
    set create_date
    **/
   public void setCreateDate( String create_date ) {
      this.create_date = create_date;
   }

   /**
    get create_date
    **/
   public String getCreateDate() {
      return this.create_date;
   }


   /**
    set template group
    **/
   public void setTemplateGroup( int template_group ) {
      this.template_group = template_group;
   }

   /**
    get template group
    **/
   public int getTemplateGroup() {
      return template_group;
   }


   /**
    Return the users lang_prefix
    **/
   public String getLangPrefix() {
      return this.langPrefix;
   }

   /**
    Set the users lang_prefix
    **/
   public void setLangPrefix( String langPrefix ) {
      this.langPrefix = langPrefix;
   }

   /**
    Get the login-type.
    **/
   public String getLoginType() {
      return this.loginType;
   }

   /**
    Set the login-type.
    **/
   public void setLoginType( String loginType ) {
      this.loginType = loginType;
   }

   public boolean isImcmsExternal() {
      return this.imcmsExternal;
   }

   public void setImcmsExternal( boolean external ){
      this.imcmsExternal = external;
   }
}

import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;

import imcode.external.diverse.* ;
import imcode.util.* ;
import imcode.server.* ;

public class UserHandler extends Administrator {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
       Executes the sproc xxxx which will update the users values in the db
    **/

    public static boolean updateUserInfoDB(IMCServiceInterface imcref, Properties p) /*throws IOException */ {
	// Lets build the users information into a string and add it to db
	String userStr = createUserInfoString(p) ;
	imcref.sqlUpdateProcedure("UpdateUser " + userStr) ;
	return true ;
    }

    /**
       Executes the sproc xxxx which will add the users values in the db
    **/

    public static void addUserInfoDB(IMCServiceInterface imcref, String userStr) throws IOException  {
	// Lets build the users information into a string and add it to db
	imcref.sqlUpdateProcedure("AddNewUser " + userStr) ;
    }




    /**
       Creates hea sql string string used to run sproc updateUser
    **/

    public static String createUserInfoString(Properties params) {

	String sqlStr = "" ;
	sqlStr += params.getProperty("user_id") + ", " ;
	sqlStr +=	"'" + params.getProperty("login_name") + "', "  ;
	sqlStr += "'" + params.getProperty("password1") + "', ";
	sqlStr +=	"'" + params.getProperty("first_name") + "', ";
	sqlStr +=	"'" + params.getProperty("last_name") + "', ";
	sqlStr +=	"'" + params.getProperty("title") + "', ";
	sqlStr +=	"'" + params.getProperty("company") + "', ";
	sqlStr +=	"'" + params.getProperty("address") + "', ";
	sqlStr +=	"'" + params.getProperty("city") + "', ";
	sqlStr +=	"'" + params.getProperty("zip") + "', ";
	sqlStr +=	"'" + params.getProperty("country") + "', " ;
	sqlStr +=	"'" + params.getProperty("country_council") + "', ";
	sqlStr +=	"'" + params.getProperty("email") + "', "  ;

	// Default values for admin_mode, last_page, archive_mode, lang_id
	sqlStr +=	"0" + ", ";
	sqlStr += "1001" + ", ";
	sqlStr +=	"0" + ", ";

	sqlStr += params.getProperty("lang_id") + ", "  ;   // lang_id
	//sqlStr +=	"1" + ", " ;     // 1 står för sverige
	sqlStr +=	params.getProperty("user_type") + ", " ;
	sqlStr +=	params.getProperty("active")  ;

	//	this.log("Userinfostring:" + sqlStr) ;
	return sqlStr ;

    } // End of createUserInfoString

    /**
       Creates a properties with all the necessary props used to update
       a users prefs in the users table in the database
    */
    public static Properties doUpdateDefaults() {

	Properties p = new Properties() ;

	p.setProperty("user_id", "") ;
	p.setProperty("login_name", "") ;
	p.setProperty("password1", "") ;
	p.setProperty("first_name", "") ;
	p.setProperty("last_name", "") ;
	p.setProperty("title", "") ;
	p.setProperty("company", "") ;
	p.setProperty("address", "") ;
	p.setProperty("city", "") ;
	p.setProperty("zip", "") ;
	p.setProperty("country", "") ;
	p.setProperty("country_council", "") ;
	p.setProperty("email", "") ;

	return p ;

    }  // End of


    /**
       Collects all userparameters from the users table in the db
       Returns null if something goes wrong
    **/
    public static Properties getUserInfoDB(IMCServiceInterface imcref, String userId) {

	// Get default props
	Properties p = doDefaultUser() ;
	Hashtable h = imcref.sqlQueryHash("GetUserInfo " + userId) ;
	Enumeration keys = h.keys() ;
	while( keys.hasMoreElements() ) {
	    Object key = keys.nextElement() ;
	    String[] values = (String[]) h.get(key) ;
	    String aValue = values[0] ;
	    p.setProperty(key.toString(), aValue ) ;
	}

	return p ;
    }

    /**
       Creates a properties with all the users properties from the
       users table. All keys are here, but not the values
    */
    public static Properties doDefaultUser() {

	Properties p = new Properties() ;

	p.setProperty("user_id", "") ;
	p.setProperty("login_name", "") ;
	p.setProperty("login_password", "") ;
	p.setProperty("first_name", "") ;
	p.setProperty("last_name", "") ;
	p.setProperty("title", "") ;
	p.setProperty("company", "") ;
	p.setProperty("address", "") ;
	p.setProperty("city", "") ;
	p.setProperty("zip", "") ;
	p.setProperty("country", "") ;
	p.setProperty("country_council", "") ;
	p.setProperty("email", "") ;

	p.setProperty("admin_mode", "") ;
	p.setProperty("last_page", "") ;
	p.setProperty("archive_mode", "") ;
	p.setProperty("lang_id", "") ;

	p.setProperty("user_type", "") ;
	p.setProperty("active", "") ;
	p.setProperty("create_date", "") ;
	return p ;

    }  // End of


    /**
       Compares 2 properties, checks if the first properties all keys is
       valid in the second, which means that they are NOT  ""
    */
    public static boolean checkNecessaryParameters(Properties necessary, Properties all) {

	// Ok, lets check that the user has typed anything in all the fields
	//Enumeration enumValues = all.elements() ;
	Enumeration necKeys = necessary.keys() ;
	while( necKeys.hasMoreElements() ) {
	    String aKey = (String) necKeys.nextElement() ;
	    if( all.getProperty(aKey) == null || all.getProperty(aKey).equals("") ) ;
	    return false	;
	}
	return true ;
    } // checkparameters


    /**
       Adds the userInformation to the htmlPage. if an empty vector is sent as argument
       then an empty one will be created
    **/
    public static VariableManager addUserInfo(VariableManager vm, Vector v) {
	// Here is the order in the vector
	// [3, Rickard, tynne, Rickard, Larsson, Drakarve, Havdhem, 620 11, Sweden, Gotland,
	// rickard@imcode.com, 0, 1001, 0, 1]
	//(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

	if(v.size() == 0) {
	    for(int i = 0; i < 20; i++)
		v.add(i, "") ;
	}

	vm.addProperty("LOGIN_NAME", v.get(1).toString()) ;
	vm.addProperty("PWD1", v.get(2).toString()) ;
	vm.addProperty("PWD2", v.get(2).toString()) ;
	//	vm.addProperty("PWD1", "") ;
	//	vm.addProperty("PWD2", "") ;
	vm.addProperty("FIRST_NAME", v.get(3).toString()) ;
	vm.addProperty("LAST_NAME", v.get(4).toString()) ;
	vm.addProperty("TITLE", v.get(5).toString()) ;
	vm.addProperty("COMPANY", v.get(6).toString()) ;

	vm.addProperty("ADDRESS", v.get(7).toString()) ;
	vm.addProperty("CITY", v.get(8).toString()) ;
	vm.addProperty("ZIP", v.get(9).toString()) ;
	vm.addProperty("COUNTRY", v.get(10).toString()) ;
	vm.addProperty("COUNTRY_COUNCIL", v.get(11).toString()) ;
	vm.addProperty("EMAIL", v.get(12).toString()) ;
	vm.addProperty("ACTIVE", "13") ;

	return vm ;
    }


    public void log( String str) {
	super.log(str) ;
	System.out.println("UserChangePrefs: " + str ) ;
    }


    /**
       The getLoginParams method gets the login params from the requstobject
    **/

    public static Properties getLoginParams(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	Properties login = new Properties() ;
	// Lets get the parameters we know we are supposed to get from the request object
	String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
	String password1 = (req.getParameter("password")==null) ? "" : (req.getParameter("password")) ;

	login.setProperty("LOGIN_NAME", login_name.trim()) ;
	login.setProperty("PASSWORD", password1.trim()) ;
	return login ;
    }

    /**
       Validates the password. Password must contain at least 4 characters
       Generates an errorpage and returns false if something goes wrong
    */

    public static boolean verifyPassword(Properties prop, HttpServletRequest req,
					 HttpServletResponse res) throws ServletException, IOException {

	String pwd1 = prop.getProperty("password1") ;
	String pwd2 = prop.getProperty("password2") ;
	String header = "Verify password error" ;
	String msg = "" ;

	if( ! pwd1.equals(pwd2) ) {
	    header = req.getServletPath() ;
	    AdminError2 err = new AdminError2(req,res,header,52) ;
	    //log(header + err.getErrorMsg()) ;
	    return false ;
	}

	if( pwd1.length() < 4) {
	    header = req.getServletPath() ;
	    AdminError2 err = new AdminError2(req,res,header,53) ;
	    //log(header + err.getErrorMsg()) ;
	    return false ;
	}

	return true ;

    } // End verifyPassword

    /**
       Validates the phonenumber. Password must contain at least 4 characters
       Generates an errorpage and returns false if something goes wrong
    */

    public static boolean verifyPhoneNumber(Properties prop, HttpServletRequest req,
					    HttpServletResponse res) throws ServletException, IOException {
	try {
	    String[] arr = {
		prop.getProperty("country_code"),
		prop.getProperty("area_code"),
		prop.getProperty("local_code")
	    } ;

	    for(int i = 0 ; i< arr.length ; i++ ) {
		Integer.parseInt(arr[i]) ;
	    }

	} catch(NumberFormatException e) {
	    // log(e.getMessage()) ;
	    AdminError2 err = new AdminError2(req,res,"",63 ) ;
	    return false ;
	} catch(NullPointerException e) {
	    // log(e.getMessage()) ;
	    AdminError2 err = new AdminError2(req,res,"",63 ) ;
	    return false ;
	}
	return true ;

    } // End phonenumber

    /**
       Validates the username. Returns true if the login_name doesnt exists.
       Returns false if the username exists
    */
    public static boolean checkExistingUserName(IMCServiceInterface imcref, Properties prop) {
	String userName = prop.getProperty("login_name") ;
	String userNameExists[] = imcref.sqlProcedure("FindUserName '" + userName + "'") ;
	if(userNameExists != null ) {
	    if(userNameExists.length > 0 ) {
		return false;
	    }
	}
	return true ;
    } // CheckExistingUserName

} // End of class

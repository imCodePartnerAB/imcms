package imcode.server ;

public class LoginUser implements java.io.Serializable {
	private final static String CVS_REV="$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	String login_name = "" ;
	String login_password = "" ;
	String user_table_name           = "users" ;            // default
	String login_name_field_name     = "login_name" ;       // default
	String login_password_field_name = "login_password" ;   // default


	public LoginUser(String login_name,String login_password) {
		this.login_name     = login_name ;
		this.login_password = login_password ;
	}


	public String createLoginQuery() {
		String sqlStr  = "select * from " + user_table_name ;
		sqlStr += " where " + login_name_field_name ;
		sqlStr += " = " + "'" + login_name + "'";
		// sqlStr += " and " + login_password_field_name + " = " + "'" + login_password + "'";
		return sqlStr ;
	}


	public String getLoginNameFieldName() {
		return login_name_field_name ;
	}

	public String getLoginPasswordFieldName() {
		return login_password_field_name ;
	}

	public String getLoginName() {
		return login_name ;
	}

	public String getLoginPassword() {
		return login_password ;
	}


} // END CLASS LoginUser
	
	
	

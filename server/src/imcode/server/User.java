package imcode.server ;

import java.util.* ;

public class User extends Hashtable {
	//Hashtable userTable = new Hashtable(10,0.5f) ;
	boolean admin_mode = false ;
	int archive_mode = 0 ;
	String browser_info[] = new String[3] ;
	String browser_str = "" ;
	String last_request = "" ;
	String login_type = "verify" ;
	int template_group = -1 ;




	public User() {
		super(10,0.5f) ;
	}

	public User(String fieldNames[],Vector fieldData) {
		super(10,0.5f) ;
		for(int i = 0 ; i < fieldData.size() ; i++)
			put(fieldNames[i],fieldData.elementAt(i)) ;

	}

	// add object
	public void addObject(String fieldName,Vector object) {
		put(fieldName,object) ;	
	}	

	// get object
	public Object getObject(String fieldName) {
		return get(fieldName) ;
	}

	// get String
	public String getString(String fieldName) {
		return get(fieldName).toString() ;
	}

	// get int
	public int getInt(String fieldName) {
		return Integer.parseInt(get(fieldName).toString()) ;
	}


	// get boolean
	public boolean getBoolean(String fieldName) {
		return (Integer.parseInt(get(fieldName).toString()) !=0) ;
	}

	public void setFields(String fieldNames[],Vector fieldData) {
		for(int i = 0 ; i < fieldData.size() ; i++)
			put(fieldNames[i],fieldData.elementAt(i)) ;
	}

	public void setField(String fieldName,String fieldData) {
		put(fieldName,fieldData) ;
	}

	// toggle admin mode
	public void toggleAdminMode() {
		admin_mode = ! admin_mode ;
		if (admin_mode)
			put("admin_mode","1") ;
		else
			put("admin_mode","0") ;
	}

	// admin off
	public void adminModeOff() {
		put("admin_mode","0") ;
	}


	// admin on
	public void adminModeOn() {
		put("admin_mode","1") ;
	}


	// archive off
	public void archiveOff() {
		put("archive_mode","0") ;
	}

	// archive on
	public void archiveOn() {
		put("archive_mode","1") ;
	}


	// set last_meta_id
	public void setLastMetaId(int last_meta_id) {
		put("last_page",Integer.toString(last_meta_id))  ;
	}

	// get browser_info
	public String[] getBrowserInfo() {
		return browser_info ;
	}

	// get browser_str
	public String getBrowserStr() {
		return browser_str ;
	}

	// set browser_info
	public void setBrowserInfo(String type,String version,String plattform) {
		browser_info[0] = type ;
		browser_info[1] = version ;
		browser_info[2] = plattform ;
		browser_str = type + version + "_" + plattform ;
	}


	// set last request
	public void setLastRequest(String last_request) {
		this.last_request = last_request ;
	}

	// get  last request
	public String getLastRequest() {
		return last_request ;
	}


	// set login_type
	public void setLoginType(String type) {
		login_type = type ;
	}

	// get login_type
	public String getLoginType() {
		return login_type ;
	}


	// set template group
	public void setTemplateGroup(int template_group) {
		this.template_group = template_group ;
	}

	// get template group
	public int getTemplateGroup() {
		return template_group ;
	}

}

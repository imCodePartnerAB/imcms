package imcode.util ;

import java.io.* ;
import java.rmi.* ;
import java.net.* ;
import java.util.* ;
import imcode.util.* ;
import imcode.server.* ;
import java.rmi.registry.* ;
/**
	Class to keep track on the RMI-interface to the ImCode netserver.
	All calls are identical to those in IMCServiceInterface, with the exception that you add one parameter in front of each.
	That parameter is a string to indicate the server to use, defined in netservers.cfg.
*/
public class IMCServiceRMI {
	static Hashtable interfaces ;	// Keeps track of servers. "ip:port"=interface

	static {
		interfaces = new Hashtable() ;
	}

	static IMCServiceInterface renewInterface(String server) throws IOException {
		if ( server == null ) {
			throw new IllegalArgumentException("Server == null") ;
		}
		String ip = "", port = "1099", object;
		try {
			StringTokenizer st = new StringTokenizer(server,":") ;
			String protocol = st.nextToken() ;
			if ( protocol.indexOf("/")!=-1 ) {
				throw new MalformedURLException ("Bad RMI-URL: "+server) ;
			}
			if ( !protocol.toLowerCase().equals("rmi") ) {
				throw new MalformedURLException ("Unknown protocol: "+protocol+" in RMI-URL "+server) ;
			}
			st.nextToken("/") ;
			String host = st.nextToken() ;
			if ( host.indexOf(":")!=-1 ) {
				StringTokenizer st2 = new StringTokenizer(host,":") ;
				host = st2.nextToken() ;
				port = st2.nextToken() ;
			}
			object = st.nextToken() ;
		} catch ( NoSuchElementException ex ) {
			throw new MalformedURLException ("Bad RMI-URL: "+server) ;
		}
		IMCServiceInterface imc ;
		try {
			Registry reg = LocateRegistry.getRegistry(ip, Integer.parseInt(port)) ;
			imc = (IMCServiceInterface)reg.lookup(object) ;
		} catch ( Exception ex ) {
			Registry reg = LocateRegistry.getRegistry(ip, Integer.parseInt(port)) ;
			try {
				imc = (IMCServiceInterface)reg.lookup(object) ;
			} catch ( NotBoundException exc ) {
				throw new RemoteException (exc.getMessage() + " No IMCService object found at "+server) ;
			}
		}
		interfaces.put(server, imc) ;
		return imc ;
	}

	static IMCServiceInterface getInterface(String server) throws IOException {
		if ( server == null ) {
			throw new IllegalArgumentException("Server == null") ;
		}
		IMCServiceInterface imc = (IMCServiceInterface)interfaces.get(server) ;
		if (imc == null) {
			imc = renewInterface(server) ;
		}
		return imc;
	}

	/**
		Flushes the interface cache, essentially resetting everything.
	*/
	public static void flush () {
		interfaces = new Hashtable() ;
	}

/*								*
 * 	IMCService functions.		*
 *								*/

	public static void removeChild( String server, int meta_id, int parent_meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.removeChild(meta_id,parent_meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.removeChild(meta_id,parent_meta_id,user) ;
		}
	}
	public static void sqlUpdateQuery( String server, String sqlStr ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.sqlUpdateQuery(sqlStr) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.sqlUpdateQuery(sqlStr) ;
		}

	}
	public static void activateChild( String server, int meta_id, User user) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.activateChild(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.activateChild(meta_id,user) ;
		}
	}
/*	public static void insertNewTexts( String server, int meta_id, User user, int no_of_txt) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.insertNewTexts(meta_id,user,no_of_txt) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.insertNewTexts(meta_id,user,no_of_txt) ;
		}
	}
*/
	public static void saveFrameset( String server, int meta_id, User user, Table doc) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveFrameset(meta_id,user,doc) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveFrameset(meta_id,user,doc) ;
		}
	}

	public static String getExternalTemplateFolder( String server, int meta_id ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getExternalTemplateFolder(meta_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getExternalTemplateFolder(meta_id) ;
		}
	}
	public static int isBrowserDoc( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.isBrowserDoc(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.isBrowserDoc(meta_id,user) ;
		}
	}
/*
	public static void saveDoc( String server, int meta_id, User user, Table doc, int[] roles, int[] user_rights) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveDoc(meta_id,user,doc,roles,user_rights) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveDoc(meta_id,user,doc,roles,user_rights) ;
		}
	}
*/
	public static ExternalDocType isExternalDoc( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.isExternalDoc(meta_id, user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.isExternalDoc(meta_id, user) ;
		}
	}
	public static void sqlUpdateProcedure( String server, String sqlStr ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.sqlUpdateProcedure(sqlStr) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.sqlUpdateProcedure(sqlStr) ;
		}
	}

	public static int getCounter( String server ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getCounter() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getCounter() ;
		}
	}
	public static void saveNewFrameset( String server, int meta_id, User user, Table doc) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveNewFrameset(meta_id,user,doc) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveNewFrameset(meta_id,user,doc) ;
		}
	}

	public static int incCounter( String server ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.incCounter() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.incCounter() ;
		}
	}

	public static boolean userIsAdmin( String server, int meta_id, User user ) throws IOException {
		return checkDocAdminRights(server, meta_id, user, 65536) ;
	}

	public static void saveText( String server, int meta_id, User user, int txt_no, String text, int toHTMLSpecial) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveText(meta_id,user,txt_no,text,toHTMLSpecial) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveText(meta_id,user,txt_no,text,toHTMLSpecial) ;
		}
	}

	public static User verifyUser( String server, LoginUser login_user, String[] fieldNames) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.verifyUser(login_user,fieldNames) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.verifyUser(login_user,fieldNames) ;
		}
	}

	public static void saveUrl( String server, int meta_id, User user, Table doc ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveUrl(meta_id,user,doc) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveUrl(meta_id,user,doc) ;
		}
	}

	public static String[] sqlProcedure( String server, String procedure ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlProcedure(procedure) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlProcedure(procedure) ;
		}
	}

	public static void addExistingDoc( String server, int meta_id, User user, int existing_meta_id, int doc_menu_no) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.addExistingDoc(meta_id,user,existing_meta_id,doc_menu_no) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.addExistingDoc(meta_id,user,existing_meta_id,doc_menu_no) ;
		}
	}

	public static String listArchive( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.listArchive(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.listArchive(meta_id,user) ;
		}
	}

	public static String interpretAdminTemplate( String server, int meta_id, User user, String admin_template_name, int index, int value1, int value2, int value3 ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.interpretAdminTemplate(meta_id,user,admin_template_name,index,value1,value2,value3) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.interpretAdminTemplate(meta_id,user,admin_template_name,index,value1,value2,value3) ;
		}
	}

	public static String getCounterDate( String server ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getCounterDate() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getCounterDate() ;
		}
	}

	public static void saveImage( String server, int meta_id, User user, int img_no, Image image ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveImage(meta_id,user,img_no,image) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveImage(meta_id,user,img_no,image) ;
		}
	}

	public static void saveUrlDoc( String server, int meta_id, User user, Table doc ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveUrlDoc(meta_id,user,doc) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveUrlDoc(meta_id,user,doc) ;
		}
	}
/*
	public static void insertNewImages( String server, int meta_id, User user, int no_of_img ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.insertNewImages(meta_id,user,no_of_img) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.insertNewImages(meta_id,user,no_of_img) ;
		}
	}
*/
	public static String sqlProcedureStr( String server, String procedure ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlProcedureStr(procedure) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlProcedureStr(procedure) ;
		}
	}
	public static String parseDoc( String server, String htmlStr, Vector variables ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.parseDoc(htmlStr,variables) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.parseDoc(htmlStr,variables) ;
		}
	}
	public static String parseDoc( String server, String htmlStr, Vector variables, Vector data ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.parseDoc(htmlStr,variables,data) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.parseDoc(htmlStr,variables,data) ;
		}
	}
	public static String[] sqlQuery( String server, String sqlQuery ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlQuery(sqlQuery) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlQuery(sqlQuery) ;
		}
	}
	public static String[] sqlQuery( String server, String sqlQuery, String catalog ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlQuery(sqlQuery,catalog) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlQuery(sqlQuery,catalog) ;
		}
	}
	public static void saveManualSort( String server, int meta_id, User user, Vector childs, Vector sort_no) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveManualSort(meta_id,user,childs,sort_no) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveManualSort(meta_id,user,childs,sort_no) ;
		}
	}
	public static void saveNewBrowserDoc( String server, int meta_id, User user, Table doc ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveNewBrowserDoc(meta_id,user,doc) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveNewBrowserDoc(meta_id,user,doc) ;
		}
	}

	public static String sqlQueryStr( String server, String sqlQuery ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlQueryStr(sqlQuery) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlQueryStr(sqlQuery) ;
		}
	}

	public static void updateTrackLog( String server, int meta_id, int to_meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.updateTrackLog(meta_id,to_meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.updateTrackLog(meta_id,to_meta_id,user) ;
		}
	}
	public static void saveNewUrlDoc( String server, int meta_id, User user, Table doc) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveNewUrlDoc(meta_id,user,doc) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveNewUrlDoc(meta_id,user,doc) ;
		}
	}
	public static String isFramesetDoc( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.isFramesetDoc(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.isFramesetDoc(meta_id,user) ;
		}
	}
	public static void deleteChilds( String server, int meta_id, int doc_menu_no, User user, String[] childsThisMenu) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.deleteChilds(meta_id,doc_menu_no,user,childsThisMenu) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.deleteChilds(meta_id,doc_menu_no,user,childsThisMenu) ;
		}
	}
	public static void archiveChilds( String server, int meta_id, User user, String[] childsThisMenu) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.archiveChilds(meta_id,user,childsThisMenu) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.archiveChilds(meta_id,user,childsThisMenu) ;
		}
	}
	public static Vector searchDocs( String server, int meta_id, User user, String question_str, String search_type, String string_match, String search_area ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.searchDocs(meta_id,user,question_str,search_type,string_match,search_area) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.searchDocs(meta_id,user,question_str,search_type,string_match,search_area) ;
		}
	}
	public static Table isUrlDoc( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.isUrlDoc(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.isUrlDoc(meta_id,user) ;
		}
	}
	public static void saveTextDoc( String server, int meta_id, User user, Table doc ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveTextDoc(meta_id,user,doc) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveTextDoc(meta_id,user,doc) ;
		}
	}
/*
	public static int saveNewDoc( String server, int meta_id, User user, Table doc, int doc_menu_no, int[] roles, int[] user_rights ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.saveNewDoc(meta_id,user,doc,doc_menu_no,roles,user_rights) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.saveNewDoc(meta_id,user,doc,doc_menu_no,roles,user_rights) ;
		}
	}
*/
	public static int setCounter( String server, int value ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.setCounter(value) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.setCounter(value) ;
		}
	}

	public static int getDefaultHomePage( String server ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getDefaultHomePage() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getDefaultHomePage() ;
		}
	}

	public static void inActiveChild( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.inActiveChild(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.inActiveChild(meta_id,user) ;
		}
	}
/*
	public static String interpretTemplate( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.interpretTemplate(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.interpretTemplate(meta_id,user) ;
		}
	}
*/
	public static boolean setCounterDate( String server, String date ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.setCounterDate(date) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.setCounterDate(date) ;
		}
	}

	public static String[] sqlQueryExt ( String server, String sqlQuery ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlQueryExt(sqlQuery) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlQueryExt(sqlQuery) ;
		}
	}

	public static Hashtable sqlQueryHash ( String server, String sqlQuery ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlQueryHash(sqlQuery) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlQueryHash(sqlQuery) ;
		}
	}

	public static Hashtable sqlProcedureHash ( String server, String sqlQuery ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlProcedureHash(sqlQuery) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlProcedureHash(sqlQuery) ;
		}
	}

	public static void deleteDocAll ( String server, int meta_id, imcode.server.User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.deleteDocAll(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.deleteDocAll(meta_id,user) ;
		}
	}

	public static String parseDoc( String server, Vector variables, String file_name, String lang_prefix) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.parseDoc(variables,file_name,lang_prefix) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.parseDoc(variables,file_name,lang_prefix) ;
		}
	}

	public static String getTemplateHome ( String server ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getTemplateHome() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getTemplateHome() ;
		}
	}

	public static int getDocType ( String server, int meta_id ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getDocType(meta_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getDocType(meta_id) ;
		}
	}

	public static int getNoOfTxt ( String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getNoOfTxt(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getNoOfTxt(meta_id,user) ;
		}
	}

	public static int saveTemplate ( String server, String name, String file_name, byte[] data, boolean overwrite, String language ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.saveTemplate(name,file_name,data,overwrite,language) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.saveTemplate(name,file_name,data,overwrite,language) ;
		}
	}

	public static Object[] getDemoTemplate (String server, int template_id) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getDemoTemplate(template_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getDemoTemplate(template_id) ;
		}
	}

	public static byte[] getTemplate (String server, int template_id) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getTemplate(template_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getTemplate(template_id) ;
		}
	}

	public static void deleteTemplate (String server, int template_id) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.deleteTemplate(template_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.deleteTemplate(template_id) ;
		}
	}

/*
	public static void changeTemplateName (String server, int template_id, String name) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.changeTemplateName(template_id,name) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.changeTemplateName(template_id,name) ;
		}
	}
*/
	public static int saveDemoTemplate (String server, int template_id, byte[] file, String suffix) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.saveDemoTemplate(template_id,file, suffix) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.saveDemoTemplate(template_id,file, suffix) ;
		}
	}
/*
	public static void assignTemplate (String server, int template_id, int[] group_id) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.assignTemplate(template_id,group_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.assignTemplate(template_id,group_id) ;
		}
	}
*/
/*
	public static void unAssignTemplate (String server, int template_id, int[] group_id) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.unAssignTemplate(template_id,group_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.unAssignTemplate(template_id,group_id) ;
		}
	}
*/
	public static void saveTemplateGroup (String server, String name, User user) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.saveTemplateGroup(name,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.saveTemplateGroup(name,user) ;
		}
	}
	public static void deleteTemplateGroup (String server, int group_id) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.deleteTemplateGroup(group_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.deleteTemplateGroup(group_id) ;
		}
	}
	public static void changeTemplateGroupName (String server, int group_id, String name) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.changeTemplateGroupName(group_id,name) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.changeTemplateGroupName(group_id,name) ;
		}
	}
	public static java.util.Date getCurrentDate ( String server ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getCurrentDate() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getCurrentDate() ;
		}
	}
	public static String[][] sqlProcedureMulti ( String server, String sqlStr ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlProcedureMulti(sqlStr) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlProcedureMulti(sqlStr) ;
		}
	}
	public static String[][] sqlQueryMulti ( String server, String sqlStr ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.sqlQueryMulti(sqlStr) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.sqlQueryMulti(sqlStr) ;
		}
	}
	public static boolean checkDocRights (String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.checkDocRights(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.checkDocRights(meta_id,user) ;
		}
	}
	public static boolean checkDocAdminRightsAny (String server, int meta_id, User user, int permissions ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.checkDocAdminRightsAny(meta_id,user,permissions) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.checkDocAdminRightsAny(meta_id,user,permissions) ;
		}
	}
	public static boolean checkDocAdminRights (String server, int meta_id, User user,int permissions ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.checkDocAdminRights(meta_id,user,permissions) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.checkDocAdminRights(meta_id,user,permissions) ;
		}
	}
	public static boolean checkDocAdminRights (String server, int meta_id, User user ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.checkDocAdminRights(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.checkDocAdminRights(meta_id,user) ;
		}
	}
	public static int deleteDemoTemplate (String server, int template_id ) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.deleteDemoTemplate(template_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.deleteDemoTemplate(template_id) ;
		}
	}
	public static String[] getDemoTemplateList (String server) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getDemoTemplateList() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getDemoTemplateList() ;
		}
	}

	public static byte[] parsePage (String server,int meta_id,User user,int flags) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.parsePage(meta_id,user,flags) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.parsePage(meta_id,user,flags) ;
		}
	}

	public static String getMenuButtons (String server,int meta_id, User user) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getMenuButtons(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getMenuButtons(meta_id,user) ;
		}
	}

	public static String getMenuButtons (String server,String meta_id, User user) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getMenuButtons(meta_id,user) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getMenuButtons(meta_id,user) ;
		}
	}

        /** getLanguage. Returns the language prefix for a language_id.
         *  Example: If the id for the swedish language is 1=se.
         *  Then the procedure call getLangPrefixFromID("1") will return 'se'
         */
	public static String getLangPrefixFromId (String server, String lang_id_nbr) throws IOException {
                IMCServiceInterface imc = getInterface( server ) ;
		try {
                        return imc.getLanguage(lang_id_nbr) ;

		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getLanguage(lang_id_nbr) ;
		}
	}

	public static String getLanguage (String server) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getLanguage() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getLanguage() ;
		}
	}

	public static SystemData getSystemData (String server) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.getSystemData() ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.getSystemData() ;
		}
	}

	public static void setSystemData (String server, SystemData sd) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
		try {
			imc.setSystemData(sd) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			imc.setSystemData(sd) ;
		}
	}

        /*

        */
      	public static Hashtable ExistingDocsGetMetaIdInfo (String server,String[] meta_id) throws IOException {
                IMCServiceInterface imc = getInterface( server ) ;
		try {
			return imc.ExistingDocsGetMetaIdInfo(meta_id) ;
		} catch ( IOException ex ) {
			imc = renewInterface(server) ;
			return imc.ExistingDocsGetMetaIdInfo(meta_id) ;
		}
	}


}

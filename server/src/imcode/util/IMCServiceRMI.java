package imcode.util ;

import java.io.* ;
import java.net.* ;
import java.util.* ;

import imcode.server.* ;
import imcode.server.User ;
import imcode.server.Table ;
import imcode.server.ExternalDocType ;
import imcode.server.LoginUser ;
import imcode.server.Image ;
import imcode.server.SystemData ;

import imcode.server.parser.ParserParameters ;

import imcode.server.IMCServiceInterface ;

/**
   Class to keep track on the RMI-interface to the ImCode netserver.
   All calls are identical to those in IMCServiceInterface, with the exception that you add one parameter in front of each.
   That parameter is a string to indicate the server to use, defined in netservers.cfg.
*/

public class IMCServiceRMI {
	private final static String CVS_REV="$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

    private static Hashtable interfaces = new Hashtable() ;	// Keeps track of servers. "ip:port"=interface
    private final static imcode.server.ApplicationServer appServer = new imcode.server.ApplicationServer() ;

    private static IMCServiceInterface renewInterface(String server) {
	if ( server == null ) {
	    throw new IllegalArgumentException("Server == null") ;
	}

        IMCServiceInterface imc = (IMCServiceInterface)appServer.getServerObject(server) ;

	if (imc == null) {
	    throw new IllegalArgumentException("Server '"+server+"' not found.") ;
	}

	interfaces.put(server, imc) ;
	return imc ;
    }

    public static IMCServiceInterface getInterface(String server) {
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
 * GetInterface. Returns an interface to the host db. The JanusDB
 */
    public static imcode.server.IMCPoolInterface getPoolInterface(String server)  {
	if ( server == null ) {
	    throw new IllegalArgumentException("Server == null") ;
	}
	imcode.server.IMCPoolInterface imc = (imcode.server.IMCPoolInterface)interfaces.get(server) ;
	if (imc == null) {
	    imc = renewPoolInterface(server) ;
	}
	return imc;
    }


/**
 * RenewInterface. Returns a renewed interface towards the host DB
 */

    private static imcode.server.IMCPoolInterface renewPoolInterface(String server) {
	if ( server == null ) {
	    throw new IllegalArgumentException("Server == null") ;
	}

        IMCPoolInterface imc = (IMCPoolInterface)appServer.getServerObject(server) ;

	if (imc == null) {
	    throw new IllegalArgumentException("Server '"+server+"' not found.") ;
	}

	interfaces.put(server, imc) ;
	return imc ;
    }


    /**
       Flushes the interface cache, essentially resetting everything.
    */
    public static void flush () {
	interfaces = new Hashtable() ;
    }

    /*								*
     *	IMCService functions.		*
     *								*/

    public static void removeChild( String server, int meta_id, int parent_meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;
	imc.removeChild(meta_id,parent_meta_id,user) ;
    }
    public static void sqlUpdateQuery( String server, String sqlStr ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.sqlUpdateQuery(sqlStr) ;
    }
    public static void activateChild( String server, int meta_id, User user) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.activateChild(meta_id,user) ;
    }
    /*	public static void insertNewTexts( String server, int meta_id, User user, int no_of_txt) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	imc.insertNewTexts(meta_id,user,no_of_txt) ;
	}
    */
    public static void saveFrameset( String server, int meta_id, User user, Table doc) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveFrameset(meta_id,user,doc) ;
    }

    public static File getExternalTemplateFolder( String server, int meta_id ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getExternalTemplateFolder(meta_id) ;
    }
    public static int isBrowserDoc( String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.isBrowserDoc(meta_id,user) ;
    }
    /*
      public static void saveDoc( String server, int meta_id, User user, Table doc, int[] roles, int[] user_rights) throws IOException {
      IMCServiceInterface imc = getInterface( server ) ;

      imc.saveDoc(meta_id,user,doc,roles,user_rights) ;
      }
    */
    public static ExternalDocType isExternalDoc( String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.isExternalDoc(meta_id, user) ;
    }
    public static void sqlUpdateProcedure( String server, String sqlStr ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.sqlUpdateProcedure(sqlStr) ;
    }

    public static int getCounter( String server ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getCounter() ;
    }
    public static void saveNewFrameset( String server, int meta_id, User user, Table doc) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveNewFrameset(meta_id,user,doc) ;
    }

    public static int incCounter( String server ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.incCounter() ;
    }

    public static boolean userIsAdmin( String server, int meta_id, User user ) throws IOException {
	return checkDocAdminRights(server, meta_id, user, 65536) ;
    }

    public static void saveText( String server, int meta_id, User user, int txt_no, String text, int toHTMLSpecial) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveText(meta_id,user,txt_no,text,toHTMLSpecial) ;
    }

    public static User verifyUser( String server, LoginUser login_user, String[] fieldNames) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.verifyUser(login_user,fieldNames) ;
    }

    public static String[] sqlProcedure( String server, String procedure ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlProcedure(procedure) ;
    }

    public static void addExistingDoc( String server, int meta_id, User user, int existing_meta_id, int doc_menu_no) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.addExistingDoc(meta_id,user,existing_meta_id,doc_menu_no) ;
    }

    public static String listArchive( String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.listArchive(meta_id,user) ;
    }

    public static String getCounterDate( String server ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getCounterDate() ;
    }

    public static void saveImage( String server, int meta_id, User user, int img_no, Image image ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveImage(meta_id,user,img_no,image) ;
    }

    public static void saveUrlDoc( String server, int meta_id, User user, Table doc ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveUrlDoc(meta_id,user,doc) ;
    }
    /*
      public static void insertNewImages( String server, int meta_id, User user, int no_of_img ) throws IOException {
      IMCServiceInterface imc = getInterface( server ) ;

      imc.insertNewImages(meta_id,user,no_of_img) ;
      }
    */
    public static String sqlProcedureStr( String server, String procedure ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlProcedureStr(procedure) ;
    }
    public static String parseDoc( String server, String htmlStr, Vector variables ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.parseDoc(htmlStr,variables) ;
    }
    public static String parseDoc( String server, String htmlStr, Vector variables, Vector data ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.parseDoc(htmlStr,variables,data) ;
    }
    public static String[] sqlQuery( String server, String sqlQuery ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlQuery(sqlQuery) ;
    }
    public static String[] sqlQuery( String server, String sqlQuery, String catalog ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlQuery(sqlQuery,catalog) ;
    }
    public static void saveManualSort( String server, int meta_id, User user, Vector childs, Vector sort_no) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveManualSort(meta_id,user,childs,sort_no) ;
    }

    public static String sqlQueryStr( String server, String sqlQuery ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlQueryStr(sqlQuery) ;
    }

    public static void updateTrackLog( String server, int meta_id, int to_meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.updateTrackLog(meta_id,to_meta_id,user) ;
    }
    public static void saveNewUrlDoc( String server, int meta_id, User user, Table doc) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveNewUrlDoc(meta_id,user,doc) ;
    }
    public static String isFramesetDoc( String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.isFramesetDoc(meta_id,user) ;
    }
    public static void deleteChilds( String server, int meta_id, int doc_menu_no, User user, String[] childsThisMenu) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.deleteChilds(meta_id,doc_menu_no,user,childsThisMenu) ;
    }
    public static void archiveChilds( String server, int meta_id, User user, String[] childsThisMenu) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.archiveChilds(meta_id,user,childsThisMenu) ;
    }

    public static void copyDocs( String server, int meta_id, int doc_menu_no,  User user, String[] childsThisMenu) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.copyDocs(meta_id,doc_menu_no,user,childsThisMenu) ;
    }

    public static Vector searchDocs( String server, int meta_id, User user, String question_str, String search_type, String string_match, String search_area ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.searchDocs(meta_id,user,question_str,search_type,string_match,search_area) ;
    }
    public static Table isUrlDoc( String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.isUrlDoc(meta_id,user) ;
    }
    public static void saveTextDoc( String server, int meta_id, User user, Table doc ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveTextDoc(meta_id,user,doc) ;
    }
    /*
      public static int saveNewDoc( String server, int meta_id, User user, Table doc, int doc_menu_no, int[] roles, int[] user_rights ) throws IOException {
      IMCServiceInterface imc = getInterface( server ) ;

      return imc.saveNewDoc(meta_id,user,doc,doc_menu_no,roles,user_rights) ;
      }
    */
    public static int setCounter( String server, int value ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.setCounter(value) ;
    }

    public static int getDefaultHomePage( String server ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getDefaultHomePage() ;
    }

    public static void inActiveChild( String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.inActiveChild(meta_id,user) ;
    }
    /*
      public static String interpretTemplate( String server, int meta_id, User user ) throws IOException {
      IMCServiceInterface imc = getInterface( server ) ;

      return imc.interpretTemplate(meta_id,user) ;
      }
    */
    public static boolean setCounterDate( String server, String date ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.setCounterDate(date) ;
    }

    public static String[] sqlQueryExt ( String server, String sqlQuery ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlQueryExt(sqlQuery) ;
    }

    public static Hashtable sqlQueryHash ( String server, String sqlQuery ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlQueryHash(sqlQuery) ;
    }

    public static Hashtable sqlProcedureHash ( String server, String sqlQuery ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlProcedureHash(sqlQuery) ;
    }

    public static void deleteDocAll ( String server, int meta_id, imcode.server.User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.deleteDocAll(meta_id,user) ;
    }

    public static String parseDoc( String server, Vector variables, String file_name, String lang_prefix) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.parseDoc(variables,file_name,lang_prefix) ;
    }

    public static File getTemplateHome ( String server ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getTemplateHome() ;
    }

    public static int getDocType ( String server, int meta_id ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getDocType(meta_id) ;
    }

    public static int saveTemplate ( String server, String name, String file_name, byte[] data, boolean overwrite, String language ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.saveTemplate(name,file_name,data,overwrite,language) ;
    }

    public static Object[] getDemoTemplate (String server, int template_id) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getDemoTemplate(template_id) ;
    }

    public static byte[] getTemplate (String server, int template_id) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getTemplate(template_id) ;
    }

    public static void deleteTemplate (String server, int template_id) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.deleteTemplate(template_id) ;
    }

    /*
      public static void changeTemplateName (String server, int template_id, String name) throws IOException {
      IMCServiceInterface imc = getInterface( server ) ;

      imc.changeTemplateName(template_id,name) ;
      }
    */
    public static int saveDemoTemplate (String server, int template_id, byte[] file, String suffix) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.saveDemoTemplate(template_id,file, suffix) ;
    }
    /*
      public static void assignTemplate (String server, int template_id, int[] group_id) throws IOException {
      IMCServiceInterface imc = getInterface( server ) ;

      imc.assignTemplate(template_id,group_id) ;
      }
    */
    /*
      public static void unAssignTemplate (String server, int template_id, int[] group_id) throws IOException {
      IMCServiceInterface imc = getInterface( server ) ;

      imc.unAssignTemplate(template_id,group_id) ;
      }
    */
    public static void saveTemplateGroup (String server, String name, User user) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.saveTemplateGroup(name,user) ;
    }
    public static void deleteTemplateGroup (String server, int group_id) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.deleteTemplateGroup(group_id) ;
    }
    public static void changeTemplateGroupName (String server, int group_id, String name) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.changeTemplateGroupName(group_id,name) ;
    }
    public static java.util.Date getCurrentDate ( String server ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getCurrentDate() ;
    }
    public static String[][] sqlProcedureMulti ( String server, String sqlStr ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlProcedureMulti(sqlStr) ;
    }
    public static String[][] sqlQueryMulti ( String server, String sqlStr ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.sqlQueryMulti(sqlStr) ;
    }
    public static boolean checkDocRights (String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.checkDocRights(meta_id,user) ;
    }
    public static boolean checkDocAdminRightsAny (String server, int meta_id, User user, int permissions ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.checkDocAdminRightsAny(meta_id,user,permissions) ;
    }
    public static boolean checkDocAdminRights (String server, int meta_id, User user,int permissions ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.checkDocAdminRights(meta_id,user,permissions) ;
    }
    public static boolean checkDocAdminRights (String server, int meta_id, User user ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.checkDocAdminRights(meta_id,user) ;
    }
	public static int getUserHighestPermissionSet (String server, int meta_id, int user_id ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getUserHighestPermissionSet(meta_id,user_id) ;
    }
    public static int deleteDemoTemplate (String server, int template_id ) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.deleteDemoTemplate(template_id) ;
    }
    public static String[] getDemoTemplateList (String server) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getDemoTemplateList() ;
    }

    public static byte[] parsePage (String server,int meta_id,User user,int flags,ParserParameters paramsToParse) throws IOException {
		IMCServiceInterface imc = getInterface( server ) ;
	    return imc.parsePage(meta_id,user,flags,paramsToParse) ;
    }

    public static String getMenuButtons (String server,int meta_id, User user) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getMenuButtons(meta_id,user) ;
    }

    public static String getMenuButtons (String server,String meta_id, User user) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getMenuButtons(meta_id,user) ;
    }

    /** getLanguage. Returns the language prefix for a language_id.
     *  Example: If the id for the swedish language is 1=se.
     *  Then the procedure call getLangPrefixFromID("1") will return 'se'
     */
    public static String getLangPrefixFromId (String server, String lang_id_nbr) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getLanguage(lang_id_nbr) ;

    }

    public static String getLanguage (String server) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getLanguage() ;
    }

    public static SystemData getSystemData (String server) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getSystemData() ;
    }

    public static void setSystemData (String server, SystemData sd) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    imc.setSystemData(sd) ;
    }

    /**
     *
     **/
    public static Hashtable ExistingDocsGetMetaIdInfo (String server,String[] meta_id) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.ExistingDocsGetMetaIdInfo(meta_id) ;
    }


    public static String[] getDocumentTypesInList(String server,String langPrefixStr)  throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getDocumentTypesInList(langPrefixStr) ;
    }

    public static Hashtable getDocumentTypesInHash (String server,String langPrefixStr)  throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getDocumentTypesInHash(langPrefixStr) ;
    }

    public static boolean checkUserDocSharePermission(String server, User user, int meta_id) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.checkUserDocSharePermission(user,meta_id) ;
    }

    public static String getInclude(String server,String path) throws IOException {
	IMCServiceInterface imc = getInterface( server ) ;

	    return imc.getInclude(path) ;
	
    }

}

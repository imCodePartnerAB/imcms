package imcode.external.diverse ;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.* ;
import java.net.* ;
import imcode.server.* ;
import imcode.util.* ;

public class RmiConf implements IMCConstants {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    static Hashtable interfaces = new Hashtable() ;	// Keeps track of servers. "ip:port"=interface
    static Hashtable paths = new Hashtable() ;	    // Keeps track of paths. "server"=interface


    // ******************* TEMPLATE FOLDER FUNCTIONS ****************


    /**
     * Checks whether an user is administrator for a meta id or not, Returns true if the user has
     * rights to administrate the document, and and false if he is not
     */
    public static boolean checkAdminRights(IMCServiceInterface imcref, String metaId, imcode.server.User user)  {
	boolean admin = false ;
	int newMetaId = Integer.parseInt(metaId) ;

	// Lets check if the user is an admin
	return imcref.checkDocAdminRights(newMetaId, user, PERM_EDIT_HEADLINE) ;
    } // checkAdminRights


    /**
     * Returns the imageHomefolder from the imcserver.cfg file.
     * Example :D:\apache\
     */
    public static String getExternalImageFolder(IMCServiceInterface imcref, String metaId ) throws IOException {
	File imageFolder = new File(imcref.getImageUrl()) ;
	imageFolder = new File(imageFolder, imcref.getLanguage()) ;
	imageFolder = new File(imageFolder, ""+imcref.getDocType(Integer.parseInt(metaId))) ;
	return imageFolder.toString() ;
    } // end getExternalImageHomeFolder

} // End class

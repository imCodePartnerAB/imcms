package imcode.util ;

import javax.servlet.http.* ;
import javax.servlet.http.HttpUtils ;
import java.io.* ;
import java.util.* ;


import org.apache.log4j.Category;

public class Utility {

    private static Hashtable mimetypes ;

    private static Category log = Category.getInstance( "server" ) ;

    static {
	// Read an apache style mime-types file (mime	ext1 ext2 ext3)
	// and invert it into a hashtable with the extensions as keys.
	mimetypes = new Hashtable() ;
	try {
	    String mime_types_path = Prefs.get("mime.types","servlet.cfg") ;
	    if (mime_types_path != null) {
		Properties mt = Prefs.getProperties(new File(mime_types_path)) ;
		Enumeration enum = mt.propertyNames() ;
		while ( enum.hasMoreElements() ) {
		    String mime = (String)enum.nextElement() ;
		    StringTokenizer file_exts = new StringTokenizer(mt.getProperty(mime)," \t") ;
		    while ( file_exts.hasMoreTokens() ) {
			mimetypes.put(file_exts.nextToken(), mime.toLowerCase()) ;
		    }
		}
	    }
	} catch ( IOException ex ) {
	    log.error("Unable to load mime-types-file",ex) ;
	}
    }

    private Utility () {

    }

    /**
       Takes a path-string and returns a file. The path is prepended with the webapp dir if the path is relative.
    **/
    public static File getAbsolutePathFromString(String pathString) {
	File path = new File(pathString) ;
	if (!path.isAbsolute()) {
	    path = new File(imcode.server.WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath(), pathString) ;
	}
	return path ;
    }

    /**
       Fetches a preference from the config file for a domain,
       as a File representing an absolute path, with the webapp dir prepended if the path is relative.
       @param pref The name of the preference to fetch.
       @param domain The domain, as it appears in the main domain-config-file.
    */
    public static File getDomainPrefPath(String pref, String domain) throws IOException {
	return getAbsolutePathFromString(getDomainPref(pref,domain)) ;
    }

    /**
       Fetches a preference from the config file for a domain.
       @param pref The name of the preference to fetch.
       @param domain The domain, as it appears in the main domain-config-file.
    */
    public static String getDomainPref(String pref, String domain) throws IOException {
	// Remove port-number from host.
	int index = domain.indexOf(":") ;
	if ( index != -1 ) {
	    domain = domain.substring(0,index) ;
	}
	domain = domain.trim() ;

	try {
	    StringTokenizer st = new StringTokenizer(Prefs.get("domains","servlet.cfg"),", ") ;
	    while ( st.hasMoreTokens() ) {
		if (st.nextToken().trim().equals(domain)) {
		    return Prefs.get(pref, Prefs.get(domain+".properties","servlet.cfg"));
		}
	    }
	} catch (NullPointerException ignored) {
	    try {
		return Prefs.get(pref, Prefs.get("default","servlet.cfg"));
	    } catch (NullPointerException ex) {
		throw new IOException ("\"domains\", \""+domain+".properties\", or \"default\" not found in servlet.cfg!") ;
	    }
	}
	try {
	    return Prefs.get(pref, Prefs.get("default","servlet.cfg"));
	} catch (NullPointerException ex) {
	    throw new IOException ("Domain \""+domain+"\" or \"default\" not found in servlet.cfg!") ;
	}
    }

    /**
       Redirects to a local URI. Considered relative unless prefixed with "/".
       @param req The request to get the redirect data from.
       @param res The response to use for redirecting.
       @param uri The URI to redirect to. Relative unless prefixed with "/".
    */
    public static void redirect ( HttpServletRequest req, HttpServletResponse res, String uri )  throws IOException {
	StringBuffer requrl = HttpUtils.getRequestURL(req) ;
	if ( uri.startsWith("/") ) {
	    int slash = requrl.toString().indexOf("://") + 3;
	    slash = requrl.toString().indexOf("/",slash) ;
	    requrl.replace(slash,requrl.length(),uri) ;
	} else {
	    int slash = requrl.toString().lastIndexOf("/") + 1;
	    requrl.replace(slash,requrl.length(),uri) ;
	}
	res.sendRedirect(requrl.toString()) ;
    }

    /**
     * Transforms a long containing an ip into a String.
     */
    public static String ipLongToString (long ip) {
	return ((ip >>> 24) & 255) +"."+ ((ip >>> 16) & 255) +"."+ ((ip >>> 8) & 255) +"."+ (ip & 255) ;
    }

    /**
     * Transforms a String containing an ip into a long.
     */
    public static long ipStringToLong (String ip) {
	long ipInt = 0;
	StringTokenizer ipTok = new StringTokenizer(ip,".") ;
	for ( int exp = 3 ; ipTok.hasMoreTokens() ; --exp ) {
	    int ipNum = Integer.parseInt(ipTok.nextToken()) ;
	    ipInt += ( ipNum * Math.pow(256,exp)) ;
	}
	return ipInt ;
    }

    /**
       Make a HttpServletResponse non-cacheable
    **/
    public static void setNoCache(HttpServletResponse res) {
	res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
	res.setHeader("Pragma","no-cache;") ;
    }

    public static String joinArray(String[] array, String separator) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (0 != i) {
                result.append(separator) ;
            }
            result.append(array[i]);
        }
        return result.toString();
    }
}

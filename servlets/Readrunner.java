import imcode.server.parser.* ;
import imcode.server.* ;
import imcode.util.* ;

import java.io.* ;
import java.util.* ;

import javax.servlet.* ;
import javax.servlet.http.* ;

import org.apache.oro.text.regex.* ;
import org.apache.log4j.* ;

public class Readrunner extends HttpServlet {

    private static Category log = Logger.getInstance( Readrunner.class.getName() ) ;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String start_url = imcref.getStartUrl() ;
	String host = req.getHeader("host") ;
	File   readrunnerPath = Utility.getDomainPrefPath("readrunner_preparsed_path",host) ;
	String readrunnerUrl = Utility.getDomainPref("readrunner_preparsed_url",host) ;

	User user ;
	if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
	    return ;
	}

	int metaId = Integer.parseInt(req.getParameter("meta_id")) ;

	if (!imcref.checkDocRights(metaId,user)) {
	    // User does not have permission to see the given document.
	    return ;
	}

	String theText = req.getParameter("text") ;
	if (null == theText) {
	    theText = "" ;
	}

	ReadrunnerParameters readrunnerParameters = getReadrunnerParameters(req) ;
	ReadrunnerFilter readrunnerFilter = new ReadrunnerFilter() ;

	String theFilteredText = readrunnerFilter.filter(theText,new Perl5Matcher(), readrunnerParameters) ;

	String[] vp = new String[] {
	    "&",   "&amp;",
	    "<",   "&lt;",
	    ">",   "&gt;",
	    "\"",  "&quot;",
	} ;

	String theHtmlEscapedText = Parser.parseDoc(theText,vp) ;

	Vector vec = new Vector() ;
	vec.add("#host#") ;           vec.add(host) ;
	vec.add("#meta_id#") ;        vec.add(""+metaId) ;
	vec.add("#text#") ;           vec.add(theHtmlEscapedText) ;
	vec.add("#readrunnertext#") ; vec.add(theFilteredText) ;
	vec.add("#quotecount#") ;     vec.add(""+readrunnerFilter.getReadrunnerQuoteSubstitutionCount()) ;

	PatternCompiler patComp  = new Perl5Compiler() ;
	Pattern textTagPattern = null ;
	try {
	    textTagPattern   = patComp.compile("#text\\d+#") ;
	} catch (MalformedPatternException ignored) {
	    // ignored, there's nothing wrong with the pattern
	}

	PatternMatcher  patMat   = new Perl5Matcher() ;
	Map textMap              = new IMCTextMap(imcref,metaId) ;
	MapSubstitution mapSubst = new MapSubstitution(textMap,true) ;

	String theReadrunnedPage = imcref.parseDoc(vec,"readrunner/template.html", user.getLangPrefix()) ;

	// Replace tags of the form "#text1#" with the corresponding text from the document we came from.
	theReadrunnedPage = Util.substitute(patMat,textTagPattern,mapSubst,theReadrunnedPage,Util.SUBSTITUTE_ALL) ;

	// Check if we want to download
	if (null != req.getParameter("download")) {
	    res.setHeader("Content-Disposition", "attachment;filename=\"readrunner.html\"") ;
	    res.setContentType("text/html");
	    Writer out = res.getWriter() ;

	    out.write(theReadrunnedPage) ;
	} else {
	    // Write out the page to a temporary file
	    File tempFile = File.createTempFile("readrunner",".html",readrunnerPath) ;

	    Writer fileOut = new FileWriter(tempFile) ;
	    fileOut.write(theReadrunnedPage) ;
	    fileOut.close() ;

	    // and redirect to the temporary file
	    res.sendRedirect(readrunnerUrl+tempFile.getName()) ;
	}
    }


    public static ReadrunnerParameters getReadrunnerParameters(HttpServletRequest req) {
	Cookie[] cookies = req.getCookies() ;
	ReadrunnerParameters readrunnerParameters = new ReadrunnerParameters(false, false) ;

	// Find a readrunner-cookie and extract readrunner-info from it.
	for (int i = 0; cookies != null && i < cookies.length; ++i) {
	    Cookie aCookie = cookies[i] ;
	    if ("RRsettings".equals(aCookie.getName())) {
		log.debug("Found Readrunner-cookie 'RRsettings', with value '"+aCookie.getValue()+"'");

		String[] arrSettings = split(aCookie.getValue(),'&') ;
		if (arrSettings.length >= 3) {  // We want the second and third token.
		    boolean stopCheck = "true".equalsIgnoreCase(split(arrSettings[1],'/')[0]) ;
		    boolean stopVal   = !"0".equals(split(arrSettings[1],'/')[1]) ;
		    boolean sepCheck =  "true".equalsIgnoreCase(split(arrSettings[2],'/')[0]) ;
		    boolean sepVal   =  !"0".equals(split(arrSettings[2],'/')[1]) ;
		    if (stopCheck && stopVal) {
			readrunnerParameters.setUseStopChars(true) ;
			log.debug("Using stop-chars in readrunner.");
		    }
		    if (sepCheck && sepVal) {
			readrunnerParameters.setUseSepChars(true) ;
			log.debug("Using separator-chars in readrunner.");
		    }
		    break ;
		}
	    }
	}

	if (null != req.getParameter("readrunner_stops")) {
	    readrunnerParameters.setUseStopChars(true) ;
	}
	if (null != req.getParameter("readrunner_separators")) {
	    readrunnerParameters.setUseSepChars(true) ;
	}
	return readrunnerParameters ;
    }

    private static String[] split (String input, char splitChar) {
	StringTokenizer tokenizer = new StringTokenizer(input,""+splitChar) ;
	String[] output = new String[tokenizer.countTokens()] ;
	for (int i = 0; i < output.length; ++i) {
	    output[i] = tokenizer.nextToken() ;
	}
	return output ;
    }

    private class IMCTextMap extends HashMap {

	IMCServiceInterface imcref ;
	int metaId ;

	public IMCTextMap(IMCServiceInterface imcref, int metaId) {
	    this.imcref = imcref ;
	    this.metaId = metaId ;
	}

	public Object get(Object key) {
	    // See if we have the result cached
	    Object result = super.get(key) ;
	    if (null == result) {
		// We didn't have it cached
		String keyStr = (String) key ;
		if (keyStr.startsWith("#text") && keyStr.endsWith("#")) {
		    // To get a text from the db we need three things.
		    // We got the IMCServiceInterface reference
		    // and the metaId for the document.
		    // Now we need to extract the text number from the tag.
		    keyStr = keyStr.substring(5,keyStr.length()-1) ;
		    try {
			int textNo = Integer.parseInt(keyStr) ;
			// Fetch text from database
			IMCText imcText = imcref.getText(metaId,textNo) ;
			if (null != imcText) {
			    result = imcText.toHtmlString() ;
			    // Cache the result for future requests.
			    super.put(key,result) ;
			}
		    } catch (NumberFormatException ignored) {
			// ignored, we return null
		    }
		}
	    }
	    return result ;
	}

    }
}

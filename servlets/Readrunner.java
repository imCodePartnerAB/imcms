import imcode.server.parser.* ;
import imcode.server.* ;
import imcode.server.document.TextDocumentTextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.* ;
import imcode.readrunner.* ;
import java.io.* ;
import java.util.* ;
import java.text.* ;

import javax.servlet.* ;
import javax.servlet.http.* ;

import org.apache.oro.text.regex.* ;
import org.apache.log4j.* ;

public class Readrunner extends HttpServlet {

    private static Category log = Logger.getInstance( Readrunner.class.getName() ) ;

    private final static String EXPIRED_DATE_PAGE = "readrunner/expired_date.html" ;
    private final static String EXPIRED_USES_PAGE = "readrunner/expired_uses.html" ;

    private final static String EXPIRED_DATE_MAIL = "readrunner/expired_date_mail.txt" ;
    private final static String EXPIRED_USES_MAIL = "readrunner/expired_uses_mail.txt" ;

    private final static String READRUNNER_CONFIG = "readrunner.properties" ;

    /** The number of milliseconds in one day **/
    private final long ONE_DAY = 86400000 ;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	String start_url = imcref.getStartUrl() ;

	File   readrunnerPath = Utility.getDomainPrefPath("readrunner_preparsed_path" ) ;
	String readrunnerUrl = Utility.getDomainPref("readrunner_preparsed_url" ) ;

	UserDomainObject user ;
	if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
	    return ;
	}

	int metaId = Integer.parseInt(req.getParameter("meta_id")) ;

	if (!imcref.checkDocRights(metaId,user)) {
	    // User does not have permission to see the given internalDocument.
	    return ;
	}

	// Check if we want to download
	if (null != req.getParameter("download")) {
	    String filename = req.getParameter("unique_id") ;
	    if (null == filename) {
		return ;
	    }
	    File downloadFile = new File(readrunnerPath, filename) ;

	    res.setHeader("Content-Disposition", "attachment;filename=\""+filename+"\"") ;
	    res.setContentType("text/html");

	    Writer out = res.getWriter() ;

	    // Stream the file
	    BufferedReader fileReader = new BufferedReader(new FileReader(downloadFile)) ;
	    int read = 0 ;
	    char[] buf = new char[32768] ;
	    while( -1 != (read = fileReader.read(buf)) ) {
		out.write(buf,0,read) ;
	    }
	    out.flush() ;

	} else {
	    ReadrunnerUserData rrUserData = imcref.getReadrunnerUserData(user) ;

	    // If the max-uses value is 0, the limit is disabled.
	    boolean uses_expired = (0 != rrUserData.getMaxUses()) && (rrUserData.getUses() >= rrUserData.getMaxUses()) ;

	    // We add one day to the expiry-date since it's an inclusive range,
	    // not an exclusive one.
	    // If the expiry-date is null, it is disabled.
	    boolean date_expired = (null != rrUserData.getExpiryDate()) && (new Date().after(new Date(rrUserData.getExpiryDate().getTime()+ONE_DAY))) ;

	    if (uses_expired) {
		Writer out = res.getWriter() ;
		ArrayList tags = new ArrayList(2) ;
		tags.add("#max_uses#") ; tags.add(""+rrUserData.getMaxUses()) ;

		out.write(imcref.parseDoc(tags,EXPIRED_USES_PAGE, user.getLangPrefix())) ;
		return ;
	    } else if (date_expired) {
		Writer out = res.getWriter() ;
		ArrayList tags = new ArrayList(2) ;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
		tags.add("#expiry_date#") ; tags.add(dateFormat.format(rrUserData.getExpiryDate())) ;

		out.write(imcref.parseDoc(tags,EXPIRED_DATE_PAGE, user.getLangPrefix())) ;
		return ;
	    }

	    String theText = req.getParameter("text") ;
	    if (null == theText) {
		theText = "" ;
	    }

	    ReadrunnerParameters readrunnerParameters = getReadrunnerParameters(req) ;
	    ReadrunnerFilter readrunnerFilter = new ReadrunnerFilter() ;

	    // Do the actual readrunner-filtering
	    String theFilteredText = readrunnerFilter.filter(theText,new Perl5Matcher(), readrunnerParameters) ;

	    // Create a temporary file to write to
	    Random rand = new Random() ;  // Use a random number to make it hard to guess the filenames.
	    File tempFile = File.createTempFile("readrunner"+rand.nextInt(),".html",readrunnerPath) ;

        String host = req.getServerName() ;
	    // Set up replacement of a couple of #tags#
	    Vector vec = new Vector() ;
	    vec.add("#host#") ;           vec.add(host) ;
	    vec.add("#meta_id#") ;        vec.add(""+metaId) ;
	    vec.add("#readrunnertext#") ; vec.add(theFilteredText) ;
	    vec.add("#quotecount#") ;     vec.add(""+readrunnerFilter.getReadrunnerQuoteSubstitutionCount()) ;
	    vec.add("#unique_id#") ;      vec.add(tempFile.getName()) ;

	    // Load the template and do the actual replacing
	    String theReadrunnedPage = imcref.parseDoc(vec,"readrunner/template.html", user.getLangPrefix()) ;

	    // Set up replacement of "#text123#"-tags.
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

	    // Replace tags of the form "#text1#" with the corresponding text from the internalDocument we came from.
	    theReadrunnedPage = Util.substitute(patMat,textTagPattern,mapSubst,theReadrunnedPage,Util.SUBSTITUTE_ALL) ;

	    Writer fileOut = new FileWriter(tempFile) ;
	    fileOut.write(theReadrunnedPage) ;
	    fileOut.close() ;

	    rrUserData.setUses(rrUserData.getUses()+1) ;

	    // and redirect to the temporary file
	    res.sendRedirect(readrunnerUrl+tempFile.getName()) ;
	    res.flushBuffer() ;

	    sendWarningMail(imcref, user, host, rrUserData) ;
	    // Update the readrunneruserdata for the user, including number of uses and whether expiry-date-warning has been sent.
	    imcref.setReadrunnerUserData(user,rrUserData) ;
	}
    }

    private void sendWarningMail(IMCServiceInterface imcref, UserDomainObject user, String host, ReadrunnerUserData rrUserData) {
	int max_uses_warning_threshold = rrUserData.getMaxUses() - (int)(rrUserData.getMaxUsesWarningThreshold() * rrUserData.getMaxUses() * 0.01 ) ;
	Date expiry_date_warning_threshold =
	    null != rrUserData.getExpiryDate() && 0 != rrUserData.getExpiryDateWarningThreshold()
	    ? new Date(rrUserData.getExpiryDate().getTime() - (ONE_DAY * rrUserData.getExpiryDateWarningThreshold()))
	    : null ;
	String theMailTemplate = null ;
	if (0 != max_uses_warning_threshold && 0 == rrUserData.getUses() - max_uses_warning_threshold) {
	    // The max-uses-warning-threshold was set and just passed.
	    theMailTemplate = EXPIRED_USES_MAIL ;
	} else if (!rrUserData.getExpiryDateWarningSent() && null != expiry_date_warning_threshold && new Date().after(expiry_date_warning_threshold)) {
	    // Expiry-date-threshold has been set and passed, and no warning has been sent about it yet.
	    theMailTemplate = EXPIRED_DATE_MAIL ;
	    rrUserData.setExpiryDateWarningSent(true) ;
	}

	if (null == theMailTemplate) {
	    log.debug("No need to send warning-mail. Uses: "+rrUserData.getUses()+
		      " Max-uses: "+rrUserData.getMaxUses()+
		      " Uses-threshold: "+max_uses_warning_threshold+
		      " Expiry-date: "+new SimpleDateFormat("yyyy-MM-dd").format(rrUserData.getExpiryDate())+
		      " Date-threshold: "+new SimpleDateFormat("yyyy-MM-dd").format(expiry_date_warning_threshold)
		      ) ;
	    return ;
	}

	imcode.server.SystemData sysData = imcref.getSystemData() ;
	String fromAddress = sysData.getServerMasterAddress() ;
	String toAddress = user.getEmailAddress() ;
	String mailserver = "" ;
	int mailport = 25 ;
	try {
	    String otherToAddresses = Prefs.get("mail-warning-to-addresses", READRUNNER_CONFIG) ;
	    if (null != otherToAddresses) {
		toAddress += " "+otherToAddresses ;
	    }

	    mailserver = Utility.getDomainPref( "smtp_server" );
	    String stringMailPort = Utility.getDomainPref( "smtp_port" );
	    String stringMailtimeout = Utility.getDomainPref( "smtp_timeout" );

	    // Handling of default-values is another area where java can't hold a candle to perl.
	    try {
		mailport = Integer.parseInt( stringMailPort );
	    } catch (NumberFormatException ignored) {
		// Do nothing, let mailport stay at default.
	    }

	    int mailtimeout = 10000 ;
	    try {
		mailtimeout = Integer.parseInt( stringMailtimeout );
	    } catch (NumberFormatException ignored) {
		// Do nothing, let mailtimeout stay at default.
	    }

	    String expiryDateString =
		null != rrUserData.getExpiryDate()
		? new SimpleDateFormat("yyyy-MM-dd").format(rrUserData.getExpiryDate())
		: "" ;

	    Vector tags = new Vector() ;
	    tags.add("#user_first_name#") ;               tags.add(user.getFirstName()) ;
	    tags.add("#user_last_name#") ;                tags.add(user.getLastName()) ;
	    tags.add("#user_full_name#") ;                tags.add(user.getFullName()) ;
	    tags.add("#uses#") ;                          tags.add(""+rrUserData.getUses()) ;
	    tags.add("#max_uses#") ;                      tags.add(""+rrUserData.getMaxUses()) ;
	    tags.add("#max_uses_warning_threshold#") ;    tags.add(""+rrUserData.getMaxUsesWarningThreshold()) ;
	    tags.add("#expiry_date#") ;                   tags.add(expiryDateString) ;
	    tags.add("#expiry_date_warning_threshold#") ; tags.add(""+rrUserData.getExpiryDateWarningThreshold()) ;

	    String theMail = imcref.parseDoc( tags, theMailTemplate, user.getLangPrefix());

	    SMTP smtp = new SMTP(mailserver, mailport, mailtimeout) ;
	    smtp.sendMailWait( fromAddress, toAddress, "Readrunner", theMail ) ;
	    log.debug("Sent mail to "+toAddress) ;
	} catch (IOException ioex) {
	    log.warn("Failed to send mail from '"+fromAddress+"' to '"+toAddress+"' ("+user.getLoginName()+") via " +mailserver+":"+mailport+" - "+ ioex.toString()) ;
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
		    // and the metaId for the internalDocument.
		    // Now we need to extract the text number from the tag.
		    keyStr = keyStr.substring(5,keyStr.length()-1) ;
		    try {
			int textNo = Integer.parseInt(keyStr) ;
			// Fetch text from database
			TextDocumentTextDomainObject imcText = imcref.getText(metaId,textNo) ;
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

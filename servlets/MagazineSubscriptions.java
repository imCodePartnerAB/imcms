import javax.servlet.* ;
import javax.servlet.http.* ;

import java.io.* ;
import java.util.* ;

import imcode.server.* ;
import imcode.util.* ;

import org.apache.log4j.Logger ;

public class MagazineSubscriptions extends HttpServlet {

    private final static String SUBSCRIPTIONS_CONFIG = "subscriptions.config" ;

    private final static String MAIL_FORMAT_TEMPLATE = "magazinesubscriptions/mailformat.txt" ;

    private final static int MAGAZINE_SUBSCRIPTION_USER_FLAG_TYPE = 1 ;

    private static Logger log = Logger.getLogger( MagazineSubscriptions.class.getName() ) ;

    public void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String startUrl	= imcref.getStartUrl() ;
	ServletOutputStream out = res.getOutputStream() ;

	User user = null ;
	/* Check if user logged on */
	if ( (user=Check.userLoggedOn(req,res,startUrl))==null ) {
	    return ;
	}

	String[] mailTags = {
	    "#user_full_name#", user.getFullName(),
	    "#user_email#",     user.getEmailAddress()
	} ;

	StringBuffer theMail = new StringBuffer(Prefs.get("mail-format", SUBSCRIPTIONS_CONFIG)) ;
	theMail = Parser.parseDoc(theMail, mailTags) ;

	Map allFlags           = imcref.getUserFlags(MAGAZINE_SUBSCRIPTION_USER_FLAG_TYPE) ;
	Map previouslySetFlags = imcref.getUserFlags(user,MAGAZINE_SUBSCRIPTION_USER_FLAG_TYPE) ;

	/* Turn the set flags into a set. */
	Set setFlags = new HashSet(Arrays.asList(emptyIfNull(req.getParameterValues("setflag")))) ;

	List flags = Arrays.asList(emptyIfNull(req.getParameterValues("flag"))) ;

	for (Iterator it = flags.iterator(); it.hasNext();) {
	    String currentFlagName = (String)it.next() ;

	    boolean flagSet           = setFlags.contains(currentFlagName) ;
	    boolean flagPreviouslySet = previouslySetFlags.containsKey(currentFlagName) ;

	    /* If flag is set and wasn't previously set, or vice versa */
	    if (flagSet ^ flagPreviouslySet) {
		if (flagSet) {
		    imcref.setUserFlag(user, currentFlagName) ;
		} else {
		    imcref.unsetUserFlag(user, currentFlagName) ;
		}

		Object object = allFlags.get(currentFlagName) ;
		addToMail(theMail, (UserFlag)object, flagSet) ;
	    }
	}

	sendMail(theMail) ;

	/* Where to go next */
	String forwardTo = req.getParameter("next_url") ;

	if (null == forwardTo || "".equals(forwardTo)) {
	    /* or, if there was no "next", back to where we came from */
	    forwardTo = req.getHeader("referer") ;
	}

	/* Forward the request to the given location */
	res.sendRedirect(forwardTo) ;

    }

    /**
       Work around java braindeadness.

       @return an empty array if ary is null, ary otherwise.
    **/
    private String[] emptyIfNull (String[] ary) {
	if (null == ary) {
	    return new String[0] ;
	}
	return ary ;
    }

    /** Build mail **/
    private void addToMail (StringBuffer theMail, UserFlag flag, boolean set) {
	try {
	    String message = Prefs.get( (set ?
					 "mail-begin-subscription-message" :
					 "mail-end-subscription-message"),
					SUBSCRIPTIONS_CONFIG) ;
	    String[] messageTags = {
		"#magazine#", flag.getDescription()
	    } ;
	    message = Parser.parseDoc(message, messageTags) ;

	    theMail.append(message) ;
	} catch (IOException ioex) {
	    log.error("Failed to add to subscription-mail.", ioex) ;
	}
    }

    /** Send mail **/
    private void sendMail (StringBuffer theMail) throws IOException {
	try {
	    String mailFromAddress = Prefs.get("mail-from-address", SUBSCRIPTIONS_CONFIG) ;
	    String mailToAddress   = Prefs.get("mail-to-address",   SUBSCRIPTIONS_CONFIG) ;
	    String mailSubject     = Prefs.get("mail-subject",      SUBSCRIPTIONS_CONFIG) ;

	    String mailServer =  Prefs.get   ( "smtp_server",  IMCConstants.HOST_PROPERTIES );
	    int    mailPort =    Prefs.getInt( "smtp_port",    IMCConstants.HOST_PROPERTIES, 25 );
	    int    mailTimeout = Prefs.getInt( "smtp_timeout", IMCConstants.HOST_PROPERTIES, 10000 );

	    /* Send the mail */
	    SMTP smtp = new SMTP(mailServer,mailPort,mailTimeout) ;
	    smtp.sendMailWait(mailFromAddress, mailToAddress, mailSubject,theMail.toString()) ;
	} catch (IOException ioex) {
	    log.error("Failed to send subscription-mail.", ioex) ;
	}
    }
}

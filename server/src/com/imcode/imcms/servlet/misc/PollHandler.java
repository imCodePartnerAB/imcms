package com.imcode.imcms.servlet.misc;

import javax.servlet.* ;
import javax.servlet.http.* ;

import java.io.* ;
import java.text.* ;
import java.util.* ;


import org.apache.log4j.Logger ;

import imcode.util.net.SMTP;
import imcode.util.poll.*;

import imcode.server.* ;
import imcode.server.document.textdocument.TextDomainObject;

public class PollHandler extends HttpServlet {


    //private final static String MAIL_ITEM_FORMAT = "poll/mailitemformat.txt" ;
    //private final static String MAIL_FORMAT      = "poll/mailformat.txt" ;


    private static Logger log = Logger.getLogger( PollHandler.class.getName() ) ;


    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {


	// Get a reference to ImcmsServices //
        ImcmsServices imcref = Imcms.getServices() ;

	// Get a new PollHandlingSystem
	PollHandlingSystem poll = imcref.getPollHandlingSystem();


	// Get the parameters from request //
	String meta_id = req.getParameter("meta_id");
	Enumeration paramEnum = req.getParameterNames() ;
	String confirmation_template = req.getParameter("confirmation_template");
	String result_template = req.getParameter("result_template");

	//Get PollParameters from db
	String[] poll_param = poll.getPollParameters(meta_id);
	int set_cookie = Integer.parseInt( poll_param[5] );
	int hide_result = Integer.parseInt( poll_param[6] );

	// Check if we are going to save answer from this client.
	// if param 'set_cookie' = 1 then we will only accept answers for this poll(meta_id)
	// one time from a client.

	boolean saveAnswers = true;

    if ( set_cookie == 1 && meta_id != null){

		String cookieName = "imcms.poll" + meta_id;

		Cookie[] cookies = req.getCookies() ;

		// Lets see if we got a pollCookie from client.
    	for (int i = 0; cookies != null && i < cookies.length; ++i) {
			if ( cookieName.equals(cookies[i].getName()) && ("true").equals(cookies[i].getValue()) ){
				saveAnswers = false;
			}
		}

		if ( saveAnswers == true) { // this is first time from this client so lets set new cookie

	    	Cookie resCookie = new Cookie( cookieName, "true" ) ;
	    	resCookie.setMaxAge(31500000) ;
	    	resCookie.setPath("/") ;
	    	res.addCookie(resCookie) ;
	    	//revisits.setRevisitsId(session.getId()) ;
	    	//revisits.setRevisitsDate(sNow) ;
		}

    }else if (set_cookie == 0 && meta_id != null ){
		// Lets delete any poll-cookie if 'set_cookie' == 0

		String cookieName = "imcms.poll" + meta_id;
		Cookie[] cookies = req.getCookies() ;

		// Lets see if we got a pollCookie from client.
    	for (int i = 0; cookies != null && i < cookies.length; ++i) {
			if ( cookieName.equals(cookies[i].getName()) ){
				Cookie currentCookie = new Cookie( cookieName, "false" ) ;
				currentCookie.setMaxAge(0);
				currentCookie.setPath("/") ;
				res.addCookie(currentCookie);
			}
		}
	}



	// Create a TreeMap to hold text answer (question-number, ansertext) that we are going to send as mail
	TreeMap textAnswers = new TreeMap();

	// Create a TreeMap to hold the question text.
	TreeMap textQuestions = new TreeMap();

	if ( saveAnswers == true) {

		// Loop through the parameters //
		while (paramEnum.hasMoreElements() && meta_id != null) {

		    // Get next parameter //
		    String aParameter = (String)paramEnum.nextElement() ;

		    // Check if the parameter looks like one we're interested in //
		    if (aParameter.startsWith("textAnswer")) {  //textAnswer1, optionAnswer1

				//lets store question number and textanswer value that we are going to send as mail to recipients
				int question_no = Integer.parseInt( aParameter.substring("textAnswer".length()));
				String text = req.getParameter(aParameter);
				if( !("").equals(text.trim()) ){
					textAnswers.put(""+question_no, text); //

					//lets store question number and question text that we are going to send as mail to recipients
					String questionText = getText(imcref, Integer.parseInt(meta_id), question_no ); // in this case question_no is equal to text_no
					textQuestions.put(""+question_no, questionText);
				}


			// else lets increase selected options (votes)
			}else if ( aParameter.startsWith("optionAnswer") ) {

				int question_no = Integer.parseInt( aParameter.substring("optionAnswer".length()));
				String[] option_no = req.getParameterValues(aParameter);

				if (option_no != null && question_no > 0){
					for (int i = 0; i < option_no.length; i++){
						poll.increasePollAnswer( meta_id, ""+question_no, option_no[i]);
					}
				}
			}
		} // end while
	}


	String forwardTo = null ;

	if ( meta_id != null){

		// If param 'hide_result' = 1 then we won´t send the result page as response only a confirmation page.
		if ( hide_result == 1 ){
			// send confirmation page to browser
			//forwardTo = "GetDoc?meta_id=" + meta_id + "&template=" + confirmation_template ;
			forwardTo = "../poll/pollResult.jsp?meta_id=" + meta_id + "&template=" + confirmation_template ;

		}else{
		// send result page to browser
			//forwardTo = "GetDoc?meta_id=" + meta_id + "&template=" + result_template ;
			forwardTo = "../poll/pollResult.jsp?meta_id=" + meta_id + "&template=" + result_template ;
		}

		log.debug("Redirecting to "+forwardTo) ;

		// Forward the request to the given location //
		res.sendRedirect(forwardTo) ;

		if(textAnswers != null && textAnswers.size() > 0 && poll_param[8] != null ){
			sendMail( imcref, poll_param, meta_id, textQuestions, textAnswers );
		}
	}

    } // end doPost



    private void sendMail ( ImcmsServices imcref, String[] poll_param, String meta_id,
                            TreeMap textQuestions, TreeMap textAnswers ) throws IOException {

		String mailFromAddress = getText( imcref, Integer.parseInt(meta_id), Integer.parseInt(poll_param[9]) );
		String mailToAddress   = getText( imcref, Integer.parseInt(meta_id), Integer.parseInt(poll_param[8]) ); // comma-separated string;
		String mailSubject     = getText( imcref, Integer.parseInt(meta_id), Integer.parseInt(poll_param[10]) );

		// get poll name
		TextDomainObject poll_name = new TextDomainObject("",0);

		if ( poll_param != null && poll_param.length !=0 && poll_param[1] != null ){
			int text_no = Integer.parseInt( poll_param[1] );
			poll_name = imcref.getText(Integer.parseInt(meta_id), text_no);
		}


		// Create the mail body
		StringBuffer mail = new StringBuffer();

		mail.append( "Enkätsvar \n" );

		// Lets get todays date
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
		Date toDay = new Date() ;
		mail.append( formatter.format(toDay) + "\n" ) ;
		mail.append( "Enkät: " +  poll_name + "\n"  );
		mail.append( "Meta_Id: " + meta_id + "\n" );


		Set answers = textAnswers.entrySet();
		Iterator answersIter = answers.iterator();

		while ( answersIter.hasNext() ){

			Map.Entry questionNo = (Map.Entry)answersIter.next();
			mail.append( "___________________________________________" + "\n" );
			//mail.append( "Fråga " + (String)questionNo.getKey()  + "\n");
			mail.append( "Fråga: \n");

			mail.append(textQuestions.get(questionNo.getKey() )  + "\n\n" );

			mail.append("Svar: \n" +  (String)questionNo.getValue() + "\n");
			mail.append("\n");
		}

		// Send the mail
		SMTP smtp = imcref.getSMTP() ;
		smtp.sendMailWait(mailFromAddress, mailToAddress, mailSubject, mail.toString()) ;
		log.debug("Sending mail to "+mailToAddress);

    } // end of sendMail()


	//Get one text from db
	private String getText( ImcmsServices imcref, int meta_id, int text_no ){

		TextDomainObject text;
		text = imcref.getText(meta_id, text_no);

		if ( text != null ) {
			return text.getText();
		}else{
			return ("");
		}
	}
}



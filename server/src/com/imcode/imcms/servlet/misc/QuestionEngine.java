package com.imcode.imcms.servlet.misc;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.util.fortune.* ;
import imcode.server.* ;
import imcode.server.user.UserDomainObject;

import org.apache.log4j.Category ;

/**
 * @author  Monika Hurtig
 * @version 1.0
 * Date : 2001-09-05
 */

public class QuestionEngine extends HttpServlet
{
    private final static String questionTemplate = "QuestionEngine.htm";

    private static Category log = Category.getInstance(imcode.server.IMCConstants.ERROR_LOG) ;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {


    IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;


    UserDomainObject user = Utility.getLoggedOnUser( req );

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String inFile = req.getParameter("file");
	if (inFile == null) {
	    inFile = "poll" ;
	}

	// Get the current poll
	List pollList = imcref.getPollList(inFile+".current.txt") ;

	Date now = new Date() ;

	if ( pollList.isEmpty() ) {
	    // There was no poll, get one.
	    pollList = this.getNewQuestion(imcref,inFile) ;
	}

	// So... the Poll.
	Poll thePoll = (Poll) pollList.get(0) ;

	if ( ! thePoll.getDateRange().contains( now ) ) {

	    // The poll was no longer current, archive it...
	    if (!"".equals(thePoll.getQuestion())) {
		List pollStatsList = imcref.getPollList(inFile+".stat.txt") ;
		pollStatsList.add(thePoll) ;
		imcref.setPollList(inFile+".stat.txt",pollStatsList) ;
	    }

	    // ... and replace it.
	    pollList = this.getNewQuestion(imcref,inFile) ;
	    thePoll = (Poll)pollList.get(0) ;

	}

	// The question...
	String question = thePoll.getQuestion() ;

	question = HTMLConv.toHTMLSpecial(question) ;

	//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
	Vector values = new Vector();
	values.add("#question#");
	values.add(question);
	values.add("#file#");
	values.add(inFile);

	String parsed = imcref.parseExternalDoc( values, questionTemplate, user, "106");

	out.print(parsed);

	return ;

    } // End doGet



    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
	this.doGet(req,res);
	return ;
    }


    private List getNewQuestion(IMCServiceInterface imcref,String inFile) throws IOException
    {

	List questionList = imcref.getQuoteList(inFile+".poll.txt") ;

	Date date = new Date();
	Iterator qIterator = questionList.iterator() ;

	while ( qIterator.hasNext() ) {
	    Quote aPollQuestion = (Quote)qIterator.next() ;

	    if (aPollQuestion.getDateRange().contains(date)) {
		String questionString = aPollQuestion.getText() ;

		List newPollList = new LinkedList() ;
		newPollList.add(new Poll(questionString,aPollQuestion.getDateRange())) ;
		imcref.setPollList(inFile+".current.txt",newPollList) ;
		return newPollList ;
	    }
	}

	// FIXME: We didn't find a question/poll... what to do, what to do?
	log.error("QuestionEngine: There are no current polls!") ;
	List newPollList = new LinkedList() ;
	DateRange dateRange = new DateRange(new Date(0),new Date(0)) ;
	newPollList.add(new Poll("",dateRange)) ;
	return newPollList ;

    }
} // End class


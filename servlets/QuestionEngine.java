import java.io.*;
import java.util.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.server.HTMLConv ;
import imcode.external.diverse.*;
import imcode.external.chat.*;

import imcode.util.* ;
import imcode.util.fortune.* ;

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

	String host = req.getHeader("Host") ;
	String imcserver = Utility.getDomainPref("userserver",host) ;
	File fortune_path = Utility.getDomainPrefPath("FortunePath",host);

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String inFile = req.getParameter("file");
	if (inFile == null) {
	    inFile = "poll" ;
	}

	// Get the current poll
	List pollList = IMCServiceRMI.getPollList(imcserver,inFile+".current.txt") ;

	Date now = new Date() ;

	if ( pollList.isEmpty() ) {
	    // There was no poll, get one.
	    pollList = this.getNewQuestion(imcserver,inFile) ;
	}

	// So... the Poll.
	Poll thePoll = (Poll) pollList.get(0) ;

	if ( ! thePoll.getDateRange().contains( now ) ) {

	    // The poll was no longer current, archive it...
	    if (!"".equals(thePoll.getQuestion())) {
		List pollStatsList = IMCServiceRMI.getPollList(imcserver,inFile+".stat.txt") ;
		pollStatsList.add(thePoll) ;
		IMCServiceRMI.setPollList(imcserver,inFile+".stat.txt",pollStatsList) ;
	    }

	    // ... and replace it.
	    pollList = this.getNewQuestion(imcserver,inFile) ;
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

	String parsed = IMCServiceRMI.parseExternalDoc(imcserver, values, questionTemplate, IMCServiceRMI.getLanguage(imcserver), "106");

	out.print(parsed);

	return ;

    } // End doGet



    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
	this.doGet(req,res);
	return ;
    }


    public List getNewQuestion(String imcserver,String inFile) throws ServletException, IOException
    {

	List questionList = IMCServiceRMI.getQuoteList(imcserver,inFile+".poll.txt") ;

	Date date = new Date();
	Iterator qIterator = questionList.iterator() ;

	while ( qIterator.hasNext() ) {
	    Quote aPollQuestion = (Quote)qIterator.next() ;

	    if (aPollQuestion.getDateRange().contains(date)) {
		String questionString = aPollQuestion.getText() ;

		List newPollList = new LinkedList() ;
		newPollList.add(new Poll(questionString,aPollQuestion.getDateRange())) ;
		IMCServiceRMI.setPollList(imcserver,inFile+".current.txt",newPollList) ;
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


import java.io.*;
import java.util.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.external.chat.*;

import imcode.util.* ;
import imcode.util.fortune.* ;

/**
 * @author  Monika Hurtig
 * @version 1.0
 * Date : 2001-09-05
 */

public class QuestionEngine extends HttpServlet
{
    String questionTemplate = "QuestionEngine.htm";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

	String host = req.getHeader("Host") ;
	String imcserver = Utility.getDomainPref("userserver",host) ;
	File fortune_path = Utility.getDomainPrefPath("FortunePath",host);

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String inFile = req.getParameter("file");
	if (inFile == null) {
	    inFile = "" ;
	}

	// Get the current poll
	List pollList = IMCServiceRMI.getPollList(imcserver,inFile+"enkatcurrent.txt") ;

	Date now = new Date() ;

	if ( pollList.isEmpty() || !((Poll)pollList.get(0)).getDateRange().contains( now ) ) {
	    // There was no poll, or it wasn't longer current, replace it.
	    pollList = this.getNewQuestion(imcserver,inFile) ;
	}

	// So... the Poll.
	Poll thePoll = (Poll) pollList.get(0) ;

	// The question...
	String question = thePoll.getQuestion() ;

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

	List questionList = IMCServiceRMI.getQuoteList(imcserver,inFile+"enkat.txt") ;

	Date date = new Date();

	Iterator qIterator = questionList.iterator() ;

	while ( qIterator.hasNext() ) {
	    Quote aPollQuestion = (Quote)qIterator.next() ;

	    if (aPollQuestion.getDateRange().contains(date)) {
		String questionString = aPollQuestion.getText() ;

		List newPollList = new LinkedList() ;
		newPollList.add(new Poll(questionString)) ;
		IMCServiceRMI.setPollList(imcserver,inFile+"enkatcurrent.txt",newPollList) ;
		return newPollList ;
	    }
	}

	// We didn't find a question/poll... what to do, what to do?
	List newPollList = new LinkedList() ;
	newPollList.add(new Poll("")) ;
	return newPollList ;

    }
} // End class

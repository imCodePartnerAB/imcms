import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.*;
import imcode.util.fortune.* ;
import imcode.server.* ;

import org.apache.log4j.Category ;

/**
 * @author  Monika Hurtig
 * @version 1.0
 * Date : 2001-09-05
 */

public class QuestionResult extends HttpServlet
{
    private final static String RESULTTEMPLATE = "QuestionResult.htm";

    private static Category log = Category.getInstance(imcode.server.IMCConstants.ERROR_LOG) ;


	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		String host = req.getHeader("Host");
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

		//get answer
		String file = req.getParameter("file");
		String answer = req.getParameter("answer");

		if (file == null) {
		    file = "poll" ;
		}

		if (answer == null) {
		    // FIXME: What to do?
		    return ;
		}

		List currentPollList = imcref.getPollList(file+".current.txt") ;

		if (currentPollList.isEmpty()) {
		    log.error("QuestionResult: No current poll!") ;
		    return ;
		}

		// So... the Poll.
		Poll thePoll = (Poll) currentPollList.get(0) ;

		thePoll.addAnswer(answer) ;

		imcref.setPollList(file+".current.txt",currentPollList) ;

		double totalAnswerCount = thePoll.getTotalAnswerCount() ;

		double yesRatio = (double) thePoll.getAnswerCount("yes") / (double) totalAnswerCount ;
		double noRatio =  (double) thePoll.getAnswerCount("no")  / (double) totalAnswerCount ;

		NumberFormat pf = NumberFormat.getPercentInstance();
		pf.setMaximumFractionDigits(0);

		String yesProcent = pf.format(yesRatio);
		String noProcent = pf.format(noRatio);

		//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
		Vector values = new Vector(8);
		values.add("#question#");
		values.add(thePoll.getQuestion());
		values.add("#yesProcent#");
		values.add(yesProcent);
		values.add("#noProcent#");
		values.add(noProcent);
		values.add("#total#");
		values.add(""+(int)totalAnswerCount);

		String parsed = imcref.parseExternalDoc( values, RESULTTEMPLATE, imcref.getLanguage(), "106");

		res.setContentType("text/html");
		Writer out = res.getWriter();
		out.write(parsed);

		return ;

	} // End doGet

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
	doGet(req,res) ;
    }


} // End class

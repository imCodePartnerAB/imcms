import java.io.*;
import java.util.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.* ;
import imcode.util.* ;
import imcode.util.fortune.* ;
import imcode.server.* ;


public class AdminQuestions extends Administrator  implements imcode.server.IMCConstants {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static String ADMIN_QUESTION_FILE	= "admin_questions_file.html";
    private final static String ADMIN_QUESTION		= "admin_questions.html";
    private final static String QUESTION_RESULT		= "show_questions.html";
    private final static String RESULT_ERR_MSG	= "qustion_result_err_msg.frag";
    private final static String OPTION_LINE		= "option_line.frag";

    private final static long ONE_DAY = 86400000 ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	res.setContentType("text/html");
	Writer out = res.getWriter();

	// Lets get the server this request was aimed for
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	    String header = "Error in AdminQuestions." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	// Lets verify that the user who tries to admin a fortune is an admin
	if (imcref.checkAdminRights(user) == false) {
	    String header = "Error in AdminQuestions." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;
	    this.log(header + msg) ;

	    // Lets get the path to the admin templates folder
	    File templateLib = getAdminTemplateFolder(imcref, user) ;

	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	//get fortunefiles
	String host = req.getHeader("host") ;
	File fortune_path = Utility.getDomainPrefPath("FortunePath",host);
	File files[] = fortune_path.listFiles();

	StringBuffer options = new StringBuffer() ;


	for(int i=0;i<files.length;i++)	{
	    //remove suffixes and create optionstring
	    String filename=files[i].getName() ;
	    int index = filename.lastIndexOf(".");
	    filename=filename.substring(0,index);
	    if ( filename.endsWith(".poll") ){
		options.append("<option value=\""  + filename.substring(0,filename.lastIndexOf(".poll")) + "\">" + filename.substring(0,filename.lastIndexOf(".poll")) + "</option>") ;
	    }
	}


	//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
	Vector values = new Vector();
	values.add("#options#");
	values.add(options.toString());

	String parsed = imcref.parseExternalDoc(values, ADMIN_QUESTION , user.getLangPrefix(), DOCTYPE_FORTUNES+"");
	out.write(parsed);

    } // End doGet

    /**
       doPost
    */
    public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException {
	// Lets get the parameters and validate them, we dont have any own
	// parameters so were just validate the metadata
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	// Lets get the server this request was aimed for
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	imcode.server.User user ;

	// Check if the user logged on
	if ( (user = Check.userLoggedOn(req,res,"StartDoc" )) == null )	{
	    return ;
	}

	HttpSession session = req.getSession();

	String whichFile = req.getParameter("AdminFile");

	if (req.getParameter("back")!=null){
	    res.sendRedirect("AdminManager") ;
	    return;
	}

	if (null == whichFile || "".equals(whichFile)){
	    res.sendRedirect("AdminQuestions") ;
	    return;
	}

	session.setAttribute("file",whichFile);

	if (req.getParameter("result")!=null){

	    StringBuffer buff = new StringBuffer();

	    List list;

	    try {
		list = imcref.getPollList( whichFile + ".stat.txt");
		Iterator iter = list.iterator();
		int counter = 0;
		SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
		while (iter.hasNext()) {
		    Poll poll = (Poll) iter.next();
		    DateRange dates = poll.getDateRange();
		    buff.append("<option value=\""  + counter++ + "\">"+dateForm.format(dates.getStartDate()) +" "+dateForm.format(new Date(dates.getEndDate().getTime()-ONE_DAY))+" "+ poll.getQuestion());
		    Iterator answerIter = poll.getAnswersIterator();
		    while (answerIter.hasNext()) {
			String answer = (String)answerIter.next();
			buff.append(" " + answer + " = ");
			buff.append(poll.getAnswerCount(answer)+" ");
		    }

		    buff.append("</option>");
		}


		//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
		Vector values = new Vector();
		values.add("#options#");
		values.add(buff.toString());

		String parsed = imcref.parseExternalDoc(values, QUESTION_RESULT , user.getLangPrefix(), DOCTYPE_FORTUNES+"");
		out.print(parsed);

		session.setAttribute("results",list);
		return;
	    } catch(NoSuchElementException ex) {
		StringBuffer buff2 = new StringBuffer("<option>");
		buff.append(imcref.parseExternalDoc(null, RESULT_ERR_MSG , user.getLangPrefix(), DOCTYPE_FORTUNES+""));
		buff2.append("</option>");
		Vector values = new Vector();
		values.add("#options#");
		values.add(buff2.toString());
		String parsed = imcref.parseExternalDoc(values, QUESTION_RESULT , user.getLangPrefix(), DOCTYPE_FORTUNES+"");
		out.print(parsed);
		return;
	    }
	} else if (req.getParameter("edit")!=null)	{

	    StringBuffer buff = new StringBuffer();

	    List lines = imcref.getPollList(whichFile+".poll.txt");
	    Iterator iter = lines.iterator();
	    int counter = 0;
	    SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
	    while (iter.hasNext()) {
		Poll poll = (Poll) iter.next();
		DateRange dates = poll.getDateRange();
		buff.append("<option value=\""  + counter++ + "\">"+dateForm.format(dates.getStartDate()) +" "+dateForm.format(new Date(dates.getEndDate().getTime()-ONE_DAY))+" "+ poll.getQuestion() + "</option>");
	    }


	    String date1 = "";
	    String date2 = "";
	    String text  = "";


	    //Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
	    Vector values = new Vector();
	    values.add("#date1#");
	    values.add(date1);
	    values.add("#date2#");
	    values.add(date2);
	    values.add("#text#");
	    values.add(text);
	    values.add("#file#");
	    values.add(whichFile);
	    values.add("#options#");
	    values.add(buff.toString());


	    String parsed = imcref.parseExternalDoc(values, ADMIN_QUESTION_FILE , user.getLangPrefix(), DOCTYPE_FORTUNES+"");
	    out.print(parsed);

	    session.setAttribute("lines",lines);

	    return;
	}

    }


} // End of class

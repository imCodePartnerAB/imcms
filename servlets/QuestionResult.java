import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.*;

/**
 * @author  Monika Hurtig
 * @version 1.0
 * Date : 2001-09-05
 */

public class QuestionResult extends HttpServlet 
{
	String resultTemplate = "QuestionResult.htm";
		
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		String host = req.getHeader("Host");
		String imcServer = Utility.getDomainPref("userserver",host);
		String fortune_path = Utility.getDomainPref("fortune_path",host);
		
		//get answer
		String file = req.getParameter("file");
		String answer = req.getParameter("answer");
		
		//gets the filecontent 
		String resFile = IMCServiceRMI.getFortune(imcServer,file + "current.txt");
		StringTokenizer tokens = new StringTokenizer(resFile,"#");
		
		String date1 = tokens.nextToken();
		String date2 = tokens.nextToken();

		String question = tokens.nextToken();
		
		int yes = Integer.parseInt( ( (tokens.nextToken() ).substring(3) ).trim() );
		int no = Integer.parseInt( ( (tokens.nextToken() ).substring(4) ).trim() );

		//save the answer to the file
		if (Integer.parseInt(answer)==1) 
		{
			yes = yes + 1;
			//++yes;
		} 
		else 
		{
			no = no + 1;
			//++no;
		}
	
		String newFileContent =date1 + "#" + date2 + "#" + question + "#ja: " + yes + "#nej: " + no + "#";
	
		BufferedWriter fileW = new BufferedWriter( new FileWriter(fortune_path + file + "current.txt" ) );
		fileW.write(newFileContent);
		fileW.flush();
		fileW.close();
		
		//get the total number of answers
		int total = yes+no;
		
		//get the %
		double yesDiv = (yes==0) ? 0 : ((double)yes/(double)total);
		double noDiv = (no==0) ? 0 : ((double)no/(double)total);
		
		NumberFormat pf = NumberFormat.getPercentInstance();
		pf.setMaximumFractionDigits(0);
		
		String yesProcent = pf.format(yesDiv);
		String noProcent = pf.format(noDiv);
		
		//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
		Vector values = new Vector();
		values.add("#question#");
		values.add(question);
		values.add("#yesProcent#");
		values.add(yesProcent);
		values.add("#noProcent#");
		values.add(noProcent);
		values.add("#total#");
		values.add(Integer.toString(total));
		
		
		String parsed = IMCServiceRMI.parseExternalDoc(imcServer, values, resultTemplate , "se", "106");
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println(parsed);
		
		return ;

	} // End doGet

	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		doGet(req,res);
		return ;
	}
	
} // End class




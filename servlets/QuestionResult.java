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
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		File fortune_path = Utility.getDomainPrefPath("fortune_path",host);
		
		//get answer
		String answer = req.getParameter("answer");
		
		//get question
		String question = req.getParameter("question");
		String fileName = question;

		String row = req.getParameter("quesrow") ;
		try {
		    fileName = "QuestionResult"+Integer.parseInt(row) ;
		} catch ( NumberFormatException ignored ) {
		    /* The question is used as filename */
		}

		//gå igenom strängen tecken för tecken och byt ut allt utom '_', siffror och bokstäver till '_'.
		for(int i = 0;i<fileName.length(); i++)
		{
			char c = fileName.charAt(i);
			if (!Character.isJavaIdentifierPart(c))
			{
				fileName=fileName.replace(c,'_');
			}
		}
		
		//get current answers from file
		int yes = 0;
		int no = 0;

		File file = new File(fortune_path,fileName.trim() + ".txt");
		if (file.exists())
		{
		    try {
			BufferedReader fileR = new BufferedReader(new FileReader(file));
			
			fileR.skip(3);
			String y = fileR.readLine();
			yes = Integer.parseInt(y.trim());
			
			fileR.skip(4);
			String n = fileR.readLine();
			no = Integer.parseInt(n.trim());
		
			fileR.close();
		    } catch (IOException ignored) {
			// yes = 0, no = 0
		    } catch (NumberFormatException ignored) {
			// yes = 0, no = 0
		    } catch (NullPointerException ignored) {
			// yes = 0, no = 0
		    } 

		}
	
		//save the answer to the file
		BufferedWriter fileW = new BufferedWriter( new FileWriter(file) );
		
		if (Integer.parseInt(answer)==1) {yes++;} else {no++;}
		
		fileW.write("ja: " + yes,0,(Integer.toString(yes)).length()+4);
		fileW.newLine();
		fileW.write("nej: " + no,0,Integer.toString(no).length()+5);
		fileW.newLine();
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




import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.external.chat.*;
import imcode.util.* ;

/**
 * @author  Monika Hurtig
 * @version 1.0
 * Date : 2001-09-05
 */

public class QuotPicEngine extends HttpServlet 
{
	String inFile = "";

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{	
		
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		//get parameters
		String type = req.getParameter("type");
		inFile = req.getParameter("file");
		
		BufferedReader readFile = new BufferedReader( new StringReader( IMCServiceRMI.getFortune(imcServer,inFile + ".txt") ) );
		SimpleDateFormat dateF = new SimpleDateFormat("yyMMdd");

		//collect the correct questions/citat/pictures
		HashMap row_texts = new HashMap(50);
		
		//rownr
		int row = 0;
			
		String line = readFile.readLine();
		
		//out.println("line: " + line + "<br>");

		//get questions
		while (line != null && line.length() != 0 ) //&& !( ( date1.before(date)||date1.equals(date) ) && ( date2.after(date)||date2.equals(date) ) ) )
		{
			//out.println("line: " + line + "<br>");
			StringTokenizer tokens = new StringTokenizer(line,"#");
			try
			{
				//the dates
				Date date1 = dateF.parse(tokens.nextToken());
				Date date2 = dateF.parse(tokens.nextToken());
				Date date = new Date();

				String tempQ = tokens.nextToken();

				if ( ( ( date1.before(date) ) || ( (dateF.format(date1)).equals(dateF.format(date)) ) ) && ( ( date2.after(date) ) || ( (dateF.format(date2)).equals(dateF.format(date)) ) ) )
				{
					row_texts.put( new Integer(row),tempQ );
				}
		  
			}
			catch(ParseException e)
			{
			 	log("ParseException in QuotPicEngine");
			}
	
			row++;
			line = readFile.readLine();
		}

		int max_row = row;

		Collection texts = row_texts.values();
		int nr = texts.size();
		
		//get the text and row to return
		String theText;
		int the_row;
		
		if (!(nr>0))
		{
			//no question was found
			theText = "Ingen text kan visas" ;
			the_row = -1;
		}
		else
		{
			//get one randomised item
			Set rows = row_texts.keySet();
		
			do
			{
				Random random = new Random();
				the_row = random.nextInt(max_row);
			}
			while(!rows.contains(new Integer(the_row)));
	
			theText = (String)row_texts.get(new Integer(the_row));
		}

		if( type.equals("pic"))
		{
			out.println( "<img src=\" " + theText + "\"> ");
		}
		else if(type.equals("quot"))
		{
			out.println(theText );
			
			//raden i filen
			out.println("<input type=\"hidden\" name=\"quotrow\" value=\"" + the_row + "\">");
			out.println("<input type=\"hidden\" name=\"quot\" value=\"" + theText + "\">");
		}
		else 
		{
			out.println( theText );
		}

		return ;

	} // End doGet

	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		this.doGet(req,res);
		return ;
	}
	
	
	


	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str) 
	{
		super.log(str) ;
	}


} // End class




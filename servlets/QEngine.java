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

public class QEngine extends HttpServlet 
{


	String inFile = "";

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		
		//get todays date
		Date theDate = new Date();
			
		GregorianCalendar cal = new GregorianCalendar();
		
		int year = cal.get(Calendar.YEAR) - 2000;
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		int date = year*10000+(month+1)*100+day;
			
		//get parameters
		String type = req.getParameter("type");
		inFile = req.getParameter("file");
		
		//gets the filecontent 
		String resFile = IMCServiceRMI.getFortune(imcServer,inFile);
		
		//collect the correct questions/citat/pictures
		HashMap row_texts = new HashMap(50);
		
		int row = 0;	
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		
		//the dates
		int bIndex = 0;
		int eIndex = 0;
		
		while ( resFile.indexOf((int)'#',bIndex) != -1)
		{
			eIndex = resFile.indexOf((int)'#',bIndex);
		
			int date1 = Integer.parseInt(resFile.substring(bIndex,eIndex));
			bIndex = eIndex + 1;
				
			eIndex = resFile.indexOf((int)'#',bIndex);
			int date2 = Integer.parseInt(resFile.substring(bIndex,eIndex));
						
			bIndex = eIndex + 1;
		
			eIndex = resFile.indexOf((int)'#',bIndex);
					
			if ( date1 <= date && date2 >= date)
			{
				row_texts.put(new Integer(row),resFile.substring(bIndex,eIndex));
			}
			
			bIndex = eIndex + 3;
			row++;
		}	

		int max_row = row;
		

		Collection texts = row_texts.values();
		int nr = texts.size();
		
		//get the text and row to return
		String theText;
		int the_row; 
		if (!(nr>0))
		{
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
			out.println( theText );
			
			//raden i filen
			out.println("<input type=\"hidden\" name=\"quotrow\" value=\"" + the_row + "\">");
			out.println("<input type=\"hidden\" name=\"quot\" value=\"" + theText + "\">");
		}
		else if(type.equals("ques"))
		{
			out.println( theText );
			
			out.println("<input type=\"hidden\" name=\"quesrow\" value=\"" + the_row + "\">");
			out.println("<input type=\"hidden\" name=\"question\" value=\"" + theText + "\">");
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




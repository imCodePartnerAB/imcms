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
		log("B:theDate: " + theDate);
		
		GregorianCalendar cal = new GregorianCalendar();
		
		int year = cal.get(Calendar.YEAR) - 2000;
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		int date = year*10000+(month+1)*100+day;
		log("dateD: " + date );
		
		//get parameters
		String type = req.getParameter("type");
		inFile = req.getParameter("file");
		
		//gets the filecontent 
		String resFile = IMCServiceRMI.getInclude(imcServer,inFile);
		
		//collect the correct questions/citat/pictures
		HashMap row_texts = new HashMap();
		
		int row = 0;	
		
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

		Collection texts = row_texts.values();
		int nr = texts.size();

		//get one randomised item
		Set rows = row_texts.keySet();

		do
		{
			Random random = new Random();
			row = random.nextInt(row+1);
		}
		while(!rows.contains(new Integer(row)));
		
		String theText = (String)row_texts.get(new Integer(row));

		if( type.equals("pic"))
		{
			out.println( "<img src=\" " + theText + "\"> ");
		}
		else if(type.equals("quot"))
		{
			out.println( theText );
			
			//raden i filen
			out.println("<input type=\"hidden\" name=\"quotrow\" value=\"" + row + "\">");
			out.println("<input type=\"hidden\" name=\"quot\" value=\"" + theText + "\">");
		}
		else if(type.equals("ques"))
		{
			out.println( theText );
			
			out.println("<input type=\"hidden\" name=\"quesrow\" value=\"" + row + "\">");
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
		System.out.println("VeckansFraga: " + str ) ;
	}


} // End class




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
		String file = new String("test.txt");
	
		BufferedWriter fileW = new BufferedWriter( new FileWriter(file) );
		
	
		
		fileW.write("ja: " );
		fileW.newLine();
	
		fileW.flush();
		fileW.close();
	/*
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
		
		BufferedReader file = new BufferedReader(new FileReader(inFile));
		
		//collect the correct questions/citat/pictures
		String[] txt = new String[20];
				
		char[] date1 = new char[5];
		char[] date2 = new char[5];
		
		file.read();
		
		int nr = 0;	
		while ( file.read(date1,0,5) != -1 )	
		{
			file.read();
			file.read();
			
			file.read(date2,0,5);
			
			int date1i = Integer.parseInt( new String(date1));
			int date2i = Integer.parseInt( new String(date2));
				
			log("date1i: " + date1i);
			log("date2i: " + date2i );
				
			if ( date >= date1i && date <= date2i )
			{
				file.read(); 
				txt[nr] = file.readLine();
				nr++;
			}
			else
			{
				file.readLine();
			}
			
			file.read();
		}	
		
		//get one randomised item
		int selected = 0;
		if (nr>1)
		{
			Random random = new Random();
			selected = random.nextInt(nr);
		}
		
		//return the selected item
		PrintWriter out = res.getWriter();
		*/
		
	/*	out.println("<html>");
		out.println("<head>");
		out.println("<title>testMaBra</title>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		out.println("</head>");
		out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
		out.println("<P>M:test av " + type + " File: " + inFile + "</P>");
	*/	
	/*	if( type.equals("pic"))
		{
			out.println("<img src=" + txt[selected] + ">");
//FIX storlek på bilden?+ " width=\"300\" height=\"400\">");
		}
		else
		{
			out.println( txt[selected] );
		}
		
		if (type.equals("ques"))
		{
			String path = this.getServletPath(req);
			out.println("<form name=\"answer\" method=\"post\" action=\"" + path + "QEngine\">");
			out.println("<input type=\"hidden\" name=\"question\" value=\"" + txt[selected] + "\">");
			out.println("<table width=\"50%\" height=\"100\">");
			out.println("<tr>");
			out.println("<td height=\"30\" width=\"25%\"><input type=\"radio\" name=\"answer\" value=\"yes\" ></td>");
			out.println("<td height=\"30\" width=\"23%\" valign=\"middle\">ja</td>");
			out.println("<td height=\"30\" width=\"23%\"><input type=\"radio\" name=\"answer\" value=\"no\" ></td>");
			out.println("<td height=\"30\" width=\"29%\" valign=\"middle\">nej </td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<div align=\"center\"><input type=\"submit\" name=\"ok\" value=\"ok\"></div>");
			out.println("</form>");
		}
		
	/*	out.println("</body>");
		out.println("</html>");
		
	*/	
	
	
		return ;

	} // End doGet

	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		log("PostM");
			
		//get answer
		String answer = req.getParameter("answer");
		log("Answer: " + answer);
		
		//get question
		String question = req.getParameter("question");
		log("question: " + question );
		
		//gå igenom strängen tecken för tecken och byt ut allt utom '_', siffror och bokstäver till '_'.
		for(int i = 0;i<question.length(); i++)
		{
			char c = question.charAt(i);
			if (!Character.isJavaIdentifierPart(c))
			{
				question=question.replace(c,'_');
			}
		}
		
		
		//hämta aktuellt värde
		
		int yes = 0;
		int no = 0;
		
		//get current library
		char searched = '\\';
		int index = inFile.lastIndexOf((int)searched);
		
		String fileLib = inFile.substring(0,index+1);
		
		//FIX ta bort D:\\
		File file = new File(fileLib + question.trim() + ".txt");
		if (file.exists())
		{
			BufferedReader fileR = new BufferedReader(new FileReader(file));
			
			fileR.skip(3);
			String y = fileR.readLine();
			yes = Integer.parseInt(y.trim());
			
			fileR.skip(4);
			String n = fileR.readLine();
			no = Integer.parseInt(n.trim());
		
			fileR.close();
		}
		
		
		
		//spara svaret till fil
		BufferedWriter fileW = new BufferedWriter( new FileWriter(file) );
		
		if (answer.equals("yes")) {yes++;} else {no++;}
		
		fileW.write("ja: " + yes,0,(Integer.toString(yes)).length()+4);
		fileW.newLine();
		fileW.write("nej: " + no,0,Integer.toString(no).length()+5);
		fileW.newLine();
		fileW.flush();
		fileW.close();
		
		int total = yes+no;
		
		//get the %
		double yesDiv = (yes==0) ? 0 : ((double)yes/(double)total);
		double noDiv = (no==0) ? 0 : ((double)no/(double)total);
		
		NumberFormat pf = NumberFormat.getPercentInstance();
		pf.setMaximumFractionDigits(2);
		
		String yesProcent = pf.format(yesDiv);
		String noProcent = pf.format(noDiv);
		
		//rita svarssidan
		PrintWriter out = res.getWriter();
		
		out.println("ja " + yesProcent );
		out.println("nej " + noProcent);
		
		
		out.println("<html>");
		out.println("<head>");
		out.println("<title>testMaBraSvar</title>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		out.println("</head>");
		out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

		out.println("<table width=\"400\" border=\"0\" height=\"20\" bordercolor=\"#CCCCCC\">");
  		out.println("<tr> ");
		out.println("<td bgcolor=\"#FF0000\" height=\"20\" width=\"" + yesProcent + "\"> </td>");
		out.println("<td bgcolor=\"#0000FF\" height=\"20\" width=\"" + noProcent + "\"> </td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<P> Filelib: " + fileLib + " Andel jasvar: " + yesProcent + " Andel nejsvar: " + noProcent + "</P>");
	
		out.println("</body>");
		out.println("</html>");
		
	/*	
		VariableManager vm = new VariableManager() ;
		Html htm = new  Html();
		vm.addProperty("yesProcent", yesProcent) ;
		vm.addProperty("noProcent", noProcent);
	//	vm.addProperty("yes", Integer.toString(yes )) ;
	//	vm.addProperty("no",Integer.toString(no));
		
	//	vm.addProperty("SERVLET_URL",this.getServletPath(req));
		sendHtml(req,res,vm,"resultatVeckansFraga.htm"); */
		
		
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

	public void sendHtml (HttpServletRequest req, HttpServletResponse res,
		VariableManager vm, String htmlFile) throws ServletException, IOException
	{

//FIX template dynamiskt?
		String templateLib = "C:\\Inetpub\\wwwroot\\maBra\\";
		
		log("templateLib: " + templateLib);
		log("htmlFile: " + htmlFile);
		
		HtmlGenerator htmlObj = new HtmlGenerator(templateLib, htmlFile) ;
		String html = htmlObj.createHtmlString(vm,req) ;
		
		htmlObj.sendToBrowser(req,res,html) ;
	
	}


	public static String getServletPath(HttpServletRequest req)
    throws ServletException, IOException 
	{
        String protocol = req.getScheme();
        String serverName = req.getServerName();
        int p = req.getServerPort();
        String port = (p == 80) ? "" : ":" + p;
        String servletPath = req.getServletPath() ;


        int lastSlash = servletPath.lastIndexOf("/") ;
        if( lastSlash != -1 ) 
		{
            servletPath =  servletPath.substring(0,lastSlash +1) ;
            String url = protocol + "://" + serverName + port + servletPath ;
            return url ;
        }
        return "" ;
    }

} // End class




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

public class QuestionResult extends HttpServlet 
{
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
	
		//get answer
		String answer = req.getParameter("answer");
		
		//get question
		String question = req.getParameter("question");

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
		
	/*	File file;
		int filenr = 0;
		
		do 
		{
			file = new File(Integer.toString(filenr) + ".txt");
			filenr++;
		}
		while(file.exists());*/
		
		File file = new File(question.trim() + ".txt");
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
		
		if (Integer.parseInt(answer)==1) {yes++;} else {no++;}
		
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
		pf.setMaximumFractionDigits(0);
		
		String yesProcent = pf.format(yesDiv);
		String noProcent = pf.format(noDiv);
		
		//rita svarssidan
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		out.println("<html>");
		out.println("<head>");
		out.println("<title>testMaBraSvar</title>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		out.println("<link rel=\"stylesheet\" href=\"../css/mabra.css\" type=\"text/css\">");
		out.println("</head>");
		out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

	/*	out.println("<table width=\"200\" border=\"0\" height=\"20\" bordercolor=\"#CCCCCC\">");
  		out.println("<tr> ");
		out.println("<td bgcolor=\"#FF0000\" height=\"20\" width=\"" + yesProcent + "\"> </td>");
		out.println("<td bgcolor=\"#0000FF\" height=\"20\" width=\"" + noProcent + "\"> </td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<P> Andel jasvar: " + yesProcent + " Andel nejsvar: " + noProcent + "</P>");
		
	*/	
		
		out.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"200\">");
out.println(" <tr align=\"left\" valign=\"top\" bgcolor=\"#999966\"> ");
	out.println("<td colspan=\"3\"><img src=\"../resource/rub_veckan.gif\" width=\"200\" height=\"77\" alt=\"Veckans fr&aring;ga\"></td>");
  out.println("</tr>");
  out.println("<tr align=\"left\" valign=\"top\" bgcolor=\"#999966\"> ");
   out.println(" <td><img src=\"../resource/transpix.gif\" width=\"6\" height=\"6\"></td>");
   out.println(" <td><span class=\"vitbrodtext\">" +  question  +"</span><br>");
    out.println("  <img src=\"../resource/transpix.gif\" width=\"5\" height=\"10\"> ");
   out.println("   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
    out.println("    <tr align=\"left\" valign=\"top\"> ");
    out.println("      <td class=\"vitrubbe\">JA&nbsp; "+ yesProcent +"</td>");
    out.println("    </tr>");
    out.println("    <tr align=\"left\" valign=\"top\">"); 
     out.println("     <td><img src=\"../resource/transpix.gif\" width=\"1\" height=\"5\"></td>");
     out.println("   </tr>");
     out.println("   <tr align=\"left\" valign=\"top\"> ");
     out.println("     <td class=\"vitrubbe\">NEJ "+ noProcent  +"</td>");
     out.println("   </tr>");
     out.println("   <tr align=\"left\" valign=\"top\"> ");
     out.println("     <td><img src=\"../resource/transpix.gif\" width=\"1\" height=\"5\"></td>");
     out.println("   </tr>");
     out.println("   <tr align=\"left\" valign=\"top\"> ");
     out.println("     <td class=\"vitbrodtext\">Antal svarande: " + Integer.toString(total)  +"</td>");
      out.println("  </tr>");
      out.println("</table><br>");
     out.println(" <img src=\"../resource/transpix.gif\" width=\"5\" height=\"10\"><br>");
     out.println(" <a href=\"#\" onclick=\"parent.close();\"><img src=\"../resource/kn_stang.gif\" width=\"49\" height=\"15\" border=\"0\" alt=\"St&auml;ng\"></a> ");
   out.println(" </td>");
    out.println("<td><img src=\"../resource/transpix.gif\" width=\"6\" height=\"6\"></td>");
 out.println(" </tr>");
 out.println(" <tr align=\"left\" valign=\"top\" bgcolor=\"#999966\"> ");
   out.println(" <td><img src=\"../resource/transpix.gif\" width=\"6\" height=\"6\"></td>");
   out.println(" <td><img src=\"../resource/transpix.gif\" width=\"123\" height=\"6\"></td>");
  out.println("  <td><img src=\"../resource/transpix.gif\" width=\"6\" height=\"6\"></td>");
  out.println("</tr>");
out.println("</table>");
		
		
	
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


	} // End doGet

	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		doGet(req,res);
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




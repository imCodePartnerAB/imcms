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

public class QuestionEngine extends HttpServlet 
{

	String questionTemplate = "QuestionEngine.htm";
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		File fortune_path = Utility.getDomainPrefPath("FortunePath",host);
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		//get parameters
		String inFile = req.getParameter("file");
		
		File statisticsFile = new File(fortune_path , inFile + "enkatstatistics.txt");
		statisticsFile.createNewFile();
		
		String question="Ingen text kan visas";
		
		//check if files exists
		File currentFile = new File(fortune_path , inFile + "enkatcurrent.txt");
		if (currentFile.createNewFile() || (IMCServiceRMI.getFortune(imcServer,inFile + "enkatcurrent.txt") ).length() <= 12)
		{
			//om filen inte fanns, skapa den och skriv in aktuell text
			question = this.getNewQuestion(host,imcServer,inFile);
		}
		else
		{
			//gets the filecontent 
			String resFile = IMCServiceRMI.getFortune(imcServer,inFile + "enkatcurrent.txt");
			
			StringTokenizer tokens = new StringTokenizer(resFile,"#");
		
			SimpleDateFormat dateF = new SimpleDateFormat("yyMMdd");
		
			Date date1 = new Date();
		
			Date date2 = new Date();
				
			try
			{
				date1 = dateF.parse(tokens.nextToken());
				date2 = dateF.parse(tokens.nextToken());
				
				//get todays date
				Date date = new Date();
		
				if( ( date1.before(date) || (dateF.format(date1)).equals(dateF.format(date))  ) && ( date2.after(date) || (dateF.format(date2)).equals(dateF.format(date)) ) )
				{
					question = tokens.nextToken();
				}
				else
				{
					//save old question 
					String svarFile = IMCServiceRMI.getFortune(imcServer,inFile + "enkatcurrent.txt");
	
					String file = new String(fortune_path + "\\" + inFile + "enkatstatistics.txt");
					BufferedWriter fileW = new BufferedWriter( new FileWriter(file,true) );
					fileW.write(svarFile);
					fileW.flush();
					fileW.close();
					
					//ta bort den gamla
					String file1 = new String(fortune_path + "\\" + inFile + "enkatcurrent.txt");
					BufferedWriter file1W = new BufferedWriter( new FileWriter(file1) );
					file1W.newLine();
					file1W.flush();
					file1W.close();
					
					//get new question
					question = this.getNewQuestion(host,imcServer,inFile);
				}
			}
			catch(ParseException e)
			{
				//logga
				log("ParseException in QuestionEngine");
				//felsida?
				//läsa/skriva om?

			}
		
		}
				
		//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
		Vector values = new Vector();
		values.add("#question#");
		values.add(question);
		values.add("#file#");
		values.add(inFile);		
		
		String parsed = IMCServiceRMI.parseExternalDoc(imcServer, values, questionTemplate , "se", "106");
		
		out.println(parsed);
		
		return ;

	} // End doGet

	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		this.doGet(req,res);
		return ;
	}	


	public String getNewQuestion(String host, String imcServer,String inFile)
	throws ServletException, IOException
	{
	
		BufferedReader readFile = new BufferedReader( new StringReader( IMCServiceRMI.getFortune(imcServer,inFile + "enkat.txt") ) );
		SimpleDateFormat dateF = new SimpleDateFormat("yyMMdd");
	
		//the dates
		Date date1 = new Date(2);
		Date date2 = new Date(1);
		
		Date date = new Date();
		
		//the question
		String theQuestion = "Ingen text kan visas";
		
		String line = readFile.readLine();

		while ( (line != null) && !( ( date1.before(date) || ( (dateF.format(date1)).equals(dateF.format(date)) ) ) && ( date2.after(date)  || ( (dateF.format(date2)).equals(dateF.format(date)) ) ) ) )
		{	
		
			StringTokenizer tokens = new StringTokenizer(line,"#");
		
			try
			{
				date1 = dateF.parse(tokens.nextToken());
				date2 = dateF.parse(tokens.nextToken());
			}
			catch(ParseException e){}
			
			theQuestion = tokens.nextToken();
			
			line = readFile.readLine();
		
		}
	
	
		if(  !( ( date1.before(date) || ( (dateF.format(date1)).equals(dateF.format(date)) ) ) && ( date2.after(date)  || ( (dateF.format(date2)).equals(dateF.format(date)) ) ) ) )
		{
			theQuestion = "Ingen text kan visas";
			questionTemplate="QuestionError.htm";
		}
		else
		{
			//update svarfilen
			File fortune_path = Utility.getDomainPrefPath("FortunePath",host);
			File file = new File(fortune_path,inFile + "enkatcurrent.txt");
			BufferedWriter fileW = new BufferedWriter( new FileWriter(file) );
			fileW.write(dateF.format(date1) + "#" + dateF.format(date2) +"#" + theQuestion + "#" +"ja: 0" + "#" + "nej: 0" + "#"); 
			fileW.flush();
			fileW.close();
		}
				
		return theQuestion;
	}

	
	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str) 
	{
		super.log(str) ;
	}


} // End class




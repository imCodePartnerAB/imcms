import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.server.* ;
import imcode.util.* ;


public class AdminFortuneFile extends Administrator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    String HTML_TEMPLATE ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		this.doPost(req,res);
	
    } // End doGet

    /**
       doPost
    */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{    

		// Lets get the server this request was aimed for
		String host = req.getHeader("Host") ;	
		String imcServer = Utility.getDomainPref("adminserver",host) ;
		
		HttpSession session = req.getSession();
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		String whichFile = (String)session.getAttribute("file") ;
		
		if (req.getParameter("back")!=null)
		{
			String url = MetaInfo.getServletPath(req) + "AdminFortunes";
			res.sendRedirect(url) ;
			return;
		}	
		
		if (req.getParameter("save")!=null)
		{				
			//out.println("okbuttonpushed <br> ");
			//spara texten till filen
			//String fortune_path = Utility.getDomainPref("FortunePath",host);
			
			
			File fortune_path = Utility.getDomainPrefPath("FortunePath",host);
			String readFile = (String)session.getAttribute("file");
			File file = new File(fortune_path,readFile + ".txt");
			BufferedWriter fileW = new BufferedWriter( new FileWriter(file) );
			//out.println("file: " + file + "<br>");
			
			//hämta nuvarande rader
			Map lines = (Map)session.getAttribute("lines");
			Set keyRows = lines.keySet();
			Iterator rowIt = keyRows.iterator();
			while(rowIt.hasNext())
			{
				Integer row = (Integer)rowIt.next();
			//	out.println(" lines2: " + lines.get(row) + "<br>" );
				
				//FIX så linjen blir ok med #
				String fullLine = ((String)lines.get(row)).trim();
				String date1 = fullLine.substring(0,6);
				String date2 = fullLine.substring(7,13);
				String text = HTMLConv.toHTML(fullLine.substring(14));
				
				//out.println(date1 + "#" + date2 + "#" + text + "#" + "<br>");
				fileW.write(date1 + "#" + date2 + "#" + text + "#" );
				fileW.newLine();	
			}
				
			fileW.flush();
			fileW.close();
			
			//tillbaks till 
			String url = MetaInfo.getServletPath(req) + "AdminFortunes" ;
			res.sendRedirect(url) ;
			return;
		}
		else
		{
			String options = "<option value=\"No_Choice\" selected>-- V&auml;lj Rad --</option>";
			String date1 = " ";
			String date2 = " ";
			String text = " ";
			
			if ((req.getParameter("add")).equals("add"))
			{
				//hämta nuvarande rader
				Map lines = (Map)session.getAttribute("lines");
				
				//hämta parametrar
				date1 = (req.getParameter("date1")).trim();
				date2 = (req.getParameter("date2")).trim();
				text = (req.getParameter("text")).trim();
								
				boolean wrong = false;
				
				if( !checkDate(date1) )
				{
					date1="Fel datum!";
					wrong = true;
				}
				
				if( !checkDate(date2) )
				{
					date2="Fel datum!";
					wrong = true;				}
				
				if( text.length()<1 )
				{
					text="Fel: Du har inte angett texten!";
					wrong = true;
				}
				
				if( !wrong )
				{
					//lägg ihop den nya raden
					String fullLine = date1 + " " + date2 + " " + text;

					//hitta högsta radnr
					int last = 0;//( (Integer)lines.lastKey() ).intValue();
					Set keys = lines.keySet();
					Iterator rowI = keys.iterator();
					while(rowI.hasNext())
					{
						int temp = (((Integer)rowI.next()).intValue());
						last = temp>last?temp:last;
					}
					
					//lägg till den nya raden
					lines.put(new Integer(last+1),fullLine);
					
					//spara de nya raderna
					session.setAttribute("lines",lines);
					
					date1 = " ";
					date2 = " ";
					text = " ";
				}
				
				Set keyRows = lines.keySet();
				Iterator rowIt = keyRows.iterator();
				while(rowIt.hasNext())
				{
					Integer row = (Integer)rowIt.next();
				//	out.println(" lines2: " + lines.get(row) + "<br>" );
					options = options + "<option value=\""  + row + "\" > " + lines.get(row) + "</option>";
				}
			}	
		
			
			if (req.getParameter("edit")!=null)
			{
				//hämta raden som är markerad
				String row = req.getParameter("AdminFile") ;
				
				Map lines = (Map)session.getAttribute("lines");
				
				if (!row.equals("No_Choice"))
				{
					Integer theRow = Integer.decode(row);
				//	out.println("row: " + row + "therow: " + theRow + "<br>");
					
					String fullLine = ((String)lines.get(theRow)).trim();
					date1 = fullLine.substring(0,6);
					date2 = fullLine.substring(7,13);
					text = fullLine.substring(14);
					
					lines.remove(theRow);
				
				}
				
				Set keyRows = lines.keySet();
				Iterator rowIt = keyRows.iterator();
				while(rowIt.hasNext())
				{
					//out.println(" lines2: " + lines.get(rowIt.next()) + "<br>" );
					Integer rad = (Integer)rowIt.next();
					options = options + "<option value=\""  + rad + "\" > " + lines.get(rad) + "</option>";
				}
			}	
		
			if (req.getParameter("remove")!=null)
			{
				//out.println("removebuttonpushed <br> ");
				
				//hämta nuvarande rader
				Map lines = (Map)session.getAttribute("lines");
				
				//hämta de rader som ska tas bort
				String rows[] = req.getParameterValues("AdminFile") ;
								
				//ta bort de som ska raderas
				for(int i=0;i<rows.length;i++)
				{
				//	out.println("rows: " + rows[i] + "<br>");
					if (!rows[i].equals("No_Choice"))
					{
						lines.remove(new Integer(rows[i]));	
					}
				}
				
				//spara de nya raderna
				session.setAttribute("lines",lines);
				
				Set keyRows = lines.keySet();
				Iterator rowIt = keyRows.iterator();
				while(rowIt.hasNext())
				{
					Integer row = (Integer)rowIt.next();
				//	out.println(" lines: " + lines.get(row) + "<br>" );
					options = options + "<option value=\""  + row + "\" > " + lines.get(row) + "</option>";
				}
				
				
				
			}
		
			if (req.getParameter("revert")!=null)
			{
				whichFile = (String)session.getAttribute("file") ;
	
				//öppna filen med detta namnet
				String openFile = IMCServiceRMI.getFortune(imcServer,whichFile + ".txt") ;
				BufferedReader readFile = new BufferedReader( new StringReader( openFile ) );
	
				options = "<option value=\"No_Choice\" selected>-- V&auml;lj Rad --</option>";
				Map lines = Collections.synchronizedMap(new TreeMap());
	
				String line = readFile.readLine();
				int row = 0;
				while ( line!=null && !(line.length()<=12) )
				{
					String fullLine = line.replace('#',' ');
					options = options + "<option value=\""  + row + "\" > " + fullLine + "</option>";
					lines.put( new Integer(row) , fullLine );
					line = readFile.readLine();
					row++;
				}
				
				session.setAttribute("lines",lines);
			}
		
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
			values.add(options);
			
			

			String parsed = IMCServiceRMI.parseExternalDoc(imcServer, values, "AdminFortuneFile.htm" , "se", "admin");
			out.print(parsed);
			return;
		
		}
		
		
   } 
   
  
   
   public boolean checkDate(String date)
   {
   		boolean check = true;
		
   		// datumet måste bestå av 6 tecken
		if( date.length()!=6 ){check = false;}
		
		//alla tecknen måste vara siffror
		for(int i=0;i<date.length();i++)
		{
			char tec = date.charAt(i);
			if (!Character.isDigit(tec))
			{check = false;}
		}
				
		return check;
   }
   
   
    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str) {
	super.log(str) ;
	System.out.println("AdminManager: " + str ) ;	
    }



} // End of class

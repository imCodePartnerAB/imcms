
/*
 *
 * @(#)PostCardServlet.java
 */


import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.util.log.* ;
import imcode.server.* ;
import imcode.external.diverse.*;

import java.net.* ;
import java.text.*;


/**
  * PARAMS in use, in doPost()
  * 
*/

public class PostcardServlet extends HttpServlet {
	private final static String CVS_REV = "$Revision$"  ;
	private final static String CVS_DATE = "$Date$";
	
	private final static String	POSTCARD_MAIL_SENT = "bekraftelse.html";
	private final static String POSTCARD_MAIL_SUBJECT = "mail_subject_postcard.html";
	private final static String POSTCARD_MAIL_ERROR = "mail_error.html";
	private final static String POSTCARD_BOTTOM = "preview_bottom.html";
	private final static String POSTCARD_MAIL_BODY = "mail_body_postcard.html";
	private final static String POSTCARD_SET = "preview.html";
	private final static String HTML_TEMPLATE = "vykort.html";//used to parse the postcard page
	private final static String QUOTE_FILE = "citat.txt";//used to parse the postcard page
	private final static String POSTCARD_FOLDER = "postcards";
	
	public void init(ServletConfig config) throws ServletException
	{

		super.init(config);
	}

	/**
	* Showing input document whit out error
	*/

	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException
	{
		//ok the user was satisfied, so now we just have to send the mail
		if (req.getParameter("action")!= null)
		{
			this.sendPostcardMail(req,res);
			return;
		}
	
		//här ska vi ladda första sidan av sidan skicka citat köret
		System.out.println("doGet");
		HttpSession session = req.getSession(true);
		
		//lets get the params we need later on
		String qText	= req.getParameter("qt");
		String qRow		= req.getParameter("qr");
		String metaId 	= req.getParameter("meta_id");
		String[] pCStuff = {qText,qRow,metaId};
		
		session.setAttribute("postCardStuff",pCStuff);
		res.sendRedirect("/imcms/servlet/GetDoc?meta_id="+metaId+"&param="+qText);
		
		return;
	}

	/**
	*   process submit
	*/
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException
	{	//här ska vi ta emot all in data och skapa en statisk html sida som vi skriver ner i en
	 	//mapp och skickar länken till mottagaren
		//System.out.println("doPost");
		//hämtar jag vet inte vad men typ allt
		String host					= req.getHeader("Host") ;	
		String imcserver			= Utility.getDomainPref("userserver",host) ;	
		String start_url			= Utility.getDomainPref( "start_url",host ) ;	
		String no_permission_url	= Utility.getDomainPref( "no_permission_url",host ) ;	
		int start_doc				= IMCServiceRMI.getDefaultHomePage(imcserver) ;
		String servlet_url			= Utility.getDomainPref( "servlet_url",host ) ;	
		File image_folder_path      = Utility.getDomainPrefPath( "image_path", host );
		SystemData sysData 			= imcode.util.IMCServiceRMI.getSystemData (imcserver);
		
		File templateLib  = getExternalTemplateFolder(req);
		
		HttpSession session = req.getSession(true);
		Object done = session.getValue("logon.isDone"); 		
		imcode.server.User user = (imcode.server.User) done ;
						
		String qLine = "1";
		String metaId ="";
		String qText = "";
		//lets get the line nr from session
		String[] pCStuff = (String[])session.getAttribute("postCardStuff")	;		
		if (pCStuff != null)
		{
			qText = pCStuff[0];
			qLine = pCStuff[1];
			metaId = pCStuff[2];
		}
				
		int qInt = 1;	
		try
		{
			qInt = Integer.parseInt(qLine);
		}catch(NumberFormatException nfe)
		{
			//some thing gon wrong, but I dont care I give them the first line'
			//instead of the one the wanted
			System.out.println("qLine wasn't a number");
			qInt = 1;
		}
				
		String resFile = IMCServiceRMI.getFortune(imcserver,QUOTE_FILE);
		
		StringTokenizer token = new StringTokenizer(resFile, "#", false);
		int counter = 0; 
		String qTextToSend="";
		
		while (token.hasMoreTokens())
		{
			String tmp = token.nextToken();		
		/*  the qengine does not suplie us with the correct number
			so we dont use this codsnippet at the moment, instead we 
			compare the whoole quot-text
			counter = counter +1;
			if ((counter/3) == qInt)
			{
				qText = tmp;
				break;
			}
		*/
			if (qText.equals(tmp))
			{
				qTextToSend = tmp;
				break;
			}
		}
		
		qTextToSend = HTMLConv.toHTML(qTextToSend);		
		//ok now we have the quot in the string qLine		
		//System.out.println("jippi: "+qLine);
		
		//lets get the info we need
		String friendName 		= req.getParameter("mailText0");	
		String friendEmail 		= req.getParameter("mailTo");
		String senderName 		= req.getParameter("mailText1");	
		String senderMessage 	= req.getParameter("mailText2");
		String imageNr			= req.getParameter("vykort");							
		//lets get the image url from db (we need serverObj, metaId and imageId to do it)
		RmiLayer rmi = new RmiLayer(user) ;	
		String sqlStr = "Select imgurl from images where meta_id='"+metaId+"' and name='"+imageNr+"'";		
		
		String imageUrl = rmi.execSqlQueryStr(imcserver, sqlStr ) ;
		if (imageUrl == null)
		{
			imageUrl = " "; 
		}else
		{
			imageUrl = "/imcms/images/"+imageUrl;
		}
		
				
		//create the taggs to parse
		Vector vect = new Vector();
		vect.add("#imageUrl#"); 	vect.add(imageUrl);
		vect.add("#citat#");		vect.add(qTextToSend);
		vect.add("#cont1#");		vect.add(friendName);
		vect.add("#cont3#");		vect.add(senderName);
		vect.add("#cont4#");		vect.add(HTMLConv.toHTMLSpecial(senderMessage));
		
		//ok nu ska vi parsa skiten med ett mall skrälle
		File pcTemplate = new File(templateLib, HTML_TEMPLATE);
		String html = IMCServiceRMI.parseDoc( imcserver,getTemplate(pcTemplate) , vect);
			
		//lets get the name to use on the file
		String pcFileName = (String) session.getAttribute("pcFileName");
		if (pcFileName == null)
		{
			//lets get the first part of the name
			GregorianCalendar cal = new GregorianCalendar();
			Date currentTime = cal.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");			
      		String dateString = formatter.format(currentTime);	
			//ok now lets get the second part (the counter)
			File counterFile = new File(templateLib, "postcardCounter.count");
			PostcardCounter count;
			try{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(counterFile));
				count =(PostcardCounter)in.readObject();			
				in.close();
			
			}catch(Exception e)
			{	
				//there wasnt any counter so lets create one
				count = new PostcardCounter();
			}
			count.increment();
			int postcardNr = count.getNumber();
			//lets save the counterObj
			try	
			{
				ObjectOutputStream out= new ObjectOutputStream(new FileOutputStream(counterFile));
				out.writeObject(count);
				out.close();
			}
			catch(IOException ioe)
			{
			 	log("Obs! Save couter failed.");
				log(ioe.getMessage());				
			}
			//lets setup the new name
			pcFileName = dateString +"_"+ postcardNr +".html";	
			session.setAttribute("pcFileName", pcFileName);				
		}
		
		//ok lets save the bottom frame page, incase it has been removed
		//by some stupid sysAdmin
		File bottom = new File( templateLib, POSTCARD_BOTTOM); 	
		String bottomString = IMCServiceRMI.parseDoc( imcserver,getTemplate(bottom) , new Vector());
		File imagePathFile = new File(imcode.util.Utility.getDomainPref("image_path",host));
		File postcardFolder = new File(imagePathFile.getParent(),POSTCARD_FOLDER);
		File bottomFile = new File(postcardFolder,"bottom.html");
		BufferedWriter buff = new BufferedWriter( new FileWriter(bottomFile) );					
		buff.write(bottomString,0,bottomString.length());
		buff.flush();
		buff.close();
		 			
		//ok lets save the postcardfile	
		File postcardFile = new File(postcardFolder,pcFileName);
		BufferedWriter fileW = new BufferedWriter( new FileWriter(postcardFile) );					
		fileW.write(html,0,html.length());
		fileW.flush();
		fileW.close();
				
		//ok nu är det sparat så nu skickar vi skiten till servern för granskning	
		File frameSet = new File( templateLib, POSTCARD_SET); 
		VariableManager vm = new VariableManager();
		vm.addProperty("postcard","/imcms/postcards/"+pcFileName);
		vm.addProperty("bottom","/imcms/postcards/bottom.html");
		HtmlGenerator htmlObj = new HtmlGenerator(templateLib, POSTCARD_SET) ;
		
		String frameSetHtml = htmlObj.createHtmlString(vm,req) ;
		//log("Before sendToBrowser: ") ;

		//lets send the prevPage to the user		
		htmlObj.sendToBrowser(req, res, frameSetHtml);
		 
		
		//now we can set up every thing we need to create the mail
		//sendMailWait( sender, mailTo, mailSubject , mailBody );
		String[] mailArr = new String[4];
		mailArr[0] = sysData.getWebMasterAddress();		
		mailArr[1]	= friendEmail;
		
		//ok lets parse the mailSubject line
		File mailSubject = new File( templateLib, POSTCARD_MAIL_SUBJECT); 	
		vect.add("#mailSubject#"); vect.add(senderName);
		mailArr[2] = IMCServiceRMI.parseDoc( imcserver,getTemplate(mailSubject) , vect);
	
		//lets parse the mailBody
		File mailBody = new File( templateLib, POSTCARD_MAIL_BODY); 	
		vect.add("#mailText0#"); 	vect.add(friendName);
		vect.add("#mailText1#"); 	vect.add(senderName);
		vect.add("#mailText2#"); 	vect.add("http://"+host);
		vect.add("#mailText3#"); 	vect.add(pcFileName);		
	   	mailArr[3]	= IMCServiceRMI.parseDoc( imcserver,getTemplate(mailBody) , vect);
		
		
		session.setAttribute("postcardMail",mailArr) ;		  
		return;			
	}
	
	
	//byte[] jaja = GetDoc.getDoc (int meta_id, int parent_meta_id, String host, HttpServletRequest req, HttpServletResponse res) throws IOException
		
	//hämta template mappen och filnamnet
	private File getExternalTemplateFolder(HttpServletRequest req) throws ServletException, IOException
	{
		String host					= req.getHeader("Host") ;	
		String imcserver			= Utility.getDomainPref("userserver",host) ;
		
		HttpSession session = req.getSession(true);
		Object done = session.getValue("logon.isDone"); 		
		imcode.server.User user = (imcode.server.User) done ;
		
		RmiLayer rmi = new RmiLayer(user) ;
		// Since our templates are located into the 105 folder, we'll have to hang on 105
	    StringBuffer templateLib = new StringBuffer(rmi.getInternalTemplateFolder(imcserver)+"\\");		
		try
		{
			
			String langPrefix = user.getLangPrefix();		
			templateLib.append(langPrefix + "\\105\\");
			
		}catch(NullPointerException npe)
		{	//use the default langue
			templateLib.append( "se\\105\\" );
		}
	
		return new File(templateLib.toString()) ;
	}
	
	/**
       get template
    */
    public String getTemplate(File file) throws IOException {
	
		StringBuffer value = new StringBuffer();

		BufferedReader fr ;

		try {
	 	  fr = new BufferedReader( new FileReader(file)) ;
		} catch(FileNotFoundException e) {
	 	   log("Failed to find the template "+file.getPath()) ;
	 	   return null ;
		}

		try {
	 	   int temp ;
	 	   while ((temp = fr.read())!=-1) {
			value.append((char)temp);
	 	   }
		} catch(IOException e) {
		    log("Failed to read template "+file.getPath()) ;
	 	   return null ;
		}

		return value.toString() ;
    }
		

	/**
	Log function. Logs the message to the log file and console
	*/
	public void log(String msg)
	{
		//if(msg == null)msg="null";
		super.log(msg) ;
		System.out.println("PostCardServlet: " + msg) ;

	}
	
	/**
	The method to handles the mail-stuff needed
	*/	
	private void sendPostcardMail( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException
	{
		/* mailserver info */
		String host					= req.getHeader("Host") ;	
		String imcserver			= Utility.getDomainPref("userserver",host) ;
		String mailserver 			= Utility.getDomainPref( "smtp_server", host );
		String stringMailPort		= Utility.getDomainPref( "smtp_port", host );
		String stringMailtimeout	= Utility.getDomainPref( "smtp_timeout", host );		
		File templateLib  			= getExternalTemplateFolder(req);
		VariableManager vm 			= new VariableManager();
		vm.addProperty(" "," "); 
		
		HttpSession session = req.getSession(false);
		if(session == null)
			res.sendRedirect("/imcms/servlet/StartDoc");
			
		String[] mailNfo = (String[]) session.getAttribute("postcardMail") ;
		
		session.removeAttribute("postcardMail") ;
		session.removeAttribute("pcFileName") ;		
		// Handling of default-values is another area where java can't hold a candle to perl.
		int mailport = 25 ;
		try
		{
			mailport = Integer.parseInt( stringMailPort );
		} catch (NumberFormatException ignored)
		{
			// Do nothing, let mailport stay at default.
		}

		int mailtimeout = 10000 ;
		try
		{
			mailtimeout = Integer.parseInt( stringMailtimeout );
		} catch (NumberFormatException ignored)
		{
			// Do nothing, let mailtimeout stay at default.
		}
		// send mail
		try 
		{
			SMTP smtp = new SMTP( mailserver, mailport, mailtimeout ) ;
			smtp.sendMailWait( mailNfo[0],mailNfo[1],mailNfo[2],mailNfo[3] );

		} catch (ProtocolException ex )
		{
			File mailError = new File( templateLib, POSTCARD_MAIL_ERROR); 	
			HtmlGenerator htmlObj = new HtmlGenerator(mailError) ;
			htmlObj.sendToBrowser(req, res, htmlObj.createHtmlString(vm,req));
			log ("Protocol error while sending mail. " + ex.getMessage()) ;
			return ;
		} catch (IOException ex )
		{
			File mailError = new File( templateLib, POSTCARD_MAIL_ERROR); 	
			HtmlGenerator htmlObj = new HtmlGenerator(mailError) ;
			htmlObj.sendToBrowser(req, res, htmlObj.createHtmlString(vm,req));
			log ("The mailservlet probably timed out. " + ex.getMessage()) ;
			return ;
		}
		
		
		File mailSent = new File( templateLib, POSTCARD_MAIL_SENT); 	
		HtmlGenerator htmlObj = new HtmlGenerator(mailSent) ;
		htmlObj.sendToBrowser(req, res, htmlObj.createHtmlString(vm,req));
		
		return;
	}
	
	/**
	A nice litle counter class to handle the numbering of postcards
	*/
	class PostcardCounter implements Serializable
	{
		private int _count = 0;
		PostcardCounter(){}
		
		int getNumber()
		{
			return _count;
		}
		
		void increment()
		{
			_count++;
		}	
	}


}


import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;

/**
  Search documents
*/
public class SearchDocuments extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	//default templates located in templates/search/  it means they are not in the langue stuff
	//and if they not wanted just add the one you want too use and supplie the names in hidden fields
	//when calling doPost
	private final static String HIT_LINE_TEMPLATE = "search_list.html";
	private final static String HIT_PAGE_TEMPLATE = "search_res1.html";
	private final static String NEXT_BUTTON = "search_next.html";
	private final static String PREV_BUTTON = "search_prev.html";
	private final static String SEARCH_PAGE_TEMPLATE = "search_documents.html";
	

    /**
       doPost()
    */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		
		String host 			= req.getHeader("Host") ;
        String imcserver 		= Utility.getDomainPref("adminserver",host) ;
        HttpSession session 	= req.getSession(true);
		imcode.server.User user	= (imcode.server.User) session.getAttribute("logon.isDone");
        
		//we must have a user obj, even if its a user extern object, so lets get one, or get rid of the req
        if (user == null) {
            String ip = req.getRemoteAddr( ) ;
			user = StartDoc.ipAssignUser( ip, host ) ;
			if (user == null) {
				res.sendRedirect("StartDoc") ;
           		return ;
			}
        }
				
		StringBuffer sqlBuff = new StringBuffer("SearchDocsIndex ");
		
		//this is the params we can get fram the browser
		String searchStringOrg 	= req.getParameter("question_field") == null? "":req.getParameter("question_field") ;
		String searchString 	= imcode.server.HTMLConv.toHTML(searchStringOrg);
        String fromDoc			= req.getParameter("fromDoc") == null? "1":req.getParameter("fromDoc");
		String maxHits			= req.getParameter("maxHits") == null? "1000":req.getParameter("maxHits");
        String searchPrep 		= req.getParameter("search_prep") == null? "and":req.getParameter("search_prep");
        String sortBy 			= req.getParameter("sortBy") == null? "meta_headline":req.getParameter("sortBy");
		String startNr			= req.getParameter("starts") == null? "0":req.getParameter("starts");
		String hitsAtTime		= req.getParameter("no_of_hits") == null? "15" : req.getParameter("no_of_hits");
		//not in use for the moment but needed to setup advanced search in the future
		//String start_date		= req.getParameter("start_date") == null? "":req.getParameter("start_date");
		//String stop_date		= req.getParameter("stop_date") == null? "":req.getParameter("stop_date");
		//String[] doctypesArr	= req.getParameter("doctypes");
		
		String format =  "yyyy-MM-dd HH:mm" ;
		Date date = new Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);

		//ok the rest of params we need to set up search sql
		String doctypes 		= "2,5,6,7,8";
		String created_start	= "";
		String create_stop		= "";//formatter.format(date);
        String changed_start	= "";
		String changed_stop 	= "";//formatter.format(date);
        String activated_start 	= "";
		String activated_stop 	= formatter.format(date);
        String archived_start 	= "";
		String archived_stop 	= "";//formatter.format(date);
		
		// lets set up the search string
		StringTokenizer token = new StringTokenizer(searchString," \"+-",true);
	   	searchString = buildSearchString(token, true)  ;	
				
		//lets set up the sql-params stringBuffer 
		sqlBuff.append(user.getUserId());			//@user_id INT,
		sqlBuff.append(",'"+searchString+"'");		//@keyword_string VARCHAR(128)
	 	sqlBuff.append(",'"+doctypes+"'");			//@doc_types_string VARCHAR(30)
	 	sqlBuff.append(","+fromDoc);				//@fromdoc INT
	 	sqlBuff.append(","+maxHits);				//@num_docs INT
	 	sqlBuff.append(",'"+sortBy+"'");			//@sortorder VARCHAR(256)
	 	sqlBuff.append(",'"+created_start+"'");		//@created_startdate DATETIME
		sqlBuff.append(",'"+create_stop+"'");		//@created_enddate DATETIME,
		sqlBuff.append(",'"+changed_start+"'");		//@modified_startdate DATETIME,
		sqlBuff.append(",'"+changed_stop+"'");		//@modified_enddate DATETIME,
		sqlBuff.append(",'"+activated_start+"'");	//@activated_startdate DATETIME,
		sqlBuff.append(",'"+activated_stop+"'");	//@activated_enddate DATETIME,
		sqlBuff.append(",'"+created_start+"'");		//@archived_startdate DATETIME,
		sqlBuff.append(",'"+archived_stop+"'");		//@archived_enddate DATETIME,
		sqlBuff.append(",'0'");						//@only_addable TINYINT
  			
		String[][] sqlResults;
		int hits = 0;
		//the counter to tell vere in the hitarr to start
		int startNrInt = 0;
		try {
			startNrInt = Integer.parseInt(startNr);
		}catch(NumberFormatException nfe) {
			//do nothing lets start at 0
		}
		
		//the counter to tell how many hits to show
		int noOfHit = 1000;
		try {
			noOfHit = Integer.parseInt(hitsAtTime);
		}catch(NumberFormatException nfe) {
			//do nothing lets start at 0
		}
		
		//check if nex or prev butons was selected or if we must do a new search i db
		if ( req.getParameter("next_button") != null) {
			noOfHit 	= Integer.parseInt(req.getParameter("hitsNo"));
			startNrInt 	= Integer.parseInt(req.getParameter("startNr"));
			sqlResults = (String[][]) session.getAttribute("search_hit_list");
			if (sqlResults == null) res.sendRedirect("StartDoc");
		}else if(req.getParameter("prev_button") != null) {
			noOfHit 	= Integer.parseInt(req.getParameter("hitsNo"));
			startNrInt 	= Integer.parseInt(req.getParameter("startNr")) - (noOfHit + noOfHit);	
			sqlResults = (String[][]) session.getAttribute("search_hit_list");
			if (sqlResults == null) res.sendRedirect("StartDoc");			
		}else {
			//its a new one so lets do a new search
			sqlResults = IMCServiceRMI.sqlProcedureMulti(imcserver, sqlBuff.toString());	
			session.setAttribute("search_hit_list",sqlResults)	;
		}		
		
		if (sqlResults != null) {
			hits = sqlResults.length;
		}
		
		//parses the result page to send back
		String oneRecHtmlSrc,resultHtmlSrc,noHitHtmlStr,returnStr;        
		String action = req.getParameter("action");
		if (action == null) {//must fix the startNo variable			
			String langPrefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		
			//the default search uses the templates in template/"langprefix"/admin/
			oneRecHtmlSrc = IMCServiceRMI.parseDoc(imcserver,null,HIT_LINE_TEMPLATE,langPrefix);
			resultHtmlSrc = IMCServiceRMI.parseDoc(imcserver,null,HIT_PAGE_TEMPLATE,langPrefix);
					
			//check if next or prev button must be created
			String nextButton = "&nbsp;";
			String prevButton = "&nbsp;";
			if (hits > 0) {
				Vector tagsV = new Vector();
				tagsV.add("#startNr#"); tagsV.add((startNrInt+noOfHit)+"" );
				tagsV.add("#hitsNo#"); 	tagsV.add(noOfHit+"");
				
				if (startNrInt + noOfHit  < hits) {
					//ok we need the next button search_next.html
					nextButton = IMCServiceRMI.parseDoc(imcserver,tagsV,NEXT_BUTTON,langPrefix);
				}
				if (startNrInt - noOfHit >= 0) {
					//ok we need the prev button
					prevButton = IMCServiceRMI.parseDoc(imcserver,tagsV,PREV_BUTTON,langPrefix);
				}				
			}
							
			//parsa i ordning träflistan
			StringBuffer buff = SearchDocuments.parseSearchResults(imcserver,oneRecHtmlSrc,sqlResults,startNrInt,noOfHit);				
			String[] tagsArr = {"#hit_list#",buff.toString()};								
			returnStr = Parser.parseDoc(resultHtmlSrc,tagsArr );				
			
			Vector tags = new Vector();
			tags.add("#search_hit_list#");	tags.add(returnStr);
			tags.add("#prev_page#")	;		tags.add(prevButton);
			tags.add("#nex_page#")	;		tags.add(nextButton);
			returnStr = IMCServiceRMI.parseDoc(imcserver,tags,SEARCH_PAGE_TEMPLATE,langPrefix);
		
		}else if (action.equalsIgnoreCase("user_search")) {
			//lets set up the <-prev- 1 2 .. -next-> stuff
			boolean nextButtonOn = false;
			boolean prevButtonOn = false;
			int hitPages = 0;
			StringBuffer buttonsSetupHtml = new StringBuffer("");
		
			if (hits > 0) {
				if (startNrInt+noOfHit  < hits) {
					//ok we need to light the nextButton
					nextButtonOn = true;
				}
				if (startNrInt - noOfHit >= 0) {
					//ok we need the prev button
					prevButtonOn = true;
				} 
				//now we need to count the number of hit-pages				
				hitPages = hits / noOfHit;
				if ((hits % noOfHit) != 0){
					hitPages++;
				}
			
			
				//lets try and get the templates to do this
				String nextTextTemplate 	= IMCServiceRMI.getSearchTemplate(imcserver ,"search_next.html");;
				String prevTextTemplate 	= IMCServiceRMI.getSearchTemplate(imcserver ,"search_prev.html");;
				String activeTemplate		= IMCServiceRMI.getSearchTemplate(imcserver ,"search_active.html");;
				String inActiveTemplate		= IMCServiceRMI.getSearchTemplate(imcserver ,"search_inactive.html");;
				String ahrefTemplate 		= IMCServiceRMI.getSearchTemplate(imcserver ,"search_ahref.html");;
				//Fix kolla att ingen mall är null om så returnera alla hitts i en lång lista
				
				//ok this is a tricky part to set up the html for the next button and so on
				//lets start with the prev button
				if (prevButtonOn) {
					String[] prevArrOn =  {"#nexOrPrev#","0","#startNr#",(startNrInt-noOfHit)+"","#value#",prevTextTemplate};
					buttonsSetupHtml.append(Parser.parseDoc(ahrefTemplate,prevArrOn )+"\n");
				}else {
					String[] prevArrOff =  {"#value#",prevTextTemplate};
					buttonsSetupHtml.append(Parser.parseDoc(inActiveTemplate,prevArrOff )+"\n");
				}
				//ok now we must do some looping to add all the hit page numbers
				for(int y=0; y < hitPages; y++) {
					//lets see if its the choosen one
					if ((y * noOfHit) == startNrInt  ){
						String[] pageActive =  {"#value#",(y+1)+""};
						buttonsSetupHtml.append(Parser.parseDoc(activeTemplate,pageActive )+"\n");
					}else {
						String[] pageInactive =  {"#nexOrPrev#","1","#startNr#",(y*noOfHit)+"","#value#",(y+1)+""};
						buttonsSetupHtml.append(Parser.parseDoc(ahrefTemplate,pageInactive )+"\n");
					}
				}
				//lets do the nextButton
				if (nextButtonOn) {
					String[] nextArrOn =  {"#nexOrPrev#","1","#startNr#",(startNrInt+noOfHit)+"","#value#",nextTextTemplate};
					buttonsSetupHtml.append(Parser.parseDoc(ahrefTemplate,nextArrOn )+"\n");
				}else {
					String[] nextArrOff =  {"#value#",nextTextTemplate};
					buttonsSetupHtml.append(Parser.parseDoc(inActiveTemplate,nextArrOff )+"\n");
				}			
			}//end (hits > 0)
			
			
			
			//user defined search this will use the templates stored in the templates/search/ folder
			//this one is used by ex maBra and they has there templates
			oneRecHtmlSrc = IMCServiceRMI.getSearchTemplate(imcserver ,req.getParameter("template_list"));
			resultHtmlSrc = IMCServiceRMI.getSearchTemplate(imcserver ,req.getParameter("template"));
			noHitHtmlStr = IMCServiceRMI.getSearchTemplate(imcserver ,req.getParameter("template_no_hit"));
			StringBuffer buff = SearchDocuments.parseSearchResults(imcserver,oneRecHtmlSrc,sqlResults,startNrInt,noOfHit);		
			//if there isnt any hitts lets add the no hit message		
			if (buff.length()==0) {		
				buff.append(noHitHtmlStr);
			}
			
			String[] paramArr = {"#search_list#",buff.toString(),
								"#nrhits#",""+hits,
								"#searchstring#",searchStringOrg,
								"#page_buttons#",buttonsSetupHtml.toString(),
								"#hitsNo#",noOfHit+""};
			returnStr = Parser.parseDoc(resultHtmlSrc,paramArr );		
		}else {	 
			//here is the place the searche code fore the get existing doc
			//but untill its implemented lets send the user to the start page
			res.sendRedirect("StartDoc");
			return;
			
		}
			
				
		//now lets send it to browser
		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream();		
		out.print(returnStr);
		out.flush();
		out.close();
		return;
	} // End of doPost

    /**
       doGet()
    */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ;
		String htmlStr = "" ;
		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream();

	 	// Get the session
		HttpSession session = req.getSession( true );
		// Does the session indicate this user already logged in?
		Object done = session.getAttribute( "logon.isDone" );  // marker object
		user = (imcode.server.User)done ;

		if( done == null ) {
		    // No logon.isDone means he hasn't logged in.
		    // Save the request URL as the true target and redirect to the login page.
		    session.setAttribute( "login.target",
				      HttpUtils.getRequestURL( req ).toString( ) );
		    String scheme = req.getScheme( );
		    String serverName = req.getServerName( );
		    int p = req.getServerPort( );
		    String port = (p == 80) ? "" : ":" + p;
		    res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
		    return ;
		}
		// Lets get the html file we use as template
		Vector tags = new Vector();
		tags.add("#search_hit_list#");tags.add("");
		
		String langPrefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		htmlStr = IMCServiceRMI.parseDoc(imcserver,tags,SEARCH_PAGE_TEMPLATE,langPrefix);
		out.print( htmlStr ) ;
		out.flush();
		out.close();
		return;
    } // End of doGet
	
			
	/**
		@Author Peter Östergren
	*/	
	private String buildSearchString(StringTokenizer token,boolean first)	{
		StringBuffer buff = new StringBuffer();
		while (token.hasMoreTokens()) {
			String str = token.nextToken();
			if(str.equals(" ")){
				buff.append(buildSearchString(token,false));
			}else {
				if (str.equals("\"")) {
					buff.append("\"");
					boolean found = false;
					while (token.hasMoreTokens() && !found) {
						str = token.nextToken();
						if(str.equals("\"")){
							buff.append("\"");
							found = true;
						}else {
							buff.append(str);
						}
						if(found)buff.append(",");
					}
					if(!first) return buff.toString();
				}else if(str.equals("+")) {
					if ( !first ) {
						return "\"and\",";			
					}else {
						buff.append("\"and\",");
					}
				}else if(str.equals("-")) {
					if ( !first ) {
						return "\"not\",";			
					}else {
						buff.append("\"not\",");
					}
				}else {
					if ( !first ) {
						return "\""+str+"\",";
					}else {
						buff.append("\""+str+"\",");
					}
				
				}
			}
		}
		return buff.toString();
	}
	
	
	/**
		@Author Peter Östergren
	*/
	private static StringBuffer parseSearchResults(String imcserver, String oneRecHtmlSrc,
      String[][] sqlResults, int startValue , int numberToParse) throws java.io.IOException{
		StringBuffer searchResults = new StringBuffer("") ;
		int stop  = startValue + numberToParse;
		if ( stop >=  sqlResults.length ) {
			stop = sqlResults.length;
		}
       	// Lets parse the searchresults
        String[] oneRecVariables = SearchDocuments.getSearchHitTaggaArr() ;
        for(int i = startValue ; i < stop; i++ ) {
            String[] oneRec = sqlResults[i] ;
            String[] tmpVecData = new String[oneRecVariables.length] ;
		
            // Lets parse one record
            for(int k = 0 ; k < oneRec.length; k++ ) {
				if (oneRec[k] == null) {
					tmpVecData[k] = "&nbsp;" ;
				}else if ( oneRec[k].equalsIgnoreCase("")) {
                	tmpVecData[k] = "&nbsp;" ;
            	}else {
                	tmpVecData[k] = oneRec[k] ;
            	}	
			}
			tmpVecData[tmpVecData.length-1] = ""+(i + 1);
			searchResults.append(Parser.parseDoc(oneRecHtmlSrc,oneRecVariables, tmpVecData )) ;
		}
      	return searchResults ;
	}
	
	
	/**
      Returns all possible variables that might be used when parse the oneRecLine to the
      search page
	  @Author Peter Östergren  		
     */
    private static String[] getSearchHitTaggaArr() {
        String[] strArr =  {"#meta_id#",
							"#doc_type#",
							"#meta_headline#",
							"#meta_text#",
							"#date_created#",
							"#date_modified#",
							"#date_activated#",
							"#date_archived#",
							"#archive#",
							"#shared#",
							"#show_meta#",
							"#disable_search#",
							"#meta_image#",
							"#hit_nbr#"} ;
       return strArr;
    }
	
	
    /**
       Log to log file
    */
    public void log(String str) {
		super.log(str) ;
    }
	
} // End class

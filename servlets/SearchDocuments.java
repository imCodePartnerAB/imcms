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
	private final static String HIT_LINE_TEMPLATE = "list.html";
	private final static String HIT_PAGE_TEMPLATE = "result.html";
	private final static String NO_HIT_PAGE_TEMPLATE = "no_hit.html";
	

    /**
       doPost()
    */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	
		String action = req.getParameter("action");
		if (action != null){
			if (action.equals("user_search")){
				this.doUserSearch(req,res);
				return;
			}
		}else {
			//is depricated and sould bee removed or fixed so it uses a stored procedure on search
			//instead of the UGLY sql-string that might not work because there is no controll if there
			//has been any changes in the database. 
			//The doJanusSearch method can only bee used as an bad-example how not to do sql-stuff.
			this.doJanusSearch(req,res);
			return;
		}
	} // End of doPost


    /**
       doGet()
    */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		//this method isn't realy needed if you sets up an usual text-doc that makes the call to the
		//doPost method with action = doUserSearch
		//its many profits with that because then the you can design a search template and get it to all meta-ids
		//if its a orinary text-document then a low-admin can change texts and stuff on the search engine page
		//and so on!
		this.getJanusSearchPage(req,res);
		return ;
    } // End of doGet
	
	/**
		Performes a search among documents, used in usermode
		and its only list dokuments the user is allowed to see
		@Author Peter Östergren
	*/
	public void doUserSearch( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 			= req.getHeader("Host") ;
        String imcserver 		= Utility.getDomainPref("adminserver",host) ;
        HttpSession session 	= req.getSession(true);
		imcode.server.User user	= (imcode.server.User) session.getAttribute("logon.isDone");
        
		//we must have a user obj, even if its a user extern object, so lets get one, or get rid of the req
        if (user == null) {
            String ip = req.getRemoteAddr( ) ;
			user = StartDoc.ipAssignUser( ip, host ) ;
			if (user == null) {
				res.sendRedirect(req.getContextPath()+"/servlet/StartDoc") ;
           		return ;
			}
        }
		
		StringBuffer sqlBuff = new StringBuffer("SearchDocs ");
		
		//this is the params we can get fram the browser
		String searchString 	= req.getParameter("question_field") == null? "":req.getParameter("question_field") ;
		searchString 	= imcode.server.HTMLConv.toHTML(searchString);
        String fromDoc			= req.getParameter("fromDoc") == null? "1":req.getParameter("fromDoc");
		String maxHits			= req.getParameter("maxHits") == null? "1000":req.getParameter("maxHits");
        String searchPrep 		= req.getParameter("search_prep") == null? "and":req.getParameter("search_prep");
        String sortBy 			= req.getParameter("sortBy") == null? "meta_headline":req.getParameter("sortBy");
		
		//not in use for the moment but needed to setup advanced search in the future
		//String start_date		= req.getParameter("start_date") == null? "":req.getParameter("start_date");
		//String stop_date		= req.getParameter("stop_date") == null? "":req.getParameter("stop_date");
		//String[] doctypesArr	= req.getParameter("doctypes");
		
		//ok the rest of params we need to set up search sql
		String doctypes 		= "2,5,6,7,8";
		String created_start	= "";
		String create_stop		= "";
        String changed_start	= "";
		String changed_stop 	= "";
        String activated_start 	= "";
		String activated_stop 	= "";
        String archived_start 	= "";
		String archived_stop 	= "";
		
		// parse searchString, replaces SPACE with RETURN and EMPTY with RETURN
        while (searchString.indexOf(" ") != -1){
	        int spaceIndex = searchString.indexOf(" ");
	        searchString = searchString.substring(0, spaceIndex)
	        + "\r" + searchString.substring(spaceIndex+1, searchString.length());
	    }
        if(searchString.equals(""))
        	searchString = "\r";
		
		String result_line = req.getParameter("template_list")==null? HIT_LINE_TEMPLATE:req.getParameter("template_list");
		String result_page = req.getParameter("template")==null? HIT_PAGE_TEMPLATE:req.getParameter("template");
		String result_no_hit_line = req.getParameter("template_no_hit")==null?NO_HIT_PAGE_TEMPLATE:req.getParameter("template_no_hit");
		
		//lets get the template strings
		String oneRecHtmlSrc = IMCServiceRMI.getSearchTemplate(imcserver ,result_line);
		String resultHtmlSrc = IMCServiceRMI.getSearchTemplate(imcserver ,result_page);
		String noHitHtmlStr = IMCServiceRMI.getSearchTemplate(imcserver ,result_no_hit_line);		
		
		//lets set up the sql-params stringBuffer 
		sqlBuff.append(user.getUserId());			//@user_id INT,
		sqlBuff.append(",'"+searchString+"'");		//@keyword_string VARCHAR(128)
	 	sqlBuff.append(",'"+searchPrep+"'");		//@and_mode VARCHAR(3)
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
		sqlBuff.append(",'1'"); 					//@starts_with TINYINT
  	
  		//System.out.println("SQL: " + sqlBuff.toString()) ;
		String[][] sqlResults = IMCServiceRMI.sqlProcedureMulti(imcserver, sqlBuff.toString());
		String hits = "";
		if (sqlResults != null) {
			hits = ""+sqlResults.length;
		}
				
		StringBuffer buff = SearchDocuments.parseSearchResults(imcserver,oneRecHtmlSrc,sqlResults);		
		//System.out.println(buff.toString());
		//if there isnt any hitts lets add the no hit message		
		if (buff.length()==0) {		
			buff.append(noHitHtmlStr);
		}
		
		//now we only have to fix the page to send back to brawser
		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream();
		
		String[] paramArr = {"#search_list#",buff.toString(),
							"#nrhits#",hits,
							"#searchstring#",searchString};
		
		out.println(Parser.parseDoc(resultHtmlSrc,paramArr ));
		
	}//end doUserSearch method
	
		
	
	/**
		@Author Peter Östergren
	*/
	private static StringBuffer parseSearchResults(String imcserver, String oneRecHtmlSrc,
      String[][] sqlResults) throws java.io.IOException{
		StringBuffer searchResults = new StringBuffer("") ;
		
       	// Lets parse the searchresults
        String[] oneRecVariables = SearchDocuments.getSearchHitVector() ;
        for(int i = 0 ; i < sqlResults.length; i++ ) {
            String[] oneRec = sqlResults[i] ;
            String[] tmpVecData = new String[oneRecVariables.length] ;
		
            // Lets parse one record
            for(int k = 0 ; k < oneRec.length; k++ ) {           
            	if( oneRec[k].equalsIgnoreCase(""))
                	tmpVecData[k] = "&nbsp;" ;
                else
                	tmpVecData[k] = oneRec[k] ;
			}
			searchResults.append(Parser.parseDoc(oneRecHtmlSrc,oneRecVariables, tmpVecData )) ;
		}
      	return searchResults ;
	}
	
	/**
      Returns all possible variables that might be used when parse the oneRecLine to the
      search page
	  @Author Peter Östergren  		
     */
    private static String[] getSearchHitVector() {
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
							"#doc_count"} ;
       return strArr;
    }
	
	/**
       init()
    */
    public void init (ServletConfig config) throws ServletException {
		super.init(config);
    }

    /**
       Log to log file
    */
    public void log(String str) {
		super.log(str) ;
		//System.out.println("SearchDocuments: "+str);
    }
	
	
	//************** Do not use anything belowe this line its ugly and depricated  *********************
	//************** but we cant remove it because there is some janus stuff in it *********************
	/**
       @deprecated Do not use! Its no garanti that it vill work at all!
    */
	public void getJanusSearchPage( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ;
		String htmlStr = "" ;
		res.setContentType( "text/html" );
		PrintWriter out = res.getWriter( );

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

		Properties props = this.getSummaryProps(servlet_url) ;
		String langPrefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		htmlStr = IMCServiceRMI.parseDoc(imcserver,convert(props),"search_documents.html",langPrefix);
		out.print( htmlStr ) ;
		return;
	}
	/**
       @deprecated Do not use! Its no garanti that it vill work at all!
    */	
	public void doJanusSearch( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		
	String host 				= req.getHeader("Host") ;
	String imcserver 			= Utility.getDomainPref("adminserver",host) ;
	String start_url        	= Utility.getDomainPref( "start_url",host ) ;
	String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

	imcode.server.User user ;
	String htmlStr = "" ;

	String question_field = "" ;
	String search_type = "" ;        // atc_icd10 or nothing
	String search_area = "" ;
	String string_match = "" ;
	String search_prep = "" ;        // and / or
	String values[] ;
	int start = 0 ;      // The search result counter. Keeps track of hits
	int nbrToShow = 40 ;  // Keeps track of how many results we should show
	// The result = nbrToShow / fieldrecSize
	int meta_id = 0 ;
	int fieldRecSize = 5 ;  // Keeps track of how many fields a record consists of in the res array

	res.setContentType( "text/html" );
	PrintWriter out = res.getWriter( );

	// get question_field
	values = req.getParameterValues( "question_field" ) ;
	if( values != null ) question_field = values[0] ;
	question_field = verifySqlText(question_field) ;

	// get search_type atc_icd10
	search_type = req.getParameter( "search_type" ) ;
	if( search_type == null) search_type = "not codes" ;
	// if( values != null ) search_type = values[0] ;
	//log("search_type = " + search_type) ;

	// Get search_preposition - AND / OR
	search_prep = req.getParameter( "search_prep" ) ;
	if( search_prep == null) search_prep = "and" ;
	//log("search_prep = " + search_prep) ;

	// get search_area
	values = req.getParameterValues( "search_area" ) ;
	if( values != null ) search_area = values[0] ;

	// get string_match
	values = req.getParameterValues( "string_match" ) ;
	if( values != null ) string_match = values[0] ;

	// get startvalue
	String startStr = req.getParameter( "start" ) ;
	if( startStr == null ) startStr = "0" ;
	try {
	    start = Integer.parseInt(startStr) ;
	} catch(NumberFormatException ex) {
	    log(ex.getMessage()) ;
	    start = 0 ;
	}

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

	// Lets check the length on the questionfield
	if( question_field.equals("")) {
	    this.doGet(req,res);
	    return ;
	}

	// Lets get the langprefix
	String langPrefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
	// Lets run the question

	String sqlStr = buildSqlStr(imcode.server.HTMLConv.toHTML(question_field), search_type, search_prep, string_match, search_area) ;
	//System.out.println("BuildSqlStr: " + sqlStr) ;  
	String[] answer= IMCServiceRMI.sqlQuery(imcserver, sqlStr) ;
	
	for(int i=0; i<answer.length ; i += 5)
	{	//lets get rid of the pages that the user not allowed to see
		if (!IMCServiceRMI.checkDocRights (imcserver, Integer.parseInt(answer[i]), user ))
		{
			for(int z = i; z<i+5; z++)
				answer[z] = null;			
		}
	}

	// Lets copy the array into a vector
	Vector docs = new Vector() ;
	for(int i=0 ; i<answer.length; i++) {
	    if(answer[i] != null)
			docs.add(answer[i]) ;
	}
    
	// Lets get the number of hits. Docs will have the following 'syntax'
	// meta id, meta_headline, meta_text
	int no_of_docs = docs.size( ) ;
	log("Antal träffar: " + (no_of_docs / fieldRecSize)) ;

	// Lets send the search paramters
	Properties props = this.getSummaryProps(servlet_url) ;
	props.setProperty("#SEARCH_TYPE#", search_type) ;
	props.setProperty("#STRING_MATCH#", string_match) ;
	props.setProperty("#SEARCH_AREA#", search_area) ;
	props.setProperty("#QUEST#", question_field) ;
	props.setProperty("#TOTAL_HITS#", "" + no_of_docs / fieldRecSize) ;
	props.setProperty("#SEARCH_PREP#", search_prep);


	// Ok, we had no hits...
	if( no_of_docs == 0 ) {
	    // Lets fix the search summary field
	    String summary = IMCServiceRMI.parseDoc(imcserver,convert(props),"SEARCH_RES_SUM.HTML",langPrefix);
	    props.setProperty("#HITS_SUMMARY#", summary) ;
	    htmlStr = IMCServiceRMI.parseDoc(imcserver,convert(props),"SEARCH_RES1.HTML",langPrefix);
	    out.println( htmlStr ) ;
	    return ;
	}
	// Lets recalculate the startvalue if necessary
        int min = start ;
        int max = min + nbrToShow ;
        if( req.getParameter("search_prev") != null ) {
	    start = start - nbrToShow * 2 ;
	    min = start ;
	    max = min + nbrToShow ;
	    start += nbrToShow ;
	    if(min < 0 )
		min = 0 ;
        } else {
	    if( (start + nbrToShow) <= no_of_docs)  ;
	    start += nbrToShow  ;
	    if( max > no_of_docs)
		max = no_of_docs ;
        }

	// Lets do for all hits on our search..
        String oneRecHtml = "" ;

	// Lets calculate our min value in the array
        
        for( int i = min ; i < no_of_docs && i < max ; i+=fieldRecSize ) {
	    Vector v = new Vector(fieldRecSize) ;
	    v.add(docs.get(i)) ;
	    v.add(docs.get(i+1)) ;
	    v.add(docs.get(i+2)) ;
	    v.add(docs.get(i+3)) ;
	    v.add(""+ (i / fieldRecSize +1) ) ;
	    v.add(question_field) ;
	    v.add(docs.get(i+4)) ;
	    oneRecHtml = parseOneRecord(imcserver, servlet_url, v, "search_list.html",langPrefix) ;

	    htmlStr += oneRecHtml;
        }
	// Lets save the old startValue
	String oldStart = "1" ;
	if (start == 0)
	    oldStart = "1" ;
	else
	    oldStart = "" + ((min / fieldRecSize) + 1) ;

	// Lets update the start variables
	props.setProperty("#START#", ""+ start) ;
	props.setProperty("#HITS_RANGE#", oldStart + "-" + (max/fieldRecSize)) ;

	// Lets update props with all the hits
        props.setProperty("#ONE_RECORD#", htmlStr) ;

	// Lets fix the search summary field
        String summary = IMCServiceRMI.parseDoc(imcserver,convert(props),"SEARCH_RES_SUM.HTML",langPrefix);
        props.setProperty("#HITS_SUMMARY#", summary) ;
        //log("Summary: " + summary) ;

	// Lets fix the 'Next page button'
        if( start < no_of_docs ) {
	    String nextPage = IMCServiceRMI.parseDoc(imcserver,convert(props),"SEARCH_NEXT.HTML",langPrefix);
	    props.setProperty("#NEXT_PAGE#", nextPage) ;
        }

	// Lets fix the previous button. Lets first recalculate our current startvalue
        if(start > (nbrToShow)) {
	    String prevPage = IMCServiceRMI.parseDoc(imcserver,convert(props),"SEARCH_PREV.HTML",langPrefix);

	    props.setProperty("#PREV_PAGE#", prevPage) ;
        }

        htmlStr = IMCServiceRMI.parseDoc(imcserver,this.convert(props),"search_res1.html",langPrefix);
        out.println( htmlStr ) ;
        return ;
	// } // End of search

	// Lets redirect to the doGetpage
	//    this.doGet(req,res) ;
 
	
	}
    /**
       @deprecated Do not use! Its no garanti that it vill work at all!
    */
    public String buildSqlStr(String question_str, String search_type,
			      String search_prep,String string_match,String search_area) {

	// search_area : all,not_archived,archived
	String sqlStr = "" ;
	Vector tokens = new Vector() ;
	Vector meta_docs = new Vector() ;
	String match = "%" ;

	if ( string_match.equals("match") )
	    match = "" ;

	StringTokenizer parser = new StringTokenizer(question_str.trim()," ,") ;
	while ( parser.hasMoreTokens() )
	    tokens.addElement(parser.nextToken()) ;

	// Lets NOT search on codes
	if ( !search_type.equals("atc_icd10") ) {
	    //log ("--- NU SÖKER VI PÅ ALLT") ;

	    // text fields                 // texts.meta_id
	    if (tokens.size() > 0) {
		sqlStr += "SELECT DISTINCT meta.meta_id,meta.meta_headline,meta.meta_text,meta.date_modified,meta.meta_image " + '\n' ;
		sqlStr += "FROM texts, meta\n" ;
		sqlStr += "WHERE (" ;
	    }
	    for ( int i = 0 ; i < tokens.size() ; i++ ) {
		String token = tokens.elementAt(i).toString() ;
		sqlStr += " (text LIKE  '%" + token + 
		    "%' OR text LIKE '%" + imcode.server.HTMLConv.toHTML(token)+"%')" ;

		if ( i < tokens.size() -1 )
		    sqlStr += " " + search_prep + " " ;
	    }

	    sqlStr += ")\n" ;

	    if (tokens.size() > 0) {
		sqlStr += "AND meta.meta_id = texts.meta_id\n" ;
		sqlStr += "AND meta.activate = 1\n" ;
		sqlStr += "AND meta.disable_search = 0\n" ;
	    }
		
		sqlStr += "and CAST(activated_date+' '+activated_time AS DATETIME) < getdate() \n";
	    
		if (search_area.equals("not_archived")) {
		sqlStr += "AND meta.archive = 0\n" ;
	    }

	    if (search_area.equals("archived")) {
		sqlStr += "AND meta.archive = 1\n" ;
	    }

	    if ( tokens.size() > 0 ) {
		sqlStr += "UNION\n" ;
	    }

	    // Lets create the select and from statement
	    if (tokens.size() > 0) {
		sqlStr += "SELECT DISTINCT meta.meta_id,meta.meta_headline,meta.meta_text,meta.date_modified,meta.meta_image \n" ;
		sqlStr += "FROM meta ,childs \n" ;
		sqlStr += "WHERE " ;
	    }

	    // Lets create the where statement
	    for ( int i = 0 ; i < tokens.size() ; i++ ) {
		sqlStr += "(meta_headline LIKE  '%" + tokens.elementAt(i).toString() + "%' \n" ;
		sqlStr += "OR meta_text LIKE  '%" + tokens.elementAt(i).toString() + "%') \n" ;
		if ( i < tokens.size() -1 )
		    sqlStr += " " + search_prep + " " ;
	    }

	    // Lets append with our new conditions
	    sqlStr += "AND activate = 1\n " ;
	    sqlStr += "AND disable_search = 0\n" ;
	    sqlStr += "AND meta.doc_type != 6	--Browserdoc\n" ;
	    // sqlStr += "AND meta.doc_type != 7	--Framesetdoc\n" ;
	    sqlStr += "AND NOT EXISTS -- docs which is not in a menu\n" ;
	    sqlStr += "\t (SELECT meta_id\n" ;
	    sqlStr += "\t FROM childs meta\n" ;
	    sqlStr += "\t WHERE childs.to_meta_id = meta.meta_id )\n" ;
		sqlStr += "and CAST(activated_date+' '+activated_time AS DATETIME) < getdate() \n";

	    if (search_area.equals("not_archived")) {
		sqlStr += "AND meta.archive = 0\n" ;
	    }

	    if (search_area.equals("archived")) {
		sqlStr += "AND meta.archive = 1\n" ;
	    }

	    if ( tokens.size() > 0 ) {
		sqlStr += "UNION\n" ;
	    }


	    // THE NEW CLASSIFICATION STYLE
	    sqlStr += "SELECT meta.meta_id, meta.meta_headline, meta.meta_text, meta.date_modified, meta.meta_image\n" ;
	    sqlStr += "FROM meta\n" ;
	    sqlStr += "JOIN meta_classification mc\n" ;
	    sqlStr += "ON  meta.meta_id = mc.meta_id\n" ;
	    sqlStr += "JOIN classification AS class\n" ;
	    sqlStr += "ON mc.class_id = class.class_id\n" ;
	    sqlStr += "AND (" ;

	    for ( int i = 0 ; i < tokens.size() ; i++ ) {
		String sWord = tokens.elementAt(i).toString() ;

		sqlStr += "( class.code LIKE '" + match + sWord + match + "' )\n" ;

		// Ok, if we search for more than 1 word, then should we always add OR
		if ( i < tokens.size() -1 ) {
		    sqlStr += " " + "OR" + " " ;
		}
	    }

  	    sqlStr += ") \n" ;
	    if (tokens.size() > 0) {
		sqlStr += "AND meta.activate = 1\n" ;
		sqlStr += "AND meta.disable_search = 0\n" ;
	    }

		sqlStr += "and CAST(activated_date+' '+activated_time AS DATETIME) < getdate() \n";
	    // Lets append with our new conditions
	    sqlStr += "AND meta.doc_type != 6	--Browserdoc\n" ;
	    // sqlStr += "AND meta.doc_type != 7	--Framesetdoc\n" ;
	    sqlStr += "AND EXISTS -- docs which is not in a menu\n" ;
	    sqlStr += "\t (SELECT meta.meta_id\n" ;
	    sqlStr += "\t FROM childs,  meta\n" ;
	    sqlStr += "\t WHERE childs.to_meta_id = meta.meta_id )\n" ;

	    if (search_area.equals("not_archived")) {
		sqlStr += "AND meta.archive = 0\n" ;
	    }

	    if (search_area.equals("archived")) {
		sqlStr += "AND meta.archive = 1\n" ;
	    }

	    // Lets add the Group by statement
	    sqlStr += "GROUP BY meta.meta_id, meta.meta_headline, meta.meta_text, meta.date_modified, meta.meta_image\n" ;

	    // Lets check if we have an 'AND' selection. If so, then the 'HAVING COUNT'
	    // must be equal to the number of parameters we are searching for
	    // If we have added an 'OR' selection, then the 'HAVING COUNT' can be
	    if(search_prep.equalsIgnoreCase("AND")) {
		sqlStr += "HAVING (COUNT(class.code) >= " + tokens.size() + ")\n" ;
	    } else {
		sqlStr += "HAVING (COUNT(class.code) >= 1)\n " ;
	    }
	    if ( tokens.size() > 0 ) {
		sqlStr += "ORDER BY date_modified DESC" ;
	
	    }
	    /// END OF SEARCH AMONG ALL FIELDS
	} else {
	    // Lets search on ICD 10 CODES
	    // This searchfunktion is for the classification fields
	    // log("Running the new search question") ;

	    // THE NEW CLASSIFICATION STYLE
	    sqlStr = "SELECT meta.meta_id, meta.meta_headline, meta.meta_text, meta.date_modified, meta.meta_image\n" ;
	    sqlStr += "FROM meta\n" ;
	    sqlStr += "JOIN meta_classification mc\n" ;
	    sqlStr += "ON  meta.meta_id = mc.meta_id\n" ;
	    sqlStr += "JOIN classification AS class\n" ;
	    sqlStr += "ON mc.class_id = class.class_id\n" ;
	    sqlStr += "AND (" ;

	    for ( int i = 0 ; i < tokens.size() ; i++ ) {
		String sWord = tokens.elementAt(i).toString() ;
		sqlStr += "( class.code LIKE '" + match + sWord + match + "' )\n" ;
		// Ok, if we search for more than 1 word, then should we always add OR
		if ( i < tokens.size() -1 ) {
		    sqlStr += " " + "OR" + " " ;
		}
	    }

  	    sqlStr += ") \n" ;
	    if (tokens.size() > 0) {
		sqlStr += "AND meta.activate = 1\n" ;
		sqlStr += "AND meta.disable_search = 0\n" ;
	    }
		
		sqlStr += "and CAST(activated_date+' '+activated_time AS DATETIME) < getdate() \n";

	    // Lets append with our new conditions
	    sqlStr += "AND meta.doc_type != 6	--Browserdoc\n" ;
	    // sqlStr += "AND meta.doc_type != 7	--Framesetdoc\n" ;
	    sqlStr += "AND EXISTS -- docs which is not in a menu\n" ;
	    sqlStr += "\t (SELECT meta.meta_id\n" ;
	    sqlStr += "\t FROM childs,  meta\n" ;
	    sqlStr += "\t WHERE childs.to_meta_id = meta.meta_id )\n" ;

	    if (search_area.equals("not_archived")) {
		sqlStr += "AND meta.archive = 0\n" ;
	    }

	    if (search_area.equals("archived")) {
		sqlStr += "AND meta.archive = 1\n" ;
	    }
		sqlStr += "and CAST(activated_date+' '+activated_time AS DATETIME) < getdate() \n";
		
	    // Lets add the Group by statement
	    sqlStr += "GROUP BY meta.meta_id, meta.meta_headline, meta.meta_text, meta.date_modified, meta.meta_image\n" ;

	    // Lets check if we have an 'AND' selection. If so, then the 'HAVING COUNT'
	    // must be equal to the number of parameters we are searching for
	    // If we have added an 'OR' selection, then the 'HAVING COUNT' can be
	    if(search_prep.equalsIgnoreCase("AND")) {
		sqlStr += "HAVING (COUNT(class.code) >= " + tokens.size() + ")\n" ;
	    } else {
		sqlStr += "HAVING (COUNT(class.code) >= 1)\n " ;
	    }
	    if ( tokens.size() > 0 ) {
		sqlStr += "ORDER BY date_modified DESC" ;

	    }
	} // End else
	System.out.println("sql: "+sqlStr);

	return sqlStr ;

    } // End buildsqlStr





    /**
       @deprecated Do not use! No garranti that it will work.
    */
    protected String parseOneRecord(String imcserver, String servletUrl, Vector dataV, String htmlFile
				    , String langPrefix) {
	Vector aRecord = new Vector();
	aRecord.add("#SERVLET_URL#") ;
	aRecord.add(servletUrl) ;
	aRecord.add("#META_ID#") ;
	aRecord.add( (String) dataV.elementAt(0)) ;
	aRecord.add("#META_HEADLINE#") ;
	aRecord.add( (String) dataV.elementAt(1)) ;
	aRecord.add("#META_TEXT#") ;
	aRecord.add( (String) dataV.elementAt(2)) ;
	aRecord.add("#MOD_DATE#") ;
	aRecord.add( dataV.elementAt(3).toString()) ;
	aRecord.add("#HIT_NBR#") ;
	aRecord.add( dataV.elementAt(4).toString()) ;

	StringTokenizer tokens = new java.util.StringTokenizer(dataV.elementAt(5).toString()) ;
	StringBuffer emps = new StringBuffer() ;
	while (tokens.hasMoreTokens()) {
	    String token = tokens.nextToken() ;
	    emps.append("&emp="+java.net.URLEncoder.encode(imcode.server.HTMLConv.toHTML(token))) ;
	    emps.append("&emp="+java.net.URLEncoder.encode(token)) ;
	}
	aRecord.add("#EMPHASIZE#") ;
	aRecord.add( emps.toString() ) ;

	
	String meta_image = dataV.elementAt(6).toString() ;
	aRecord.add("#META_IMAGE#") ;
	if (!"".equals(meta_image)) {
	    aRecord.add("<img src=\""+meta_image+"\" width=\"32\" height=\"32\">") ;
	} else {
	    aRecord.add("") ;
	}

	try {
	    return IMCServiceRMI.parseDoc(imcserver,aRecord,htmlFile,langPrefix);
	} catch(java.io.IOException e) {
	    log(e.getMessage()) ;
	    return "" ;
	}
    }

    /**
       @deprecated Do not use! Its no garanti that it vill work at all!
    */
    protected Properties getSummaryProps(String servlet_url) {
	Properties p = new Properties() ;
	//Summary
	p.put("#SERVLET_URL#",servlet_url)  ;
	p.put("#TOTAL_HITS#", "0") ;
	p.put("#HITS_RANGE#", "1-10") ;
	p.put("#START#", "0") ;
	// Next Previous pages
	p.put("#NEXT_PAGE#", "") ;
	p.put("#PREV_PAGE#", "") ;
	//Search parameters
	p.put("#SEARCH_TYPE#", "") ;
	p.put("#STRING_MATCH#", "") ;
	p.put("#SEARCH_AREA#", "") ;
	p.put("#QUEST#", "") ;
	p.put("#SEARCH_PREP#", "");

	p.put("#ONE_RECORD#", "") ;


	return p ;
    }


    /**
       @deprecated Do not use! Its no garanti that it vill work at all!
    */
    protected Vector convert(Properties p ) {
	Vector v = new Vector();
	Enumeration keys = p.keys() ;
	String key ;
	String val ;
	while( keys.hasMoreElements() ) {
	    key = (String) keys.nextElement() ;
	    val = p.getProperty(key) ;
	    v.add(key) ;
	    v.add(val) ;
	}

	return v ;
    }

    /**
       @deprecated Do not use! Its no garanti that it vill work at all!
    */
    public String verifySqlText(String str ) { 
	StringBuffer buf =  new StringBuffer(str) ;
	char apostrof = '\'' ;
	for(int i = 0 ; i < buf.length() ; i++) { 
	    if (buf.charAt(i) == apostrof ) {
		buf.insert(i,apostrof) ;
		i+=1 ;
	    }
	}
	str = buf.toString() ;
	return str ;
    }
	
	
} // End class

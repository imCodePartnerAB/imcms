import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.* ;
import imcode.util.* ;
import imcode.util.log.* ;
import java.text.* ;


/**
  Templates in use by this servlet:
    existing_doc.html     = the startpage
    existing_doc_hit.html = One record hit html
    existing_doc_res.html = summary page for all hits on the search

**/

public class GetExistingDoc extends HttpServlet {




        /**
         * init()
         */
    public void init(ServletConfig config) throws ServletException {
        super.init(config) ;
    }

        /**
         * doPost()
         */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String host 			= req.getHeader("Host") ;
        String imcserver 		= Utility.getDomainPref("adminserver",host) ;
        String start_url        	= Utility.getDomainPref( "start_url",host ) ;

        imcode.server.User user ;
        String htmlStr = "" ;
        String submit_name = "" ;
        String values[] ;
        int existing_meta_id = 0 ;

        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();

        // get meta_id
        int meta_id = Integer.parseInt(req.getParameter("meta_id_value")) ;

        // get submit
        values = req.getParameterValues("submit") ;
        if (values != null)
            submit_name = values[0] ;

        // Get the session
        HttpSession session = req.getSession(true);

        // Does the session indicate this user already logged in?
        Object done = session.getValue("logon.isDone");  // marker object
        user = (imcode.server.User)done ;

        if (done == null) {
            // No logon.isDone means he hasn't logged in.
            // Save the request URL as the true target and redirect to the login page.
            String scheme = req.getScheme();
            String serverName = req.getServerName();
            int p = req.getServerPort();
            String port = (p == 80) ? "" : ":" + p;
            res.sendRedirect(scheme + "://" + serverName + port + start_url) ;
            return ;
        }

        // Lets get the doc_menu_number
        int doc_menu_no = 0 ;
        try {
            doc_menu_no = Integer.parseInt(req.getParameter("doc_menu_no")) ;
        } catch (NumberFormatException ex) {
            Log.getLog("errors").log(Log.ERROR, "\"doc_menu_no\" not found in GetExistingDoc.", ex) ;
            return ;
        }

        StringBuffer searchResults = new StringBuffer() ;

        if ( (req.getParameter("cancel") != null) || (req.getParameter("cancel.x") != null) ) {
           log("Cancel add existing doc") ;
           byte [] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
           out.write ( tempbytes ) ;
           return ;
        } // Cancel

        // SEARCH
        // Lets do a search among existing documents.
        // Lets collect the parameters and build a sql searchstring
        if ( (req.getParameter("search") != null) || (req.getParameter("search.x") != null) ) {
            // log("Search an existing doc") ;

            String sqlString = "";
            String fromDoc = "";
            String userId = "";
            String searchString = "";
            String searchPrep = "";
            String doctype = "";
            String sortBy = "";
            String includeDocStr = "";

            searchString = req.getParameter("searchstring");
            searchPrep = req.getParameter("search_prep");

            // Lets build a comma separetad string with the doctypes
            String docTypes[] = req.getParameterValues("doc_type") ;
            doctype = this.createDocTypeString(docTypes) ;

            String start_date = req.getParameter("start_date");
            String end_date = req.getParameter("end_date");
            String include_docs[] = req.getParameterValues("include_doc");
            String dateString = "'" + start_date + "','" + end_date + "'";
            String created_date = "'', ''";
            String changed_date = "'', ''";
            String activated_date = "'', ''";
            String archived_date = "'', ''";

            if(include_docs == null)
              include_docs = new String[0] ;

            for(int i = 0; i < include_docs.length; i++) {
                if(include_docs[i].equals("created"))
                    created_date = dateString;
                if(include_docs[i].equals("changed"))
                    changed_date = dateString;
                if(include_docs[i].equals("activated"))
                    activated_date = dateString;
                if(include_docs[i].equals("archived"))
                    archived_date = dateString;
            }

            includeDocStr = created_date + ", " + changed_date + ", " + activated_date + ", " + archived_date;
            sortBy = req.getParameter("sortBy");
            userId = "" + user.getObject("user_id");
            fromDoc = "1";

            // Lets get the language prefix
            String langPrefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = " + user.getInt("lang_id"));

            // Lets check that the sortby option is valid by run the method
            // "SortOrder_GetExistingDocs 'lang_prefix' wich will return
            // an array with all the document types. By adding the key-value pair
            // array into an hashtable and check if the sortorder exists in the hashtable.
            // we are able to determine if the sortorder is okay.

            // Lets fix the sortby list, first get the displaytexts from the database
             String[] sortOrder = IMCServiceRMI.sqlProcedure(imcserver,  "SortOrder_GetExistingDocs '" + langPrefix + "'") ;
             Vector sortOrderV = this.convert2Vector(sortOrder) ;
             Hashtable sortOrderHash  = this.convert2Hashtable(sortOrder) ;
             if (sortOrderHash.containsKey(sortBy) == false)
             {
                log("GetExistingDoc: invalid parameter:sortBy=" + sortBy
                + "\n      setting sortBy to 'meta_id'");
                sortBy = "meta_id";
             }

             // log("sortBy+ " + sortBy) ;

             /*

            check.put("meta_id", "meta_id");
            check.put("doc_type", "doc_type");
            check.put("meta_headline", "meta_headline");
            check.put("meta_text", "meta_text");
            check.put("show_meta", "show_meta");
            check.put("disable_search", "disable_search");
            check.put("date_created", "date_created");
            check.put("date_modified", "date_modified");
            check.put("date_activated", "date_activated");
            check.put("date_archived", "date_archived");
            check.put("archive", "archive");
            check.put("shared", "shared");
            if (check.get(sortBy) == null)
            {
                log("GetExistingDoc: invalid parameter:sortBy=" + sortBy
                + "\n      setting sortBy to 'meta_id'");
                sortBy = "meta_id";

            }
            */

            //------------------------------------------------------------------
            // parse searchString, replaces SPACE with RETURN and EMPTY with RETURN
            Character char13 = new Character((char)(13));
            while (searchString.indexOf(" ") != -1)
            {
                int spaceIndex = searchString.indexOf(" ");
                searchString = searchString.substring(0, spaceIndex)
                + char13 + searchString.substring(spaceIndex+1, searchString.length());
            }
            if(searchString.equals(""))
                searchString = "" + char13;

            // FIXME: Maximum number of hits is 1000.
            sqlString = "SearchDocs " + userId + ",'" + searchString + "', '" + searchPrep + "', '" + doctype + "', " + fromDoc + ", " + "1000" + ", '" + sortBy + "', " + includeDocStr + ", '1'";
            //log("SQL: " + sqlString) ;
            String[][] sqlResults = IMCServiceRMI.sqlProcedureMulti(imcserver, sqlString);
            Vector outVector = new Vector();

            // Lets get the resultpage fragment used for an result
            String oneRecHtmlSrc = IMCServiceRMI.parseDoc(imcserver, null, "existing_doc_hit.html", langPrefix) ;

            // Lets get all document types and put them in a hashTable
             String[] allDocTypesArray = IMCServiceRMI.getDocumentTypesInList(imcserver, langPrefix) ;
             Hashtable allDocTypesHash = this.convert2Hashtable(allDocTypesArray) ;

            // Lets parse the searchresults
              searchResults =  this.parseSearchResults(imcserver, oneRecHtmlSrc, sqlResults, allDocTypesHash) ;

            // Lets get the surrounding resultpage fragment used for all the result
            // and parse all the results into this summarize html template for all the results
            Vector tmpV = new Vector() ;
            tmpV.add("#searchResults#") ;
            tmpV.add(searchResults.toString()) ;
            searchResults.replace(0, searchResults.length(),
            IMCServiceRMI.parseDoc(imcserver, tmpV, "existing_doc_res.html", langPrefix)) ;

             // Lets parse out hidden fields
            outVector.add("#meta_id#") ;
            outVector.add(""+ meta_id) ;
            outVector.add("#doc_menu_no#") ;
            outVector.add("" +doc_menu_no) ;

            // Lets get the searchstring and add it to the page
            outVector.add("#searchstring#") ;
            String searchStr = (req.getParameter("searchstring")==null) ? "" : (req.getParameter("searchstring")) ;
            outVector.add(searchStr);

            // Lets get the date used in the html page, otherwise, use todays date
            String startDateStr = (req.getParameter("start_date")==null) ? "" : (req.getParameter("start_date")) ;
            String endDateStr = (req.getParameter("end_date")==null) ? "" : (req.getParameter("end_date")) ;
            Date startDate = new Date() ;
            Date endDate = new Date() ;
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");

            try {
              startDate = formatter.parse(startDateStr) ;
              endDate = formatter.parse(endDateStr) ;
            } catch( ParseException e) {
              // we failed to parse the startdatestring, however, we have already take care of that circumstance
            }

            outVector.add("#start_date#") ;
            outVector.add( formatter.format(startDate) ) ;
            outVector.add("#end_date#") ;
            outVector.add( formatter.format(endDate) ) ;


            // Lets take care of the document types. Get those who were selected
            // and select those again in the page to send back to the user.
            // First, put them in an hashtable for easy access.
            Hashtable selectedDocTypes = new Hashtable(docTypes.length) ;
            for(int i = 0 ; i < docTypes.length; i++ ) {
              selectedDocTypes.put(docTypes[i], docTypes[i]) ;
            }

            // Lets get all possible values of for the documenttypes from database
            for(int i = 0 ; i < allDocTypesArray.length; i+=2 ) {
              outVector.add("#checked_" + allDocTypesArray[i] + "#" ) ;
              if( selectedDocTypes.containsKey(allDocTypesArray[i]) )
                outVector.add("checked" ) ;
              else
                outVector.add("" ) ;
            }


             // Lets take care of the created, changed boxes.
             // first, getallchecked values and put them in a hashtable
            String[] includeDocs = req.getParameterValues("include_doc") ;
            if(includeDocs == null )
              includeDocs = new String[0] ;

            Hashtable selectedIncludeDocs = new Hashtable(includeDocs.length) ;
            for(int i = 0 ; i < includeDocs.length; i++ ) {
              selectedIncludeDocs.put(includeDocs[i], includeDocs[i]) ;
            }

            // Lets create an array with all possible values.
            // in this case just changed resp. created
            String[] allPossibleIncludeDocsValues = {"created", "changed"} ;
            for(int i = 0 ; i < allPossibleIncludeDocsValues.length; i++ ) {
              outVector.add("#include_check_" + allPossibleIncludeDocsValues[i] + "#" ) ;
              if( selectedIncludeDocs.containsKey(allPossibleIncludeDocsValues[i]) )
                outVector.add("checked" ) ;
              else
                outVector.add("" ) ;
            }


            // Lets take care of the search_prep condition, eg and / or
            // first, getallchecked values and put them in a hashtable
            String[] searchPrepArr = req.getParameterValues("search_prep") ;
            if(searchPrepArr == null )
              searchPrepArr = new String[0] ;

            Hashtable selectedsearchPrep = new Hashtable(searchPrepArr.length) ;
            for(int i = 0 ; i < searchPrepArr.length; i++ ) {
              selectedsearchPrep.put(searchPrepArr[i], searchPrepArr[i]) ;
            }
             // Lets create an array with all possible values.
            // in this case just changed resp. created
            String[] allPossibleSearchPreps = {"and", "or"} ;
            for(int i = 0 ; i < allPossibleSearchPreps.length; i++ ) {
              outVector.add("#search_prep_check_" + allPossibleSearchPreps[i] + "#" ) ;
              if( selectedsearchPrep.containsKey(allPossibleSearchPreps[i]) )
                outVector.add("checked" ) ;
              else
                outVector.add("" ) ;
            }

          // Lets fix the sortby list, first get the displaytexts from the database
           // String[] sortOrder = IMCServiceRMI.sqlProcedure(imcserver,  "SortOrder_GetExistingDocs '" + langPrefix + "'") ;
            //Vector sortOrderV = this.convert2Vector(sortOrder) ;
            Html htm  = new Html() ;
            String sortOrderStr = htm.createHtmlCode("ID_OPTION", sortBy, sortOrderV) ;
            outVector.add("#sortBy#") ;
            outVector.add( sortOrderStr ) ;

            outVector.add("#searchResults#") ;
            outVector.add( searchResults.toString() ) ;

        // Send page to browser
        // htmlOut = IMCServiceRMI.parseDoc(imcserver, htmlOut, outVector);
            String htmlOut = IMCServiceRMI.parseDoc(imcserver, outVector, "existing_doc.html", langPrefix);
            out.print(htmlOut);
            return ;
        } // End of searchstuff

     // ************** Lets add a document ***********************
      else if( req.getParameter("addExistingDocs") != null ) {
            // log("Add an existing doc") ;
            user.put("flags",new Integer(262144)) ;

            // get the seleced existing docs
            values = req.getParameterValues("existing_meta_id") ;
            if (values == null)
                values = new String[0] ;

            // Lets loop through all the selected existsing meta ids and add them to the current menu
            try {
                for( int m = 0 ; m < values.length; m++ ) {
                    existing_meta_id = Integer.parseInt(values[m]) ;

                    // Ok, Lets see what we got
                    //log("existing_meta_id: " + existing_meta_id) ;
                    //log("doc_menu_no: " +  doc_menu_no) ;

                    String sqlStr = "select shared from meta where meta_id = "+existing_meta_id ;
                    String shared = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
                    String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

                    // Fetch all doctypes from the db and put them in an option-list
                    // First, get the doc_types the current user may use.
                    String[] user_dt = IMCServiceRMI.sqlProcedure(imcserver,"GetDocTypesForUser "+meta_id+","+user.getInt("user_id")+",'"+lang_prefix+"'") ;
                    HashSet user_doc_types = new HashSet() ;

                    // I'll fill a HashSet with all the doc-types the current user may use,
                    // for easy retrieval.
                    for ( int i=0 ; i<user_dt.length ; i+=2 ) {
                        user_doc_types.add(user_dt[i]) ;
                    }

                    sqlStr = "select doc_type from meta where meta_id = "+existing_meta_id ;
                    String doc_type = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;

                    // Add the document in menu if user is admin for the document OR the document is shared.
                    if ((req.getParameter("addExistingDocs")!=null) && user_doc_types.contains(doc_type) && ("1".equals(shared) || IMCServiceRMI.checkDocAdminRights(imcserver,existing_meta_id,user))) {
                        IMCServiceRMI.addExistingDoc(imcserver,meta_id,user,existing_meta_id,doc_menu_no) ;
                    }

                } // End of for loop
            } catch ( NumberFormatException ex ) {
                byte [] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
                out.write ( tempbytes ) ;
                return ;
            }

            byte [] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
            out.write ( tempbytes ) ;
        } // End of adding document
    } // End doPost


    /**
     * Returns the variables used to parse one row in the resultset from the
     * search page
     *
     *
     */
    private static Vector getSearchHitVector() {
        Vector vector = new Vector() ;
        vector.add("#meta_id#");
        vector.add("#doc_type#");
        vector.add("#meta_headline#");
        vector.add("#meta_text#");
        vector.add("#date_created#");
        vector.add("#date_modified#");
        vector.add("#date_activated#");
        vector.add("#date_archived#");
        vector.add("#archive#");
        vector.add("#shared#");
        vector.add("#show_meta#");
        vector.add("#disable_search#");
        vector.add("#doc_count");
        return vector ;
    }

         /**
          * * Local helpmehtod
         * Convert array to vector
         */

    private static Vector convert2Vector(String[] arr) {
        if(arr == null)
          return new Vector() ;

        Vector v = new Vector(arr.length) ;
        for(int i = 0; i<arr.length; i++)
            v.add(arr[i]) ;
        return v ;
    }


    /**
     * Local helpmehtod
    * Takes an array as argument and creates an hashtable the information.
    * Expects that the first element will be the key and the next element in the
    * array will be the value.
    */

    private static Hashtable convert2Hashtable(String[] arr) {

      Hashtable h = new Hashtable() ;
      for(int i = 0; i <arr.length; i+=2 ) {
        h.put(arr[i], arr[i+1] ) ;
      }
      return h ;
    }


    /**
     * Local helpmehtod
    * Takes an array as argument and creates a commasepared string with the values int the array
    * The string will be used when we create the sql searchquestion
    */

    private static String createDocTypeString(String[] docTypes) {
        StringBuffer doctype = new StringBuffer() ;
        if(docTypes == null)
              docTypes = new String[0] ;

        for(int k = 0; k < docTypes.length; k++) {
           doctype.append( docTypes[k] ) ;
           if(k != docTypes.length - 1)
              doctype.append( ", ") ;
        }
        return doctype.toString() ;
    }


    /**
    * Local helpmehtod
    * Parses all the searchhits and returns an StringBuffer
    */

    private static StringBuffer parseSearchResults(String imcserver, String oneRecHtmlSrc,
      String[][] sqlResults, Hashtable allDocTypesHash) throws java.io.IOException{
        StringBuffer searchResults = new StringBuffer(1024) ;
        int docTypeIndex = 1 ;  // Index of where the doctype id is placed in one record array

       // Lets parse the searchresults
         Vector oneRecVariables = GetExistingDoc.getSearchHitVector() ;
         for(int i = 0 ; i < sqlResults.length; i++ ) {
            String[] oneRec = sqlResults[i] ;
            Vector tmpVecData = new Vector(oneRecVariables.size() ) ;

            // Lets parse one record
            for(int k = 0 ; k < oneRec.length; k++ ) {
            //log("oneRec: "  + oneRec[k]) ;
                    if(docTypeIndex == k ) {
                      String docTypeName = (String) allDocTypesHash.get("" + oneRec[docTypeIndex]) ;
                      //log("docTypeName: " + docTypeName ) ;
                      oneRec[k] = docTypeName ;
                    }

                    if( oneRec[k].equalsIgnoreCase(""))
                        tmpVecData.add("&nbsp;") ;
                    else
                        tmpVecData.add(oneRec[k]) ;
                }
                searchResults.append(IMCServiceRMI.parseDoc(imcserver, oneRecHtmlSrc,oneRecVariables, tmpVecData )) ;
            }
      return searchResults ;
      }

} // End class

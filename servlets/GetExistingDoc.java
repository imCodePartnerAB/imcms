import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.server.*;
import imcode.util.* ;
import imcode.util.log.* ;

public class GetExistingDoc extends HttpServlet {

/*	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}
*/
   	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

        res.setContentType("text/html");
        ServletOutputStream out = res.getOutputStream();
        HttpSession session = req.getSession(true);
        Object done = session.getValue("logon.isDone");
        User user = (User)done;
        if(done == null) {
            String scheme = req.getScheme();
            String serverName = req.getServerName();
            int i = req.getServerPort();
            String port = i != 80 ? ":" + i : "";
            res.sendRedirect(scheme + "://" + serverName + port + start_url);
            return;
        }

        // Lets insert the existing doc
          if ( req.getParameter("addExistingDocs") != null ) {
             log("Lets add a document") ;
             String[] metaId = req.getParameterValues("existing_meta_id") ;
             try {
              for( int i = 0; i< metaId.length; i++) {
                String aMetaId = metaId[i] ;
                int mId = Integer.parseInt(aMetaId) ;
                //String sql = IMCServiceRMI.activateChild(imcServer, aMetaId, user);
              }
             } catch (NumberFormatException e) {

                  // FIXME: Verify the arguments to this function
                  Log.log("?????" , Log.ERROR, "No metaid could be found: " + e.getMessage(), e) ;
                  throw new RuntimeException(e.getMessage()) ;
               }

              // Send page to userhere....
              return ;
             }


        // ******************'  Test av funktion
      //  String[] arr  =  {"1001", "1002"} ;
      //  Hashtable h = IMCServiceRMI.ExistingDocsGetMetaIdInfo(imcserver,arr) ;
      //  log("h: " + h.toString()) ;
      //   *********************************


//**********************************************************************************
        String sqlString = "";
        String hitsPerPage = "";
        String fromDoc = "";
        String userId = "";
        String searchString = "";
        String searchPrep = "";
        String doctype = "";
        String sortBy = "";
        String includeDocStr = "";
        //String s15 = "";
        //String s17 = "";
        int j = 0;

        if( req.getParameter("sqlstring") == null) {
            log("sqlString saknas") ;
            searchString = req.getParameter("searchstring");
            searchPrep = req.getParameter("search_prep");

           // Lets build a comma separetad string with the doctypes
            String docTypes[] = req.getParameterValues("doc_type");
            for(int k = 0; k < docTypes.length; k++) {
                doctype = doctype + docTypes[k];
                if(k != docTypes.length - 1)
                    doctype = doctype + ", ";
            }

            String start_date = req.getParameter("start_date");
            String end_date = req.getParameter("end_date");
            String include_docs[] = req.getParameterValues("include_doc");
            String dateString = "'" + start_date + "','" + end_date + "'";
            String created_date = "'', ''";
            String changed_date = "'', ''";
            String activated_date = "'', ''";
            String archived_date = "'', ''";
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
            hitsPerPage = req.getParameter("HitsPerPage");
            userId = "" + user.getObject("user_id");
            fromDoc = "1";
        } else {
            log("SQL string var inte null") ;
            sqlString = req.getParameter("sqlstring");
            hitsPerPage = req.getParameter("hitsPerPage");
            fromDoc = req.getParameter("fromDoc");
            userId = req.getParameter("userID");
            searchString = req.getParameter("searchString");
            searchPrep = req.getParameter("search_prep");
            doctype = req.getParameter("doc_types");
            sortBy = req.getParameter("sortBy");
            includeDocStr = req.getParameter("includeDocStr");
            j = Integer.parseInt(req.getParameter("doc_count"));
            if(req.getParameter("next") != null)
                fromDoc = "" + (Integer.parseInt(fromDoc) + Integer.parseInt(hitsPerPage));
            else
                fromDoc = "" + (Integer.parseInt(fromDoc) - Integer.parseInt(hitsPerPage));
        }

		//-----------------------------------------------------------------
		// check form-parameters: sortBy

		Hashtable check = new Hashtable();
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
		//log("searchString: " + searchString);
		//------------------------------------------------------------------
        sqlString = "SearchDocs " + userId + ",'" + searchString + "', '" + searchPrep + "', '" + doctype + "', " + fromDoc + ", " + hitsPerPage + ", '" + sortBy + "', " + includeDocStr + ", '1'";
        //String s19 = "";
        //String s20 = "";
        log("SQL: " + sqlString) ;
        Hashtable hashtable = IMCServiceRMI.sqlQueryHash(imcserver, sqlString);
        String nbrOfHits[] = (String[])hashtable.get("doc_count");
        try {
            j = Integer.parseInt(nbrOfHits[1]);
        }
        catch(Exception exception) {
            j = 0;
        }
        Vector vector = new Vector();
        vector.add("#meta_id#");
        vector.add("metaID");
        vector.add("#meta_headline#");
        vector.add("Rubrik");
        vector.add("#meta_text#");
        vector.add("Underrubrik");
        vector.add("#doc_type#");
        vector.add("Dok.typ");
        vector.add("#date_created#");
        vector.add("Skapad datum");
        vector.add("#date_modified#");
        vector.add("\304ndrad datum");
        vector.add("#date_archived#");
        vector.add("Arkiverad datum");
        vector.add("#date_activated#");
        vector.add("Aktiverad datum");
        vector.add("#archive#");
        vector.add("Arkiverad");
        vector.add("#doc_start#");
        vector.add(fromDoc);
        vector.add("#doc_end#");
        int l = (Integer.parseInt(fromDoc) + Integer.parseInt(hitsPerPage)) - 1;
        if(l > j)
            l = j;
        vector.add("" + l);
        if(fromDoc.equals("1")) {
            vector.add("#butt_hide_start1#");
            vector.add("<!--");
            vector.add("#butt_hide_end1#");
            vector.add("-->");
        } else {
            vector.add("#butt_hide_start1#");
            vector.add("");
            vector.add("#butt_hide_end1#");
            vector.add("");
        }
        if((Integer.parseInt(fromDoc) + Integer.parseInt(hitsPerPage)) - 1 >= j) {
            vector.add("#butt_hide_start2#");
            vector.add("<!--");
            vector.add("#butt_hide_end2#");
            vector.add("-->");
        } else {
            vector.add("#butt_hide_start2#");
            vector.add("");
            vector.add("#butt_hide_end2#");
            vector.add("");
        }
        vector.add("#end_value#");
        vector.add("" + hitsPerPage);
        vector.add("#sqlstring#");
        vector.add(sqlString);
        vector.add("#hitsPerPage#");
        vector.add(hitsPerPage);
        vector.add("#fromDoc#");
        vector.add(fromDoc);
        vector.add("#userID#");
        vector.add(userId);
        vector.add("#searchString#");
        vector.add(searchString);
        vector.add("#search_prep#");
        vector.add(searchPrep);
        vector.add("#doc_types#");
        vector.add(doctype);
        vector.add("#sortBy#");
        vector.add(sortBy);
        vector.add("#includeDocStr#");
        vector.add(includeDocStr);
        String langID = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = " + user.getInt("lang_id"));

        // Lets get the template
        String htmlOut = IMCServiceRMI.parseDoc(imcserver, vector, "existing_doc_RESULTS.html", langID);
        int j1 = htmlOut.indexOf("<?");
        int k1 = htmlOut.indexOf("?>", j1);
        if(j1 == -1 || k1 == -1 || j1 > k1)
            throw new RuntimeException("Can't find start of loop!");
        int l1 = htmlOut.indexOf("@loop", j1);
        int i2 = htmlOut.indexOf("%name:", j1);
        int j2 = htmlOut.indexOf("%start:", j1);
        int k2 = htmlOut.indexOf("%end:", j1);
        if(i2 == -1 || j2 == -1 || k2 == -1 || l1 == -1 || i2 > k1 || j2 > k1 || k2 > k1 || l1 > k1)
            throw new RuntimeException("Can't find loop parameters!");
        i2 = htmlOut.indexOf("\"", i2);
        int l2 = htmlOut.indexOf("\"", i2 + 1);
        j2 = htmlOut.indexOf("\"", j2);
        int i3 = htmlOut.indexOf("\"", j2 + 1);
        k2 = htmlOut.indexOf("\"", k2);
        int j3 = htmlOut.indexOf("\"", k2 + 1);
        String s29 = htmlOut.substring(i2 + 1, l2);
        int k3 = 0;
        int l3 = 0;
        try {
            k3 = Integer.parseInt(htmlOut.substring(j2 + 1, i3));
            l3 = Integer.parseInt(htmlOut.substring(k2 + 1, j3));
            if(l3 > l - Integer.parseInt(fromDoc))
                l3 = l - Integer.parseInt(fromDoc);
        }
        catch(NumberFormatException numberformatexception) {
            throw new RuntimeException("Can't make out start and/or stop parameters!");
        }
        int i4 = htmlOut.indexOf("<?", k1 + 1);
        int j4 = htmlOut.indexOf("?>", i4);
        l1 = htmlOut.indexOf("@/loop", i4);
        if(i4 == -1 || j4 == -1 || l1 == -1 || i4 > j4 || l1 > j4)
            throw new RuntimeException("Can't find the end of the loop!");
        String s30 = htmlOut.substring(k1 + 2, i4);
        String s31 = htmlOut.substring(0, j1);
        String s32 = htmlOut.substring(j4 + 2, htmlOut.length());
        String s33 = "";
        String s34 = s30;
        int k4 = s34.indexOf("%" + s29 + "%");
        for(int i = k3; i <= l3; i++) {
            while(k4 != -1)  {
                s34 = s34.substring(0, k4) + i + s34.substring(k4 + s29.length() + 2, s34.length());
                k4 = s34.indexOf("%" + s29 + "%");
            }
            s33 = s33 + s34;
            s34 = s30;
            k4 = s34.indexOf("%" + s29 + "%");
        }

        htmlOut = s31 + s33 + s32;
        vector.clear();
        for(Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();) {
            String tmpElement = (String)enumeration.nextElement();
            String as2[] = (String[])hashtable.get(tmpElement);
            for(int i = 0; i < as2.length; i++) {
                vector.add("#" + tmpElement + (k3 + i) + "#");
                vector.add(as2[i].equals("") ? "&nbsp;" : ((Object) (as2[i])));
            }

            for(int j5 = as2.length; j5 < Integer.parseInt(hitsPerPage); j5++) {
                vector.add("#" + tmpElement + (k3 + j5) + "#");
                vector.add("&nbsp;");
            }

        }

        htmlOut = IMCServiceRMI.parseDoc(imcserver, htmlOut, vector);
        out.print(htmlOut);
    }
}

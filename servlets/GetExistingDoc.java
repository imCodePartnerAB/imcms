import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.server.*;
import imcode.util.* ;
import imcode.util.log.* ;

public class GetExistingDoc extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

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
//**********************************************************************************
        String sqlString = "";
        String s6 = "";
        String s7 = "";
        String s9 = "";
        String searchString = "";
        String searchPrep = "";
        String s12 = "";
        String sortBy = "";
        String s14 = "";
        String s15 = "";
        String s17 = "";
        int j = 0;
        if(req.getParameter("sqlstring") == null) {
            searchString = req.getParameter("searchstring");
            searchPrep = req.getParameter("search_prep");
            String as[] = req.getParameterValues("doc_type");
            for(int k = 0; k < as.length; k++) {
                s12 = s12 + as[k];
                if(k != as.length - 1)
                    s12 = s12 + ", ";
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

            s14 = created_date + ", " + changed_date + ", " + activated_date + ", " + archived_date;
            sortBy = req.getParameter("sortBy");
            s6 = req.getParameter("HitsPerPage");
            s9 = "" + user.getObject("user_id");
            s7 = "1";
        } else {
            sqlString = req.getParameter("sqlstring");
            s6 = req.getParameter("hitsPerPage");
            s7 = req.getParameter("fromDoc");
            s9 = req.getParameter("userID");
            searchString = req.getParameter("searchString");
            searchPrep = req.getParameter("search_prep");
            s12 = req.getParameter("doc_types");
            sortBy = req.getParameter("sortBy");
            s14 = req.getParameter("includeDocStr");
            j = Integer.parseInt(req.getParameter("doc_count"));
            if(req.getParameter("next") != null)
                s7 = "" + (Integer.parseInt(s7) + Integer.parseInt(s6));
            else
                s7 = "" + (Integer.parseInt(s7) - Integer.parseInt(s6));
        }
		// kolla parametrar
		// sortBy
		//---------------------------
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
		//----------------------------
        sqlString = "EXEC SearchDocs " + s9 + ",'" + searchString + "', '" + searchPrep + "', '" + s12 + "', " + s7 + ", " + s6 + ", '" + sortBy + "', " + s14 + ", '1'";
        String s19 = "";
        String s20 = "";
        Hashtable hashtable = IMCServiceRMI.sqlQueryHash(imcserver, sqlString);
        String as3[] = (String[])hashtable.get("doc_count");
        try {
            j = Integer.parseInt(as3[1]);
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
        vector.add(s7);
        vector.add("#doc_end#");
        int l = (Integer.parseInt(s7) + Integer.parseInt(s6)) - 1;
        if(l > j)
            l = j;
        vector.add("" + l);
        if(s7.equals("1")) {
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
        if((Integer.parseInt(s7) + Integer.parseInt(s6)) - 1 >= j) {
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
        vector.add("" + s6);
        vector.add("#sqlstring#");
        vector.add(sqlString);
        vector.add("#hitsPerPage#");
        vector.add(s6);
        vector.add("#fromDoc#");
        vector.add(s7);
        vector.add("#userID#");
        vector.add(s9);
        vector.add("#searchString#");
        vector.add(searchString);
        vector.add("#search_prep#");
        vector.add(searchPrep);
        vector.add("#doc_types#");
        vector.add(s12);
        vector.add("#sortBy#");
        vector.add(sortBy);
        vector.add("#includeDocStr#");
        vector.add(s14);
        String langID = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = " + user.getInt("lang_id"));
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
            if(l3 > l - Integer.parseInt(s7))
                l3 = l - Integer.parseInt(s7);
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
        for(int l4 = k3; l4 <= l3; l4++) {
            while(k4 != -1)  {
                s34 = s34.substring(0, k4) + l4 + s34.substring(k4 + s29.length() + 2, s34.length());
                k4 = s34.indexOf("%" + s29 + "%");
            }
            s33 = s33 + s34;
            s34 = s30;
            k4 = s34.indexOf("%" + s29 + "%");
        }

        htmlOut = s31 + s33 + s32;
        vector.clear();
        for(Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();) {
            String s21 = (String)enumeration.nextElement();
            String as2[] = (String[])hashtable.get(s21);
            for(int i5 = 0; i5 < as2.length; i5++) {
                vector.add("#" + s21 + (k3 + i5) + "#");
                vector.add(as2[i5].equals("") ? "&nbsp;" : ((Object) (as2[i5])));
            }

            for(int j5 = as2.length; j5 < Integer.parseInt(s6); j5++) {
                vector.add("#" + s21 + (k3 + j5) + "#");
                vector.add("&nbsp;");
            }

        }

        htmlOut = IMCServiceRMI.parseDoc(imcserver, htmlOut, vector);
        out.print(htmlOut);
    }
}

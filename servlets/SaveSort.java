import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;

/**
   Save document sorting (date,name,manual)
*/
public class SaveSort extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
    private final static String COPY_PREFIX_TEMPLATE = "copy_prefix.html";

    private final static String FILE_WARNING_TEMPLATE = "copy_file_warning.html" ;

    /**
       init()
    */
    public void init( ServletConfig config ) throws ServletException {
	super.init( config ) ;
    }

    /**
       service()
    */
    public void service( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String start_url	= imcref.getStartUrl() ;

	imcode.server.User user ;
	String htmlStr = "" ;
	String child_str = "" ;
	int meta_id ;
	Vector childs  = new Vector( ) ;
	Vector sort_no = new Vector( ) ;

	res.setContentType( "text/html" );
	Writer out = res.getWriter( ) ;
	meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
	int doc_menu_no = Integer.parseInt( req.getParameter( "doc_menu_no" ) ) ;
	child_str =  req.getParameter( "childs" ) ;


	// Check if user logged on
	if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
	    return ;
	}

	if ( !imcref.checkDocAdminRights(meta_id,user,262144 ) ) {	// Checking to see if user may edit this
	    String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
	    if ( output != null ) {
		out.write(output) ;
	    }
	    return ;
	}

	String temp_str = "" ;
	String childsThisMenu[] ;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
	Date dt = imcref.getCurrentDate() ;
	String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
	imcref.sqlUpdateQuery(sqlStr) ;

	sqlStr = "select to_meta_id from childs where meta_id = " + meta_id ;
	String[] foo = imcref.sqlQuery( sqlStr) ;

	for ( int i=0 ; i<foo.length ; ++i ) {
	    temp_str = req.getParameter(foo[i]) ;
	    if ( temp_str != null ) {
		childs.add(foo[i]) ;
		sort_no.add(temp_str) ;
	    }
	}


	childsThisMenu = req.getParameterValues( "archiveDelBox" ) ;

	user.put("flags",new Integer(262144)) ;

	if( req.getParameter("sort")!=null ) {
	    String sort_order  = req.getParameter("sort_order") ;
	    imcref.sqlUpdateQuery("update text_docs set sort_order = "+sort_order+" where meta_id = "+meta_id) ;
	    if ( childs.size() > 0 ) {
		imcref.saveManualSort(meta_id,user,childs,sort_no ) ;
	    }
	} else if( req.getParameter("delete")!=null ) {
	    if( childsThisMenu != null ) {
		imcref.deleteChilds(meta_id,doc_menu_no,user,childsThisMenu ) ;
	    }
	} else if( req.getParameter("archive")!=null ) {
	    if( childsThisMenu != null ) {
		imcref.archiveChilds(meta_id,user,childsThisMenu ) ;
	    }
	} else if( req.getParameter("copy")!=null ) {
	    if( childsThisMenu != null ) {
		String copyPrefix = imcref.parseDoc( null, COPY_PREFIX_TEMPLATE, user.getLangPrefix());

		String[] file_meta_ids = imcref.copyDocs(meta_id,doc_menu_no,user,childsThisMenu,copyPrefix ) ;
		if (file_meta_ids.length > 0) {
		    // Build an option-list
		    StringBuffer fileMetaIds = new StringBuffer() ;
		    for (int i=0; i<file_meta_ids.length; ++i) {
			imcode.server.parser.Document doc = imcref.getDocument(Integer.parseInt(file_meta_ids[i])) ;
			fileMetaIds.append("<option>["+file_meta_ids[i]+"] "+doc.getHeadline()+"</option>") ;
		    }
		    Vector vec = new Vector() ;
		    vec.add("#meta_id#") ; vec.add(""+meta_id) ;
		    vec.add("#filedocs#") ; vec.add(fileMetaIds.toString()) ;
		    String fileWarning = imcref.parseDoc(vec,FILE_WARNING_TEMPLATE, user.getLangPrefix()) ;
		    out.write(fileWarning) ;
		    return ;
		}
	    }
	}

	String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
	if ( output != null ) {
	    out.write(output) ;
	}
    }
}

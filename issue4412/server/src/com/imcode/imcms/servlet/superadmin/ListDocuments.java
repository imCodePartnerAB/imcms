package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.math.IntRange;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

public class ListDocuments extends HttpServlet {

    public static final String PARAMETER_BUTTON__LIST = "showspan";
    public static final String PARAMETER__LIST_START = "start";
    public static final String PARAMETER__LIST_END = "end";

    public static final String REQUEST_ATTRIBUTE__FORM_DATA = "formData";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );

        if (!user.isSuperAdmin()) {
            return ;
        }

        ImcmsServices imcref = Imcms.getServices();

        IntRange allDocumentsRange = new IntRange( getMinDocumentId( imcref ), getMaxDocumentId( imcref ) );

        String startString = req.getParameter( PARAMETER__LIST_START );
        String endString = req.getParameter( PARAMETER__LIST_END );

        int start = null != startString ? Integer.parseInt( startString ) : allDocumentsRange.getMinimumInteger() ;
        int end = null != endString ? Integer.parseInt( endString ) : allDocumentsRange.getMaximumInteger();

        FormData formData = new FormData();
        formData.selectedRange = new IntRange( start, end ) ;
        DocumentMapper documentMapper = imcref.getDocumentMapper() ;
        if ( req.getParameter( PARAMETER_BUTTON__LIST ) != null ) {
            formData.documentsIterator = documentMapper.getDocumentsIterator(formData.selectedRange) ;
        }

        req.setAttribute( REQUEST_ATTRIBUTE__FORM_DATA, formData );
        req.getRequestDispatcher( "/imcms/"+user.getLanguageIso639_2()+"/jsp/document_list.jsp").forward( req, res );

    }

    private int getMaxDocumentId( ImcmsServices imcref ) {
        final Object[] parameters = new String[0];
        return Integer.parseInt( (String) imcref.getDatabase().execute(new SqlQueryCommand("select max(meta_id) from meta", parameters, Utility.SINGLE_STRING_HANDLER)) );
    }

    private int getMinDocumentId( ImcmsServices imcref ) {
        final Object[] parameters = new String[0];
        return Integer.parseInt( (String) imcref.getDatabase().execute(new SqlQueryCommand("select min(meta_id) from meta", parameters, Utility.SINGLE_STRING_HANDLER)) );
    }

    public static class FormData {

        public IntRange selectedRange;

        public Iterator documentsIterator;
    }

}


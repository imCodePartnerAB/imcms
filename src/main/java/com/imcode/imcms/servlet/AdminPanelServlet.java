package com.imcode.imcms.servlet;

import com.imcode.imcms.api.TextDocumentViewing;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.admin.AdminDoc;
import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Tommy Ullberg, imCode
 * Mail: tommy@imcode.com
 * Date: 2010-okt-20
 * Time: 15:41:09
 */
public class AdminPanelServlet extends HttpServlet {
	
    public static final String PARAM_COOKIE_PANEL_HIDE = "imcmsToolBarHide" ;
    
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response, false) ;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response, true) ;
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean isPOST) throws ServletException, IOException {
        boolean hasAdminPanel = false ;
        UserDomainObject user = Utility.getLoggedOnUser(request) ;
        DocumentDomainObject document ;
        try {
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper() ;
            int metaId = AdminPanelServlet.getIntRequestParameter("meta_id", 0, request) ;
            document = documentMapper.getDocument(metaId) ;
            hasAdminPanel = user.hasAdminPanelForDocument(document) ;
        } catch (Exception ignore) {}
        if (hasAdminPanel && null != request.getParameter("ajax")) {
            /*DocumentRequest documentRequest = new DocumentRequest(Imcms.getServices(), user, document, null, request, response) ;
            final ParserParameters parserParameters = new ParserParameters(documentRequest) ;
            Integer userflags = (Integer) request.getSession().getAttribute(AdminDoc.PARAMETER__DISPATCH_FLAGS) ;
            int flags = (null == userflags) ? 0 : userflags ;
            try {
                flags = Integer.parseInt( request.getParameter(AdminDoc.PARAMETER__DISPATCH_FLAGS)) ;
            } catch (Exception ignore) {}
            parserParameters.setFlags(flags) ;
            TextDocumentViewing viewing = new TextDocumentViewing(parserParameters) ;
            TextDocumentViewing.putInRequest(viewing) ;
            ParserParameters.putInRequest(parserParameters) ;*/
            request.getRequestDispatcher("/WEB-INF/imcms/jsp/admin_panel/ajax_handler.jsp").forward(request, response);
        }
    }
    
    public static String getPathToAjaxHandler(int metaId, HttpServletRequest request) {
        return request.getContextPath() + "/servlet/AdminPanelServlet?meta_id=" + metaId + "&ajax=true" ;
    }
    
    public static int getIntRequestParameter(String param, int defaultVal, HttpServletRequest request) {
        if (null != request.getParameter(param) && StringUtils.isNumeric(request.getParameter(param))) {
            try {
                return Integer.parseInt(request.getParameter(param)) ;
            } catch (Exception ignore) {}
        }
        return defaultVal ;
    }

    public static String getCookie( String theName, HttpServletRequest request ) {
        Cookie[] cookies = request.getCookies() ;
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (theName.equalsIgnoreCase(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "" ;
    }

    public static void setCookie( String theName, String theValue, HttpServletResponse response ) {
        Cookie cookie = new Cookie(theName, theValue) ;
        cookie.setMaxAge(60*60*24*365) ;
        cookie.setPath("/") ;
        response.addCookie(cookie) ;
    }
    
}

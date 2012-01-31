package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.mapping.DocumentMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;

import imcode.server.ImcmsServices;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

public class ListDocumentAliasPage extends OkCancelPage {

    public static final String REQUEST_PARAMETER_BUTTON__LIST = "showspan";
    public static final String REQUEST_PARAMETER__LIST_START = "start";
    public static final String REQUEST_PARAMETER__LIST_END = "end";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";

    public String startString ;
    public String endString ;
    public Set<String> aliasInSelectedRange = new TreeSet<String>();

    public ListDocumentAliasPage(DispatchCommand okDispatchCommand, DispatchCommand cancelDispatchCommand, HttpServletRequest request) {
        super(okDispatchCommand, cancelDispatchCommand);
        updateFromRequest(request);
    }

    protected void updateFromRequest(HttpServletRequest request) {
        startString = StringUtils.defaultString(request.getParameter( REQUEST_PARAMETER__LIST_START ), "A");
        endString = StringUtils.defaultString(request.getParameter( REQUEST_PARAMETER__LIST_END ), "Ö");
    }

    protected void dispatchOther(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper() ;
        List alphaRange = Arrays.asList(new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","Å","Ä","Ö" });
        if(!alphaRange.contains(startString.toUpperCase())) {
            startString = alphaRange.get(0).toString();
        }
        if (alphaRange.indexOf(endString.toUpperCase()) < alphaRange.indexOf(startString.toUpperCase())) {
            endString = startString;
        }
        if ( request.getParameter( REQUEST_PARAMETER_BUTTON__LIST ) != null ) {
            List selectedRange = alphaRange.subList(alphaRange.indexOf(startString.toUpperCase()), alphaRange.indexOf(endString.toUpperCase())+1);
            aliasInSelectedRange.clear();
            for (String alias : documentMapper.getAllDocumentAlias()) {
                if (alias.length() > 0 && selectedRange.contains(alias.toUpperCase().substring(0,1))) {
                    aliasInSelectedRange.add(alias);
                }
            }
        }
        forward(request, response);
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return "/imcms/" + user.getLanguageIso639_2()
               + "/jsp/document_alias_list.jsp";
    }

}

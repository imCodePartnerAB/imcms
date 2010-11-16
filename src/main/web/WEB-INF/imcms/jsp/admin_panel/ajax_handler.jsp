<%@ page
	
	import="com.imcode.imcms.api.DocumentVersion,
	        com.imcode.imcms.api.DocumentVersionInfo,
	        com.imcode.imcms.api.I18nLanguage,
	        com.imcode.imcms.mapping.DocumentMapper,
	        com.imcode.imcms.servlet.AdminPanelServlet,
	        com.imcode.imcms.servlet.admin.AdminDoc,
	        imcode.server.DocumentRequest,
	        imcode.server.Imcms,
	        imcode.server.ImcmsConstants,
	        imcode.server.document.textdocument.TextDocumentDomainObject,
	        imcode.server.parser.ParserParameters,
	        imcode.server.user.UserDomainObject,
	        imcode.util.Utility, org.apache.commons.lang.StringUtils, java.util.List, java.util.Set, com.imcode.imcms.servlet.Version, org.apache.commons.lang.StringEscapeUtils, org.apache.oro.text.perl.Perl5Util, org.apache.oro.text.perl.MalformedPerl5PatternException, imcode.util.Html, imcode.server.document.*, java.util.ArrayList, imcode.server.ImcmsServices, java.util.Iterator, org.apache.commons.collections.iterators.ReverseListIterator"
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%!

private String getSubPanelStart(String cp) {
    return
        "<table class=\"imcmsToolBarSubTable\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
        "<tr>\n" +
        "	<td class=\"imcmsToolBarSubTdLeft\"><img src=\"" + cp + "/imcms/images/1x1.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>\n" +
        "  <td class=\"imcmsToolBarSubTdMid\">" ;
}

private String getSubPanelEnd(String cp) {
    return
        "</td>\n" +
        "  <td class=\"imcmsToolBarSubTdRight\"><img src=\"" + cp + "/imcms/images/1x1.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>\n" +
        "</tr>\n" +
        "<tr>\n" +
        "	<td class=\"imcmsToolBarSubWidthTdLeft\"><img src=\"" + cp + "/imcms/images/1x1.gif\" width=\"7\" height=\"1\" alt=\"\" /></td>\n" +
        "  <td class=\"imcmsToolBarSubWidthTdMid\"><img src=\"" + cp + "/imcms/images/1x1.gif\" width=\"420\" height=\"1\" alt=\"\" /></td>\n" +
        "  <td class=\"imcmsToolBarSubWidthTdRight\"><img src=\"" + cp + "/imcms/images/1x1.gif\" width=\"12\" height=\"1\" alt=\"\" /></td>\n" +
        "</tr>\n" +
        "</table>" ;
}

private String reFormatVersion(String imcmsVersionComplete) {
	String imcmsVersionShort = "imCMS" ;
	Perl5Util re = new Perl5Util() ;
	// imCMS 6.0.0-alpha42
	try {
		imcmsVersionComplete = imcmsVersionComplete.trim() ;
		String majorVersion = "" ;
		String minorVersion = "" ;
		if (re.match("/^imCMS ([\\d](\\.[\\d])*).*/i", imcmsVersionComplete)) {
			majorVersion = re.group(1) ;
		}
		if (re.match("/.*(alpha|beta)([\\d]+)$/i", imcmsVersionComplete)) {
			minorVersion = re.group(1).substring(0,1) + re.group(2) ;
		}
		imcmsVersionShort = majorVersion ;
		if (!"".equals(majorVersion) && !"".equals(minorVersion)) {
			imcmsVersionShort += "-" + minorVersion ;
		}
	} catch (Exception ignore) {}
	return imcmsVersionShort ;
}

%><%

int metaId = AdminPanelServlet.getIntRequestParameter("meta_id", 0, request) ;
int flags  = AdminPanelServlet.getIntRequestParameter("flags", 0, request) ;
String get = StringUtils.defaultString(request.getParameter("get")) ;

DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper() ;
UserDomainObject user = Utility.getLoggedOnUser(request) ;
DocumentDomainObject document = documentMapper.getDocument(metaId) ;

if (null == user || null == document) {
	//out.print("null: " + (null == user) + ", " + (null == document)) ;
	return;
}

String userLang = user.getLanguageIso639_2() ;
pageContext.setAttribute("userLang", userLang);

/* *******************************************************************************************
 *         Check Mode                                                                        *
 ******************************************************************************************* */

boolean isNormalMode      = true ;
boolean isTextMode        = false ;
boolean isContentLoopMode = false ;
boolean isImageMode       = false ;
boolean isMenuMode        = false ;
boolean isTemplateMode    = false ;
boolean isIncludesMode    = false ;
boolean isDocInfoMode     = false ;
boolean isEditMode        = false ;
boolean isPermissionMode  = false ;

String templateName = "" ;

ImcmsServices service = Imcms.getServices();

DocumentRequest documentRequest = new DocumentRequest(service, user, document, null, request, response) ;
ParserParameters view = new ParserParameters(documentRequest) ;
view.setFlags(flags) ;

if (null != view && document instanceof TextDocumentDomainObject) {
    //TextDocumentViewing view = TextDocumentViewing.fromRequest(request) ;
    TextDocumentDomainObject textDDO = (TextDocumentDomainObject) document ;
    isTextMode        = view.isTextMode() ;
    isContentLoopMode = view.isContentLoopMode() ;
    isImageMode       = view.isImageMode() ;
    isMenuMode        = view.isMenuMode() ;
    isTemplateMode    = view.isTemplateMode() ;
    isIncludesMode    = view.isIncludesMode() ;
    templateName      = textDDO.getTemplateName() ;
}
if (null != view) {
    isDocInfoMode     = view.isMode( ImcmsConstants.PERM_EDIT_DOCINFO, DocumentPermissionSetDomainObject.EDIT_DOCUMENT_INFORMATION ) ;
    isEditMode        = view.isMode( ImcmsConstants.PERM_EDIT_DOCUMENT, DocumentPermissionSetDomainObject.EDIT ) ;
    isPermissionMode  = view.isMode( ImcmsConstants.PERM_EDIT_PERMISSIONS, DocumentPermissionSetDomainObject.EDIT_PERMISSIONS ) ;
}
isNormalMode = (
        !isTextMode && !isContentLoopMode && !isImageMode && !isMenuMode &&
        !isTemplateMode && !isIncludesMode && !isDocInfoMode && !isEditMode && !isPermissionMode) ;


    
    
    
DocumentPermissionSetDomainObject documentPermissionSet = user.getPermissionSetFor(document) ;

String queryString = request.getQueryString();
StringBuffer baseURL = request.getRequestURL();

// Page base url
// TODO: dirty implementation, refactor
if (queryString == null) {
	baseURL.append("?" + ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE + "=");
} else {
	queryString = queryString.replaceAll("&" + ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE + "=\\w*", "");
	queryString = queryString.replaceFirst("&?" + ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE + "=..", "");
	baseURL.append("?" + queryString + "&" + ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE + "=");
}


pageContext.setAttribute("baseURL", baseURL);

/* *******************************************************************************************
*         Available document's versions                                                     *
******************************************************************************************* */
DocumentVersionInfo docVersionInfo = documentMapper.getDocumentVersionInfo(document.getId());
DocumentVersion version = document.getVersion();

/* *******************************************************************************************
*         Get languages                                                                     *
******************************************************************************************* */
List<I18nLanguage> languages = Imcms.getI18nSupport().getLanguages();
Set<I18nLanguage> enabledLanguages = document.getMeta().getLanguages();
I18nLanguage defaultLanguage = Imcms.getI18nSupport().getDefaultLanguage();
I18nLanguage currentLanguage = Imcms.getUser().getDocGetterCallback().getLanguage();



boolean isWorkingVersion = DocumentVersionInfo.isWorkingVersion(version);

String imcmsVersionComplete = Version.getImcmsVersion(getServletConfig().getServletContext()) ;
String imcmsVersionShort = reFormatVersion(imcmsVersionComplete) ;

String cp = request.getContextPath() ;



if ("adminPanelHtml".equals(get)) { %>
<div id="imcmsToolBar">
    <div id="imcmsToolBarMain">
        <div id="imcmsToolBarLeft">
            <table id="imcmsToolBarLeftTable" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td id="imcmsToolBarLogoTd"><img src="<%= cp %>/imcms/images/adminpanel/logo_imcms.gif" width="67" height="15" alt="" /></td>
                <td id="imcmsToolBarMetaTd" rowspan="2">
                <ul>
                    <li id="statusIcon"><%= Html.getLinkedStatusIconTemplate( document, user, request ) %></li><%
                boolean hasManyLanguages = (languages.size() > 6) ;
                for (I18nLanguage lang: languages) {
                    String langCode       = lang.getCode() ;
                    String langName       = lang.getName() ;
                    String langNameNative = lang.getNativeName() ;
                    boolean isEnabled = enabledLanguages.contains(lang);
                    boolean isDefault = (null != defaultLanguage && defaultLanguage.equals(lang)) ;
                    boolean isCurrent = (null != currentLanguage && currentLanguage.equals(lang)) ;
                    String href_0     = "<a href=\"" + baseURL + langCode + "\" title=\"" + langName + "/" + langNameNative + "#DATA#\" class=\"imcmsToolTip\">" ;
                    String href_1     = "</a>" ;
                    String sData = "" ;
                    if (isDefault)  sData += "default " ;
                    if (isCurrent)  sData += "current " ;
                    if (!isEnabled) sData += "disabled " ;
                    if (!"".equals(sData)) {
                        sData = " (" + sData.trim() + ")" ;
                    }
                    href_0 = href_0.replace("#DATA#", sData) ; %>
                    <li class="langIcon<%=
                    isDefault ? " langIconDefault" : "" %><%=
                    isCurrent ? " langIconCurrent" : "" %><%=
                    !isEnabled ? " langIconDisabled" : "" %>"><%=
                    href_0 %><img src="<%= cp %>/imcms/images/icons/flags_iso_639_1/<%= langCode %>.gif" alt="" /><%
                    if (!hasManyLanguages) {
                        %><span class="langCode"><%= langCode %></span><%
                    } %><%= href_1 %></li><%
                } %>
                    <li id="imcmsMetaIdTd"><label>Page:</label> <%= document.getId() %></li>
                    <li><label>Alias:</label> <%= (null != document.getAlias()) ? document.getAlias() : "-" %></li>
                    <li><label>Template:</label> <%= templateName %><%
                    %><%//= ", flags:" + view.getFlags() + ", isTemplateMode:" + isTemplateMode + ", view:" + (null != view) %></li>
                </ul></td>
            </tr>
            <tr>
                <td id="imcmsToolBarVersionTd"><span class="imcmsToolTip" title="<%=
                StringEscapeUtils.escapeHtml(imcmsVersionComplete) %>"><%= imcmsVersionShort %></span></td>
            </tr>
            </table>
            <div class="tabsDiv">
                <table class="imcmsToolBarTabsTable" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><button id="imcmsToolBarBtnBack" class="imcmsToolBarTab imcmsToolTip"
                                onclick="imAdmGoTo(flags_iso_event, '<%= cp %>/servlet/BackDoc'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbuttons.html/2001" />"<%
                        %>>&lt;&lt;</button></td><%
                    if (user.canEdit(document)) { %>
                    <td><button id="imcmsToolBarBtnNormal" class="imcmsToolBarTab imcmsToolBarTabMode<%= isNormalMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= Utility.getAbsolutePathToDocument( request, document ) %>'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton2_0.html/2001" />"<%
                        %>><fmt:message key="templates/sv/adminbuttons/adminbutton2_0.html/2001" /></button></td><%
                    }
                    if (document instanceof TextDocumentDomainObject) {
                        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet ;
                        if (isWorkingVersion) {
                            if (textDocumentPermissionSet.getEditTexts()) { %>
                    <td><button id="imcmsToolBarBtnText" class="imcmsToolBarTab imcmsToolBarTabMode<%= isTextMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=65536'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton2_65536.html/2001" />"<%
                                %>><fmt:message key="templates/sv/adminbuttons/adminbutton2_65536.html/2001" /></button></td>
                    <td><button id="imcmsToolBarBtnContentLoop" class="imcmsToolBarTab imcmsToolBarTabMode<%= isContentLoopMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=2097152'); return false"
                                title="ContentLoop"<%
                                %>>ContentLoop</button></td><%
                            }
                            if (textDocumentPermissionSet.getEditImages()) { %>
                    <td><button id="imcmsToolBarBtnImage" class="imcmsToolBarTab imcmsToolBarTabMode<%= isImageMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=131072'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton2_131072.html/2001" />"<%
                                %>><fmt:message key="templates/sv/adminbuttons/adminbutton2_131072.html/2001" /></button></td><%
                            }
                            if( textDocumentPermissionSet.getEditMenus()) { %>
                    <td><button id="imcmsToolBarBtnLink" class="imcmsToolBarTab imcmsToolBarTabMode<%= isMenuMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=262144'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton2_262144.html/2001" />"<%
                                %>><fmt:message key="templates/sv/adminbuttons/adminbutton2_262144.html/2001" /></button></td><%
                            }
                            if (textDocumentPermissionSet.getEditTemplates()) { %>
                    <td><button id="imcmsToolBarBtnAppearance" class="imcmsToolBarTab imcmsToolBarTabMode<%= isTemplateMode ? " imcmsToolBarTabActive" : "" %>"
                                <%--onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=524288'); return false"
                                --%>title="<fmt:message key="templates/sv/adminbuttons/adminbutton2_524288.html/2001" />"<%
                                %>><fmt:message key="templates/sv/adminbuttons/adminbutton2_524288.html/2001" /></button></td><%
                            }
                            if (textDocumentPermissionSet.getEditIncludes()) { %>
                    <td><button id="imcmsToolBarBtnIncludes" class="imcmsToolBarTab imcmsToolBarTabMode<%= isIncludesMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=1048576'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton2_1048576.html/2001" />"<%
                                %>><fmt:message key="templates/sv/adminbuttons/adminbutton2_1048576.html/2001" /></button></td><%
                            }
                        }
                    } else if (documentPermissionSet.getEdit()) { %>
                    <td><button id="imcmsToolBarBtnEdit" class="imcmsToolBarTab imcmsToolBarTabMode<%= isEditMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=65536'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton7_65536.html/2001" />"<%
                        %>><fmt:message key="templates/sv/adminbuttons/adminbutton7_65536.html/2001" /></button></td><%
                    }
                    if( documentPermissionSet.getEditDocumentInformation() ) { %>
                    <td><button id="imcmsToolBarBtnDocInfo" class="imcmsToolBarTab imcmsToolBarTabMode<%= isDocInfoMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=1'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton_1.html/2001" />"<%
                        %>><fmt:message key="templates/sv/adminbuttons/adminbutton_1.html/2002" /></button></td><%
                    }
                    if( documentPermissionSet.getEditPermissions() ) { %>
                    <td><button id="imcmsToolBarBtnPermissions" class="imcmsToolBarTab imcmsToolBarTabMode<%= isPermissionMode ? " imcmsToolBarTabActive" : "" %>"
                                onclick="imAdmGoTo(event, '<%= cp %>/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=4'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbutton_4.html/2001" />"<%
                        %>><fmt:message key="templates/sv/adminbuttons/adminbutton_4.html/2001" /></button></td><%
                    } %>
                </tr>
                </table>
            </div>
        </div>
        <div id="imcmsToolBarRight">
            <div id="imcmsToolBarRightTop">
                <table border="0" cellspacing="0" cellpadding="0">
                <tr><%
                    if ( !user.isDefaultUser() ) { %>
                    <td><a href="<%= cp %>/servlet/LogOut" id="imcmsToolBarBtnLogOut" class="imcmsToolBarLink imcmsToolTipRev"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbuttons.html/2002" />"<%
                        %>><fmt:message key="templates/sv/adminbuttons/adminbuttons.html/2002" /></a></td><%
                    }
                    %>
                    <td><img id="imcmsToolBarHide" class="imcmsToolBarIconLink imcmsToolTipRev" src="<%= cp %>/imcms/images/adminpanel/btn_hide_1.gif" alt=""
                             title="<fmt:message key="templates/sv/adminbuttons/adminbuttons.html/2006" />" /></td>
                </tr>
                </table>
            </div>
            <div class="tabsDiv">
                <table class="imcmsToolBarTabsTable" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><button id="imcmsToolBarBtnHelp" class="imcmsToolBarTab imcmsToolTipRev"
                                onclick="imcmsOpenHelpWin('HelpStart'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/adminbuttons.html/2003" />"<%
                        %>><fmt:message key="templates/sv/adminbuttons/adminbuttons.html/2004" /></button></td>
                    
                    <td><button id="imcmsToolBarBtnSettings" class="imcmsToolBarTab">Settings</button></td><%
                    
                    if ( user.canAccessAdminPages() ) { %>
                    <td><button id="imcmsToolBarBtnAdmin" class="imcmsToolBarTab imcmsToolTipRev"
                                onclick="imAdmOpen('<%= cp %>/servlet/AdminManager'); return false"
                                title="<fmt:message key="templates/sv/adminbuttons/superadminbutton.html/2002" />"<%
                        %>><fmt:message key="templates/sv/adminbuttons/superadminbutton.html/2001" /></button></td><%
                    } %>
                </tr>
                </table>
            </div>
        </div>
        <div id="imcmsToolBarSubAppearance" class="imcmsToolBarSubPanel imcmsToolBarSubPanelLeft"></div><%-- --%>
        <div id="imcmsToolBarSubSettings" class="imcmsToolBarSubPanel imcmsToolBarSubPanelRight">
            <%= getSubPanelStart(cp) %><%
            Iterator iterator = new ReverseListIterator(docVersionInfo.getVersions()) ;
            %>
                <table border="0" cellspacing="0" cellpadding="2">
                <tr>
                    <td style="white-space:nowrap;">
                    <form action="<%= cp + "/" + document.getId()%>" method="post">
                        <input type="hidden" name="<%=ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE%>" value="<%=currentLanguage.getCode()%>"/>
                        <select name="<%=ImcmsConstants.REQUEST_PARAM__DOC_VERSION%>" class="imcmsSelectBox"><%
                        while (iterator.hasNext()) {
                            DocumentVersion v = (DocumentVersion)iterator.next() ;
                            String sSelected = v.getNo().equals(version.getNo()) ? " selected=\"selected\"" : "" ;
                            String displayName = DocumentVersionInfo.isWorkingVersion(v) ? "DRAFT" : "Version " + v.getNo().toString() ;
                            if (docVersionInfo.isDefaultVersion(v)) {
                                displayName += " (default)" ;    
                            } %>
                            <option value="<%=v.getNo()%>" <%= sSelected %>><%=(displayName)%></option><%
                        } %>
                        </select>
                        <input type="submit" name="cmd" value="Show" class="imcmsToolBarBtn" />
                    </form></td><%
                    if (user.canEdit(document)) { %>
                    <td style="padding-left:10px; white-space:nowrap;">
                    <form action="<%= cp %>/servlet/AdminDoc?meta_id=<%=document.getId()%>" method="post">
                        <input type="text" size="3" name="no" value="<%=docVersionInfo.getDefaultVersion().getNo()%>" class="imcmsTextField"/>
                        <input type="hidden" name="<%=ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE%>" value="<%=currentLanguage.getCode()%>"/>
                        <input type="hidden" name="flags" value="8388608"/>
                        <input type="submit" name="cmd" value="Set default" class="imcmsToolBarBtn" />
                    </form></td><%
                    }
                    if (user.canEdit(document) && isWorkingVersion) { %>
                    <td style="padding-left:10px;">
                    <form action="<%= cp %>/servlet/AdminDoc?meta_id=<%=document.getId()%>" method="post">
                        <input type="hidden" name="<%=ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE%>" value="<%=currentLanguage.getCode()%>"/>
                        <input type="hidden" name="flags" value="4194304"/>
                        <input type="submit" name="cmd" value="Save draft as next version" class="imcmsToolBarBtn" />
                    </form></td><%
                    } %>
                </tr>
                </table>
            <%= getSubPanelEnd(cp) %>
        </div>
        <div class="imcmsToolBarClear"></div>
    </div>
</div>
<div id="imcmsToolBarHidden">
    <div id="imcmsToolBarHiddenMain">
        <div id="imcmsToolBarShow" class="imcmsToolTipRev" title="<fmt:message key="templates/sv/adminbuttons/adminbuttons.html/2005" />"></div>
        <div class="imcmsToolBarClear"></div>
    </div>
</div><%





} else if ("changePageHtml".equals(get)) {
    
    TextDocumentDomainObject textDDO = (TextDocumentDomainObject) document ;
    
    TemplateMapper templateMapper = service.getTemplateMapper();
    
    TemplateGroupDomainObject selectedTemplateGroup = user.getTemplateGroup();
    if ( null == selectedTemplateGroup ) {
        selectedTemplateGroup = templateMapper.getTemplateGroupById( textDDO.getTemplateGroupId() );
    }
    
    TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );
    
    Set allowedTemplateGroupIds = textDocumentPermissionSet.getAllowedTemplateGroupIds();
    List allowedTemplateGroups = templateMapper.getTemplateGroups(allowedTemplateGroupIds);
    String templateGroupsHtmlOptionList = templateMapper.createHtmlOptionListOfTemplateGroups( allowedTemplateGroups, selectedTemplateGroup );
    
    List<TemplateDomainObject> templates = new ArrayList<TemplateDomainObject>();
    if ( allowedTemplateGroupIds.contains(new Integer(selectedTemplateGroup.getId()) ) ) {
        templates = templateMapper.getTemplatesInGroup( selectedTemplateGroup );
    }
    String templateId = textDDO.getTemplateName();
    TemplateDomainObject template = templateMapper.getTemplateByName(templateId) ;
    String templatesHtmlOptionList = templateMapper.createHtmlOptionListOfTemplates( templates, template );
    
    
%><%= getSubPanelStart(cp) %>
    <form name="changePageForm" method="post" action="<%= cp %>/servlet/SaveInPage">
    <input type="hidden" name="meta_id" value="<%= metaId %>" />
    <table border="0" cellspacing="0" cellpadding="2">
    <tr class="changePageTdBottom">
        <td style="padding-left:10px;">
        <select name="group" size="1" class="imcmsSelectBox">
            <%= templateGroupsHtmlOptionList %>
        </select></td>
        
        <td><input type="submit" name="change_group" value="<fmt:message key="templates/sv/textdoc/inPage_admin.html/2001" />"
                   onclick="imcmsResetTarget()" class="imcmsToolBarBtn" style="width:90px;" /></td>
        
        <td style="padding-left:10px;">
        <select name="template" size="1" class="imcmsSelectBox">
            <%= templatesHtmlOptionList %>
            <option></option>
        </select></td>
        
        <td>
        <input type="submit" name="preview" value="<fmt:message key="templates/sv/textdoc/inPage_admin.html/2002" />"
               onclick="imcmsTargetNewWindow()" class="imcmsToolBarBtn" /></td>
        
        <td style="padding-left:10px;">
        <input type="submit" name="update" value="<fmt:message key="templates/sv/textdoc/inPage_admin.html/2003" />"
               onclick="imcmsResetTarget()" class="imcmsToolBarBtn" /></td>
    </tr>
    </table>
    </form>
<%= getSubPanelEnd(cp) %><%
} %>
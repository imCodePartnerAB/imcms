<%@ page contentType="text/html; charset=cp1252"  import="imcode.server.ApplicationServer,
                 org.apache.commons.lang.StringEscapeUtils,
                 java.util.Date,
                 java.text.SimpleDateFormat,
                 imcode.util.DateConstants,
                 java.text.DateFormat,
                 org.apache.commons.lang.StringUtils,
                 java.util.Arrays,
                 java.util.Set,
                 java.util.HashSet,
                 imcode.external.diverse.Html,
                 org.apache.commons.collections.iterators.TransformIterator,
                 org.apache.commons.collections.Transformer,
                 imcode.server.LanguageMapper,
                 imcode.server.IMCServiceInterface,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 imcode.server.document.*,
                 com.imcode.imcms.servlet.admin.DocumentComposer,
                                                          org.apache.commons.lang.ObjectUtils"%><%

    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
    final DocumentMapper documentMapper = service.getDocumentMapper();

    DocumentDomainObject document = (DocumentDomainObject)DocumentComposer.getObjectFromSessionWithKeyInRequest(request, DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);
    final boolean editingExistingDocument = DocumentComposer.ACTION__EDIT_DOCUMENT_INFORMATION.equalsIgnoreCase( (String)request.getAttribute( DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION  )) ;
    DocumentComposer.NewDocumentParentInformation newDocumentParentInformation = (DocumentComposer.NewDocumentParentInformation)DocumentComposer.getObjectFromSessionWithKeyInRequest(request, DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);
    boolean creatingNewDocument = !editingExistingDocument;

%><%!

    String formatDatetime(Date datetime) {
        if (null == datetime) {
            return "" ;
        }
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_NO_SECONDS_FORMAT_STRING ) ;
        return dateFormat.format(datetime) ;
    }

    String formatDate(Date date) {
        if (null == date) {
            return "" ;
        }
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING ) ;
        return dateFormat.format(date) ;
    }

    String formatTime(Date time) {
        if (null == time) {
            return "" ;
        }
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.TIME_FORMAT_NO_SECONDS_STRING ) ;
        return dateFormat.format(time) ;
    }
%><html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_information_title ?></title>

<link rel="stylesheet" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="document.getElementsByName('<%= StringEscapeUtils.escapeJavaScript( DocumentComposer.PARAMETER__HEADLINE ) %>').item(0).focus()">

<script>
imcmsGui("outer_start", null);
imcmsGui("head", null);
</script>
<form name="mainForm" method="POST" action="<%= request.getContextPath() %>/servlet/DocumentComposer">
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="submit" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2002 ?>" title="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(77)"></td>
</tr>
</table>
<script>
imcmsGui("mid", null);
</script>
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME %>"
            value="<%= DocumentComposer.getSessionAttributeNameFromRequest( request, DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME ) %>">
    <%
        if (creatingNewDocument) { %>
        <input type="hidden" name="<%=DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION%>" value="<%=DocumentComposer.ACTION__PROCESS_NEW_DOCUMENT_INFORMATION%>" />
        <input type="hidden"
            name="<%= DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME %>"
            value="<%= DocumentComposer.getSessionAttributeNameFromRequest( request, DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME ) %>">
    <tr>
        <td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/new_document_procedure_description ?>
        &nbsp;</td>
    </tr>
    <tr>
        <td><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/document_information.jsp/create_document_heading ?>","656");</script></td>
    </tr>
    <% } else { %>
        <input type="hidden" name="<%=DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION%>" value="<%=DocumentComposer.ACTION__PROCESS_EDITED_DOCUMENT_INFORMATION%>" />
    <tr>
        <td><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/document_information.jsp/edit_document_heading ?> <%= document.getId() %>","656");</script></td>
    </tr>
    <% } %>
    <tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="656">
	<tr>
		<td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/docadmin/document_information.jsp/6 ?><sup class="imNote">1</sup></td>
		<td>
            <input type="text" name="<%= DocumentComposer.PARAMETER__HEADLINE %>" size="105" maxlength="255" style="width: 100%"
            value="<%= StringEscapeUtils.escapeHtml(document.getHeadline()) %>">
        </td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1002 ?>&nbsp;</td>
		<td class="imcmsAdmForm">
		<textarea name="<%= DocumentComposer.PARAMETER__MENUTEXT %>" class="imcmsAdmForm" cols="47" rows="3" wrap="virtual" style="width:100%">
<%= StringEscapeUtils.escapeHtml(document.getMenuText()) %></textarea>
		<table border="0" cellspacing="0" cellpadding="0">
		<% if (creatingNewDocument && newDocumentParentInformation.documentTypeId == DocumentDomainObject.DOCTYPE_TEXT) { %>
        <tr>
			<td><input type="CHECKBOX" name="<%= DocumentComposer.PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS %>" value="1" checked></td>
			<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/copy_headline_and_text_to_textfields ?></td>
		</tr>
        <% } %>
		</table></td>
	<tr>
		<td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/docadmin/document_information.jsp/10 ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="560">
		<tr>
			<td>
                <input type="text" name="<%= DocumentComposer.PARAMETER__IMAGE %>" size="85" maxlength="255" style="width: 100%"
                    value="<%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getMenuImage(), "" )) %>">
            </td>
			<td align="right">
                <input type="submit" class="imcmsFormBtnSmall" name="<%= DocumentComposer.PARAMETER__GO_TO_IMAGE_BROWSE%>" value=" <? install/htdocs/global/pageinfo/browse ?> ">
                <input type="hidden" name="<%=DocumentComposer.PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION%>" value="<%= request.getAttribute( DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION )%>"/>
			</td>
		</tr>
		</table></td>
	</tr>
	</table>
	<table border="0" cellspacing="0" cellpadding="0" width="656">
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
    <tr>
        <td class="imcmsAdmText">
            <? install/htdocs/sv/jsp/docadmin/document_information.jsp/status ?>
        </td>
        <td>
            <select name="<%= DocumentComposer.PARAMETER__STATUS %>">
                <option value="<%= DocumentDomainObject.STATUS_NEW %>"<% if (DocumentDomainObject.STATUS_NEW == document.getStatus()) { %> selected<% } %>>
                    <? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_new ?>
                </option>
                <option value="<%= DocumentDomainObject.STATUS_PUBLICATION_APPROVED %>"<% if (DocumentDomainObject.STATUS_PUBLICATION_APPROVED == document.getStatus()) { %> selected<% } %>>
                    <? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_publication_approved ?>
                </option>
                <option value="<%= DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED %>"<% if (DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED == document.getStatus()) { %> selected<% } %>>
                    <? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_publication_disapproved ?>
                </option>
            </select>
            <table border="0">
                <tr>
                    <td class="imcmsAdmText">
                        <%
                            Date now = new Date();
                            Date publicationStartDatetime = document.getPublicationStartDatetime();
                            if (document.getStatus() != DocumentDomainObject.STATUS_PUBLICATION_APPROVED || null == publicationStartDatetime || publicationStartDatetime.after(now)) { %>
                                <? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_be_published_at ?>
                            <% } else { %>
                                <? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_was_published_at ?>
                            <% } %>
                    </td>
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <input type="text" name="<%= DocumentComposer.PARAMETER__PUBLICATION_START_DATE %>" size="11" maxlength="10" style="width: 7em;"
                                        value="<%= StringEscapeUtils.escapeHtml( formatDate(publicationStartDatetime) ) %>">
                                </td>
                                <td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1007 ?></td>
                                <td>
                                    <input type="text" name="<%= DocumentComposer.PARAMETER__PUBLICATION_START_TIME %>" size="5" maxlength="5" style="width: 4em;"
                                        value="<%= StringEscapeUtils.escapeHtml( formatTime(document.getPublicationStartDatetime()) ) %>">
                                </td>
                                <td>
                                    <%= formatDatetime( publicationStartDatetime ) %>&nbsp;
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="imcmsAdmText">
                        <%
                            Date archivedDatetime = document.getArchivedDatetime();
                            if (null == archivedDatetime || archivedDatetime.after(now)) { %>
                                <? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_be_archived_at ?>
                            <% } else { %>
                                <? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_was_archived_at ?>
                            <% } %>
                    </td>
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td>
                                <input type="text" name="<%= DocumentComposer.PARAMETER__ARCHIVED_DATE %>" size="11" maxlength="10" style="width: 7em;"
                                    value="<%= StringEscapeUtils.escapeHtml( formatDate(archivedDatetime) ) %>">
                            </td>
                            <td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1009 ?></td>
                            <td>
                                <input type="text" name="<%= DocumentComposer.PARAMETER__ARCHIVED_TIME %>" size="5" maxlength="5" style="width: 4em;"
                                    value="<%= StringEscapeUtils.escapeHtml( formatTime(archivedDatetime) ) %>">
                            </td>
                            <td>
                                <%= formatDatetime( archivedDatetime ) %>&nbsp;
                            </td>
                        </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="imcmsAdmText">
                        <%
                            Date publicationEndDatetime = document.getPublicationEndDatetime();
                            if (null == publicationEndDatetime || publicationEndDatetime.after(now)) { %>
                                <? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_cease_to_be_published_at ?>
                            <% } else { %>
                                <? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_ceased_to_be_published_at ?>
                            <% } %>
                    </td>
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td>
                                <input type="text" name="<%= DocumentComposer.PARAMETER__PUBLICATION_END_DATE %>" size="11" maxlength="10" style="width: 7em;"
                                    value="<%= StringEscapeUtils.escapeHtml( formatDate(publicationEndDatetime) ) %>">
                            </td>
                            <td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1009 ?></td>
                            <td>
                                <input type="text" name="<%= DocumentComposer.PARAMETER__PUBLICATION_END_TIME %>" size="5" maxlength="5" style="width: 4em;"
                                    value="<%= StringEscapeUtils.escapeHtml( formatTime(publicationEndDatetime) ) %>">
                            </td>
                            <td>
                                <%= formatDatetime( publicationEndDatetime ) %>&nbsp;
                            </td>
                        </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
	<tr>
		<td><img src="@imcmsimageurl@/admin/1x1.gif" width="96" height="1"></td>
		<td><img src="@imcmsimageurl@/admin/1x1.gif" width="556" height="1"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td>&nbsp;<br><script>imcHeading("<? install/htdocs/sv/jsp/docadmin/document_information.jsp/21/1 ?>","656");</script></td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/22 ?></td>
		<td class="imcmsAdmText">
		<select name="<%= DocumentComposer.PARAMETER__SECTIONS %>" size="5" multiple>
            <% SectionDomainObject[] sections = documentMapper.getAllSections() ;
                Arrays.sort(sections) ;
                SectionDomainObject[] documentSections = document.getSections() ;
                Transformer sectionToStrings = new Transformer() {
                    public Object transform( Object o ) {
                        SectionDomainObject section = (SectionDomainObject) o ;
                        return new String[] { ""+section.getId(), section.getName() } ;
                    }
                } ;
            %><%= Html.createOptionList( Arrays.asList( sections ), Arrays.asList(documentSections), sectionToStrings ) %>
		</select>
		&nbsp; <? install/htdocs/sv/jsp/docadmin/document_information.jsp/current_section ?>
        <%=
            0 == documentSections.length
                ? "<? install/htdocs/sv/jsp/docadmin/document_information.jsp/no_section ?>"
                : StringUtils.join(documentSections, ", ")
        %>
        </td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/26 ?></td>
		<td class="imcmsAdmText">
		<select name="<%= DocumentComposer.PARAMETER__LANGUAGE %>" size="1">
		    <%= LanguageMapper.getLanguageOptionList( service, user, document.getLanguageIso639_2() ) %>
        </select>
		&nbsp; <? install/htdocs/sv/jsp/docadmin/document_information.jsp/current_language ?> <%= LanguageMapper.getCurrentLanguageNameInUsersLanguage( service, user, document.getLanguageIso639_2() )%></td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/29 ?></td>
		<td class="imcmsAdmText"><%
		CategoryTypeDomainObject[] categoryTypes = documentMapper.getAllCategoryTypes() ;
		Arrays.sort(categoryTypes) ;
		for ( int i = 0; i < categoryTypes.length; i++ ) {
			CategoryTypeDomainObject categoryType = categoryTypes[i] ;
			if( !categoryType.hasImages() ) {%>
		<div style="float: left; margin: auto 1em 1ex auto;">
		<a href="@imcmsjspurl@/category_descriptions.jsp?category_type_name=<%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %>" target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br><img src="@imcmsimageurl@/admin/1x1.gif" width="1" height="3"><br>
		<select name="<%= DocumentComposer.PARAMETER__CATEGORIES %>"<% if (1 != categoryType.getMaxChoices()) { %>size="4" multiple<% } %>>
		<%= Html.createOptionListOfCategoriesOfTypeForDocument( documentMapper, categoryType, document) %>
		</select></div><%
			}
		}
		for ( int i = 0; i < categoryTypes.length; i++ ) {
			CategoryTypeDomainObject categoryType = categoryTypes[i] ;
			if( categoryType.hasImages() ) { %>
		<div style="float: left; margin: auto 1em 1ex auto;">
		<a href="@imcmsjspurl@/category_descriptions.jsp?category_type_name=<%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %>" target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br><%
				
				boolean radioButton = categoryType.getMaxChoices() == 1;
				String typeStr = radioButton?"radio":"checkbox";
				CategoryDomainObject[] documentSelectedCategories = document.getCategoriesOfType(categoryType);
				Set selectedValuesSet = new HashSet( Arrays.asList(documentSelectedCategories) );
				CategoryDomainObject[] categories = documentMapper.getAllCategoriesOfType(categoryType);
				for (int k = 0; k < categories.length; k++) {
					CategoryDomainObject category = categories[k];
					boolean checked = selectedValuesSet.contains(category);
					String checkedStr = checked?"checked":"";
					boolean hasImage = !category.getImage().equals("");
					String imageStr = hasImage ? "<img src=\"" + category.getImage() + "\"/>" : ""; %>
		<table border="0" cellspacing="2" cellpadding="0" style="float:left;">
		<tr>
			<td bgcolor="#000000">
			<table border="0" cellspacing="1" cellpadding="2">
			<tr>
				<td bgcolor="#ffffff"><%=imageStr%></td>
				<td bgcolor="#ffffff"><input id="<%= DocumentComposer.PARAMETER__CATEGORIES + "" + category.getId() %>" name="<%= DocumentComposer.PARAMETER__CATEGORIES %>" type="<%=typeStr%>" value="<%=category.getId()%>"<%=checkedStr%>></td>
				<td bgcolor="#ffffff"><label for="<%= DocumentComposer.PARAMETER__CATEGORIES + "" + category.getId() %>"><%=category.getName()%></label></td>
			</tr>
			</table></td>
		</tr>
		</table><%
				}
			} %></div><%
		} %></td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/32 ?></td>
		<td class="imcmsAdmText">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="CHECKBOX" name="<%= DocumentComposer.PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS %>" value="1"<% if (document.isVisibleInMenusForUnauthorizedUsers()) {%> checked<%} %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/global/pageinfo/show_link_to_unauthorized_user ?></td>
   		</tr>
        <tr>
			<td><input type="CHECKBOX" name="<%= DocumentComposer.PARAMETER__LINKABLE_BY_OTHER_USERS %>" value="1" <% if (document.isLinkableByOtherUsers()) {%> checked<%}%>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/global/pageinfo/share ?></td>
		</tr>
		</table>	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/35 ?></td>
		<td class="imcmsAdmText">
        <input type="text" name="<%= DocumentComposer.PARAMETER__KEYWORDS %>" size="105" maxlength="200" style="width: 100%"
                value="<%= StringEscapeUtils.escapeHtml( StringUtils.join( document.getKeywords(), ", " ) ) %>"><br>
		<span class="imcmsAdmDim"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1014 ?></span><br>
		<input type="CHECKBOX" name="<%= DocumentComposer.PARAMETER__SEARCH_DISABLED %>" value="1" <% if (document.isSearchDisabled()) { %> checked<% } %>> <? install/htdocs/sv/jsp/docadmin/document_information.jsp/37 ?></td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/39 ?></td>
		<td class="imcmsAdmText" nowrap>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
            <% String target = document.getTarget() ; %>
			<td><input type="radio" name="<%= DocumentComposer.PARAMETER__TARGET %>" value="_self"<% if ("_self".equalsIgnoreCase( target ) || "".equals( target )) { %> checked<% target = null; } %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1015 ?> &nbsp;</td>
			<td><input type="radio" name="<%= DocumentComposer.PARAMETER__TARGET %>" value="_blank"<% if ("_blank".equalsIgnoreCase( target ) ) { %> checked<% target = null; } %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1016 ?> &nbsp;</td>
			<td><input type="radio" name="<%= DocumentComposer.PARAMETER__TARGET %>" value="_top"<% if ("_top".equalsIgnoreCase( target ) ) { %> checked<% target = null; } %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1017 ?> &nbsp;</td>
			<td><input type="radio" name="<%= DocumentComposer.PARAMETER__TARGET %>" <% if (null != target) { %> checked<% } %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1018 ?>&nbsp;</td>
			<td>
            <input type="text" name="<%= DocumentComposer.PARAMETER__TARGET %>" size="20" maxlength="20"
                value="<% if (null != target) { %><%= StringEscapeUtils.escapeHtml( target ) %><% } %>">
        </td>
		</tr>
		</table></td>
	</tr>
    <% if( editingExistingDocument ) { %>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/created ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
                <input type="text" name="<%= DocumentComposer.PARAMETER__CREATED_DATE %>" size="11" maxlength="10" style="width: 7em;"
                        value="<%= formatDate( document.getCreatedDatetime() ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/time ?></td>
			<td>
                <input type="text" name="<%= DocumentComposer.PARAMETER__CREATED_TIME %>" size="5" maxlength="5" style="width: 4em;"
                        value="<%= formatTime( document.getCreatedDatetime() ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/created_by ?> <%= document.getCreator().getFullName() %> (<%= document.getCreator().getLoginName() %>)</td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/changed ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
                <input type="text" name="<%= DocumentComposer.PARAMETER__MODIFIED_DATE %>" size="11" maxlength="10" style="width: 7em;"
                        value="<%= formatDate( document.getModifiedDatetime() ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/time ?></td>
			<td>
                <input type="text" name="<%= DocumentComposer.PARAMETER__MODIFIED_TIME %>" size="5" maxlength="5" style="width: 4em;"
                        value="<%= formatTime( document.getModifiedDatetime() ) %>">
            </td>
			<td class="imcmsAdmDim">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/date_format ?></td>
		</tr>
		</table></td>
	</tr>
    <% } %>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/42 ?></td>
		<td class="imcmsAdmText">
		<select name="<%= DocumentComposer.PARAMETER__PUBLISHER_ID %>" size="1">
			<%= Html.createPublisherOptionList( service.getImcmsAuthenticatorAndUserAndRoleMapper(), document.getPublisher()) %>
		</select>
		&nbsp; <? install/htdocs/sv/jsp/docadmin/document_information.jsp/current_publisher ?> <% UserDomainObject publisher = document.getPublisher() ; %>
        <%=
            null == publisher
                ? "<? install/htdocs/sv/jsp/docadmin/document_information.jsp/no_publisher ?>"
                : publisher.getLastName()+", "+publisher.getFirstName()
            %>
        </td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"blue");</script></td>
	</tr>
	<tr>
		<td colspan="2">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="imNoteComment"><sup class="imNote">1</sup>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/46 ?></td>
			<td align="right">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><input type="SUBMIT" class="imcmsFormBtn" value=" <? install/htdocs/sv/jsp/docadmin/document_information.jsp/2004 ?> " name="<%= DocumentComposer.PARAMETER_BUTTON__OK %>"></td>
				<td>&nbsp;</td>
				<td><input type="RESET" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2005 ?>" name="reset"></td>
				<td>&nbsp;</td>
			    <td><input type="SUBMIT" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2006 ?>"></td>
			</tr>
			</table></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td><img src="@imcmsimageurl@/admin/1x1.gif" width="98" height="1"></td>
		<td><img src="@imcmsimageurl@/admin/1x1.gif" width="558" height="1"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
</table>
</form>
<script>
imcmsGui("bottom", null);
imcmsGui("outer_end", null);
</script>

</body>
</html>

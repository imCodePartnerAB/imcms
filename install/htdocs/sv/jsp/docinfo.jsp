<%@ page import="imcode.server.ApplicationServer,
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
                 imcode.server.document.*"%>
<%
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
    final DocumentMapper documentMapper = service.getDocumentMapper();
    final int metaId = Integer.parseInt(request.getParameter("meta_id"));
    DocumentDomainObject document = documentMapper.getDocument(metaId) ;
%>
<%!
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
%>

<html>
<head>
<title><? sv/jsp/docinfo.jsp/1 ?></title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link rel="stylesheet" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'meta_headline'); getDefaultPubl(null);">

<script>
imcmsGui("outer_start", null);
imcmsGui("head", null);
</script>
<table border="0" cellspacing="0" cellpadding="0">
<form>
<tr>
	<td><input type="BUTTON" class="imcmsFormBtn" value="<? sv/jsp/docinfo.jsp/2001 ?>" onClick="document.forms.abortForm.submit(); return false"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? sv/jsp/docinfo.jsp/2002 ?>" title="<? sv/jsp/docinfo.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(77)"></td>
</tr>
</form>
</table>
<script>
imcmsGui("mid", null);
</script>
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="mainForm" method="POST" action="<%= request.getContextPath() %>/servlet/SaveDocument">
<!-- input type="hidden" name="doc_type" value="#doc_type#">
<input type="hidden" name="doc_menu_no" value="#doc_menu_no#">
<input type="hidden" name="parent_meta_id" value="#parent_meta_id#">
<input type="hidden" name="description" value="" oldvalue="#description#">
<input type="hidden" name="current_section_id" value="#current_section_id#">
<input type="hidden" name="archive" value="0" -->
<input type="hidden" name="meta_id" value="<%= document.getMetaId() %>">
<tr>
	<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/1001 ?>
	&nbsp;</td>
</tr>
<tr>
	<td><script>imcHeading("<? sv/jsp/docinfo.jsp/5/1 ?>","656");</script></td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="656">
	<tr>
		<td class="imcmsAdmText" nowrap><? sv/jsp/docinfo.jsp/6 ?><sup class="imNote">1</sup></td>
		<td>
            <input type="text" name="meta_headline" size="105" maxlength="255" style="width: 100%"
            value="<%= StringEscapeUtils.escapeHtml(document.getHeadline()) %>">
        </td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap><? sv/jsp/docinfo.jsp/1002 ?>&nbsp;</td>
		<td class="imcmsAdmForm">
		<textarea name="meta_text" class="imcmsAdmForm" cols="47" rows="3" wrap="virtual" style="width:100%">
<%= StringEscapeUtils.escapeHtml(document.getMenuText()) %></textarea>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="CHECKBOX" name="copyMetaHeader" value="1" checked></td>
			<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/9 ?></td>
		</tr>
		</table></td>
	<tr>
		<td class="imcmsAdmText" nowrap><? sv/jsp/docinfo.jsp/10 ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="560">
		<tr>
			<td>
                <input type="text" name="meta_image" size="85" maxlength="255" style="width: 100%"
                    value="<%= StringEscapeUtils.escapeHtml( document.getImage() ) %>">
            </td>
			<td align="right">
                <!-- input type="hidden" name="caller" value="AddDoc" -->
                <input type="submit" class="imcmsFormBtnSmall" name="ImageBrowse" value=" <? global/pageinfo/browse ?> ">
			</td>
		</tr>
		</table></td>
	</tr>
	</table>
	<table border="0" cellspacing="0" cellpadding="0" width="656" id="dateHide">
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/15 ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
                <input type="text" name="activated_date" size="11" maxlength="10" style="width: 7em;"
                    value="<%= StringEscapeUtils.escapeHtml( formatDate(document.getActivatedDatetime()) ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/1007 ?></td>
			<td>
                <input type="text" name="activated_time" size="5" maxlength="5" style="width: 4em;"
                    value="<%= StringEscapeUtils.escapeHtml( formatTime(document.getActivatedDatetime()) ) %>">
            </td>
			<td class="imcmsAdmDim">&nbsp;<? sv/jsp/docinfo.jsp/date_format ?></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/18 ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
                <input type="text" name="archived_date" size="11" maxlength="10" style="width: 7em;"
                    value="<%= StringEscapeUtils.escapeHtml( formatDate(document.getArchivedDatetime()) ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/1009 ?></td>
			<td>
                <input type="text" name="archived_time" size="5" maxlength="5" style="width: 4em;"
                    value="<%= StringEscapeUtils.escapeHtml( formatTime(document.getArchivedDatetime()) ) %>">
            </td>
			<td class="imcmsAdmDim">&nbsp;<? sv/jsp/docinfo.jsp/date_format ?></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td><img src="@imcmsimageurl@/admin/1x1.gif" width="96" height="1"></td>
		<td><img src="@imcmsimageurl@/admin/1x1.gif" width="556" height="1"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td>&nbsp;<br><script>imcHeading("<? sv/jsp/docinfo.jsp/21/1 ?>","656");</script></td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/22 ?></td>
		<td class="imcmsAdmText">
		<select name="change_section" size="5" multiple>
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
		&nbsp; <? sv/jsp/docinfo.jsp/current_section ?>
        <%=
            0 == documentSections.length
                ? "<? sv/jsp/docinfo.jsp/no_section ?>"
                : StringUtils.join(documentSections, ", ")
        %>
        </td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/26 ?></td>
		<td class="imcmsAdmText">
		<select name="lang_prefix" size="1">
		    <%= LanguageMapper.getLanguageOptionList( service, user, document.getLanguageIso639_2() ) %>
        </select>
		&nbsp; <? sv/jsp/docinfo.jsp/current_language ?> <%= LanguageMapper.getCurrentLanguageNameInUsersLanguage( service, user, document.getLanguageIso639_2() )%></td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/29 ?></td>
		<td class="imcmsAdmText">
            <% CategoryTypeDomainObject[] categoryTypes = documentMapper.getAllCategoryTypes() ;
                Arrays.sort(categoryTypes) ;
                for ( int i = 0; i < categoryTypes.length; i++ ) {
                    CategoryTypeDomainObject categoryType = categoryTypes[i] ;
                    %>
                    <div style="float: left; margin: auto 1em 1ex auto;">
                    <a href="@imcmsjspurl@/category_descriptions.jsp?category_type_name=<%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %>"
                        target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br>
                    <select name="categories"<% if (1 != categoryType.getMaxChoices()) { %>size="4" multiple<% } %>>
                        <%= Html.createOptionListOfCategoriesOfTypeForDocument( documentMapper, categoryType, document) %>
                    </select>
                    </div>
                    <%
                }
            %>
        </td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/32 ?></td>
		<td class="imcmsAdmText">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="CHECKBOX" name="show_meta" value="1"<% if (document.isVisibleInMenuForUnauthorizedUsers()) {%> checked<%} %>></td>
			<td class="imcmsAdmText">&nbsp;<? global/pageinfo/show_link_to_unauthorized_user ?></td>
   		</tr>
        <tr>
			<td><input type="CHECKBOX" name="shared" value="1" <% if (document.isLinkableByOtherUsers()) {%> checked<%}%>></td>
			<td class="imcmsAdmText">&nbsp;<? global/pageinfo/share ?></td>
		</tr>
		</table>	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/35 ?></td>
		<td class="imcmsAdmText">
        <input type="text" name="classification" size="105" maxlength="200" style="width: 100%"
                value="<%= StringEscapeUtils.escapeHtml( StringUtils.join( document.getKeywords(), ", " ) ) %>"><br>
		<span class="imcmsAdmDim"><? sv/jsp/docinfo.jsp/1014 ?></span><br>
		<input type="CHECKBOX" name="disable_search" value="1" <% if (document.isSearchDisabled()) { %> checked<% } %>> <? sv/jsp/docinfo.jsp/37 ?></td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/39 ?></td>
		<td class="imcmsAdmText" nowrap>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
            <% String target = document.getTarget() ; %>
			<td><input type="radio" name="target" value="_self"<% if ("_self".equalsIgnoreCase( target ) ) { %> checked<% target = null; } %>></td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/1015 ?> &nbsp;</td>
			<td><input type="radio" name="target" value="_blank"<% if ("_blank".equalsIgnoreCase( target ) ) { %> checked<% target = null; } %>></td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/1016 ?> &nbsp;</td>
			<td><input type="radio" name="target" value="_top"<% if ("_top".equalsIgnoreCase( target ) ) { %> checked<% target = null; } %>></td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/1017 ?> &nbsp;</td>
			<td><input type="radio" name="target" <% if (null != target) { %> checked<% } %>></td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/1018 ?>&nbsp;</td>
			<td>
            <input type="text" name="target" size="20" maxlength="20"
                value="<% if (null != target) { %><%= StringEscapeUtils.escapeHtml( target ) %><% } %>">
        </td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/created ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
                <input type="text" name="date_created" size="11" maxlength="10" style="width: 7em;"
                        value="<%= formatDate( document.getCreatedDatetime() ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/time ?></td>
			<td>
                <input type="text" name="created_time" size="5" maxlength="5" style="width: 4em;"
                        value="<%= formatTime( document.getCreatedDatetime() ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/created_by ?> <%= document.getCreator().getFullName() %> (<%= document.getCreator().getLoginName() %>)</td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/changed ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
                <input type="text" name="date_modified" size="11" maxlength="10" style="width: 7em;"
                        value="<%= formatDate( document.getModifiedDatetime() ) %>">
            </td>
			<td class="imcmsAdmText">&nbsp;<? sv/jsp/docinfo.jsp/time ?></td>
			<td>
                <input type="text" name="modified_time" size="5" maxlength="5" style="width: 4em;"
                        value="<%= formatTime( document.getModifiedDatetime() ) %>">
            </td>
			<td class="imcmsAdmDim">&nbsp;<? sv/jsp/docinfo.jsp/date_format ?></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td colspan="2"><script>hr("100%",656,"cccccc");</script></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? sv/jsp/docinfo.jsp/42 ?></td>
		<td class="imcmsAdmText">
		<select name="publisher_id" size="1">
			<%= Html.createPublisherOptionList( service.getImcmsAuthenticatorAndUserAndRoleMapper(), document.getPublisher()) %>
		</select>
		&nbsp; <? sv/jsp/docinfo.jsp/current_publisher ?> <% UserDomainObject publisher = document.getPublisher() ; %>
        <%=
            null == publisher
                ? "<? sv/jsp/docinfo.jsp/no_publisher ?>"
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
			<? sv/jsp/docinfo.jsp/46 ?></td>
			<td align="right">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><input type="SUBMIT" class="imcmsFormBtn" value=" <? sv/jsp/docinfo.jsp/2004 ?> " name="ok"></td>
				<td>&nbsp;</td>
				<td><input type="RESET" class="imcmsFormBtn" value="<? sv/jsp/docinfo.jsp/2005 ?>" name="reset"></td>
				<td>&nbsp;</td>
				<td>
				<table border="0" cellspacing="0" cellpadding="0">
				</form>
				<form name="abortForm" action="AdminDoc">
				<!-- input type="hidden" name="meta_id" value="#parent_meta_id#" -->
				<!-- input type="hidden" name="flags" value="262144" -->
				<tr>
					<td><input type="BUTTON" class="imcmsFormBtn" value="<? sv/jsp/docinfo.jsp/2006 ?>" onClick="document.forms.abortForm.submit(); return false"></td>
				</tr>
				</form>
				</table></td>
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
<script>
imcmsGui("bottom", null);
imcmsGui("outer_end", null);
</script>

</body>
</html>

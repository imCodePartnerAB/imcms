<%@ page import="com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 java.util.Date,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 imcode.util.DateConstants,
                 java.util.List,
                 com.imcode.imcms.api.User,
                 imcode.server.document.DocumentMapper,
                 imcode.server.Imcms,
                 java.util.HashMap"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>

<%! private String IMG_PATH;
    private String TAB_TO_SHOW;
    private String LIST_TYPE;
    private String SORTORDER_OPTION_SELECTED;


%>
<%

    AdminManager.AdminManagerPage adminManagerPage = (AdminManager.AdminManagerPage) request.getAttribute(AdminManager.AdminManagerPage.REQUEST_ATTRIBUTE__PAGE);
    DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

    IMG_PATH  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    TAB_TO_SHOW = "new";
%>

<%@ include file="gui_tabs.jsp" %>

<%
    String[][] arrTabs = {
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0 ?>" , "AdminManager?show=new" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1 ?>" , "AdminManager?show=reminders" },
	{ "<? web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2 ?>" , "AdminManager?show=summary" },
	{ "<? global/Search ?>"                                         , "AdminManager?show=search" }
} ;

    String tabs = getTabs(arrTabs, 3) ;

%>

<vel:velocity>
<html>
<head>

<title><? web/imcms/lang/jsp/admin/admin_manager.jsp/6 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>
<body id="body" onLoad="focusField(1,'AdminTask')">
<!--gui_outer_start -->
#gui_outer_start()
<!--gui_head -->
#gui_head( "<? web/imcms/lang/jsp/admin/admin_manager.jsp/1 ?> - <? global/Search ?>" )
<!-- /gui_head -->

<table  border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td colspan="4" id="adm">
	    <table border="0" cellspacing="0" cellpadding="0">
        <form method="post" action="AdminManager">
	    <% if ( null != adminManagerPage.getHtml_admin_part() ) { %>
        <tr>
		    <td><%= adminManagerPage.getHtml_admin_part() %></td>
		    <td>&nbsp; &nbsp;</td>
		    <td><input type="submit" class="imcmsFormBtnSmall" style="height:20"  name="submit" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/2 ?>"></td>
        </tr>
        <% }else { %>
        <tr><td colspan="3">&nbsp;</td></tr>
        <%}%>
        </form>
	    </table></td>
        <form action="StartDoc">
    <td colspan="2" align="right"><input type="submit" class="imcmsFormBtnSmall" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/3 ?>">
	    &nbsp;&nbsp;<input type="button" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/4 ?>" title="<? web/imcms/lang/jsp/admin/admin_manager.jsp/5 ?>" class="imcmsFormBtnSmall" onClick="openHelpW(28)"></td>
	    </form>
</tr>
</table>
#gui_mid_tabs1()
<%= tabs %>
#gui_mid_tabs2()

<table border="0" cellspacing="0" cellpadding="2" width="656" align="center">

<tr>
    <td colspan="2"><img src="http://dev.imcode.com/1.11.0-beta6-swe/imcms/swe/images/admin/1x1.gif" width="1" height="25"></td>
</tr>
<form>
<input type="hidden" name="list_type" value="">
<tr>
    <td colspan="2" height="22"><span class="imcmsAdmHeading">Sök bland mina dokument</span></td>
</tr>

</form>
<tr>
    <td colspan="2"><img src="http://dev.imcode.com/1.11.0-beta6-swe/imcms/swe/images/admin/1x1_20568d.gif"
    width="100%" height="1" vspace="8"></td>
</tr>
<tr>
    <td colspan="2">

    <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <form>
    <tr>

        <td height="24">Fritext</td>
        <td colspan="3"><input type="text" name="search_string" value="" size="20" maxlength="255" style="width:300"></td>
    </tr>
    <tr>
        <td colspan="4"><img src="http://dev.imcode.com/1.11.0-beta6-swe/imcms/swe/images/admin/1x1_cccccc.gif"
        width="100%" height="1" vspace="8"></td>
    </tr>
    <tr>
        <td height="20">Visa dokument</td>

        <td colspan="3">
        <select name="a">
            <option value="">- Alla dokument
            <option value="">där jag står som skapare
            <option value="">där jag står som ansvarig utgivare
            <option value="">som jag har rätt att ändra
            <option value="">som jag har rätt att se
        </select></td>
    </tr>
    <tr>
        <td height="24">Status</td>

        <td colspan="3">
        <table border="0" cellspacing="0" cellpadding="2">
        <tr>
            <td><input type="checkbox" name="s" value="0" checked></td>
            <td>Nya &nbsp;</td>
            <td><input type="checkbox" name="s" value="2"></td>
            <td>Godkända &nbsp;</td>
            <td><input type="checkbox" name="s" value="1"></td>

            <td>Ej godkända</td>
        </tr>
        </table></td>
    </tr>
    <tr>
        <td colspan="4"><img src="http://dev.imcode.com/1.11.0-beta6-swe/imcms/swe/images/admin/1x1_cccccc.gif"
        width="100%" height="1" vspace="8"></td>
    </tr>
    <tr>

        <td height="24">Datum</td>
        <td colspan="3">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
            <select name="a">
                <option value="">Publicerade
                <option value="">Avpublicerade
                <option value="">Skapade
                <option value="">Arkiverade
                <option value="">Ändrade
            </select></td>

        </tr>
        </table></td>
    </tr>
    <tr>
        <td height="24">&nbsp;</td>
        <td colspan="3">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="text" name="d0" value="2004-10-01" size="10" maxlength="10" style="width:65"></td>

            <td><a href="#"><img src="images/btn_calendar.gif" width="16" height="16" hspace="4" vspace="1" border="0" alt=""></a></td>
            <td nowrap>&nbsp; &nbsp; - &nbsp; &nbsp;</td>
            <td><input type="text" name="d1" value="2004-10-21" size="10" maxlength="10" style="width:65"></td>
            <td><a href="#"><img src="images/btn_calendar.gif" width="16" height="16" hspace="4" vspace="1" border="0" alt=""></a></td>
        </tr>
        </table></td>
    </tr>

    <tr>
        <td colspan="4"><img src="http://dev.imcode.com/1.11.0-beta6-swe/imcms/swe/images/admin/1x1_cccccc.gif"
        width="100%" height="1" vspace="8"></td>
    </tr>
    <tr>
        <td height="24">Sortera efter</td>
        <td>
        <select name="a">
            <option value="">publiceringsdatum (nya först)
            <option value="">publiceringsdatum (gamla först)
            <option value="">&nbsp; ändringssdatum (nya först)
            <option value="">&nbsp; ändringssdatum (gamla först)
            <option value="">arkiveringssdatum (nya först)
            <option value="">arkiveringssdatum (gamla först)
            <option value="">&nbsp; rubrik (A-Ö)
            <option value="">&nbsp; rubrik (Ö-A)
            <option value="">menytext (A-Ö)
            <option value="">menytext (Ö-A)
            <option value="">&nbsp; meta_id (0-9)
            <option value="">&nbsp; meta_id (9-0)
        </select></td>

        <td>Visa</td>
        <td>
        <select name="a">
            <option value="">5 träffar/sida
            <option value="">10 träffar/sida
            <option value="">20 träffar/sida
            <option value="">50 träffar/sida
            <option value="">100 träffar/sida
            <option value="">alla på en sida
        </select></td>
    </tr>

    <tr>
        <td colspan="4"><img src="http://dev.imcode.com/1.11.0-beta6-swe/imcms/swe/images/admin/1x1_cccccc.gif"
        width="100%" height="1" vspace="8"></td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" align="right">
        <input type="submit" name="search" value="Sök" class="imcmsFormBtn" style="width:100">
        <input type="reset" name="reset" value="Återställ" class="imcmsFormBtn" style="width:100"></td>
    </tr>

    </form>
    </table></td>
</tr>
</table>



#gui_end_of_page()

</vel:velocity>

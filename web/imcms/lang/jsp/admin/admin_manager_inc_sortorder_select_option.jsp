<%@page contentType="text/html; charset=UTF-8" %>
<% String sort = (String)request.getAttribute( "SORT" ) ; %>
<option value="MOD" <%= "MOD".equals( sort ) ? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option1 ?>
<option value="MODR" <%= "MODR".equals( sort ) ? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option2 ?>
<option value="PUBS" <%= "PUBS".equals( sort ) ? "selected" : "" %> >&nbsp; <? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option3 ?>
<option value="PUBSR" <%= "PUBSR".equals( sort ) ? "selected" : "" %> >&nbsp; <? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option4 ?>
<option value="PUBE" <%= "PUBE".equals( sort ) ? "selected" : "" %> >&nbsp; <? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option5 ?>
<option value="PUBER" <%= "PUBER".equals( sort ) ? "selected" : "" %> >&nbsp; <? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option6 ?>
<option value="ARC" <%= "ARC".equals( sort ) ? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option7 ?>
<option value="ARCR" <%= "ARCR".equals( sort ) ? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option8 ?>
<option value="HEADL" <%= "HEADL".equals( sort ) ? "selected" : "" %> >&nbsp; <? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option9 ?>
<option value="HEADLR" <%= "HEADLR".equals( sort ) ? "selected" : "" %> >&nbsp; <? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option10 ?>
<option value="ID" <%= "ID".equals( sort ) ? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option11 ?>
<option value="IDR" <%= "IDR".equals( sort ) ? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/sort_option12 ?>

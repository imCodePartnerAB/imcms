<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta http-equiv="Content-Style-Type" content="text/css">
  <title></title>
  <meta name="Generator" content="Cocoa HTML Writer">
  <meta name="CocoaVersion" content="1038.11">
  <style type="text/css">
    p.p1 {margin: 0.0px 0.0px 0.0px 0.0px; font: 14.0px Helvetica}
    p.p2 {margin: 0.0px 0.0px 0.0px 0.0px; font: 14.0px Helvetica; min-height: 17.0px}
    span.Apple-tab-span {white-space:pre}
  </style>
</head>
<body>
<p class="p1">&lt;%@ page</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>import="org.apache.commons.lang.StringUtils"</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>pageEncoding="UTF-8"</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1">%&gt;&lt;%</p>
<p class="p2"><br></p>
<p class="p1">String host = StringUtils.defaultString(request.getHeader("Host")) ;</p>
<p class="p2"><br></p>
<p class="p1">String heading = "Info" ;</p>
<p class="p1">String text = (!"".equals(host)) ? "&lt;b&gt;Info!&lt;/b&gt; The site " + host + request.getContextPath() + " is temporarily down for maintenance!" :</p>
<p class="p1"><span class="Apple-converted-space">              </span>"&lt;b&gt;Info!&lt;/b&gt; The site is temporarily down for maintenance!" ;</p>
<p class="p2"><br></p>
<p class="p1">String imcms_url = "http://doc.imcms.net/4.0.0/" ;</p>
<p class="p2"><br></p>
<p class="p1">%&gt;&lt;!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"&gt;</p>
<p class="p1">&lt;html&gt;</p>
<p class="p1">&lt;head&gt;</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;title&gt;&lt;%= heading %&gt;&lt;/title&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;link rel="stylesheet" type="text/css" href="&lt;%= imcms_url %&gt;/imcms/css/imcms_admin.css.jsp"&gt;</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1">&lt;/head&gt;</p>
<p class="p1">&lt;body&gt;</p>
<p class="p2"><br></p>
<p class="p2"><br></p>
<p class="p1">&lt;table border="0" cellspacing="0" cellpadding="0" class="imcmsAdmTable" align="center" style="margin: 0 auto;"&gt;</p>
<p class="p1">&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmTable"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;table border="0" cellspacing="0" cellpadding="0"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmBgHead" colspan="6"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="20" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr class="imcmsAdmBgHead"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td nowrap="nowrap"&gt;&lt;span class="imcmsAdmHeadingTop"&gt;&lt;%= heading %&gt;&lt;/span&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td align="right"&gt;&lt;a href="http://www.imcms.net/" target="_blank"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/logo_imcms_admin.gif" width="100" height="20" alt="www.imcms.net" border="0" /&gt;&lt;/a&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="6" class="imcmsAdmBgHead"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="20" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr class="imcmsAdmBgHead"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;&lt;input type="button" class="imcmsFormBtn" value="Try again" onclick="document.location='&lt;%= request.getContextPath() %&gt;/'; return false" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmBgHead" colspan="6"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="20" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td height="10" class="imcmsAdmBorder"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmBgCont" colspan="4"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmBorder"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr class="imcmsAdmBgCont"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmBorder"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;</p>
<p class="p2"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span></p>
<p class="p2"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;div class="imcmsAdmText" style="width:350px; padding: 10px 0; font-size:14px;"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;%= text %&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;/div&gt;</p>
<p class="p2"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span></p>
<p class="p2"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmBorder"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td height="10" class="imcmsAdmBorder"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="4" class="imcmsAdmBgCont"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td class="imcmsAdmBorder"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr class="imcmsAdmBgCont"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="24" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="24" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td&gt;&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/1x1.gif" width="1" height="1" alt="" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/table&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;td align="right" valign="top" style="background: transparent url(&lt;%= imcms_url + "/imcms/eng/images/admin/imcms_admin_shadow_right.gif" %&gt;) top right repeat-y;"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/imcms_admin_shadow_right_top.gif" width="12" height="12" alt="" border="0" /&gt;&lt;/td&gt;</p>
<p class="p1">&lt;/tr&gt;</p>
<p class="p1">&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;td colspan="2"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;table border="0" cellspacing="0" cellpadding="0" width="100%"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td style="background: transparent url(&lt;%= imcms_url + "/imcms/eng/images/admin/imcms_admin_shadow_bottom.gif" %&gt;) top left repeat-x;"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/imcms_admin_shadow_bottom_left.gif" width="12" height="12" alt="" border="0" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;td style="background: transparent url(&lt;%= imcms_url + "/imcms/eng/images/admin/imcms_admin_shadow_bottom.gif" %&gt;) top left repeat-x;" align="right"&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span><span class="Apple-tab-span">	</span>&lt;img src="&lt;%= imcms_url %&gt;/imcms/eng/images/admin/imcms_admin_shadow_bottom_right.gif" width="12" height="12" alt="" border="0" /&gt;&lt;/td&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/tr&gt;</p>
<p class="p1"><span class="Apple-tab-span">	</span>&lt;/table&gt;&lt;/td&gt;</p>
<p class="p1">&lt;/tr&gt;</p>
<p class="p1">&lt;/table&gt;</p>
<p class="p2"><br></p>
<p class="p2"><br></p>
<p class="p1">&lt;/body&gt;</p>
<p class="p1">&lt;/html&gt;</p>
</body>
</html>

<html>
<head><title><? templates/sv/AdminIpWhiteList_Add.jsp/1 ?></title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css.jsp">
    <script src="${contextPath}/imcms/${language}/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
</head>
<body><!-- gui_outer_start -->
<table border="0" cellspacing="0" cellpadding="0" class="imcmsAdmTable" align="center">
    <tr>
        <td class="imcmsAdmTable">            <!-- /gui_outer_start -->            <!-- gui_head -->
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="imcmsAdmBgHead" colspan="6"><img
                            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="20" alt=""/>
                    </td>
                </tr>
                <tr class="imcmsAdmBgHead">
                    <td colspan="2"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                    <td nowrap="nowrap"><span class="imcmsAdmHeadingTop"><? global/imcms_administration ?></span></td>
                    <td align="right"><a href="http://www.imcms.net/" target="_blank" title="www.imcms.net"><img
                            src="${contextPath}/imcms/${language}/images/admin/logo_imcms_admin.gif" width="100"
                            height="20" alt="www.imcms.net" border="0"/></a></td>
                    <td colspan="2"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                </tr>
                <tr>
                    <td colspan="6" class="imcmsAdmBgHead"><img
                            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="20" alt=""/>
                    </td>
                </tr>
                <tr class="imcmsAdmBgHead">
                    <td colspan="2"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                    <td colspan="2">                        <!-- /gui_head -->
                        <!-- gui_head_help_and_back_buttons -->
                        <form method="post" action="AdminIpWhiteList">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><input type="submit" class="imcmsFormBtn" name="CANCEL_ADD_IP"
                                               value="<? global/back ?>"/></td>
                                    <td style="padding-left:10px;"><input type="button" value="<? global/help ?>"
                                                                          title="<? global/openthehelppage ?>"
                                                                          class="imcmsFormBtn"
                                                                          onclick="openHelpW('IPAccess'); return false"/>
                                    </td>
                                </tr>
                            </table>
                        </form>                        <!-- /gui_head_help_and_back_buttons -->
                        <!-- gui_mid -->                    </td>
                    <td colspan="2"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                </tr>
                <tr>
                    <td class="imcmsAdmBgHead" colspan="6"><img
                            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="20" alt=""/>
                    </td>
                </tr>
                <tr>
                    <td height="10" class="imcmsAdmBorder"><img
                            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                    <td class="imcmsAdmBgCont" colspan="4"><img
                            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                    <td class="imcmsAdmBorder"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                </tr>
                <tr class="imcmsAdmBgCont">
                    <td class="imcmsAdmBorder"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                    <td colspan="2">                        <!-- /gui_mid -->
                        <form method="post" action="AdminIpWhiteList" name="addIP">
                            <table border="0" cellspacing="0" cellpadding="2" width="400">
                                <tr>
                                    <td colspan="2">                                        <!-- gui_heading --> <span
                                            class="imcmsAdmHeading"><? templates/sv/AdminIpWhiteList_Add.jsp/1 ?></span><br/>
                                        <img src="${contextPath}/imcms/${language}/images/admin/1x1_20568d.gif"
                                             width="100%" height="1" style="margin: 8px 0;" alt=""/>
                                        <!-- /gui_heading -->                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2"><? templates/sv/AdminIpWhiteList_Add.jsp/2 ?></td>
                                </tr>
                                <tr>
                                    <td><? templates/sv/AdminIpAccess.htm/4 ?>${userIP}<br> &nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="2"><img
                                            src="${contextPath}/imcms/${language}/images/admin/1x1_cccccc.gif"
                                            width="100%" height="1" style="margin: 8px 0;" alt=""/></td>
                                </tr>
                                <tr>
                                    <td><label for="roles-select"><? templates/sv/AdminIpWhiteList_Add.jsp/3 ?></label>
                                    </td>
                                    <td><select id="roles-select" name="IS_ADMIN" size="1">
                                        <option value="1">Superadmin</option>
                                        <option value="0">Other roles</option>
                                    </select></td>
                                </tr>
                                <tr>
                                    <td><? templates/sv/AdminIpAccess_Add.htm/6 ?></td>
                                    <td><input type="text" name="IP_START" size="15" maxlength="15"></td>
                                </tr>
                                <tr>
                                    <td><? templates/sv/AdminIpAccess_Add.htm/7 ?></td>
                                    <td><input type="text" name="IP_END" size="15" maxlength="15"></td>
                                </tr>
                                <tr>
                                    <td colspan="2"><img
                                            src="${contextPath}/imcms/${language}/images/admin/1x1_cccccc.gif"
                                            width="100%" height="1" style="margin: 8px 0;" alt=""/></td>
                                </tr>
                                <tr>
                                    <td colspan="2"><? templates/sv/AdminIpAccess_Add.htm/8 ?></td>
                                </tr>
                                <tr>
                                    <td colspan="2"><img
                                            src="${contextPath}/imcms/${language}/images/admin/1x1_20568d.gif"
                                            width="100%" height="1" style="margin: 8px 0;" alt=""/></td>
                                </tr>
                                <tr>
                                    <td colspan="2" align="right"><input type="submit" class="imcmsFormBtn"
                                                                         name="ADD_NEW_IP_RANGE"
                                                                         value="<? templates/sv/AdminIpAccess_Add.htm/2001 ?>">
                                        <input type="submit" class="imcmsFormBtn" name="CANCEL_ADD_IP"
                                               value="<? templates/sv/AdminIpAccess_Add.htm/2002 ?>"></td>
                                </tr>
                            </table>
                        </form>                        <!-- gui_bottom -->                    </td>
                    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                    <td class="imcmsAdmBorder"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                </tr>
                <tr>
                    <td height="10" class="imcmsAdmBorder"><img
                            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                    <td colspan="4" class="imcmsAdmBgCont"><img
                            src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                    <td class="imcmsAdmBorder"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif"
                                                    width="1" height="1" alt=""/></td>
                </tr>
                <tr class="imcmsAdmBgCont">
                    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="24" height="1" alt=""/>
                    </td>
                    <td colspan="2"><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1"
                                         height="1" alt=""/></td>
                    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="24" height="1" alt=""/>
                    </td>
                    <td><img src="${contextPath}/imcms/${language}/images/admin/1x1.gif" width="1" height="1" alt=""/>
                    </td>
                </tr>
            </table>            <!-- /gui_bottom -->            <!-- gui_outer_end -->
        <td align="right" valign="top"
            style="background: transparent url(${contextPath}/imcms/${language}/images/admin/imcms_admin_shadow_right.gif) top left repeat-y;">
            <img src="${contextPath}/imcms/${language}/images/admin/imcms_admin_shadow_right_top.gif" width="12"
                 height="12" border="0" alt=""/></td>
    </tr>
    <tr>
        <td colspan="2">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="background: transparent url(${contextPath}/imcms/${language}/images/admin/imcms_admin_shadow_bottom.gif) top left repeat-x;">
                        <img src="${contextPath}/imcms/${language}/images/admin/imcms_admin_shadow_bottom_left.gif"
                             width="12" height="12" border="0" alt=""/></td>
                    <td style="background: transparent url(${contextPath}/imcms/${language}/images/admin/imcms_admin_shadow_bottom.gif) top left repeat-x;"
                        align="right"><img
                            src="${contextPath}/imcms/${language}/images/admin/imcms_admin_shadow_bottom_right.gif"
                            width="12" height="12" border="0" alt=""/></td>
                </tr>
            </table>
        </td>
    </tr>
</table><!-- /gui_outer_end --></body>
</html>

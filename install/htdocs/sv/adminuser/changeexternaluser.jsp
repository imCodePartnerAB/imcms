<%@ page import="com.imcode.imcms.WebAppConstants,
                 com.imcode.imcms.UserMapper,
                 com.imcode.imcms.User,
                 java.util.*,
                 javax.servlet.http.HttpServletResponse,
                 javax.servlet.ServletException,
                 java.io.IOException"%>
<%!
   private final static String ACTION_SAVE_USER = "SAVE_USER" ;
   private final static String ACTION_CANCEL = "CANCEL" ;

   private final static String FORM_SELECT_ROLES = "roles" ;

   private final static String SERVLET_ADMIN_USER = "/servlet/AdminUser" ;
   private final static String MY_LOCATION_URL = "/adminuser/changeexternaluser.jsp" ;

   private static boolean buttonPressed (HttpServletRequest request, String buttonName) {
      boolean buttonPressed = null != request.getParameter(buttonName) ;
      return buttonPressed ;
   }

   private static void redirectToAdminUser (HttpServletRequest request,
                                           HttpServletResponse response) throws javax.servlet.ServletException, IOException {
      String url = request.getContextPath() + SERVLET_ADMIN_USER;
      response.sendRedirect( url );
   }

%>

<%
   UserMapper  userMapper = (UserMapper)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );

   String userLoginName = request.getParameter( WebAppConstants.USER_LOGIN_NAME );
   User user = userMapper.getUser( userLoginName );

   out.print( request.getParameter( ACTION_CANCEL ) );
   out.print( request.getParameter( ACTION_SAVE_USER ) );

   if ( buttonPressed(request, ACTION_CANCEL) ) {
      redirectToAdminUser(request,response) ;
      return ;
   } else if ( buttonPressed(request, ACTION_SAVE_USER) ) {
      String[] roleNames = request.getParameterValues(FORM_SELECT_ROLES) ;
      userMapper.setUserRoles(user,roleNames) ;
      redirectToAdminUser(request,response) ;
      return ;
   }
%>

<html>
<head>
   <title>Redigera extern användare</title>
</head>
<body>
<form method="POST" action="<%=request.getContextPath() + MY_LOCATION_URL%>">
<table width="550" border="0" cellspacing="0" bgcolor="#bababa">
  <tr bgcolor="#333366">
    <td width="5%">&nbsp;</td>
    <td colspan="3" align="center">
       <font face="Verdana, Arial, Helvetica, sans-serif" color="#ffffff" size="2">
         <b>Redigera extern användare</b>
       </font>
    </td>
    <td width="5%">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="5">&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Informationen
      som ej g&aring;r att &auml;ndra &auml;r h&auml;mtad fr&aring;n ett system
      utanf&ouml;r Imcms. Informationen &auml;ndras i det systemet.</font></td>
    <td>&nbsp;</td>
    <td nowrap><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp; </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td colspan="5">&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td width="74%"><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Användarnamn</font></td>
    <td width="2%">&nbsp;</td>
    <td width="14%" nowrap><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getLoginName()%>
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Förnamn:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"> <%=user.getFirstName()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Efternamn:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"> <%=user.getLastName()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Titel:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getTitle()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Arbetsplats:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCompany()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Gatuadress:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getAddress()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Postnummer:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getZip()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Postort:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCity()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>Telefon, arbetet</td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif" ><%=user.getWorkPhone()%>
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>Telefon, mobil</td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getMobilePhone()%>
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>Telefon, hem</td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getHomePhone()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Län:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCountyCouncil()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Land:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getCountry()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Email:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.getEmailAddress()%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td colspan="3"><hr></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Aktiv:</font></td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%=user.isActive()?"Ja":"Nej"%>&nbsp;
      </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><p><font size="2" face="Verdana, Arial, Helvetica, sans-serif">Användarens 
          Roller:</font></p>
        <p><font size="2" face="Verdana, Arial, Helvetica, sans-serif">(Varning! 
          Endast de roller som hanteras internt i Imcms blir varaktigt lagrade 
          p&aring; anv&auml;ndaren. Roller som hanteras av det externa systemet 
          uppdateras vid inloggning.)</font></p>
        </td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">
      <select name="<%= FORM_SELECT_ROLES %>" size="5" multiple onchange="activateUseradmin_roles(); return true;">
	<%
      String[] allRoleNames = userMapper.getAllRolesNames();
      Set setOfAllRoleNames = new TreeSet( Arrays.asList(allRoleNames) );

      String[] userRoleNames = userMapper.getRoleNames( user );
      Set setOfUserRoleNames = new HashSet( Arrays.asList( userRoleNames ));

      for( Iterator iterator = setOfAllRoleNames.iterator(); iterator.hasNext(); ) {
         String roleName = (String)iterator.next();
      %>
      <option value="<%= roleName %>"
         <%= (setOfUserRoleNames.contains(roleName) ? "selected" : "") %>
      >
         <%= roleName %>
      </option>
    <%
      }
%>
	</select></font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td><font size="2" face="Verdana, Arial, Helvetica, sans-serif">&nbsp; </font></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td colspan="3">
      <input type="hidden"
      name="<%=WebAppConstants.USER_LOGIN_NAME%>"
      value="<%=userLoginName%>">
      <input type="submit" name="<%= ACTION_SAVE_USER %>" value="Spara">
      <input type="submit" name="<%= ACTION_CANCEL %>" value="Avbryt">
      &nbsp;
      <a href="GetDoc?meta_id=71" target="_blank" onClick="openHelp('71'); return false;">
         <img src="@imageurl@/se/helpimages/btn_help_round.gif" width="19" border="0" alt="&Ouml;ppna hj&auml;lpsidan">
      </a>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td colspan="5">&nbsp;</td>
  </tr>
</table>
</form>
</body>
</html>

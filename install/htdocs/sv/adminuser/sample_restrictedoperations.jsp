<%@ page
 errorPage="sample_error.jsp"
 import="com.imcode.imcms.UserBean,
                 com.imcode.imcms.UserMapperBean,
                 com.imcode.imcms.WebAppConstants"%><h2>Test, Things that not everywone should be able to do </h2>
<%
   UserMapperBean userMapper = (UserMapperBean)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );
   UserBean[] allUsers = userMapper.getAllUsers();
   String[] systemRoleNames = userMapper.getAllRolesNames();

   UserBean oneUser = allUsers[0];
   userMapper.setUserRoles( oneUser, systemRoleNames );
%>
Changed the roles for user <%=oneUser.getLoginName()%> all possible roles.
<%@ page
 errorPage="sample_error.jsp"
 import="com.imcode.imcms.UserBean,
                 com.imcode.imcms.UserMapperBean,
                 com.imcode.imcms.WebAppConstants"%>
<h2>Test, Things that not everywone should be able to do </h2>

<h3>You must be superadmin to do this:</h3>

<%
   UserMapperBean userMapper = (UserMapperBean)request.getAttribute( WebAppConstants.USER_MAPPER_ATTRIBUTE_NAME );
   UserBean[] allUsers = userMapper.getAllUsers();
   for( int i = 0; i<allUsers.length; i++ ){%>
       <%= allUsers[i] %><%
   }
%>

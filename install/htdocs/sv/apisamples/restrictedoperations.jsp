<%@ page
 errorPage="sample_error.jsp"
 import="com.imcode.imcms.*"%>
<h2>Test, Things that not everywone should be able to do </h2>

<h3>You must be superadmin to do this:</h3>

<%
    ImcmsSystem imcmsSystem = (ImcmsSystem)request.getAttribute( RequestConstants.SYSTEM );
    UserMapperBean userMapper = imcmsSystem.getUserMapperBean();
    UserBean[] allUsers = userMapper.getAllUsers();
    for( int i = 0; i<allUsers.length; i++ ){%>
       <%= allUsers[i] %><%
    }
%>

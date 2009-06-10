<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<form:form method="POST" modelAttribute="searchParams">
      <table>
          <tr>
              <td>From</td>
              <td><form:input path="range.from" /></td>
          </tr>
          <tr>
              <td>To</td>
              <td><form:input path="range.to" /></td>
          </tr>
          <tr>
              <td colspan="2">
                  <input type="submit" value="Search" />
              </td>
          </tr>
      </table>
  </form:form>

<form>
</form>

</body>
</html>
<%@ page import="java.io.File" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="org.aspectj.util.FileUtil" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.imcode.imcms.admin.backdoor.ClojureUtils" %>

<%!
    String node(File node) {
        return node.isDirectory() ? dir(node, node.getName() + "/") : file(node);
    }

    String dir(File dir, String displayName) {
        return String.format("<a href='?cd=%s'>%s</a>", dir.getPath(), displayName);
    }

    String file(File file) {
        return String.format("<a href='?show=%s' target='_blank'>%s</a>", file.getPath(), file.getName());
    }

%>

<%
    String show = request.getParameter("show");

    if (show != null) {
        String content = FileUtil.readAsString(new File(show));
        response.setContentType("text/plain");
        out.write(content);

        return;
    }

    if (request.getParameter("error") != null) {
        response.setContentType("text/plain");
        Imcms.getCmsStartupEx().printStackTrace(new PrintWriter(out));

        return;
    }

    if (request.getParameter("start") != null) {
        try {
            Imcms.startCms();
        } catch (Exception e) {}
    }

    if (request.getParameter("stop") != null) {
        try {
            Imcms.stopCms();
        } catch (Exception e) {}
    }

    if (request.getParameter("mode") != null) {
        if (request.getParameter("mode").equals("c"))
            Imcms.setCmsMode();
        else
            Imcms.setMaintenanceMode();
    }

    String sh = request.getParameter("sh");

    if (sh != null) {
        ClojureUtils.startReplServer(Integer.parseInt(sh));
    }


    String cd = request.getParameter("cd");
    File path = null;

    if (cd != null) {
        path = new File(cd);
    }

    if (path == null) {
        path = Imcms.getPath();
    }
    
    session.setAttribute("path", path);
%>
                                                               
<html>
  <body>
  <hr/>
  ::WELCOME TO IMCMS MAINTENANCE::

  <hr/>
  :Commands:&nbsp;
    |&nbsp;<a href="?start">Start appliction</a>
    |&nbsp;<a href="?stop">Stop appliction</a>
    |&nbsp;<a href="?mode=c">CMS MODE</a>
    |&nbsp;<a href="?mode=m">MAINTENANCE MODE</a>

  <hr/>
  :Status:<br/>
    &nbsp;&nbsp;:Mode:&nbsp;<%=Imcms.getMode()%><br/>  
    &nbsp;&nbsp;:Running:&nbsp;<%=Imcms.getServices() == null ? "NO" : "YES"%><br/>
    &nbsp;&nbsp;:Startup errors:&nbsp;
      <%
        if (Imcms.getCmsStartupEx() == null) {
            %>
            NO ERRORS
            <%
        } else {
            %>
            <a href="?error" target="_blank"><%=Imcms.getCmsStartupEx().getClass()%></a>
            <%
        }
      %>

  <hr/>
  :Quick links:&nbsp;
    |&nbsp;<%=dir(Imcms.getPath(), "*imCms*")%>
    |&nbsp;<%=node(new File(Imcms.getPath(), "WEB-INF/conf"))%>
    |&nbsp;<%=node(new File(Imcms.getPath(), "WEB-INF/classes"))%>
    |&nbsp;<%=node(new File(Imcms.getPath(), "WEB-INF/conf/server.properties"))%>

  <hr/>
  :Navigation:&nbsp;<%=path.getPath()%>
  <br/>
  <table>
      <tr>
        <td><%=dir(path.getParentFile(), "..")%></td>
      </tr>
        <%
        for (File file: path.listFiles()) { %>
            <tr>
              <td><%=node(file)%></td>
            </tr>
        <%
        }
        %>
    </table>
  <hr/>

  <%--
  <%=request.getPathInfo()%>   <br/>
  <%=request.getPathTranslated()%><br/>
  <%=request.getRequestURI()%>         <br/>
  <%=request.getRequestURL()%>              <br/>
  <%=request.getServletPath()%>                  <br/>
  --%>
  </body>
</html>
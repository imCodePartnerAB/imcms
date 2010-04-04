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
        Imcms.getStartEx().printStackTrace(new PrintWriter(out));

        return;
    }

    if (request.getParameter("start") != null) {
        try {
            Imcms.start();
        } catch (Exception e) {}
    }

    if (request.getParameter("stop") != null) {
        try {
            Imcms.stop();
        } catch (Exception e) {}
    }

    if (request.getParameter("mode") != null) {
        if (request.getParameter("mode").equals("n"))
            Imcms.setNormalMode();
        else
            Imcms.setMaintenanceMode();
    }

    String repl = request.getParameter("repl");

    if (repl != null) {
        ClojureUtils.startReplServer(Integer.parseInt(repl));
    }


    String swank = request.getParameter("swank");

    if (swank != null) {
        ClojureUtils.startSwankServer(Integer.parseInt(swank));
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
    |&nbsp;<a href="?mode=n">NORMAL MODE</a>
    |&nbsp;<a href="?mode=m">MAINTENANCE MODE</a>

  <hr/>
  :Status:<br/>
    &nbsp;&nbsp;:Mode:&nbsp;<%=Imcms.getMode()%><br/>  
    &nbsp;&nbsp;:Running:&nbsp;<%=Imcms.getServices() == null ? "NO" : "YES"%><br/>
    &nbsp;&nbsp;:Startup errors:&nbsp;
      <%
        if (Imcms.getStartEx() == null) {
            %>
            NO ERRORS
            <%
        } else {
            %>
            <a href="?error" target="_blank"><%=Imcms.getStartEx().getClass()%></a>
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
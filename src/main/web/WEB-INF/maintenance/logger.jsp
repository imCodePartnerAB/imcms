<%@ page import="java.io.File" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="org.aspectj.util.FileUtil" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.imcode.imcms.admin.backdoor.ClojureUtils" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="org.apache.log4j.spi.LoggerRepository" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.Enumeration" %>

<%!
%>

<%
    LoggerRepository loggerRepository = LogManager.getLoggerRepository();
    //loggerRepository.getCurrentLoggers()
%>

<html>
  <body>
  <hr/>
  ::LOGGER SETTINGS::
  <hr/>
  :Threshold: <%=loggerRepository.getThreshold()%>
  </hr>
  :Loggers and its levels:
  <br/>
  <br/>
  <%

  Enumeration loggers = loggerRepository.getCurrentLoggers();

  while (loggers.hasMoreElements()) {
      Logger logger = (Logger)loggers.nextElement();
    %>
    Logger: <%=logger.getName()%>:&nbsp;<%=logger.getLevel()%><br/>
    <%
  }
  %>
  <hr/>
  :Conf:<br>
  <%%>
  </body>
</html>
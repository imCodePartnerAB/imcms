<%@ page import="java.io.File,
                 java.util.Arrays,
                 org.apache.commons.io.filefilter.FileFilterUtils,
                 java.io.FileFilter"%>
<%@page contentType="text/html"%>
<html>
    <head>
        <title>Included documentation</title>
    </head>
    <body>
        <h1>Included documentation</h1>
        <%
            File servletFile = new File(config.getServletContext().getRealPath( request.getServletPath() ) );
            File[] directories = servletFile.getParentFile().listFiles((FileFilter)FileFilterUtils.directoryFileFilter() ) ;
            File[] files = servletFile.getParentFile().listFiles((FileFilter)FileFilterUtils.notFileFilter( FileFilterUtils.directoryFileFilter() )) ;
            Arrays.sort( directories ) ;
            Arrays.sort( files ) ;
        %>
        <ul>
        <%
            for ( int i = 0; i < directories.length; i++ ) {
                File directory = directories[i];
                %><li><a href="<%= directory.getName() %>/"><%= directory.getName() %>/</a></li><%
            }
        %>
        </ul>
        <ul>
        <%
            for ( int i = 0; i < files.length; i++ ) {
                File file = files[i];
                if (servletFile.equals( file )) {
                    continue;
                }
                %><li><a href="<%= file.getName() %>"><%= file.getName() %></a></li><%
            }
        %>
        </ul>
    </body>
</html>

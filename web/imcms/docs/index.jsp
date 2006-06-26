<%@ page import="java.io.File,
                 java.util.Arrays,
                 java.io.FileFilter,
                 org.apache.commons.io.filefilter.*"%>
<%@page contentType="text/html; charset=UTF-8"%>
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
                String filename = file.getName().toLowerCase() ;
                boolean filenameOk = filename.endsWith( ".txt" ) || filename.endsWith( ".html" );
                if (!filenameOk) {
                    continue;
                }
                %><li><a href="<%= file.getName() %>"><%= file.getName() %></a></li><%
            }
        %>
        </ul>
    </body>
</html>

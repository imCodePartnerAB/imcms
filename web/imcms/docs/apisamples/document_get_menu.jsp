<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %><%!
    private String makeLink(Document document,HttpServletRequest request) {
        return "<a href=\""+request.getContextPath()+"/servlet/GetDoc?meta_id="+ document.getId() +"\">document "+ document.getId() + "</a> with headline <b>"+document.getHeadline()+"</b>" ;
    }

    private String getTreeOutput( int treeLevel, int startIndex, TextDocument.MenuItem[] menuItems, HttpServletRequest request ) {
        StringBuffer result = new StringBuffer();
        int index = startIndex;
        if( "".equals( menuItems[index].getTreeKey().toString()) ){
            if ( index+1 < menuItems.length  ){
                result.append(getTreeOutput( treeLevel, index+1, menuItems, request ));
            }
        } else {
            result.append( "<ul>\n" ); // Beginning of each branch
            while( existMoreKeysInBranch( treeLevel, index, menuItems ) ){
                if( treeLevel == menuItems[index].getTreeKey().getLevelCount() ) {
                    result.append( "<li>"+ menuItems[index].getTreeKey() + ", " + makeLink(menuItems[index].getDocument(),request) + "</li>\n" ); // Node output
                    index++;
                } else { /* sublevel */
                    String subLevelResult = getTreeOutput( treeLevel + 1, index, menuItems, request );
                    result.append( subLevelResult );
                    index += numberOfUsedIndexInSubLevelTree( treeLevel + 1, index, menuItems );
                }
            }
            result.append( "</ul>" ); // End of each branch
        }
        return result.toString();
    }

    private int numberOfUsedIndexInSubLevelTree( int treeLevel, int index, TextDocument.MenuItem[] menuItems ) {
        int count = 0;
        for( int i = index; i < menuItems.length && treeLevel <= menuItems[i].getTreeKey().getLevelCount(); i++ ){
            count++;
        }
        return count;
    }

    private  boolean existMoreKeysInBranch( int treeLevel, int startIndex, TextDocument.MenuItem[] menuItems ) {
        boolean result = false;
        for (int i = startIndex; i < menuItems.length && treeLevel <= menuItems[i].getTreeKey().getLevelCount() ; i++) {
            result = true;
        }
        return result;
    }
%><%
    int documentId = 1001 ;
    int menuIndex = 1 ;
%><html>
<body>
<p>
    The documents in menu number <%= menuIndex %> on <%= documentId %> are:
</p>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.Menu menu = document.getMenu(menuIndex) ;

    TextDocument.MenuItem[] menuItems = menu.getVisibleMenuItems();
    // To also get non-visible menuitems:
    // TextDocument.MenuItem[] menuItems = menu.getMenuItems();
    if (menuItems.length > 0) { %>
        <ul><%
        for ( int i = 0; i < menuItems.length; i++ ) {
            TextDocument.MenuItem menuItem = menuItems[i];
            Document linkedDocument = menuItem.getDocument();
            %><li><%=makeLink( linkedDocument, request )%><br>
            Manual sort key:<%=menuItem.getSortKey()%><br>
            Tree key:<%=menuItem.getTreeKey()%></li><%
        }
        %></ul><%
    } else {
        %>There are no documents in menu <%= menuIndex %> on <%= documentId %>.<%
    }
%>
<p>
Using the tree keys to order the documents in a tree structure:
</p>
<%
    int treeStartLevel = 1;
    int startIndex = 0;
    if( menuItems.length > 0 ) {
        %><%= getTreeOutput( treeStartLevel, startIndex, menuItems, request ) %><%
    }
%>
</body>
</html>

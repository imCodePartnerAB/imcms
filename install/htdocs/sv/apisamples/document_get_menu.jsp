<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    private String makeLink(Document document) throws NoPermissionException {
        return "<a href=\"../servlet/GetDoc?meta_id="+ document.getId() +"\">document "+ document.getId() + "</a> with headline <b>"+document.getHeadline()+"</b>" ;
    }

    public String getTreeOutput( int treeLevel, int startIndex, TextDocument.MenuItem[] menuItems ) throws NoPermissionException {
        StringBuffer result = new StringBuffer();
        int index = startIndex;
        if( "".equals( menuItems[index].getTreeKey().toString()) ){
            result.append("Warning, tree key (at index " + index + ") has no value, stepping to next.<br>");
            result.append(getTreeOutput( treeLevel, startIndex+1, menuItems));
        } else {
            result.append( "<ul>\n" ); // Beginning of each branch
            while( existMoreKeysInBranch( treeLevel, index, menuItems ) ){
                if( treeLevel == menuItems[index].getTreeKey().getLevelCount() ) {
                    result.append( "<li>"+ menuItems[index].getTreeKey() + ", " + makeLink(menuItems[index].getDocument()) + "</li>\n" ); // Node output
                    index++;
                } else { /* sublevel */
                    String subLevelResult = getTreeOutput( treeLevel + 1, index, menuItems );
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
%>

<%
    int documentId = 1001 ;
    int menuIndex = 1 ;
%>

The documents in menu number <%= menuIndex %> on <%= documentId %> is:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.Menu menu = document.getMenu(menuIndex) ;

    Document[] documents = menu.getDocuments() ;
    if (documents.length > 0) {
        for ( int i = 0; i < documents.length; i++ ) {
            Document linkedDocument = documents[i];
            %><%=makeLink( linkedDocument )%><br><%
        }
    } else {
        %>there are no documents in menu <%= menuIndex %> on <%= documentId %>.<%
    }
%>
<br><br>
If you want to user any kind of ordering you have to use the sort keys.<br>
The menu items with there sort keys:<br><br>
<%
    TextDocument.MenuItem[] menuItems = menu.getMenuItems();
    for (int i = 0; i < menuItems.length; i++) {
        TextDocument.MenuItem menuItem = menuItems[i];
        %>
            Manual sort key:<%=menuItem.getManualNumber()+"<br>"%>
            Tree key:<%=menuItem.getTreeKey()+"<br><br>"%>
        <%
    }
%><br>

When the (tree) keys is used to order the document in a tree structure:<br>
<%
    String treeStr = "";
    int treeStartLevel = 1;
    int startIndex = 0;
    treeStr = getTreeOutput( treeStartLevel, startIndex, menuItems );
%>
<%=treeStr%>



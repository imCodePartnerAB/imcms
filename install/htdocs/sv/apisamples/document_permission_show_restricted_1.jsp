<? sv/apisamples/document_permission_show_restricted_1.jsp/1 ?>
<ul>
  <li><? sv/apisamples/document_permission_show_restricted_1.jsp/2 ?></li>
  <li><? sv/apisamples/document_permission_show_restricted_1.jsp/3 ?></li>
  <li><? sv/apisamples/document_permission_show_restricted_1.jsp/4 ?></li>
  <li><? sv/apisamples/document_permission_show_restricted_1.jsp/5 ?></li>
  <li><? sv/apisamples/document_permission_show_restricted_1.jsp/6 ?></li>
  <li><? sv/apisamples/document_permission_show_restricted_1.jsp/7 ?></li>
</ul>
<? sv/apisamples/document_permission_show_restricted_1.jsp/8 ?>
            <ul><%
            String[] menuNames = restrictedOne.getEditableMenuDocumentTypeNames();
            for( int i = 0; i < menuNames.length ; i++ ) {%>
                <li><%=menuNames[i].toString()%></li><%
            }%>
            </ul> <? sv/apisamples/document_permission_show_restricted_1.jsp/9 ?>
            <ul> <%
            String[] templateNames = restrictedOne.getEditableTemplateGroupNames();
            for( int i = 0; i < templateNames.length ; i++ ) {%>
                <li><%=templateNames[i].toString()%></li><%
            }%>
            </ul> <%
        }
%>







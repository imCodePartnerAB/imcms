<? sv/apisamples/document_set_sortorder.jsp/1 ?><option value="+value+ (currentSelection == value ? " selected" : "")+"><? sv/apisamples/document_set_sortorder.jsp/2 ?></option><? sv/apisamples/document_set_sortorder.jsp/3 ?><p><? sv/apisamples/document_set_sortorder.jsp/4 ?></p><%
    }
    int currentSortOrder =  document.getMenuSortOrder();
%>
        <form method="POST">
            <? sv/apisamples/document_set_sortorder.jsp/5 ?> <select name="sortorder">
                <%= makeOption(TextDocument.Menu.SORT_BY_HEADLINE, "Headline", currentSortOrder) %>
                <%= makeOption(TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING, "Modified date/time", currentSortOrder) %>
                <%= makeOption(TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING, "Manual order", currentSortOrder) %>
            </select>
            <input type="submit" value="<? sv/apisamples/document_set_sortorder.jsp/2001 ?>">
        </form>

package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.SearchItem;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import imcode.server.document.index.IndexSearchResult;
import org.apache.solr.client.solrj.SolrQuery;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchTag extends BodyTagSupport implements IPageableTag {
    private static final String SKIP_KEY = "skip";
    private static final String TAKE_KEY = "take";
    private static final String SEARCH_REQUEST_KEY = "sr";
    private volatile IndexSearchResult<DocumentStoredFields> searchResult;
    private volatile List<DocumentStoredFields> resultList;
    private volatile ContentManagementSystem contentManagementSystem;
    private volatile String searchRequest = "";
    private volatile Integer skip = 0;
    private volatile Integer take = 20;
    private volatile int currentItemIndex = -1;

    public int doStartTag() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        skip = request.getParameterMap().containsKey(SKIP_KEY) ?
                Integer.parseInt(request.getParameter(SKIP_KEY)) : skip;
        take = request.getParameterMap().containsKey(TAKE_KEY) ?
                Integer.parseInt(request.getParameter(TAKE_KEY)) : take;
        searchRequest = request.getParameterMap().containsKey(SEARCH_REQUEST_KEY) ?
                request.getParameter(SEARCH_REQUEST_KEY) : searchRequest;
        if (searchRequest.isEmpty() || take < 1 || skip < 0) return SKIP_BODY;
        searchResult = Imcms.getServices()
                .getDocumentMapper()
                .getDocumentIndex()
                .search(createQuery());

        if (!searchResult.isEmpty()) {
            currentItemIndex = -1;
            resultList = searchResult.storedFieldsList().subList(
                    (skip) >= searchResult.size() ? 0 : skip,
                    (skip + take) >= searchResult.size() ? searchResult.size() : (skip + take)
            );
            contentManagementSystem = ContentManagementSystem.fromRequest(pageContext.getRequest());
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    private SolrQuery createQuery() {
        String query = Arrays.stream(new String[]{DocumentIndex.FIELD__META_ID, DocumentIndex.FIELD__META_HEADLINE, DocumentIndex.FIELD__META_TEXT,
		        DocumentIndex.FIELD__KEYWORD, DocumentIndex.FIELD__META_ALIAS, DocumentIndex.FIELD__TEXT
        }).map(field -> String.format("%s:*%s*", field, searchRequest)).collect(Collectors.joining(" "));
        return new SolrQuery(query);
    }

    public SearchItem nextSearchItem() {
        if (++currentItemIndex >= resultList.size())
            return null;
        DocumentStoredFields storedFields = resultList.get(currentItemIndex);
        SearchItem searchItem = new SearchItem(
                contentManagementSystem.getDocumentService().getDocument(storedFields.id()),
                storedFields);
        pageContext.setAttribute("searchItem", searchItem);
        return searchItem;
    }

    public int doAfterBody() {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        try {
            String bodyContentString = null != getBodyContent() ? getBodyContent().getString() : "";
            bodyContent = null;
            pageContext.getOut().write(bodyContentString);
        } catch (IOException | RuntimeException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }


    public String getSearchRequest() {
        return searchRequest;
    }

    public void setSearchRequest(String searchRequest) {
        this.searchRequest = searchRequest;
    }

    public IndexSearchResult getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(IndexSearchResult searchResult) {
        this.searchResult = searchResult;
    }

    @Override
    public Integer size() {
        return searchResult.size();
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Integer getTake() {
        return take;
    }

    public void setTake(Integer take) {
        this.take = take;
    }
}

package com.imcode.imcms.servlet;

import com.imcode.imcms.servlet.admin.Handler;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.QueryParser;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.util.Utility;
import org.apache.commons.collections.SetUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.lucene.search.BooleanClause.Occur;

public class DocumentFinder extends WebComponent {

    private Handler<Integer> selectDocumentCommand;
    private Query restrictingQuery;
    private QueryParser queryParser = new DefaultQueryParser();
    private Set extraSearchResultColumns = SetUtils.orderedSet(new HashSet());
    private DocumentFinderPage page;
    private Comparator<DocumentDomainObject> documentComparator;
    private boolean logged;

    public DocumentFinder() {
        this(new SearchDocumentsPage());
    }

    public DocumentFinder(DocumentFinderPage page) {
        this.page = page;
        page.setDocumentFinder(this);
    }

    public void selectDocument(DocumentDomainObject selectedDocument) {
        selectDocumentCommand.handle(selectedDocument.getId());
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        forwardWithPage(request, response, page);
    }

    void forwardWithPage(HttpServletRequest request, HttpServletResponse response, DocumentFinderPage page) throws IOException, ServletException {
        ImcmsServices service = Imcms.getServices();
        DocumentIndex index = service.getDocumentMapper().getDocumentIndex();
        final BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

        if (null != page.getQuery()) {
            booleanQueryBuilder.add(page.getQuery(), Occur.MUST);
        }
        if (null != restrictingQuery) {
            booleanQueryBuilder.add(restrictingQuery, Occur.MUST);
        }

        final BooleanQuery booleanQuery = booleanQueryBuilder.build();

        if (booleanQuery.iterator().hasNext()) {
            List<DocumentDomainObject> documentsFound = index.search(new SimpleDocumentQuery(booleanQuery, null, logged), Utility.getLoggedOnUser(request));
            if (null != documentComparator) {
                documentsFound.sort(documentComparator);
            }
            page.setDocumentsFound(documentsFound);
        }
        page.forward(request, response);
    }

    public boolean isDocumentsSelectable() {
        return null != selectDocumentCommand;
    }

    public void setSelectDocumentCommand(Handler<Integer> selectDocumentCommand) {
        this.selectDocumentCommand = selectDocumentCommand;
    }

    public void setRestrictingQuery(Query restrictingQuery) {
        this.restrictingQuery = restrictingQuery;
    }

    public void setQueryParser(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    public Query parse(String queryString) throws ParseException {
        return queryParser.parse(queryString);
    }

    public void addExtraSearchResultColumn(SearchResultColumn searchResultColumn) {
        extraSearchResultColumns.add(searchResultColumn);
    }

    public SearchResultColumn[] getExtraSearchResultColumns() {
        return (SearchResultColumn[]) extraSearchResultColumns.toArray(new SearchResultColumn[extraSearchResultColumns.size()]);
    }

    public void setDocumentComparator(Comparator<DocumentDomainObject> documentComparator) {
        this.documentComparator = documentComparator;
    }

    public void dispatchReturn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        cancel(request, response);
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public interface SearchResultColumn extends Serializable {

        String render(DocumentDomainObject document, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

        LocalizedMessage getName();
    }

}

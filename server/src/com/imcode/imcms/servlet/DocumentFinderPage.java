package com.imcode.imcms.servlet;

import org.apache.lucene.search.Query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.List;
import java.io.IOException;

public interface DocumentFinderPage {

    Query getQuery();

    void setDocumentsFound(List documentsFound);

    void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException ;

    void setDocumentFinder(DocumentFinder documentFinder);
}
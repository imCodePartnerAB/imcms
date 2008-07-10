package com.imcode.imcms.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Query;

public interface DocumentFinderPage {

    Query getQuery();

    void setDocumentsFound(List documentsFound);

    void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException ;

    void setDocumentFinder(DocumentFinder documentFinder);
}
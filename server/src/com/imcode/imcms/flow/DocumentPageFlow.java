package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;

import javax.servlet.http.HttpServletRequest;

public abstract class DocumentPageFlow extends HttpPageFlow {

    public final static String URL_I15D_PAGE__PREFIX = "/imcms/";
    public static final String PAGE__EDIT = "edit";

    public abstract DocumentDomainObject getDocument() ;

    protected abstract void saveDocument(HttpServletRequest request) ;

}
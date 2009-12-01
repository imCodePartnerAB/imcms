package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.RequestInfo;

// Experimental
public class SetActiveDocVersionPageFlow extends DocumentPageFlow {

	private DocumentDomainObject document;

	private UserDomainObject user;

	public SetActiveDocVersionPageFlow(DocumentDomainObject document, DispatchCommand returnCommand,
			SaveDocumentCommand saveDocumentCommand, UserDomainObject user) {
		super(returnCommand, saveDocumentCommand);

		this.document = document;
		this.user = user;
	}

	@Override
	public DocumentDomainObject getDocument() {
		return document;
	}

	@Override
	protected void dispatchFromPage(HttpServletRequest request,
			HttpServletResponse response, String page) throws IOException,
			ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dispatchOk(HttpServletRequest request,
			HttpServletResponse response, String page) throws IOException,
			ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void dispatchToFirstPage(HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
	    saveDocument( request );
		Imcms.getRequestInfo().setDocVersionMode(RequestInfo.DocVersionMode.DEFAULT);
    	//dispatchReturn( request, response );
		request.getRequestDispatcher("/servlet/GetDoc").forward(request, response);
	}


}
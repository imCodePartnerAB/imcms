package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.DocumentVersionTag;
import com.imcode.imcms.flow.DocumentPageFlow.SaveDocumentCommand;

// Experemental
public class PublishDocumentPageFlow extends DocumentPageFlow {

	private DocumentDomainObject document;
	
	private UserDomainObject user;
	
	public PublishDocumentPageFlow(DocumentDomainObject document, DispatchCommand returnCommand,
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
		user.getDocumentShowSettings().setDocumentVersionTag(DocumentVersionTag.PUBLISHED);
    	dispatchReturn( request, response );		
	}
	
	
}
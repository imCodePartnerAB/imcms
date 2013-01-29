package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;

public class I18nDisabledException extends I18nException {

	private DocumentDomainObject document;
	
	private ContentLanguage language;
	
	public I18nDisabledException(DocumentDomainObject document, ContentLanguage language) {
		this.document = document;
		this.language = language;
	}

	public DocumentDomainObject getDocument() {
		return document;
	}

	public void setDocument(DocumentDomainObject document) {
		this.document = document;
	}

	public ContentLanguage getLanguage() {
		return language;
	}

	public void setLanguage(ContentLanguage language) {
		this.language = language;
	}
}
package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;

public class I18nDisabledException extends I18nException {

	private DocumentDomainObject document;
	
	private I18nLanguage language;
	
	public I18nDisabledException(DocumentDomainObject document, I18nLanguage language) {
		this.document = document;
		this.language = language;
	}

	public DocumentDomainObject getDocument() {
		return document;
	}

	public void setDocument(DocumentDomainObject document) {
		this.document = document;
	}

	public I18nLanguage getLanguage() {
		return language;
	}

	public void setLanguage(I18nLanguage language) {
		this.language = language;
	}
}
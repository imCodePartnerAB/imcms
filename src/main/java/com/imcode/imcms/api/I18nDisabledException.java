package com.imcode.imcms.api;

import com.imcode.imcms.mapping.orm.DocumentLanguage;
import imcode.server.document.DocumentDomainObject;

public class I18nDisabledException extends I18nException {

	private DocumentDomainObject document;
	
	private DocumentLanguage language;
	
	public I18nDisabledException(DocumentDomainObject document, DocumentLanguage language) {
		this.document = document;
		this.language = language;
	}

	public DocumentDomainObject getDocument() {
		return document;
	}

	public void setDocument(DocumentDomainObject document) {
		this.document = document;
	}

	public DocumentLanguage getLanguage() {
		return language;
	}

	public void setLanguage(DocumentLanguage language) {
		this.language = language;
	}
}
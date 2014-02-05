package com.imcode.imcms.api;

import com.imcode.imcms.mapping.orm.DocLanguage;
import imcode.server.document.DocumentDomainObject;

public class I18nDisabledException extends I18nException {

	private DocumentDomainObject document;
	
	private DocLanguage language;
	
	public I18nDisabledException(DocumentDomainObject document, DocLanguage language) {
		this.document = document;
		this.language = language;
	}

	public DocumentDomainObject getDocument() {
		return document;
	}

	public void setDocument(DocumentDomainObject document) {
		this.document = document;
	}

	public DocLanguage getLanguage() {
		return language;
	}

	public void setLanguage(DocLanguage language) {
		this.language = language;
	}
}
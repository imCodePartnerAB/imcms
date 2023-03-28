package com.imcode.imcms.domain.dto.export;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import imcode.server.document.UrlDocumentDomainObject;

import java.io.IOException;

public class URLDocumentSerializer extends AbstractDocumentSerializer<UrlDocumentDomainObject> {
	@Override
	protected void serializeContent(UrlDocumentDomainObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStringField("url", value.getUrl());
	}

}

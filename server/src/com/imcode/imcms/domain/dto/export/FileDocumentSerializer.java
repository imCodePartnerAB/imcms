package com.imcode.imcms.domain.dto.export;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import imcode.server.document.FileDocumentDomainObject;

import java.io.IOException;
import java.util.Map;

public class FileDocumentSerializer extends AbstractDocumentSerializer<FileDocumentDomainObject> {
	@Override
	protected void serializeContent(FileDocumentDomainObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		writeFiles(value, gen, provider);
	}

	private void writeFiles(FileDocumentDomainObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final Map files = value.getFiles();
		if (files.isEmpty()) return;

		gen.writeArrayFieldStart("files");

		for (Object key : files.keySet()) {
			final String fileId = (String) key;
			final FileDocumentDomainObject.FileDocumentFile documentFile = (FileDocumentDomainObject.FileDocumentFile) files.get(key);
			gen.writeStartObject();

			gen.writeStringField("id", documentFile.getId());
			gen.writeStringField("variant_name", fileId);
			gen.writeStringField("filename", documentFile.getFilename());
			gen.writeStringField("mime", documentFile.getMimeType());
			gen.writeBooleanField("created_as_image", documentFile.isCreatedAsImage());
			gen.writeNumberField("size", documentFile.getInputStreamSource().getSize());
			gen.writeBooleanField("default", fileId.equals(value.getDefaultFileId()));

			gen.writeEndObject();
		}

		gen.writeEndArray();
	}
}

package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BasicImportDocumentInfo {

	public BasicImportDocumentInfo(BasicImportDocumentInfo from) {
		setId(from.getId());
		setMetaId(from.getMetaId());
		setStatus(from.getStatus());
	}

	public abstract Integer getId();

	public abstract void setId(Integer id);

	public abstract Integer getMetaId();

	public abstract void setMetaId(Integer metaId);

	public abstract ImportDocumentStatus getStatus();

	public abstract void setStatus(ImportDocumentStatus status);

}

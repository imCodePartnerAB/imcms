package com.imcode.imcms.api.orm;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

@Entity
public class OrmFileDocument extends OrmDocument {

	// ORDER BY default_variant DESC, variant_name
	@Embeddable
	public static class FileRef {
		
		@Column(name="filename")
		private String filename;
		
		@Column(name="created_as_image")
		private Boolean createdAsImage;
		
		@Column(name="mime")
		private String mimeType;
		
		@Column(name="default_variant")
		private Boolean defaultFileId;
		
		

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public Boolean getCreatedAsImage() {
			return createdAsImage;
		}

		public void setCreatedAsImage(Boolean createdAsImage) {
			this.createdAsImage = createdAsImage;
		}

		public Boolean isDefaultFileId() {
			return defaultFileId;
		}

		public void setDefaultFileId(Boolean defaultFileId) {
			this.defaultFileId = defaultFileId;
		}

		public String getMimeType() {
			return mimeType;
		}

		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
	}
	
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(
    	name = "fileupload_docs",
    	joinColumns = @JoinColumn(name = "meta_id"))    		
    @org.hibernate.annotations.MapKey(columns = @Column(name="variant_name"))
	private Map<String, FileRef> fileRefsMap = new HashMap<String, FileRef>();

	public Map<String, FileRef> getFileRefsMap() {
		return fileRefsMap;
	}

	public void setFileRefsMap(Map<String, FileRef> fileRefsMap) {
		this.fileRefsMap = fileRefsMap;
	}
}
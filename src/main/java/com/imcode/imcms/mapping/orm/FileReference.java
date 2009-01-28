/**
 * 
 */
package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//ORDER BY default_variant DESC, variant_name
@Entity
@Table(name="fileupload_docs")
public class FileReference {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="meta_id")
	private Integer metaId;	
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="created_as_image")
	private Boolean createdAsImage;
	
	@Column(name="mime")
	private String mimeType;
	
	@Column(name="default_variant")
	private Boolean defaultFileId;
	
	@Column(name="variant_name")
	private String fileId;			

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
}
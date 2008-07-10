package com.imcode.imcms.api;

import java.io.Serializable;

import javax.persistence.Column;

public class ImageId implements Serializable {

	@Column(name="meta_id")
	private int metaId;
	
	private String name = "";
	
	public ImageId() {}
	
	public ImageId(int metaId, String name) {
		setMetaId(metaId);
		setName(name);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ImageId)) {
			return false;
		}
		
		ImageId imageId = (ImageId) o;
		
		return metaId == imageId.metaId
			&& name.equals(imageId.name);
	}
	
	@Override 
	public int hashCode() {
		return name.hashCode() + metaId;
	}

	public int getMetaId() {
		return metaId;
	}

	public void setMetaId(int metaId) {
		this.metaId = metaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

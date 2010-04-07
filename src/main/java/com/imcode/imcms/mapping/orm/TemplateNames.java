package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Text document templates names. 
 */
@Entity
@Table(name="text_docs")
public class TemplateNames implements Cloneable {
	
    @Id 
	@Column(name="meta_id")
	private Integer metaId;
	
	@Column(name="template_name")
    private String templateName;
	
	@Column(name="group_id")
    private int templateGroupId;
	
	@Column(name="default_template")
    private String defaultTemplateName;
	
	@Column(name="default_template_1")
    private String defaultTemplateNameForRestricted1;
	
	@Column(name="default_template_2")
    private String defaultTemplateNameForRestricted2;

	@Override
    public TemplateNames clone() {
        try {
            return (TemplateNames)super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw new UnhandledException(e);
        }
    }

//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder(15, 3)
//                .append(id)
//                .append(metaId)
//                .append(templateName)
//                .append(templateGroupId)
//                .append(defaultTemplateNameForRestricted1)
//                .append(defaultTemplateNameForRestricted2)
//                .hashCode();
//
//    }
//
//
//    @Override
//    public boolean equals(Object o) {
//        return o instanceof TemplateNames && o.hashCode() == hashCode();
//    }


    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public int getTemplateGroupId() {
        return templateGroupId;
    }

    public void setTemplateGroupId(int templateGroupId) {
        this.templateGroupId = templateGroupId;
    }

    public String getDefaultTemplateName() {
        return defaultTemplateName;
    }

    public void setDefaultTemplateName(String defaultTemplateName) {
        this.defaultTemplateName = defaultTemplateName;
    }

    public String getDefaultTemplateNameForRestricted1() {
        return defaultTemplateNameForRestricted1;
    }

    public void setDefaultTemplateNameForRestricted1(String defaultTemplateNameForRestricted1) {
        this.defaultTemplateNameForRestricted1 = defaultTemplateNameForRestricted1;
    }

    public String getDefaultTemplateNameForRestricted2() {
        return defaultTemplateNameForRestricted2;
    }

    public void setDefaultTemplateNameForRestricted2(String defaultTemplateNameForRestricted2) {
        this.defaultTemplateNameForRestricted2 = defaultTemplateNameForRestricted2;
    }

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}
}
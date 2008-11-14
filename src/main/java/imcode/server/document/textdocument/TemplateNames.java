package imcode.server.document.textdocument;

import javax.persistence.Embeddable;

import imcode.util.LazilyLoadedObject;

import org.apache.commons.lang.UnhandledException;

@Embeddable
public class TemplateNames implements LazilyLoadedObject.Copyable<TemplateNames>, Cloneable {

    private String templateName;
	
    private int templateGroupId;
	
    private String defaultTemplateName;
	
    private String defaultTemplateNameForRestricted1;
	
    private String defaultTemplateNameForRestricted2;

    public TemplateNames copy() {
        return (TemplateNames) clone() ;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw new UnhandledException(e);
        }
    }

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
}
package imcode.server.document;

import imcode.server.IMCConstants;
import imcode.server.Template;

import java.util.Date;

/** Stores all info about a text-internalDocument. **/

public class DocumentDomainObject implements IMCConstants {

    /**
     * These are taken from the databse table doc_types
     */
    public static final String TEXT_DOC_TYPE = "2";
    public static final String URL_DOC_TYPE = "5";
    public static final String BROWSER_CONTROLL_DOC_TYPE = "6";
    public static final String HTML_DOC_TYPE = "7";
    public static final String FILE_DOC_TYPE = "8";
    public static final String CONFERANCE_DOC_TYPE = "102";
    public static final String CHAT_DOC_TYPE = "103";
    public static final String BILLBOARD_DOC_TYPE = "104";
    public static final String CALENDAR_DOC_TYPE = "107";

    private int metaId;
    private int documentType;
    private boolean archived;
    private Date createdDatetime;
    private Date modifiedDatetime;
    private Date activatedDatetime;
    private Date archivedDatetime;
    private String headline;
    private String text;
    private String image;
    private String target;
    private String section;

    /* Filedocs only */
    private String filename;

    /* Textdocs only */
    private Template template;
    private int templateGroupId;
    private int menuSortOrder;


    /**
     * Get the value of metaId.
     * @return value of metaId.
     */
    public int getMetaId() {
        return metaId;
    }

    /**
     * Set the value of metaId.
     * @param v  Value to assign to metaId.
     */
    public void setMetaId( int v ) {
        this.metaId = v;
    }

    /**
     * Get the value of documentType.
     * @return value of documentType.
     */
    public int getDocumentType() {
        return documentType;
    }

    /**
     * Set the value of documentType.
     * @param v  Value to assign to documentType.
     */
    public void setDocumentType( int v ) {
        this.documentType = v;
    }

    /**
     * Check whether this internalDocument is active.
     * A internalDocument is active if it isn't archived, and if activatedDatetime is in the past.
     * @return value of archived.
     */
    public boolean isActive() {
        Date now = new Date();
        return (activatedDatetime == null || activatedDatetime.before( now )) && !isArchivedAtTime( now );
    }

    /**
     * Check whether this internalDocument is archived.
     * A internalDocument is archived if either 'archived' is true, or archiveDatetime is in the past.
     * @return value of archived.
     */
    public boolean isArchived() {
        Date now = new Date();
        return isArchivedAtTime( now );
    }

    private boolean isArchivedAtTime( Date time ) {
        return archived || (archivedDatetime != null && archivedDatetime.before( time ));
    }


    /**
     * Set the value of archived.
     * @param v  Value to assign to archived.
     */
    public void setArchived( boolean v ) {
        this.archived = v;
    }

    /**
     * Get the value of createdDatetime.
     * @return value of createdDatetime.
     */
    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    /**
     * Set the value of createdDatetime.
     * @param v  Value to assign to createdDatetime.
     */
    public void setCreatedDatetime( Date v ) {
        this.createdDatetime = v;
    }

    /**
     * Get the value of modifiedDatetime.
     * @return value of modifiedDatetime.
     */
    public Date getModifiedDatetime() {
        return modifiedDatetime;
    }

    /**
     * Set the value of modifiedDatetime.
     * @param v  Value to assign to modifiedDatetime.
     */
    public void setModifiedDatetime( Date v ) {
        this.modifiedDatetime = v;
    }

    /**
     * Get the value of archivedDatetime.
     * @return value of archivedDatetime.
     */
    public Date getArchivedDatetime() {
        return archivedDatetime;
    }

    /**
     * Set the value of archivedDatetime.
     * @param v  Value to assign to archivedDatetime.
     */
    public void setArchivedDatetime( Date v ) {
        this.archivedDatetime = v;
    }

    /**
     * Get the value of activatedDatetime.
     * @return value of activatedDatetime.
     */
    public Date getActivatedDatetime() {
        return activatedDatetime;
    }

    /**
     * Set the value of archivedDatetime.
     * @param v  Value to assign to archivedDatetime.
     */
    public void setActivatedDatetime( Date v ) {
        this.activatedDatetime = v;
    }

    /**
     * Get the value of headline.
     * @return value of headline.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * Set the value of headline.
     * @param v  Value to assign to headline.
     */
    public void setHeadline( String v ) {
        this.headline = v;
    }

    /**
     * Get the value of text.
     * @return value of text.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the value of text.
     * @param v  Value to assign to text.
     */
    public void setText( String v ) {
        this.text = v;
    }

    /**
     * Get the value of image.
     * @return value of image.
     */
    public String getImage() {
        return image;
    }

    /**
     * Set the value of image.
     * @param v  Value to assign to image.
     */
    public void setImage( String v ) {
        this.image = v;
    }

    /**
     * Get the value of target.
     * @return value of target.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Set the value of target.
     * @param v  Value to assign to target.
     */
    public void setTarget( String v ) {
        this.target = v;
    }

    /**
     * Get the value of filename.
     * @return value of filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the value of filename.
     * @param v  Value to assign to filename.
     */
    public void setFilename( String v ) {
        this.filename = v;
    }

    /**
     * Get the value of section.
     * @return value of section.
     */
    public String getSection() {
        return section;
    }

    /**
     * Set the value of section.
     * @param v  Value to assign to section.
     */
    public void setSection( String v ) {
        this.section = v;
    }

    /**
     * Get the value of template.
     * @return value of template.
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Set the value of template.
     * @param v  Value to assign to template.
     */
    public void setTemplate( Template v ) {
        this.template = v;
    }

    /**
     * Get the value of menuSortOrder.
     * @return value of menuSortOrder.
     */
    public int getMenuSortOrder() {
        return menuSortOrder;
    }

    /**
     * Set the value of menuSortOrder.
     * @param v  Value to assign to menuSortOrder.
     */
    public void setMenuSortOrder( int v ) {
        this.menuSortOrder = v;
    }

    /**
     * Get the value of templateGroupId.
     * @return value of templateGroupId.
     */
    public int getTemplateGroupId() {
        return templateGroupId;
    }

    /**
     * Set the value of templateGroupId.
     * @param v  Value to assign to templateGroupId.
     */
    public void setTemplateGroupId( int v ) {
        this.templateGroupId = v;
    }

}

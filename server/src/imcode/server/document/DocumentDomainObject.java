package imcode.server.document;

import imcode.server.IMCConstants;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

import imcode.server.*;

/** Stores all info about a internalDocument. **/

public class DocumentDomainObject implements IMCConstants {

    // If an field is added, make sure to update DocumentMapper
    private int metaId;
    private int documentType;

    private boolean archivedFlag;
    private Date createdDatetime;
    private Date modifiedDatetime;
    private Date activatedDatetime;
    private Date archivedDatetime;
    private String headline;
    private String text;
    private String image;
    private String target;
    private UserDomainObject creator ;
    private UserDomainObject publisher;
    private String languageIso639_2;
    private Set sections = new HashSet();
    private Set categories = new HashSet() ;
    private Map rolesMappedToPermissionSetIds = new HashMap();


    // todo: classification/Search words is missing

    /* Filedocs only */
    private String filename;

    /* Textdocs only */
    private TemplateDomainObject template;
    private int templateGroupId;
    private int menuSortOrder;
    // If an field is added, make sure to update DocumentMapper

    public final static int DOCTYPE_TEXT       = 2 ;
    public final static int DOCTYPE_URL        = 5 ;
    public final static int DOCTYPE_BROWSER    = 6 ;
    public final static int DOCTYPE_HTML       = 7 ;
    public final static int DOCTYPE_FILE       = 8 ;
    public final static int DOCTYPE_LOWEST_EXTERNAL = 100;
    public final static int DOCTYPE_DIAGRAM    = 101 ;
    public final static int DOCTYPE_CONFERENCE = 102 ;
    public final static int DOCTYPE_CHAT       = 103 ;
    public final static int DOCTYPE_BILLBOARD  = 104 ;
    public final static int DOCTYPE_POSTCARD   = 105 ;
    public final static int DOCTYPE_FORTUNES   = 106 ;
    public final static int DOCTYPE_CALENDER   = 107 ;

    protected DocumentDomainObject() {

    }

    public DocumentDomainObject(IMCServiceInterface serverObject, int meta_id)
            throws IndexOutOfBoundsException, SQLException {
        String[] result = serverObject.sqlProcedure("GetDocumentInfo", new String[]{ ""+meta_id });

        //lets start and do some controlls of the resulted data
        if (result == null || result.length < 25) {
            throw new IndexOutOfBoundsException("No such document: " + meta_id);
        }

        DateFormat dateform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //ok lets set all the document stuff
        try {
            setMetaId(Integer.parseInt(result[0]));
            setDocumentType(Integer.parseInt(result[2]));
        } catch (NumberFormatException nfe) {
            throw new SQLException(
                    "SQL: GetDocumentInfo " + meta_id + " returned corrupt data! '" + result[0] + "' '"
                    + result[2]
                    + "'");
        }
        setHeadline(result[3]);
        setText(result[4]);
        setImage(result[5]);
        setTarget(result[21]);

        setArchivedFlag(!"0".equals(result[12]));

        setSections(DocumentMapper.getSections(serverObject,meta_id));

        try {
            setCreatedDatetime(dateform.parse(result[16]));
        } catch (NullPointerException npe) {
            setCreatedDatetime(null);
        } catch (java.text.ParseException pe) {
            setCreatedDatetime(null);
        }
        try {
            setModifiedDatetime(dateform.parse(result[17]));
        } catch (NullPointerException npe) {
            setModifiedDatetime(null);
        } catch (java.text.ParseException pe) {
            setModifiedDatetime(null);
        }
        try {
            setActivatedDatetime(dateform.parse(result[23]));
        } catch (NullPointerException npe) {
            setActivatedDatetime(null);
        } catch (java.text.ParseException pe) {
            setActivatedDatetime(null);
        }
        try {
            setArchivedDatetime(dateform.parse(result[24]));
        } catch (NullPointerException npe) {
            setArchivedDatetime(null);
        } catch (java.text.ParseException pe) {
            setArchivedDatetime(null);
        }
        if (getDocumentType() == DOCTYPE_FILE) {
            setFilename(serverObject.getFilename(meta_id));
        }
        if (getDocumentType() == DOCTYPE_TEXT) {
            String[] textdoc_data = serverObject.sqlProcedure("GetTextDocData",
                                                              new String[]{String.valueOf(meta_id)});

            if (textdoc_data.length >= 4) {
                setTemplate( serverObject.getTemplateMapper().getTemplateById( Integer.parseInt( textdoc_data[0] ) ) );
                setMenuSortOrder(Integer.parseInt(textdoc_data[2]));
                setTemplateGroupId(Integer.parseInt(textdoc_data[3]));
            }
        }
    }

    public SectionDomainObject[] getSections() {
        return (SectionDomainObject[]) sections.toArray(new SectionDomainObject[sections.size()]) ;
    }

    public void setSections(SectionDomainObject[] sections) {
        this.sections.addAll(Arrays.asList(sections));
    }

    /**
     * Get the value of metaId.
     *
     * @return value of metaId.
     */
    public int getMetaId() {
        return metaId;
    }

    /**
     * Set the value of metaId.
     *
     * @param v Value to assign to metaId.
     */
    public void setMetaId( int v ) {
        this.metaId = v;
    }

    /**
     * Get the value of documentType.
     *
     * @return value of documentType.
     */
    public int getDocumentType() {
        return documentType;
    }

    /**
     * Set the value of documentType.
     *
     * @param v Value to assign to documentType.
     */
    public void setDocumentType( int v ) {
        this.documentType = v;
    }

    /**
     * Check whether this document is active.
     * A document is active if it isn't archived, and if activatedDatetime is in the past.
     **/
    public boolean isActive() {
        Date now = new Date();
        return (activatedDatetime == null || activatedDatetime.before( now )) && !isArchived();
    }

    /**
     * Check whether this document is archived.
     * A document is archived if its archived-flag is set or if archived-datetime is in the past.
     **/
    public boolean isArchived() {
        return isArchivedFlag() || isArchivedAtTime( new Date() ) ;
    }

    public boolean isArchivedAtTime( Date time ) {
        return (archivedDatetime != null && archivedDatetime.before( time ));
    }

    public boolean isArchivedFlag() {
        return archivedFlag;
    }

    /**
     * Set the value of archivedFlag.
     *
     * @param v Value to assign to archivedFlag.
     */
    public void setArchivedFlag( boolean v ) {
        this.archivedFlag = v;
    }

    /**
     * Get the value of createdDatetime.
     *
     * @return value of createdDatetime.
     */
    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    /**
     * Set the value of createdDatetime.
     *
     * @param v Value to assign to createdDatetime.
     */
    public void setCreatedDatetime( Date v ) {
        this.createdDatetime = v;
    }

    /**
     * Get the value of modifiedDatetime.
     *
     * @return value of modifiedDatetime.
     */
    public Date getModifiedDatetime() {
        return modifiedDatetime;
    }

    /**
     * Set the value of modifiedDatetime.
     *
     * @param v Value to assign to modifiedDatetime.
     */
    public void setModifiedDatetime( Date v ) {
        this.modifiedDatetime = v;
    }

    /**
     * Get the value of archivedDatetime.
     *
     * @return value of archivedDatetime.
     */
    public Date getArchivedDatetime() {
        return archivedDatetime;
    }

    /**
     * Set the value of archivedDatetime.
     *
     * @param v Value to assign to archivedDatetime.
     */
    public void setArchivedDatetime( Date v ) {
        this.archivedDatetime = v;
    }

    /**
     * Get the value of activatedDatetime.
     *
     * @return value of activatedDatetime.
     */
    public Date getActivatedDatetime() {
        return activatedDatetime;
    }

    /**
     * Set the value of archivedDatetime.
     *
     * @param v Value to assign to archivedDatetime.
     */
    public void setActivatedDatetime( Date v ) {
        this.activatedDatetime = v;
    }

    /**
     * Get the value of headline.
     *
     * @return value of headline.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * Set the value of headline.
     *
     * @param v Value to assign to headline.
     */
    public void setHeadline( String v ) {
        this.headline = v;
    }

    /**
     * Get the value of text.
     *
     * @return value of text.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the value of text.
     *
     * @param v Value to assign to text.
     */
    public void setText( String v ) {
        this.text = v;
    }

    /**
     * Get the value of image.
     *
     * @return value of image.
     */
    public String getImage() {
        return image;
    }

    /**
     * Set the value of image.
     *
     * @param v Value to assign to image.
     */
    public void setImage( String v ) {
        this.image = v;
    }

    /**
     * Get the value of target.
     *
     * @return value of target.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Set the value of target.
     *
     * @param v Value to assign to target.
     */
    public void setTarget( String v ) {
        this.target = v;
    }

    /**
     * Get the value of filename.
     *
     * @return value of filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the value of filename.
     *
     * @param v Value to assign to filename.
     */
    public void setFilename(String v) {
        this.filename = v;
    }

    /**
     * Get the value of template.
     *
     * @return value of template.
     */
    public TemplateDomainObject getTemplate() {
        return template;
    }

    /**
     * Set the value of template.
     *
     * @param v Value to assign to template.
     */
    public void setTemplate( TemplateDomainObject v ) {
        this.template = v;
    }

    /**
     * Get the value of menuSortOrder.
     *
     * @return value of menuSortOrder.
     */
    public int getMenuSortOrder() {
        return menuSortOrder;
    }

    /**
     * Set the value of menuSortOrder.
     *
     * @param v Value to assign to menuSortOrder.
     */
    public void setMenuSortOrder( int v ) {
        this.menuSortOrder = v;
    }

    /**
     * Get the value of templateGroupId.
     *
     * @return value of templateGroupId.
     */
    public int getTemplateGroupId() {
        return templateGroupId;
    }

    /**
     * Set the value of templateGroupId.
     *
     * @param v Value to assign to templateGroupId.
     */
    public void setTemplateGroupId( int v ) {
        this.templateGroupId = v;
    }

    public UserDomainObject getPublisher() {
        return publisher;
    }

    public UserDomainObject getCreator() {
        return creator;
    }

    public void setCreator( UserDomainObject creator ) {
        this.creator = creator;
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        this.languageIso639_2 = languageIso639_2;
    }

    public String getLanguageIso639_2() {
        return languageIso639_2;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DocumentDomainObject ) ) {
            return false;
        }

        final DocumentDomainObject documentDomainObject = (DocumentDomainObject) o;

        if ( metaId != documentDomainObject.metaId ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return metaId;
    }

    public void setPermissionSetForRole( RoleDomainObject role, int permissionSetId ) {
        rolesMappedToPermissionSetIds.put(role,new Integer(permissionSetId)) ;
    }

    public Map getRolesMappedToPermissionSetIds() {
        return rolesMappedToPermissionSetIds;
    }

    public CategoryDomainObject[] getCategories() {
        return (CategoryDomainObject[]) categories.toArray(new CategoryDomainObject[categories.size()]);
    }

    public void addCategory( CategoryDomainObject category ) {
        categories.add(category) ;
    }

    public void removeCategory( CategoryDomainObject category ) {
        categories.remove(category) ;
    }

    public void removeAllCategories() {
        categories.clear() ;
    }

    public CategoryDomainObject[] getCategoriesOfType(CategoryTypeDomainObject type) {
        CategoryDomainObject[] categories = getCategories() ;
        List categoriesOfType = new ArrayList() ;
        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject category = categories[i];
            if (type.equals(category.getType())) {
               categoriesOfType.add(category);
            }
        }
        final CategoryDomainObject[] arrayOfCategoriesOfType = new CategoryDomainObject[categoriesOfType.size()];
        return (CategoryDomainObject[]) categoriesOfType.toArray(arrayOfCategoriesOfType) ;
    }

    public void setPublisher( UserDomainObject user ) {
        publisher = user;
    }

    public void addSection( SectionDomainObject section ) {
        sections.add( section );
    }

    public void removeSection( SectionDomainObject section ) {
        sections.remove( section );
    }

    public void removeAllSection( SectionDomainObject section ) {
        sections.clear();
    }
}

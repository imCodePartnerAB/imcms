package imcode.server.document;

import imcode.server.IMCConstants;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.*;

import imcode.server.*;

/** Stores info about a document. **/
public class DocumentDomainObject implements IMCConstants {

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

    private Date activatedDatetime;
    private Date archivedDatetime;
    private boolean archivedFlag;
    private Set categories = new HashSet() ;
    private Date createdDatetime;
    private UserDomainObject creator ;
    private int documentType;
    /* Filedocs only */
    private String filename;
    private String headline;
    private String image;
    private Set keywords = new HashSet() ;
    private String languageIso639_2;
    /* Textdocs only */
    private int menuSortOrder;
    private int metaId;
    /* Filedocs only */
    private String mime;
    private Date modifiedDatetime;
    private UserDomainObject publisher;
    private Map rolesMappedToPermissionSetIds = new HashMap();
    private boolean searchDisabled;
    private Set sections = new HashSet() ;
    private String target;
    /* Textdocs only */
    private TemplateDomainObject template;
    /* Textdocs only */
    private int templateGroupId;
    private String text;
    /* url documents only*/
    private String urlRef;

    // If an field is added, make sure to update DocumentMapper

    protected DocumentDomainObject() {

    }

    public Date getActivatedDatetime() {
        return activatedDatetime;
    }

    public void setActivatedDatetime( Date v ) {
        this.activatedDatetime = v;
    }

    public Date getArchivedDatetime() {
        return archivedDatetime;
    }

    public void setArchivedDatetime( Date v ) {
        this.archivedDatetime = v;
    }

    public CategoryDomainObject[] getCategories() {
        return (CategoryDomainObject[]) categories.toArray(new CategoryDomainObject[categories.size()]);
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime( Date v ) {
        this.createdDatetime = v;
    }

    public UserDomainObject getCreator() {
        return creator;
    }

    public void setCreator( UserDomainObject creator ) {
        this.creator = creator;
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType( int v ) {
        this.documentType = v;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String v) {
        this.filename = v;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline( String v ) {
        this.headline = v;
    }

    public String getImage() {
        return image;
    }

    public void setImage( String v ) {
        this.image = v;
    }

    public String[] getKeywords() {
        return (String[]) keywords.toArray(new String[keywords.size()]) ;
    }

    public void setKeywords( String[] keywords ) {
        this.keywords = new HashSet(Arrays.asList(keywords));
    }

    public String getLanguageIso639_2() {
        return languageIso639_2;
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        this.languageIso639_2 = languageIso639_2;
    }

    public int getMenuSortOrder() {
        return menuSortOrder;
    }

    public void setMenuSortOrder( int v ) {
        this.menuSortOrder = v;
    }

    public int getMetaId() {
        return metaId;
    }

    public void setMetaId( int v ) {
        this.metaId = v;
    }

    public Date getModifiedDatetime() {
        return modifiedDatetime;
    }

    public void setModifiedDatetime( Date v ) {
        this.modifiedDatetime = v;
    }

    public UserDomainObject getPublisher() {
        return publisher;
    }

    public void setPublisher( UserDomainObject user ) {
        publisher = user;
    }

    public Map getRolesMappedToPermissionSetIds() {
        return rolesMappedToPermissionSetIds;
    }

    public SectionDomainObject[] getSections() {
        return (SectionDomainObject[]) sections.toArray(new SectionDomainObject[sections.size()]) ;
    }

    public void setSections(SectionDomainObject[] sections) {
        this.sections = new HashSet(Arrays.asList(sections));
    }

    public String getTarget() {
        return target;
    }

    public void setTarget( String v ) {
        this.target = v;
    }

    public TemplateDomainObject getTemplate() {
        return template;
    }

    public void setTemplate( TemplateDomainObject v ) {
        this.template = v;
    }

    public int getTemplateGroupId() {
        return templateGroupId;
    }

    public void setTemplateGroupId( int v ) {
        this.templateGroupId = v;
    }

    public String getText() {
        return text;
    }

    public void setText( String v ) {
        this.text = v;
    }

    /**
     * Check whether this document is archived.
     * A document is archived if its archived-flag is set or if archived-datetime is in the past.
     **/
    public boolean isArchived() {
        return isArchivedFlag() || isArchivedAtTime( new Date() ) ;
    }

    public boolean isArchivedFlag() {
        return archivedFlag;
    }

    public void setArchivedFlag( boolean v ) {
        this.archivedFlag = v;
    }

    public boolean isSearchDisabled() {
        return searchDisabled;
    }

    public void setSearchDisabled( boolean searchDisabled ) {
        this.searchDisabled = searchDisabled;
    }

    public void addCategory( CategoryDomainObject category ) {
        categories.add(category) ;
    }

    public void addSection( SectionDomainObject section ) {
        sections.add( section );
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

    public int hashCode() {
        return metaId;
    }

    /**
     * Check whether this document is active.
     * A document is active if it isn't archived, and if activatedDatetime is in the past.
     **/
    public boolean isActive() {
        Date now = new Date();
        return (activatedDatetime == null || activatedDatetime.before( now )) && !isArchived();
    }

    public boolean isArchivedAtTime( Date time ) {
        return (archivedDatetime != null && archivedDatetime.before( time ));
    }

    public void removeAllCategories() {
        categories.clear() ;
    }

    public void removeCategory( CategoryDomainObject category ) {
        categories.remove(category) ;
    }

    public void setPermissionSetForRole( RoleDomainObject role, int permissionSetId ) {
        rolesMappedToPermissionSetIds.put(role,new Integer(permissionSetId)) ;
    }

    public String getUrlRef() {
        return urlRef;
    }

    public void setUrlRef(String urlRef) {
        this.urlRef = urlRef;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }
}

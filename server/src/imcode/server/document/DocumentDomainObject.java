package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Stores info about a document. *
 */
public abstract class DocumentDomainObject implements Cloneable, Serializable {

    public final static int DOCTYPE_TEXT = 2;
    public final static int DOCTYPE_URL = 5;
    public final static int DOCTYPE_BROWSER = 6;
    public final static int DOCTYPE_HTML = 7;
    public final static int DOCTYPE_FILE = 8;
    public final static int DOCTYPE_DIAGRAM = 101;
    public final static int DOCTYPE_CONFERENCE = 102;
    public final static int DOCTYPE_CHAT = 103;
    public final static int DOCTYPE_BILLBOARD = 104;
    public static final int DOCTYPE_FORTUNES = 106;

    public static final int STATUS_NEW = 0;
    public static final int STATUS_PUBLICATION_DISAPPROVED = 1;
    public static final int STATUS_PUBLICATION_APPROVED = 2;

    protected DocumentProperties documentProperties = new DocumentProperties();

    protected DocumentDomainObject() {

    }

    public Date getArchivedDatetime() {
        return documentProperties.archivedDatetime;
    }

    public void setArchivedDatetime( Date v ) {
        documentProperties.archivedDatetime = v;
    }

    public CategoryDomainObject[] getCategories() {
        return (CategoryDomainObject[])documentProperties.categories.toArray( new CategoryDomainObject[documentProperties.categories.size()] );
    }

    public Date getCreatedDatetime() {
        return documentProperties.createdDatetime;
    }

    public void setCreatedDatetime( Date v ) {
        documentProperties.createdDatetime = v;
    }

    public UserDomainObject getCreator() {
        return documentProperties.creator;
    }

    public void setCreator( UserDomainObject creator ) {
        documentProperties.creator = creator;
    }

    public void setDocumentProperties( DocumentProperties documentProperties ) {
        this.documentProperties = documentProperties;
    }

    public String getHeadline() {
        return documentProperties.headline;
    }

    public void setHeadline( String v ) {
        documentProperties.headline = v;
    }

    public int getId() {
        return documentProperties.id;
    }

    public void setId( int v ) {
        documentProperties.id = v;
    }

    public String getMenuImage() {
        return documentProperties.image;
    }

    public void setMenuImage( String v ) {
        documentProperties.image = v;
    }

    public String[] getKeywords() {
        return (String[])documentProperties.keywords.toArray( new String[documentProperties.keywords.size()] );
    }

    public void setKeywords( String[] keywords ) {
        documentProperties.keywords = new HashSet( Arrays.asList( keywords ) );
    }

    public String getLanguageIso639_2() {
        return documentProperties.languageIso639_2;
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        documentProperties.languageIso639_2 = languageIso639_2;
    }

    public String getMenuText() {
        return documentProperties.menuText;
    }

    public void setMenuText( String v ) {
        documentProperties.menuText = v;
    }

    public Date getModifiedDatetime() {
        return documentProperties.modifiedDatetime;
    }

    public void setModifiedDatetime( Date v ) {
        documentProperties.modifiedDatetime = v;
    }

    public Date getPublicationEndDatetime() {
        return documentProperties.publicationEndDatetime;
    }

    public void setPublicationEndDatetime( Date datetime ) {
        documentProperties.publicationEndDatetime = datetime;
    }

    public Date getPublicationStartDatetime() {
        return documentProperties.publicationStartDatetime;
    }

    public void setPublicationStartDatetime( Date v ) {
        documentProperties.publicationStartDatetime = v;
    }

    public UserDomainObject getPublisher() {
        return documentProperties.publisher;
    }

    public void setPublisher( UserDomainObject user ) {
        documentProperties.publisher = user;
    }

    public Map getRolesMappedToPermissionSetIds() {
        return Collections.unmodifiableMap( documentProperties.rolesMappedToPermissionSetIds );
    }

    public SectionDomainObject[] getSections() {
        return (SectionDomainObject[])documentProperties.sections.toArray( new SectionDomainObject[documentProperties.sections.size()] );
    }

    public void setSections( SectionDomainObject[] sections ) {
        documentProperties.sections = new HashSet( Arrays.asList( sections ) );
    }

    public int getStatus() {
        return documentProperties.status;
    }

    public void setStatus( int status ) {
        switch ( status ) {
            default:
                throw new IllegalArgumentException( "Bad status." );
            case STATUS_NEW:
            case STATUS_PUBLICATION_APPROVED:
            case STATUS_PUBLICATION_DISAPPROVED:
                documentProperties.status = status;
        }
    }

    public String getTarget() {
        return documentProperties.target;
    }

    public void setTarget( String v ) {
        documentProperties.target = v;
    }

    public boolean isArchived() {
        return isArchivedAtTime( new Date() );
    }

    public boolean isLinkableByOtherUsers() {
        return documentProperties.linkableByOtherUsers;
    }

    public void setLinkableByOtherUsers( boolean linkableByOtherUsers ) {
        documentProperties.linkableByOtherUsers = linkableByOtherUsers;
    }

    public boolean isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo() {
        return documentProperties.permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
    }

    public void setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo(
            boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo ) {
        documentProperties.permissionSetOneIsMorePrivilegedThanPermissionSetTwo =
        permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
    }

    public boolean isPublished() {
        return isPublishedAtTime( new Date() );
    }

    public boolean isPublishedAndNotArchived() {
        return isPublished() && !isArchived();
    }

    public boolean isNoLongerPublished() {
        return isNoLongerPublishedAtTime( new Date() );
    }

    private boolean isNoLongerPublishedAtTime( Date date ) {
        Date publicationEndDatetime = documentProperties.publicationEndDatetime;
        return publicationEndDatetime != null && publicationEndDatetime.before( date );
    }

    public boolean isSearchDisabled() {
        return documentProperties.searchDisabled;
    }

    public void setSearchDisabled( boolean searchDisabled ) {
        documentProperties.searchDisabled = searchDisabled;
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return documentProperties.visibleInMenusForUnauthorizedUsers;
    }

    public void setVisibleInMenusForUnauthorizedUsers( boolean visibleInMenusForUnauthorizedUsers ) {
        documentProperties.visibleInMenusForUnauthorizedUsers = visibleInMenusForUnauthorizedUsers;
    }

    public void addCategory( CategoryDomainObject category ) {
        documentProperties.categories.add( category );
    }

    public void addSection( SectionDomainObject section ) {
        documentProperties.sections.add( section );
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals( Object o ) {
        return documentProperties.equals( o );
    }

    public static DocumentDomainObject fromDocumentTypeId( int documentTypeId ) {
        DocumentDomainObject document = null;

        switch ( documentTypeId ) {
            case DOCTYPE_TEXT:
                document = new TextDocumentDomainObject();
                break;
            case DOCTYPE_URL:
                document = new UrlDocumentDomainObject();
                break;
            case DOCTYPE_BROWSER:
                document = new BrowserDocumentDomainObject();
                break;
            case DOCTYPE_FILE:
                document = new FileDocumentDomainObject();
                break;
            case DOCTYPE_HTML:
                document = new HtmlDocumentDomainObject();
                break;
            case DOCTYPE_CHAT:
                document = new ChatDocumentDomainObject();
                break;
            case DOCTYPE_CONFERENCE:
                document = new ConferenceDocumentDomainObject();
                break;
            case DOCTYPE_BILLBOARD:
                document = new BillboardDocumentDomainObject();
                break;
        }

        return document;
    }

    public CategoryDomainObject[] getCategoriesOfType( CategoryTypeDomainObject type ) {
        CategoryDomainObject[] categories = (CategoryDomainObject[])documentProperties.categories.toArray( new CategoryDomainObject[documentProperties.categories.size()] );
        List categoriesOfType = new ArrayList();
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            if ( type.equals( category.getType() ) ) {
                categoriesOfType.add( category );
            }
        }
        final CategoryDomainObject[] arrayOfCategoriesOfType = new CategoryDomainObject[categoriesOfType.size()];
        return (CategoryDomainObject[])categoriesOfType.toArray( arrayOfCategoriesOfType );
    }

    public abstract int getDocumentTypeId();

    public int hashCode() {
        return documentProperties.hashCode();
    }

    public abstract void initDocument( DocumentMapper documentMapper );

    public boolean isArchivedAtTime( Date time ) {
        DocumentProperties documentProperties = this.documentProperties;
        return ( documentProperties.archivedDatetime != null && documentProperties.archivedDatetime.before( time ) );
    }

    public abstract void processNewDocumentInformation( DocumentComposer documentInformation,
                                                        DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                                        UserDomainObject user, HttpServletRequest request,
                                                        HttpServletResponse response ) throws IOException, ServletException;

    public void removeAllCategories() {
        documentProperties.categories.clear();
    }

    public void removeAllSections() {
        documentProperties.sections.clear();
    }

    public void removeCategory( CategoryDomainObject category ) {
        documentProperties.categories.remove( category );
    }

    public abstract void saveDocument( DocumentMapper documentMapper, UserDomainObject user );

    public abstract void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user );

    public void setPermissionSetIdForRole( RoleDomainObject role, int permissionSetId ) {
        documentProperties.rolesMappedToPermissionSetIds.put( role, new Integer( permissionSetId ) );
    }

    private boolean isPublishedAtTime( Date date ) {
        DocumentProperties documentProperties = this.documentProperties;
        boolean publicationStartDatetimeIsNotNullAndInThePast = documentProperties.publicationStartDatetime != null
                                                                && documentProperties.publicationStartDatetime.before( date );
        boolean publicationEndDatetimeIsNullOrInTheFuture = documentProperties.publicationEndDatetime == null
                                                            || documentProperties.publicationEndDatetime.after( date );
        boolean statusIsApproved = documentProperties.status == STATUS_PUBLICATION_APPROVED;
        boolean isPublished = statusIsApproved && publicationStartDatetimeIsNotNullAndInThePast
                              && publicationEndDatetimeIsNullOrInTheFuture;
        return isPublished;
    }

    public void setPermissionSetForRestrictedOne( DocumentPermissionSetDomainObject permissionSetForRestrictedOne ) {
        this.documentProperties.permissionSetForRestrictedOne = permissionSetForRestrictedOne;
    }

    public void setPermissionSetForRestrictedTwo( DocumentPermissionSetDomainObject permissionSetForRestrictedTwo ) {
        this.documentProperties.permissionSetForRestrictedTwo = permissionSetForRestrictedTwo;
    }

    public void setPermissionSetForRestrictedOneForNewDocuments(
            DocumentPermissionSetDomainObject permissionSetForRestrictedOneForNewDocuments ) {
        this.documentProperties.permissionSetForRestrictedOneForNewDocuments = permissionSetForRestrictedOneForNewDocuments;
    }

    public void setPermissionSetForRestrictedTwoForNewDocuments(
            DocumentPermissionSetDomainObject permissionSetForRestrictedTwoForNewDocuments ) {
        this.documentProperties.permissionSetForRestrictedTwoForNewDocuments = permissionSetForRestrictedTwoForNewDocuments;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedOne() {
        return this.documentProperties.permissionSetForRestrictedOne;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedOneForNewDocuments() {
        return this.documentProperties.permissionSetForRestrictedOneForNewDocuments;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedTwo() {
        return this.documentProperties.permissionSetForRestrictedTwo;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedTwoForNewDocuments() {
        return this.documentProperties.permissionSetForRestrictedTwoForNewDocuments;
    }

    public DocumentProperties getDocumentProperties() {
        return documentProperties;
    }

    public static class DocumentProperties implements Cloneable, Serializable {

        private Date archivedDatetime;
        private Set categories = new HashSet();
        private Date createdDatetime;
        private UserDomainObject creator;
        private String headline;
        private String image;
        private Set keywords = new HashSet();
        private String languageIso639_2;
        private boolean linkableByOtherUsers;
        private String menuText;
        private int id;
        private Date modifiedDatetime;
        private boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
        private Date publicationStartDatetime;
        private Date publicationEndDatetime;
        private UserDomainObject publisher;
        private Map rolesMappedToPermissionSetIds = new HashMap();
        private boolean searchDisabled;
        private Set sections = new HashSet();
        private int status;
        private String target;
        private boolean visibleInMenusForUnauthorizedUsers;

        private DocumentPermissionSetDomainObject permissionSetForRestrictedOne;
        private DocumentPermissionSetDomainObject permissionSetForRestrictedTwo;
        private DocumentPermissionSetDomainObject permissionSetForRestrictedOneForNewDocuments;
        private DocumentPermissionSetDomainObject permissionSetForRestrictedTwoForNewDocuments;

        public Object clone() throws CloneNotSupportedException {
            DocumentProperties clone = (DocumentProperties)super.clone();
            clone.categories = new HashSet( categories );
            clone.keywords = new HashSet( keywords );
            clone.rolesMappedToPermissionSetIds = new HashMap( rolesMappedToPermissionSetIds );
            clone.sections = new HashSet( sections );

            return clone;
        }

        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof DocumentProperties ) ) {
                return false;
            }

            final DocumentProperties documentInformation = (DocumentProperties)o;

            if ( id != documentInformation.id ) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            return id;
        }

    }

}

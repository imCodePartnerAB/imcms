package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.ApplicationServer;
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

    protected Attributes attributes;

    protected DocumentDomainObject() {
        attributes = new Attributes();
    }

    public Object clone() throws CloneNotSupportedException {
        DocumentDomainObject clone = (DocumentDomainObject)super.clone();
        if ( null != attributes ) {
            clone.attributes = (Attributes)attributes.clone();
        }
        return clone;
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
            default:
                throw new RuntimeException( "Unknown document-type-id: " + documentTypeId );
        }

        return document;
    }

    public Date getArchivedDatetime() {
        return attributes.archivedDatetime;
    }

    public void setArchivedDatetime( Date v ) {
        attributes.archivedDatetime = v;
    }

    public CategoryDomainObject[] getCategories() {
        return (CategoryDomainObject[])getLazilyLoaded().categories.toArray( new CategoryDomainObject[getLazilyLoaded().categories.size()] );
    }

    public Date getCreatedDatetime() {
        return attributes.createdDatetime;
    }

    public void setCreatedDatetime( Date v ) {
        attributes.createdDatetime = v;
    }

    public UserDomainObject getCreator() {
        return attributes.creator;
    }

    public void setCreator( UserDomainObject creator ) {
        attributes.creator = creator;
    }

    public void setAttributes( Attributes attributes ) {
        this.attributes = attributes;
    }

    public String getHeadline() {
        return attributes.headline;
    }

    public void setHeadline( String v ) {
        attributes.headline = v;
    }

    public int getId() {
        return attributes.id;
    }

    public void setId( int v ) {
        if ( 0 != attributes.id ) {
            getLazilyLoaded();
        }
        attributes.id = v;
    }

    public String getMenuImage() {
        return attributes.image;
    }

    public void setMenuImage( String v ) {
        attributes.image = v;
    }

    public String[] getKeywords() {
        return (String[])getLazilyLoaded().keywords.toArray( new String[getLazilyLoaded().keywords.size()] );
    }

    public void setKeywords( String[] keywords ) {
        getLazilyLoaded().keywords = new HashSet( Arrays.asList( keywords ) );
    }

    public String getLanguageIso639_2() {
        return attributes.languageIso639_2;
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        attributes.languageIso639_2 = languageIso639_2;
    }

    public String getMenuText() {
        return attributes.menuText;
    }

    public void setMenuText( String v ) {
        attributes.menuText = v;
    }

    public Date getModifiedDatetime() {
        return attributes.modifiedDatetime;
    }

    public void setModifiedDatetime( Date v ) {
        attributes.modifiedDatetime = v;
    }

    public Date getPublicationEndDatetime() {
        return attributes.publicationEndDatetime;
    }

    public void setPublicationEndDatetime( Date datetime ) {
        attributes.publicationEndDatetime = datetime;
    }

    public Date getPublicationStartDatetime() {
        return attributes.publicationStartDatetime;
    }

    public void setPublicationStartDatetime( Date v ) {
        attributes.publicationStartDatetime = v;
    }

    public UserDomainObject getPublisher() {
        return attributes.publisher;
    }

    public void setPublisher( UserDomainObject user ) {
        attributes.publisher = user;
    }

    public Map getRolesMappedToPermissionSetIds() {
        return Collections.unmodifiableMap( getLazilyLoaded().rolesMappedToPermissionSetIds );
    }

    public SectionDomainObject[] getSections() {
        return (SectionDomainObject[])getLazilyLoaded().sections.toArray( new SectionDomainObject[getLazilyLoaded().sections.size()] );
    }

    public void setSections( SectionDomainObject[] sections ) {
        getLazilyLoaded().sections = new HashSet( Arrays.asList( sections ) );
    }

    public int getStatus() {
        return attributes.status;
    }

    public void setStatus( int status ) {
        switch ( status ) {
            case STATUS_NEW:
            case STATUS_PUBLICATION_APPROVED:
            case STATUS_PUBLICATION_DISAPPROVED:
                attributes.status = status;
                break;
            default:
                throw new IllegalArgumentException( "Bad status." );
        }
    }

    public String getTarget() {
        return attributes.target;
    }

    public void setTarget( String v ) {
        attributes.target = v;
    }

    public boolean isArchived() {
        return isArchivedAtTime( new Date() );
    }

    public boolean isLinkableByOtherUsers() {
        return attributes.linkableByOtherUsers;
    }

    public void setLinkableByOtherUsers( boolean linkableByOtherUsers ) {
        attributes.linkableByOtherUsers = linkableByOtherUsers;
    }

    public boolean isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo() {
        return attributes.permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
    }

    public void setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo(
            boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo ) {
        attributes.permissionSetOneIsMorePrivilegedThanPermissionSetTwo =
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
        Date publicationEndDatetime = attributes.publicationEndDatetime;
        return publicationEndDatetime != null && publicationEndDatetime.before( date );
    }

    public boolean isSearchDisabled() {
        return attributes.searchDisabled;
    }

    public void setSearchDisabled( boolean searchDisabled ) {
        attributes.searchDisabled = searchDisabled;
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return attributes.visibleInMenusForUnauthorizedUsers;
    }

    public void setVisibleInMenusForUnauthorizedUsers( boolean visibleInMenusForUnauthorizedUsers ) {
        attributes.visibleInMenusForUnauthorizedUsers = visibleInMenusForUnauthorizedUsers;
    }

    public void addCategory( CategoryDomainObject category ) {
        getLazilyLoaded().categories.add( category );
    }

    public void addSection( SectionDomainObject section ) {
        getLazilyLoaded().sections.add( section );
    }

    public boolean equals( Object o ) {
        return attributes.equals( o );
    }

    public CategoryDomainObject[] getCategoriesOfType( CategoryTypeDomainObject type ) {
        CategoryDomainObject[] categories = (CategoryDomainObject[])getLazilyLoaded().categories.toArray( new CategoryDomainObject[getLazilyLoaded().categories.size()] );
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
        return attributes.hashCode();
    }

    public boolean isArchivedAtTime( Date time ) {
        Attributes documentProperties = this.attributes;
        return ( documentProperties.archivedDatetime != null && documentProperties.archivedDatetime.before( time ) );
    }

    public abstract void processNewDocumentInformation( DocumentComposer documentInformation,
                                                        DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                                        UserDomainObject user, HttpServletRequest request,
                                                        HttpServletResponse response ) throws IOException, ServletException;

    public void removeAllCategories() {
        getLazilyLoaded().categories.clear();
    }

    public void removeAllSections() {
        getLazilyLoaded().sections.clear();
    }

    public void removeCategory( CategoryDomainObject category ) {
        getLazilyLoaded().categories.remove( category );
    }

    public abstract void saveDocument( DocumentMapper documentMapper, UserDomainObject user );

    public abstract void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user );

    public void setPermissionSetIdForRole( RoleDomainObject role, int permissionSetId ) {
        getLazilyLoaded().rolesMappedToPermissionSetIds.put( role, new Integer( permissionSetId ) );
    }

    private boolean isPublishedAtTime( Date date ) {
        Attributes documentProperties = this.attributes;
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
        this.getLazilyLoaded().permissionSetForRestrictedOne = permissionSetForRestrictedOne;
    }

    public void setPermissionSetForRestrictedTwo( DocumentPermissionSetDomainObject permissionSetForRestrictedTwo ) {
        this.getLazilyLoaded().permissionSetForRestrictedTwo = permissionSetForRestrictedTwo;
    }

    public void setPermissionSetForRestrictedOneForNewDocuments(
            DocumentPermissionSetDomainObject permissionSetForRestrictedOneForNewDocuments ) {
        this.getLazilyLoaded().permissionSetForRestrictedOneForNewDocuments = permissionSetForRestrictedOneForNewDocuments;
    }

    public void setPermissionSetForRestrictedTwoForNewDocuments(
            DocumentPermissionSetDomainObject permissionSetForRestrictedTwoForNewDocuments ) {
        this.getLazilyLoaded().permissionSetForRestrictedTwoForNewDocuments = permissionSetForRestrictedTwoForNewDocuments;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedOne() {
        return this.getLazilyLoaded().permissionSetForRestrictedOne;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedOneForNewDocuments() {
        return this.getLazilyLoaded().permissionSetForRestrictedOneForNewDocuments;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedTwo() {
        return this.getLazilyLoaded().permissionSetForRestrictedTwo;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedTwoForNewDocuments() {
        return this.getLazilyLoaded().permissionSetForRestrictedTwoForNewDocuments;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public synchronized Attributes.LazilyLoadedAttributes getLazilyLoaded() {
        if ( null == attributes.lazilyLoadedAttributes ) {
            attributes.lazilyLoadedAttributes = new Attributes.LazilyLoadedAttributes();
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            documentMapper.initLazilyLoadedDocumentAttributes( this );
        }
        return attributes.lazilyLoadedAttributes;
    }

    public abstract void initDocument( DocumentMapper documentMapper );

    public static class Attributes implements Cloneable, Serializable {

        private Date archivedDatetime;
        private Date createdDatetime;
        private UserDomainObject creator;
        private String headline;
        private String image;
        private String languageIso639_2;
        private boolean linkableByOtherUsers;
        private String menuText;
        private int id;
        private Date modifiedDatetime;
        private boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
        private Date publicationStartDatetime;
        private Date publicationEndDatetime;
        private UserDomainObject publisher;
        private boolean searchDisabled;
        private int status;
        private String target;
        private boolean visibleInMenusForUnauthorizedUsers;

        private LazilyLoadedAttributes lazilyLoadedAttributes = null;

        public Object clone() throws CloneNotSupportedException {
            Attributes clone = (Attributes)super.clone();
            if ( null != lazilyLoadedAttributes ) {
                clone.lazilyLoadedAttributes = (LazilyLoadedAttributes)lazilyLoadedAttributes.clone();
            }
            return clone;
        }

        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof Attributes ) ) {
                return false;
            }

            final Attributes documentInformation = (Attributes)o;

            if ( id != documentInformation.id ) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            return id;
        }

        static class LazilyLoadedAttributes implements Cloneable, Serializable {

            private Set categories = new HashSet();
            private Set keywords = new HashSet();
            private Map rolesMappedToPermissionSetIds = new HashMap();
            private Set sections = new HashSet();
            private DocumentPermissionSetDomainObject permissionSetForRestrictedOne;
            private DocumentPermissionSetDomainObject permissionSetForRestrictedTwo;
            private DocumentPermissionSetDomainObject permissionSetForRestrictedOneForNewDocuments;
            private DocumentPermissionSetDomainObject permissionSetForRestrictedTwoForNewDocuments;

            public Object clone() throws CloneNotSupportedException {
                LazilyLoadedAttributes clone = (LazilyLoadedAttributes)super.clone();
                clone.categories = new HashSet( categories );
                clone.keywords = new HashSet( keywords );
                clone.rolesMappedToPermissionSetIds = new HashMap( rolesMappedToPermissionSetIds );
                clone.sections = new HashSet( sections );
                return clone;
            }

        }

    }
}

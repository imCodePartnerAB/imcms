package imcode.server.document;

import com.imcode.imcms.api.util.ChainableReversibleNullComparator;
import imcode.server.ApplicationServer;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;

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

    private Attributes attributes;
    private static Logger log = Logger.getLogger( DocumentDomainObject.class );

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
                String errorMessage = "Unknown document-type-id: " + documentTypeId;
                log.error(errorMessage);
                throw new RuntimeException( errorMessage );
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
        return (CategoryDomainObject[])getLazilyLoadedDocumentCategories().categories.toArray( new CategoryDomainObject[getLazilyLoadedDocumentCategories().categories.size()] );
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
            getLazilyLoadedDocumentAttributes();
            getLazilyLoadedDocumentCategories();
            getLazilyLoadedRolesMappedToDocumentPermissionSetIds();
            loadAllLazilyLoadedDocumentTypeSpecificAttributes();
        }
        attributes.id = v;
    }

    protected abstract void loadAllLazilyLoadedDocumentTypeSpecificAttributes();

    public String getMenuImage() {
        return attributes.image;
    }

    public void setMenuImage( String v ) {
        attributes.image = v;
    }

    public String[] getKeywords() {
        return (String[])getLazilyLoadedDocumentAttributes().keywords.toArray( new String[getLazilyLoadedDocumentAttributes().keywords.size()] );
    }

    public void setKeywords( String[] keywords ) {
        getLazilyLoadedDocumentAttributes().keywords = new HashSet( Arrays.asList( keywords ) );
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
        return Collections.unmodifiableMap( getLazilyLoadedRolesMappedToDocumentPermissionSetIds().rolesMappedToDocumentPermissionSetIds );
    }

    public SectionDomainObject[] getSections() {
        return (SectionDomainObject[])getLazilyLoadedDocumentAttributes().sections.toArray( new SectionDomainObject[getLazilyLoadedDocumentAttributes().sections.size()] );
    }

    public void setSections( SectionDomainObject[] sections ) {
        getLazilyLoadedDocumentAttributes().sections = new HashSet( Arrays.asList( sections ) );
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
        getLazilyLoadedDocumentCategories().categories.add( category );
    }

    public void addSection( SectionDomainObject section ) {
        getLazilyLoadedDocumentAttributes().sections.add( section );
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DocumentDomainObject ) ) {
            return false;
        }

        final DocumentDomainObject document = (DocumentDomainObject)o;

        if ( attributes.id != document.attributes.id ) {
            return false;
        }

        return true;
    }

    public CategoryDomainObject[] getCategoriesOfType( CategoryTypeDomainObject type ) {
        CategoryDomainObject[] categories = (CategoryDomainObject[])getLazilyLoadedDocumentCategories().categories.toArray( new CategoryDomainObject[getLazilyLoadedDocumentCategories().categories.size()] );
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
        return attributes.id ;
    }

    private boolean isArchivedAtTime( Date time ) {
        Attributes documentProperties = this.attributes;
        return ( documentProperties.archivedDatetime != null && documentProperties.archivedDatetime.before( time ) );
    }

    public void removeAllCategories() {
        getLazilyLoadedDocumentCategories().categories.clear();
    }

    public void removeAllSections() {
        getLazilyLoadedDocumentAttributes().sections.clear();
    }

    public void removeCategory( CategoryDomainObject category ) {
        getLazilyLoadedDocumentCategories().categories.remove( category );
    }

    public abstract void saveDocument( DocumentMapper documentMapper, UserDomainObject user );

    public abstract void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user );

    public void setPermissionSetIdForRole( RoleDomainObject role, int permissionSetId ) {
        getLazilyLoadedRolesMappedToDocumentPermissionSetIds().rolesMappedToDocumentPermissionSetIds.put( role, new Integer( permissionSetId ) );
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
        this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedOne = permissionSetForRestrictedOne;
    }

    public void setPermissionSetForRestrictedTwo( DocumentPermissionSetDomainObject permissionSetForRestrictedTwo ) {
        this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedTwo = permissionSetForRestrictedTwo;
    }

    public void setPermissionSetForRestrictedOneForNewDocuments(
            DocumentPermissionSetDomainObject permissionSetForRestrictedOneForNewDocuments ) {
        this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedOneForNewDocuments = permissionSetForRestrictedOneForNewDocuments;
    }

    public void setPermissionSetForRestrictedTwoForNewDocuments(
            DocumentPermissionSetDomainObject permissionSetForRestrictedTwoForNewDocuments ) {
        this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedTwoForNewDocuments = permissionSetForRestrictedTwoForNewDocuments;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedOne() {
        return this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedOne;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedOneForNewDocuments() {
        return this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedOneForNewDocuments;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedTwo() {
        return this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedTwo;
    }

    public DocumentPermissionSetDomainObject getPermissionSetForRestrictedTwoForNewDocuments() {
        return this.getLazilyLoadedDocumentAttributes().permissionSetForRestrictedTwoForNewDocuments;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    private synchronized Attributes.LazilyLoadedDocumentAttributes getLazilyLoadedDocumentAttributes() {
        if ( null == attributes.lazilyLoadedDocumentAttributes ) {
            attributes.lazilyLoadedDocumentAttributes = new Attributes.LazilyLoadedDocumentAttributes();
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            documentMapper.initLazilyLoadedDocumentAttributes( this );
        }
        return attributes.lazilyLoadedDocumentAttributes;
    }

    private synchronized Attributes.LazilyLoadedDocumentCategories getLazilyLoadedDocumentCategories() {
        if ( null == attributes.lazilyLoadedDocumentCategories ) {
            attributes.lazilyLoadedDocumentCategories = new Attributes.LazilyLoadedDocumentCategories();
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            documentMapper.initLazilyLoadedDocumentCategories( this );
        }
        return attributes.lazilyLoadedDocumentCategories;
    }

    private synchronized Attributes.LazilyLoadedRolesMappedToDocumentPermissionSetIds getLazilyLoadedRolesMappedToDocumentPermissionSetIds() {
        if ( null == attributes.lazilyLoadedRolesMappedToDocumentPermissionSetIds ) {
            attributes.lazilyLoadedRolesMappedToDocumentPermissionSetIds = new Attributes.LazilyLoadedRolesMappedToDocumentPermissionSetIds();
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            documentMapper.initLazilyLoadedRolesMappedToDocumentPermissionSetIds( this );
        }
        return attributes.lazilyLoadedRolesMappedToDocumentPermissionSetIds;
    }

    public abstract void initDocument( DocumentMapper documentMapper );

    public abstract void accept( DocumentVisitor documentVisitor ) ;

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

        private LazilyLoadedDocumentAttributes lazilyLoadedDocumentAttributes = null;
        private LazilyLoadedDocumentCategories lazilyLoadedDocumentCategories = null;
        private LazilyLoadedRolesMappedToDocumentPermissionSetIds lazilyLoadedRolesMappedToDocumentPermissionSetIds = null;

        public Object clone() throws CloneNotSupportedException {
            Attributes clone = (Attributes)super.clone();
            if ( null != lazilyLoadedDocumentAttributes ) {
                clone.lazilyLoadedDocumentAttributes = (LazilyLoadedDocumentAttributes)lazilyLoadedDocumentAttributes.clone();
            }
            if ( null != lazilyLoadedDocumentCategories ) {
                clone.lazilyLoadedDocumentCategories = (LazilyLoadedDocumentCategories)lazilyLoadedDocumentCategories.clone();
            }
            return clone;
        }

        private static class LazilyLoadedDocumentAttributes implements Cloneable, Serializable {

            private Set keywords = new HashSet();
            private Set sections = new HashSet();
            private DocumentPermissionSetDomainObject permissionSetForRestrictedOne;
            private DocumentPermissionSetDomainObject permissionSetForRestrictedTwo;
            private DocumentPermissionSetDomainObject permissionSetForRestrictedOneForNewDocuments;
            private DocumentPermissionSetDomainObject permissionSetForRestrictedTwoForNewDocuments;

            public Object clone() throws CloneNotSupportedException {
                LazilyLoadedDocumentAttributes clone = (LazilyLoadedDocumentAttributes)super.clone();
                clone.keywords = new HashSet( keywords );
                clone.sections = new HashSet( sections );
                return clone;
            }
        }

        private static class LazilyLoadedDocumentCategories implements Cloneable, Serializable {

            private Set categories = new HashSet();

            public Object clone() throws CloneNotSupportedException {
                LazilyLoadedDocumentCategories clone = (LazilyLoadedDocumentCategories)super.clone();
                clone.categories = new HashSet( categories );
                return clone;
            }
        }

        private static class LazilyLoadedRolesMappedToDocumentPermissionSetIds implements Cloneable, Serializable {

            private Map rolesMappedToDocumentPermissionSetIds = new HashMap();

            public Object clone() throws CloneNotSupportedException {
                LazilyLoadedRolesMappedToDocumentPermissionSetIds clone = (LazilyLoadedRolesMappedToDocumentPermissionSetIds)super.clone();
                clone.rolesMappedToDocumentPermissionSetIds = new HashMap( rolesMappedToDocumentPermissionSetIds );
                return clone;
            }
        }
    }

    public abstract static class DocumentComparator extends ChainableReversibleNullComparator {

        public int compare( Object o1, Object o2 ) {
            final DocumentDomainObject d1 = (DocumentDomainObject)o1;
            final DocumentDomainObject d2 = (DocumentDomainObject)o2;
            try {
                return compareDocuments( d1, d2 );
            } catch ( NullPointerException npe ) {
                throw new NullPointerException( "Tried sorting on null fields! You need to call .nullsFirst() or .nullsLast() on your Comparator.") ;
            }
        }

        protected abstract int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) ;

        public final static DocumentDomainObject.DocumentComparator ID = new DocumentDomainObject.DocumentComparator() {
            protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
                return d1.getId() - d2.getId();
            }
        };

        public final static DocumentComparator HEADLINE = new DocumentDomainObject.DocumentComparator() {
            protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
                return d1.getHeadline().compareTo( d2.getHeadline() ) ;
            }

        };

        public final static DocumentComparator MODIFIED_DATETIME = new DocumentDomainObject.DocumentComparator() {
            protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
                return d1.getModifiedDatetime().compareTo( d2.getModifiedDatetime() ) ;
            }
        };

    }
}

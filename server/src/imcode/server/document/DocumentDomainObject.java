package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.IMCConstants;
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
public abstract class DocumentDomainObject implements IMCConstants, Cloneable, Serializable {

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

    protected DocumentProperties documentProperties = new DocumentProperties();
    public static final int STATUS_NEW = 0;
    public static final int STATUS_PUBLICATION_DISAPPROVED = 1;
    public static final int STATUS_PUBLICATION_APPROVED = 2;

    protected DocumentDomainObject() {

    }

    public Date getArchivedDatetime() {
        return getDocumentProperties().getArchivedDatetime();
    }

    public void setArchivedDatetime( Date v ) {
        getDocumentProperties().setArchivedDatetime( v );
    }

    public CategoryDomainObject[] getCategories() {
        return getDocumentProperties().getCategories();
    }

    public Date getCreatedDatetime() {
        return getDocumentProperties().getCreatedDatetime();
    }

    public void setCreatedDatetime( Date v ) {
        getDocumentProperties().setCreatedDatetime( v );
    }

    public UserDomainObject getCreator() {
        return getDocumentProperties().getCreator();
    }

    public void setCreator( UserDomainObject creator ) {
        getDocumentProperties().setCreator( creator );
    }

    public DocumentProperties getDocumentProperties() {
        return documentProperties;
    }

    public void setDocumentProperties( DocumentProperties documentProperties ) {
        this.documentProperties = documentProperties;
    }

    public String getHeadline() {
        return getDocumentProperties().getHeadline();
    }

    public void setHeadline( String v ) {
        getDocumentProperties().setHeadline( v );
    }

    public int getId() {
        return getDocumentProperties().getId();
    }

    public void setId( int v ) {
        getDocumentProperties().setId( v );
    }

    public String getMenuImage() {
        return getDocumentProperties().getMenuImage();
    }

    public void setMenuImage( String v ) {
        getDocumentProperties().setMenuImage( v );
    }

    public String[] getKeywords() {
        return getDocumentProperties().getKeywords();
    }

    public void setKeywords( String[] keywords ) {
        getDocumentProperties().setKeywords( keywords );
    }

    public String getLanguageIso639_2() {
        return getDocumentProperties().getLanguageIso639_2();
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        getDocumentProperties().setLanguageIso639_2( languageIso639_2 );
    }

    public String getMenuText() {
        return getDocumentProperties().getMenuText();
    }

    public void setMenuText( String v ) {
        getDocumentProperties().setMenuText( v );
    }

    public Date getModifiedDatetime() {
        return getDocumentProperties().getModifiedDatetime();
    }

    public void setModifiedDatetime( Date v ) {
        getDocumentProperties().setModifiedDatetime( v );
    }

    public Date getPublicationEndDatetime() {
        return getDocumentProperties().getPublicationEndDatetime();
    }

    public void setPublicationEndDatetime( Date datetime ) {
        getDocumentProperties().setPublicationEndDatetime( datetime );
    }

    public Date getPublicationStartDatetime() {
        return getDocumentProperties().getPublicationStartDatetime();
    }

    public void setPublicationStartDatetime( Date v ) {
        getDocumentProperties().setPublicationStartDatetime( v );
    }

    public UserDomainObject getPublisher() {
        return getDocumentProperties().getPublisher();
    }

    public void setPublisher( UserDomainObject user ) {
        getDocumentProperties().setPublisher( user );
    }

    public Map getRolesMappedToPermissionSetIds() {
        return getDocumentProperties().getRolesMappedToPermissionSetIds();
    }

    public SectionDomainObject[] getSections() {
        return getDocumentProperties().getSections();
    }

    public void setSections( SectionDomainObject[] sections ) {
        getDocumentProperties().setSections( sections );
    }

    public int getStatus() {
        return getDocumentProperties().getStatus();
    }

    public void setStatus( int status ) {
        getDocumentProperties().setStatus( status );
    }

    public String getTarget() {
        return getDocumentProperties().getTarget();
    }

    public void setTarget( String v ) {
        getDocumentProperties().setTarget( v );
    }

    public boolean isArchived() {
        return isArchivedAtTime( new Date() );
    }

    public boolean isLinkableByOtherUsers() {
        return getDocumentProperties().isLinkableByOtherUsers();
    }

    public void setLinkableByOtherUsers( boolean linkableByOtherUsers ) {
        getDocumentProperties().setLinkableByOtherUsers( linkableByOtherUsers );
    }

    public boolean isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo() {
        return getDocumentProperties().isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo();
    }

    public void setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo(
            boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo ) {
        getDocumentProperties().setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo( permissionSetOneIsMorePrivilegedThanPermissionSetTwo );
    }

    public boolean isPublished() {
        return isPublishedAtTime( new Date() );
    }

    public boolean isPublishedAndNotArchived() {
        return isPublished() && !isArchived();
    }

    public boolean isNoLongerPublished() {
        return isNoLongerPublishedAtTime( new Date() ) ;
    }

    private boolean isNoLongerPublishedAtTime( Date date ) {
        Date publicationEndDatetime = getDocumentProperties().publicationEndDatetime;
        return publicationEndDatetime != null && publicationEndDatetime.before( date ) ;
    }

    public boolean isSearchDisabled() {
        return getDocumentProperties().isSearchDisabled();
    }

    public void setSearchDisabled( boolean searchDisabled ) {
        getDocumentProperties().setSearchDisabled( searchDisabled );
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return getDocumentProperties().isVisibleInMenusForUnauthorizedUsers();
    }

    public void setVisibleInMenusForUnauthorizedUsers( boolean visibleInMenusForUnauthorizedUsers ) {
        getDocumentProperties().setVisibleInMenusForUnauthorizedUsers( visibleInMenusForUnauthorizedUsers );
    }

    public void addCategory( CategoryDomainObject category ) {
        getDocumentProperties().addCategory( category );
    }

    public void addSection( SectionDomainObject section ) {
        getDocumentProperties().addSection( section );
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals( Object o ) {
        return getDocumentProperties().equals( o );
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
        return getDocumentProperties().getCategoriesOfType( type );
    }

    public abstract int getDocumentTypeId();

    public int hashCode() {
        return getDocumentProperties().hashCode();
    }

    public abstract void initDocument( DocumentMapper documentMapper );

    public boolean isArchivedAtTime( Date time ) {
        DocumentProperties documentProperties = getDocumentProperties();
        return ( documentProperties.archivedDatetime != null && documentProperties.archivedDatetime.before( time ) );
    }

    public abstract void processNewDocumentInformation( DocumentComposer documentInformation,
                                                        DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                                        UserDomainObject user, HttpServletRequest request,
                                                        HttpServletResponse response ) throws IOException, ServletException;

    public void removeAllCategories() {
        getDocumentProperties().removeAllCategories();
    }

    public void removeAllSections() {
        getDocumentProperties().removeAllSections();
    }

    public void removeCategory( CategoryDomainObject category ) {
        getDocumentProperties().removeCategory( category );
    }

    public abstract void saveDocument( DocumentMapper documentMapper, UserDomainObject user );

    public abstract void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) ;

    public void setPermissionSetForRole( RoleDomainObject role, int permissionSetId ) {
        getDocumentProperties().setPermissionSetForRole( role, permissionSetId );
    }

    private boolean isPublishedAtTime( Date date ) {
        DocumentProperties documentProperties = getDocumentProperties();
        boolean publicationStartDatetimeIsNotNullAndInThePast = documentProperties.publicationStartDatetime != null
                                                                && documentProperties.publicationStartDatetime.before( date );
        boolean publicationEndDatetimeIsNullOrInTheFuture = documentProperties.publicationEndDatetime == null
                                                            || documentProperties.publicationEndDatetime.after( date );
        boolean statusIsApproved = documentProperties.getStatus() == STATUS_PUBLICATION_APPROVED;
        boolean isPublished = statusIsApproved && publicationStartDatetimeIsNotNullAndInThePast && publicationEndDatetimeIsNullOrInTheFuture;
        return isPublished;
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

        private Date getArchivedDatetime() {
            return archivedDatetime;
        }

        private void setArchivedDatetime( Date v ) {
            this.archivedDatetime = v;
        }

        private CategoryDomainObject[] getCategories() {
            return (CategoryDomainObject[])categories.toArray( new CategoryDomainObject[categories.size()] );
        }

        private Date getCreatedDatetime() {
            return createdDatetime;
        }

        private void setCreatedDatetime( Date v ) {
            this.createdDatetime = v;
        }

        private UserDomainObject getCreator() {
            return creator;
        }

        private void setCreator( UserDomainObject creator ) {
            this.creator = creator;
        }

        private String getHeadline() {
            return headline;
        }

        private void setHeadline( String v ) {
            this.headline = v;
        }

        private int getId() {
            return id;
        }

        private void setId( int v ) {
            this.id = v;
        }

        private String getMenuImage() {
            return image;
        }

        private void setMenuImage( String v ) {
            this.image = v;
        }

        private String[] getKeywords() {
            return (String[])keywords.toArray( new String[keywords.size()] );
        }

        private void setKeywords( String[] keywords ) {
            this.keywords = new HashSet( Arrays.asList( keywords ) );
        }

        private String getLanguageIso639_2() {
            return languageIso639_2;
        }

        private void setLanguageIso639_2( String languageIso639_2 ) {
            this.languageIso639_2 = languageIso639_2;
        }

        private String getMenuText() {
            return menuText;
        }

        private void setMenuText( String v ) {
            this.menuText = v;
        }

        private Date getModifiedDatetime() {
            return modifiedDatetime;
        }

        private void setModifiedDatetime( Date v ) {
            this.modifiedDatetime = v;
        }

        private Date getPublicationEndDatetime() {
            return publicationEndDatetime;
        }

        private void setPublicationEndDatetime( Date publicationEndDatetime ) {
            this.publicationEndDatetime = publicationEndDatetime;
        }

        private Date getPublicationStartDatetime() {
            return publicationStartDatetime;
        }

        private void setPublicationStartDatetime( Date v ) {
            this.publicationStartDatetime = v;
        }

        private UserDomainObject getPublisher() {
            return publisher;
        }

        private void setPublisher( UserDomainObject user ) {
            publisher = user;
        }

        private Map getRolesMappedToPermissionSetIds() {
            return Collections.unmodifiableMap( rolesMappedToPermissionSetIds );
        }

        private SectionDomainObject[] getSections() {
            return (SectionDomainObject[])sections.toArray( new SectionDomainObject[sections.size()] );
        }

        private void setSections( SectionDomainObject[] sections ) {
            this.sections = new HashSet( Arrays.asList( sections ) );
        }

        private int getStatus() {
            return status;
        }

        private void setStatus( int status ) {
            switch ( status ) {
                default:
                    throw new IllegalArgumentException( "Bad status." );
                case STATUS_NEW:
                case STATUS_PUBLICATION_APPROVED:
                case STATUS_PUBLICATION_DISAPPROVED:
                    this.status = status;
            }
        }

        private String getTarget() {
            return target;
        }

        private void setTarget( String v ) {
            this.target = v;
        }

        private boolean isLinkableByOtherUsers() {
            return linkableByOtherUsers;
        }

        private void setLinkableByOtherUsers( boolean linkableByOtherUsers ) {
            this.linkableByOtherUsers = linkableByOtherUsers;
        }

        private boolean isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo() {
            return permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
        }

        private void setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo(
                boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo ) {
            this.permissionSetOneIsMorePrivilegedThanPermissionSetTwo =
            permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
        }

        private boolean isSearchDisabled() {
            return searchDisabled;
        }

        private void setSearchDisabled( boolean searchDisabled ) {
            this.searchDisabled = searchDisabled;
        }

        private boolean isVisibleInMenusForUnauthorizedUsers() {
            return visibleInMenusForUnauthorizedUsers;
        }

        private void setVisibleInMenusForUnauthorizedUsers( boolean visibleInMenusForUnauthorizedUsers ) {
            this.visibleInMenusForUnauthorizedUsers = visibleInMenusForUnauthorizedUsers;
        }

        private void addCategory( CategoryDomainObject category ) {
            categories.add( category );
        }

        private void addSection( SectionDomainObject section ) {
            sections.add( section );
        }

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

        private CategoryDomainObject[] getCategoriesOfType( CategoryTypeDomainObject type ) {
            CategoryDomainObject[] categories = getCategories();
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

        public int hashCode() {
            return id;
        }

        private void removeAllCategories() {
            categories.clear();
        }

        private void removeAllSections() {
            sections.clear();
        }

        private void removeCategory( CategoryDomainObject category ) {
            categories.remove( category );
        }

        private void setPermissionSetForRole( RoleDomainObject role, int permissionSetId ) {
            rolesMappedToPermissionSetIds.put( role, new Integer( permissionSetId ) );
        }

    }

}

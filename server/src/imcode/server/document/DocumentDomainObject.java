package imcode.server.document;

import imcode.server.IMCConstants;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.*;
import java.io.IOException;
import java.io.Serializable;

import com.imcode.imcms.servlet.admin.DocumentComposer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 * Stores info about a document. *
 */
public abstract class DocumentDomainObject implements IMCConstants, Cloneable, Serializable {

    public final static int DOCTYPE_TEXT = 2;
    public final static int DOCTYPE_URL = 5;
    public final static int DOCTYPE_BROWSER = 6;
    public final static int DOCTYPE_HTML = 7;
    public final static int DOCTYPE_FILE = 8;
    public final static int DOCTYPE_LOWEST_EXTERNAL = 100;
    public final static int DOCTYPE_DIAGRAM = 101;
    public final static int DOCTYPE_CONFERENCE = 102;
    public final static int DOCTYPE_CHAT = 103;
    public final static int DOCTYPE_BILLBOARD = 104;
    public static final int DOCTYPE_FORTUNES = 106;

    protected DocumentProperties documentInformation = new DocumentProperties();

    protected DocumentDomainObject() {

    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone() ;
    }

    public abstract int getDocumentTypeId();

    public DocumentProperties getDocumentInformation() {
        return documentInformation;
    }

    public void setDocumentInformation( DocumentProperties documentInformation ) {
        this.documentInformation = documentInformation;
    }

    public void addCategory( CategoryDomainObject category ) {
        getDocumentInformation().addCategory( category );
    }

    public void addSection( SectionDomainObject section ) {
        getDocumentInformation().addSection( section );
    }

    public boolean equals( Object o ) {
        return getDocumentInformation().equals( o );
    }

    public Date getActivatedDatetime() {
        return getDocumentInformation().getActivatedDatetime();
    }

    public Date getArchivedDatetime() {
        return getDocumentInformation().getArchivedDatetime();
    }

    public CategoryDomainObject[] getCategories() {
        return getDocumentInformation().getCategories();
    }

    public CategoryDomainObject[] getCategoriesOfType( CategoryTypeDomainObject type ) {
        return getDocumentInformation().getCategoriesOfType( type );
    }

    public Date getCreatedDatetime() {
        return getDocumentInformation().getCreatedDatetime();
    }

    public UserDomainObject getCreator() {
        return getDocumentInformation().getCreator();
    }

    public String getHeadline() {
        return getDocumentInformation().getHeadline();
    }

    public String getImage() {
        return getDocumentInformation().getImage();
    }

    public String[] getKeywords() {
        return getDocumentInformation().getKeywords();
    }

    public String getLanguageIso639_2() {
        return getDocumentInformation().getLanguageIso639_2();
    }

    public String getMenuText() {
        return getDocumentInformation().getMenuText();
    }

    public int getId() {
        return getDocumentInformation().getId();
    }

    public Date getModifiedDatetime() {
        return getDocumentInformation().getModifiedDatetime();
    }

    public UserDomainObject getPublisher() {
        return getDocumentInformation().getPublisher();
    }

    public Map getRolesMappedToPermissionSetIds() {
        return getDocumentInformation().getRolesMappedToPermissionSetIds();
    }

    public SectionDomainObject[] getSections() {
        return getDocumentInformation().getSections();
    }

    public String getTarget() {
        return getDocumentInformation().getTarget();
    }

    public int hashCode() {
        return getDocumentInformation().hashCode();
    }

    public boolean isActivated() {
        return getDocumentInformation().isActivated();
    }

    public boolean isActivatedAndNotArchived() {
        return getDocumentInformation().isActivatedAndNotArchived();
    }

    public boolean isActivatedAtTime( Date time ) {
        return getDocumentInformation().isActivatedAtTime( time );
    }

    public boolean isArchived() {
        return getDocumentInformation().isArchived();
    }

    public boolean isArchivedAtTime( Date time ) {
        return getDocumentInformation().isArchivedAtTime( time );
    }

    public boolean isArchivedFlag() {
        return getDocumentInformation().isArchivedFlag();
    }

    public boolean isLinkableByOtherUsers() {
        return getDocumentInformation().isLinkableByOtherUsers();
    }

    public boolean isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo() {
        return getDocumentInformation().isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo();
    }

    public boolean isSearchDisabled() {
        return getDocumentInformation().isSearchDisabled();
    }

    public boolean isVisibleInMenuForUnauthorizedUsers() {
        return getDocumentInformation().isVisibleInMenuForUnauthorizedUsers();
    }

    public void removeAllCategories() {
        getDocumentInformation().removeAllCategories();
    }

    public void removeAllSections() {
        getDocumentInformation().removeAllSections();
    }

    public void removeCategory( CategoryDomainObject category ) {
        getDocumentInformation().removeCategory( category );
    }

    public void setActivatedDatetime( Date v ) {
        getDocumentInformation().setActivatedDatetime( v );
    }

    public void setArchivedDatetime( Date v ) {
        getDocumentInformation().setArchivedDatetime( v );
    }

    public void setArchivedFlag( boolean v ) {
        getDocumentInformation().setArchivedFlag( v );
    }

    public void setCreatedDatetime( Date v ) {
        getDocumentInformation().setCreatedDatetime( v );
    }

    public void setCreator( UserDomainObject creator ) {
        getDocumentInformation().setCreator( creator );
    }

    public void setHeadline( String v ) {
        getDocumentInformation().setHeadline( v );
    }

    public void setImage( String v ) {
        getDocumentInformation().setImage( v );
    }

    public void setKeywords( String[] keywords ) {
        getDocumentInformation().setKeywords( keywords );
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        getDocumentInformation().setLanguageIso639_2( languageIso639_2 );
    }

    public void setLinkableByOtherUsers( boolean linkableByOtherUsers ) {
        getDocumentInformation().setLinkableByOtherUsers( linkableByOtherUsers );
    }

    public void setMenuText( String v ) {
        getDocumentInformation().setMenuText( v );
    }

    public void setMetaId( int v ) {
        getDocumentInformation().setMetaId( v );
    }

    public void setModifiedDatetime( Date v ) {
        getDocumentInformation().setModifiedDatetime( v );
    }

    public void setPermissionSetForRole( RoleDomainObject role, int permissionSetId ) {
        getDocumentInformation().setPermissionSetForRole( role, permissionSetId );
    }

    public void setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo(
            boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo ) {
        getDocumentInformation().setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo( permissionSetOneIsMorePrivilegedThanPermissionSetTwo );
    }

    public void setPublisher( UserDomainObject user ) {
        getDocumentInformation().setPublisher( user );
    }

    public void setSearchDisabled( boolean searchDisabled ) {
        getDocumentInformation().setSearchDisabled( searchDisabled );
    }

    public void setSections( SectionDomainObject[] sections ) {
        getDocumentInformation().setSections( sections );
    }

    public void setTarget( String v ) {
        getDocumentInformation().setTarget( v );
    }

    public void setVisibleInMenuForUnauthorizedUsers( boolean visibleInMenuForUnauthorizedUsers ) {
        getDocumentInformation().setVisibleInMenuForUnauthorizedUsers( visibleInMenuForUnauthorizedUsers );
    }

    public static DocumentDomainObject fromDocumentTypeId( int documentTypeId ) {
        DocumentDomainObject document = null ;

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
        }

        return document ;
    }

    public abstract void processNewDocumentInformation( DocumentComposer documentInformation,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException, ServletException;

    public abstract void saveDocument( DocumentMapper documentMapper ) ;

    public abstract void saveNewDocument( DocumentMapper documentMapper ) throws IOException;

    public abstract void initDocumentFromDb( DocumentMapper documentMapper ) ;

    public static class DocumentProperties implements Cloneable, Serializable {

        private Date activatedDatetime;
        private Date archivedDatetime;
        private boolean archivedFlag;
        private Set categories = new HashSet();
        private Date createdDatetime;
        private UserDomainObject creator;
        private String headline;
        private String image;
        private Set keywords = new HashSet();
        private String languageIso639_2;
        private boolean linkableByOtherUsers;
        private String menuText;
        private int metaId;
        private Date modifiedDatetime;
        private boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
        private UserDomainObject publisher;
        private Map rolesMappedToPermissionSetIds = new HashMap();
        private boolean searchDisabled;
        private Set sections = new HashSet();
        private String target;
        private boolean visibleInMenuForUnauthorizedUsers;

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
            return (CategoryDomainObject[])categories.toArray( new CategoryDomainObject[categories.size()] );
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
            return (String[])keywords.toArray( new String[keywords.size()] );
        }

        public void setKeywords( String[] keywords ) {
            this.keywords = new HashSet( Arrays.asList( keywords ) );
        }

        public String getLanguageIso639_2() {
            return languageIso639_2;
        }

        public void setLanguageIso639_2( String languageIso639_2 ) {
            this.languageIso639_2 = languageIso639_2;
        }

        public int getId() {
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
            return Collections.unmodifiableMap( rolesMappedToPermissionSetIds );
        }

        public SectionDomainObject[] getSections() {
            return (SectionDomainObject[])sections.toArray( new SectionDomainObject[sections.size()] );
        }

        public void setSections( SectionDomainObject[] sections ) {
            this.sections = new HashSet( Arrays.asList( sections ) );
        }

        public String getTarget() {
            return target;
        }

        public void setTarget( String v ) {
            this.target = v;
        }

        public String getMenuText() {
            return menuText;
        }

        public void setMenuText( String v ) {
            this.menuText = v;
        }

        /**
         * Check whether this document is archived.
         * A document is archived if its archived-flag is set or if archived-datetime is in the past.
         */
        public boolean isArchived() {
            return isArchivedFlag() || isArchivedAtTime( new Date() );
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
            categories.add( category );
        }

        public void addSection( SectionDomainObject section ) {
            sections.add( section );
        }

        public CategoryDomainObject[] getCategoriesOfType( CategoryTypeDomainObject type ) {
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
            return metaId;
        }

        public boolean isActivated() {
            Date now = new Date();
            return isActivatedAtTime( now );
        }

        private boolean isActivatedAtTime( Date time ) {
            return ( activatedDatetime == null || activatedDatetime.before( time ) );
        }

        public boolean isActivatedAndNotArchived() {
            return isActivated() && !isArchived();
        }

        public boolean isArchivedAtTime( Date time ) {
            return ( archivedDatetime != null && archivedDatetime.before( time ) );
        }

        public void removeAllCategories() {
            categories.clear();
        }

        public void removeCategory( CategoryDomainObject category ) {
            categories.remove( category );
        }

        public void setPermissionSetForRole( RoleDomainObject role, int permissionSetId ) {
            rolesMappedToPermissionSetIds.put( role, new Integer( permissionSetId ) );
        }

        public boolean isVisibleInMenuForUnauthorizedUsers() {
            return visibleInMenuForUnauthorizedUsers;
        }

        public void setVisibleInMenuForUnauthorizedUsers( boolean visibleInMenuForUnauthorizedUsers ) {
            this.visibleInMenuForUnauthorizedUsers = visibleInMenuForUnauthorizedUsers;
        }

        public boolean isLinkableByOtherUsers() {
            return linkableByOtherUsers;
        }

        public void setLinkableByOtherUsers( boolean linkableByOtherUsers ) {
            this.linkableByOtherUsers = linkableByOtherUsers;
        }

        public void removeAllSections() {
            sections.clear();
        }

        public Object clone() throws CloneNotSupportedException {
            DocumentProperties clone = (DocumentProperties)super.clone();
            clone.categories = new HashSet( categories );
            clone.keywords = new HashSet( keywords );
            clone.rolesMappedToPermissionSetIds = new HashMap( rolesMappedToPermissionSetIds );
            clone.sections = new HashSet( sections );

            return clone;
        }

        public boolean isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo() {
            return permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
        }

        public void setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo(
                boolean permissionSetOneIsMorePrivilegedThanPermissionSetTwo ) {
            this.permissionSetOneIsMorePrivilegedThanPermissionSetTwo =
            permissionSetOneIsMorePrivilegedThanPermissionSetTwo;
        }

        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof DocumentProperties ) ) {
                return false;
            }

            final DocumentProperties documentInformation = (DocumentProperties)o;

            if ( metaId != documentInformation.metaId ) {
                return false;
            }

            return true;
        }

    }

}

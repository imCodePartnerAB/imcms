package com.imcode.imcms.api;

import com.imcode.imcms.api.util.ChainableReversibleNullComparator;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.document.*;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;

import java.util.*;

public class Document {

    SecurityChecker securityChecker;
    DocumentService documentService;
    DocumentDomainObject internalDocument;
    DocumentMapper documentMapper;
    private DocumentPermissionSetMapper documentPermissionSetMapper;
    UserAndRoleMapper userAndRoleMapper;
    IMCServiceInterface service;

    private final static Logger log = Logger.getLogger( com.imcode.imcms.api.Document.class.getName() );
    public static final int STATUS_NEW = DocumentDomainObject.STATUS_NEW ;
    public static final int STATUS_PUBLICATION_DISAPPROVED = DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED ;
    public static final int STATUS_PUBLICATION_APPROVED = DocumentDomainObject.STATUS_PUBLICATION_APPROVED ;

    public Document( DocumentDomainObject document, IMCServiceInterface service, SecurityChecker securityChecker, DocumentService documentService,
                     DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper,
                     UserAndRoleMapper userAndRoleMapper ) {

        this.securityChecker = securityChecker;
        this.service = service;
        this.documentService = documentService;
        this.internalDocument = document;
        this.documentMapper = documentMapper;
        this.documentPermissionSetMapper = documentPermissionSetMapper;
        this.userAndRoleMapper = userAndRoleMapper;
    }

    /**
     * @return map of rolename String -> DocumentPermissionSet instances.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( this );

        Map rolesMappedToPermissionSetIds = internalDocument.getRolesMappedToPermissionSetIds();

        Map result = new HashMap();
        for ( Iterator it = rolesMappedToPermissionSetIds.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry rolePermissionTuple = (Map.Entry)it.next();
            RoleDomainObject role = (RoleDomainObject)rolePermissionTuple.getKey();
            int permissionType = ( (Integer)rolePermissionTuple.getValue() ).intValue();
            switch ( permissionType ) {
                case DocumentPermissionSetDomainObject.TYPE_ID__FULL:
                    result.put( role.getName(), DocumentPermissionSetDomainObject.FULL );
                    break;
                case DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1:
                case DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2:
                    result.put( role.getName(),
                                documentPermissionSetMapper.createRestrictedPermissionSet( internalDocument,
                                                                                           permissionType,
                                                                                           false ) );
                    break;
                case DocumentPermissionSetDomainObject.TYPE_ID__READ:
                    result.put( role.getName(), DocumentPermissionSetDomainObject.READ );
                    break;
                case DocumentPermissionSetDomainObject.TYPE_ID__NONE:
                    break;
                default:
                    log.warn( "A missing mapping in DocumentPermissionSetMapper" );
                    break;
            }
        }

        return wrapDomainObjectsInMap( result );

    }

    private static Map wrapDomainObjectsInMap( Map rolesMappedToPermissionsIds ) {
        Map result = new HashMap();
        Set keys = rolesMappedToPermissionsIds.keySet();
        Iterator keyIterator = keys.iterator();
        while ( keyIterator.hasNext() ) {
            String roleName = (String)keyIterator.next();
            DocumentPermissionSetDomainObject documentPermissionSetDO = (DocumentPermissionSetDomainObject)rolesMappedToPermissionsIds.get(
                    roleName );
            DocumentPermissionSet documentPermissionSet = new DocumentPermissionSet( documentPermissionSetDO );
            result.put( roleName, documentPermissionSet );
        }
        return result;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Document ) ) {
            return false;
        }

        final Document document = (Document)o;

        if ( !internalDocument.equals( document.internalDocument ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return internalDocument.hashCode();
    }

    public DocumentPermissionSet getPermissionSetRestrictedOne() throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        DocumentPermissionSetDomainObject restrictedOne = documentPermissionSetMapper.getPermissionSetRestrictedOne(
                internalDocument );
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedOne );
        return result;
    }

    public DocumentPermissionSet getPermissionSetRestrictedTwo() throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        DocumentPermissionSetDomainObject restrictedTwo = documentPermissionSetMapper.getPermissionSetRestrictedTwo(
                internalDocument );
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedTwo );
        return result;
    }

    public int getId() throws NoPermissionException {
        //securityChecker.userHasAtLeastDocumentReadPermission(this);
        // Dont check permissions on this, its used when we check permissions
        // and we get at stack overflow situation.
        // and the document id is no secret anyway?
        return internalDocument.getId();
    }

    public String getHeadline() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getHeadline();
    }

    public String getMenuText() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getMenuText();
    }

    public String getMenuImageURL() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getMenuImage();
    }

    public void setHeadline( String headline ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setHeadline( headline );
    }

    public void setMenuText( String menuText ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setMenuText( menuText );
    }

    public void setMenuImageURL( String imageUrl ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setMenuImage( imageUrl );
    }

    public User getCreator() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return new User( internalDocument.getCreator(), userAndRoleMapper, securityChecker );
    }

    DocumentDomainObject getInternal() {
        return internalDocument;
    }

    public Language getLanguage() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return Language.getLanguageByISO639_2( internalDocument.getLanguageIso639_2() );
    }

    public void setLanguage( int lang_id ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        String lang_prefix = service.getLanguagePrefixByLangId( lang_id);
        internalDocument.setLanguageIso639_2( lang_prefix);
    }

    public void addCategory( Category category ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.addCategory( category.getInternal() );
    }

    public void removeCategory( Category category ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.removeCategory( category.getInternal() );
    }

    /**
     * @return An array of Categories, an empty if no one found.
     * @throws NoPermissionException
     */
    public Category[] getCategories() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        CategoryDomainObject[] categoryDomainObjects = internalDocument.getCategories();
        return getCategoryArrayFromCategoryDomainObjectArray( categoryDomainObjects );
    }

    private Category[] getCategoryArrayFromCategoryDomainObjectArray( CategoryDomainObject[] categoryDomainObjects ) {
        Category[] categories = new Category[categoryDomainObjects.length];

        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject categoryDomainObject = categoryDomainObjects[i];
            categories[i] = new Category( categoryDomainObject );
        }
        return categories;
    }

    public void setPermissionSetForRole( String roleName, int permissionSet ) throws NoSuchRoleException, NoPermissionException {
        securityChecker.hasEditPermission( this );
        RoleDomainObject role = userAndRoleMapper.getRoleByName( roleName );
        if ( null == role ) {
            throw new NoSuchRoleException( "No role by the name '" + roleName + "'." );
        }
        internalDocument.setPermissionSetIdForRole( role, permissionSet );
    }

    /**
     * @param categoryType
     * @return an array of Categories, empty array if no one found.
     * @throws NoPermissionException
     */
    public Category[] getCategoriesOfType( CategoryType categoryType ) throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        CategoryDomainObject[] categoryDomainObjects = internalDocument.getCategoriesOfType(
                categoryType.getInternal() );
        return getCategoryArrayFromCategoryDomainObjectArray( categoryDomainObjects );
    }

    public User getPublisher() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        UserDomainObject publisher = internalDocument.getPublisher();
        if ( null != publisher ) {
            return new User( publisher, userAndRoleMapper, securityChecker );
        } else {
            return null;
        }
    }

    public String getTarget() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getTarget();
    }

    /** @deprecated Use {@link #getPublicationStartDatetime()} */
    public Date getActivatedDatetime() throws NoPermissionException {
        return getPublicationStartDatetime();
    }

    public Date getPublicationStartDatetime() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getPublicationStartDatetime();
    }

    /** @deprecated Use {@link #setPublicationStartDatetime(java.util.Date)} **/
    public void setActivatedDatetime( Date datetime ) throws NoPermissionException {
        setPublicationStartDatetime( datetime );
    }

    public void setPublicationStartDatetime( Date datetime ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setPublicationStartDatetime( datetime );
    }

    public Date getArchivedDatetime() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getArchivedDatetime();
    }

    public void setArchivedDatetime( Date datetime ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setArchivedDatetime( datetime );
    }

    public void setPublisher( User user ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setPublisher( user.getInternalUser() );
    }

    /**
     * @return An array of Sections, an empty arrya if no one found.
     * @throws NoPermissionException
     */
    public Section[] getSections() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        SectionDomainObject[] sectionDomainObjects = internalDocument.getSections();
        Section[] sections = new Section[sectionDomainObjects.length];
        for ( int i = 0; i < sectionDomainObjects.length; i++ ) {
            SectionDomainObject sectionDomainObject = sectionDomainObjects[i];
            sections[i] = new Section( sectionDomainObject );
        }
        return sections;
    }

    public void setSections( Section[] sections ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        SectionDomainObject[] internalSections = new SectionDomainObject[sections.length];
        for ( int i = 0; i < sections.length; i++ ) {
            Section section = sections[i];
            internalSections[i] = section.internalSection;
        }
        internalDocument.setSections( internalSections );
    }

    /**
     * @deprecated Use {@link #getModifiedDatetime()} instead.
     */
    public Date getModifiedDateTime() throws NoPermissionException {
        return getModifiedDatetime();
    }

    public Date getModifiedDatetime() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getModifiedDatetime();
    }

    /**
     * @deprecated Use {@link #setModifiedDatetime(java.util.Date)} instead.
     */
    public void setModifiedDateTime( Date date ) throws NoPermissionException {
        setModifiedDatetime( date );
    }

    public void setModifiedDatetime( Date date ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setModifiedDatetime( date );
    }

    /**
     * @deprecated Use {@link #getCreatedDatetime()} instead.
     */
    public Date getCreatedDateTime() throws NoPermissionException {
        return getCreatedDatetime();
    }

    public Date getCreatedDatetime() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getCreatedDatetime();
    }

    public void addSection( Section section ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.addSection( section.internalSection );
    }

    public void setStatus( int status ) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        internalDocument.setStatus(status);
    }

    public void setLanguage(Language language) throws NoPermissionException {
        securityChecker.hasEditPermission(this);
        internalDocument.setLanguageIso639_2(language.getIsoCode639_2());
    }

    public void setPublicationEndDatetime( Date datetime ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setPublicationEndDatetime( datetime ) ;
    }

    public Date getPublicationEndDatetime() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getPublicationEndDatetime();
    }

    public int getStatus() throws NoPermissionException {
        securityChecker.hasAtLeastDocumentReadPermission( this );
        return internalDocument.getStatus() ;
    }

    public abstract static class Comparator extends ChainableReversibleNullComparator {

        public int compare( Object o1, Object o2 ) {
            final Document d1 = (Document)o1;
            final Document d2 = (Document)o2;
            try {
                return compareDocuments( d1, d2 );
            } catch ( NullPointerException npe ) {
                throw new NullPointerException( "Tried sorting on null fields! You need to call .nullsFirst() or .nullsLast() on your Comparator.") ;
            } catch ( NoPermissionException e ) {
                throw new RuntimeException( e );
            }
        }

        protected abstract int compareDocuments( Document d1, Document d2 ) throws NoPermissionException;

        public final static Comparator ID = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) throws NoPermissionException {
                return d1.getId() - d2.getId();
            }
        };

        public final static Comparator HEADLINE = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) throws NoPermissionException {
                return d1.getHeadline().compareToIgnoreCase( d2.getHeadline() );
            }
        };

        public final static Comparator CREATED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) throws NoPermissionException {
                return d1.getCreatedDatetime().compareTo( d2.getCreatedDatetime() );
            }
        };

        public final static Comparator MODIFIED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) throws NoPermissionException {
                return d1.getModifiedDatetime().compareTo( d2.getModifiedDatetime() );
            }
        };

        public final static Comparator PUBLICATION_START_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) throws NoPermissionException {
                return document1.getPublicationStartDatetime().compareTo( document2.getPublicationStartDatetime() );
            }
        };

        /** @deprecated Use {@link #PUBLICATION_START_DATETIME} instead **/
        public final static Comparator ACTIVATED_DATETIME = PUBLICATION_START_DATETIME ;

        public final static Comparator PUBLICATION_END_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) throws NoPermissionException {
                return document1.getPublicationEndDatetime().compareTo( document2.getPublicationEndDatetime() );
            }
        };

        public final static Comparator ARCHIVED_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) throws NoPermissionException {
                return document1.getArchivedDatetime().compareTo( document2.getArchivedDatetime() );
            }
        };

    }

}

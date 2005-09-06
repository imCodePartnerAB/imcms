package com.imcode.imcms.api;

import com.imcode.util.ChainableReversibleNullComparator;
import imcode.server.document.*;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Document implements Serializable {

    private final DocumentDomainObject internalDocument;
    ContentManagementSystem contentManagementSystem;

    private final static Logger log = Logger.getLogger( com.imcode.imcms.api.Document.class.getName() );

    /** @deprecated Use {@link Document.PublicationStatus#NEW} instead. */
    public static final int STATUS_NEW = 0;
    /** @deprecated Use {@link Document.PublicationStatus#DISAPPROVED} instead. */
    public static final int STATUS_PUBLICATION_DISAPPROVED = 1;
    /** @deprecated Use {@link Document.PublicationStatus#APPROVED} instead. */
    public static final int STATUS_PUBLICATION_APPROVED = 2;

    protected Document( DocumentDomainObject document, ContentManagementSystem contentManagementSystem ) {
        this.internalDocument = document;
        this.contentManagementSystem = contentManagementSystem;
    }

    DocumentDomainObject getInternal() {
        return internalDocument;
    }

    public int getId() {
        return internalDocument.getId();
    }

    /**
     * @return map of roles Role -> DocumentPermissionSet instances.
     */
    public Map getRolesMappedToPermissions() {
        RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings = internalDocument.getRoleIdsMappedToDocumentPermissionSetTypes();

        Map result = new HashMap();
        RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappings = roleIdToDocumentPermissionSetTypeMappings.getMappings();
        RoleGetter roleGetter = contentManagementSystem.getInternal().getRoleGetter();
        for ( int i = 0; i < mappings.length; i++ ) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = mappings[i];
            RoleId roleId = mapping.getRoleId();
            RoleDomainObject role = roleGetter.getRole(roleId) ;
            DocumentPermissionSetTypeDomainObject documentPermissionSetType = mapping.getDocumentPermissionSetType();
            if ( DocumentPermissionSetTypeDomainObject.FULL.equals(documentPermissionSetType) ) {
                result.put(role, DocumentPermissionSetDomainObject.FULL);
            } else if ( DocumentPermissionSetTypeDomainObject.RESTRICTED_1.equals(documentPermissionSetType) ) {
                result.put(role, internalDocument.getPermissionSetForRestrictedOne());
            } else if ( DocumentPermissionSetTypeDomainObject.RESTRICTED_2.equals(documentPermissionSetType) ) {
                result.put(role, internalDocument.getPermissionSetForRestrictedTwo());
            } else if ( DocumentPermissionSetTypeDomainObject.READ.equals(documentPermissionSetType) ) {
                result.put(role, DocumentPermissionSetDomainObject.READ);
            } else if ( !DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) ) {
                log.warn("A missing mapping in DocumentPermissionSetMapper");
            }
        }

        return wrapDomainObjectsInMap( result );

    }

    private static Map wrapDomainObjectsInMap( Map rolesMappedToPermissionsIds ) {
        Map result = new HashMap();
        Set keys = rolesMappedToPermissionsIds.keySet();
        Iterator keyIterator = keys.iterator();
        while ( keyIterator.hasNext() ) {
            RoleDomainObject role = (RoleDomainObject)keyIterator.next();
            DocumentPermissionSetDomainObject documentPermissionSetDO = (DocumentPermissionSetDomainObject)rolesMappedToPermissionsIds.get( role );
            DocumentPermissionSet documentPermissionSet = new DocumentPermissionSet( documentPermissionSetDO );
            result.put( new Role(role), documentPermissionSet );
        }
        return result;
    }

    public boolean isSearchDisabled() {
        return internalDocument.isSearchDisabled();
    }

    public void setSearchDisabled(boolean searchDisabled) {
        internalDocument.setSearchDisabled(searchDisabled);
    }

    public DocumentPermissionSet getDocumentPermissionSetForUser() {
        return new DocumentPermissionSet( contentManagementSystem.getCurrentUser().getInternal().getPermissionSetFor( internalDocument ) );
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Document ) ) {
            return false;
        }

        final Document document = (Document)o;

        return internalDocument.equals(document.internalDocument);

    }

    public int hashCode() {
        return internalDocument.hashCode();
    }

    public Set getKeywords() {
        return internalDocument.getKeywords();
    }

    public void setKeywords(Set keywords) {
        internalDocument.setKeywords(keywords);
    }

    public DocumentPermissionSet getPermissionSetRestrictedOne() {
        DocumentPermissionSetDomainObject restrictedOne = internalDocument.getPermissionSetForRestrictedOne() ;
        return new DocumentPermissionSet( restrictedOne );
    }

    public DocumentPermissionSet getPermissionSetRestrictedTwo() {
        DocumentPermissionSetDomainObject restrictedTwo = internalDocument.getPermissionSetForRestrictedTwo() ;
        return new DocumentPermissionSet( restrictedTwo );
    }

    public String getHeadline() {
        return internalDocument.getHeadline();
    }

    public String getMenuText() {
        return internalDocument.getMenuText();
    }

    public String getMenuImageURL() {
        return internalDocument.getMenuImage();
    }

    public void setHeadline( String headline ) {
        internalDocument.setHeadline( headline );
    }

    public void setMenuText( String menuText ) {
        internalDocument.setMenuText( menuText );
    }

    public void setMenuImageURL( String imageUrl ) {
        internalDocument.setMenuImage( imageUrl );
    }

    public User getCreator() {
        return new User( internalDocument.getCreator() );
    }

    public void setCreator( User creator ) {
        internalDocument.setCreator( creator.getInternal() );
    }

    public Language getLanguage() {
        return Language.getLanguageByISO639_2( internalDocument.getLanguageIso639_2() );
    }

    public void addCategory( Category category ) {
        internalDocument.addCategory( category.getInternal() );
    }

    public void removeCategory( Category category ) {
        internalDocument.removeCategory( category.getInternal() );
    }

    /**
     * @return An array of Categories, an empty if no one found.
     */
    public Category[] getCategories() {
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

    /**
        @param permissionSetId One of the constants in {@link DocumentPermissionSet}.
        @deprecated Use {@link #setPermissionSetTypeForRole(Role, DocumentPermissionSetType)}
        @since 2.0
     **/
    public void setPermissionSetIdForRole( Role role, int permissionSetId ) {
        if ( null != role ) {
            internalDocument.setDocumentPermissionSetTypeForRoleId(role.getInternal().getId(), DocumentPermissionSetTypeDomainObject.fromInt(permissionSetId));
        }
    }

    /**
        @since 3.0
     */
    public void setPermissionSetTypeForRole( Role role, DocumentPermissionSetType documentPermissionSetType ) {
        internalDocument.setDocumentPermissionSetTypeForRoleId(role.getInternal().getId(), documentPermissionSetType.getInternal());
    }

    /**
     *   @deprecated Use {@link #getPermissionSetTypeForRole(Role)}
         @since 2.0
     */
    public int getPermissionSetIdForRole( Role role ) {
        return internalDocument.getDocumentPermissionSetTypeForRoleId(role.getInternal().getId()).getId();
    }

    /**
        @since 3.0
    */
    public DocumentPermissionSetType getPermissionSetTypeForRole( Role role ) {
        return new DocumentPermissionSetType(internalDocument.getDocumentPermissionSetTypeForRoleId(role.getInternal().getId())) ;
    }

    /**
     * @param categoryType
     * @return an array of Categories, empty array if no one found.
     */
    public Category[] getCategoriesOfType( CategoryType categoryType ) {
        CategoryDomainObject[] categoryDomainObjects = internalDocument.getCategoriesOfType( categoryType.getInternal() );
        return getCategoryArrayFromCategoryDomainObjectArray( categoryDomainObjects );
    }

    public User getPublisher() {
        UserDomainObject publisher = internalDocument.getPublisher();
        if ( null != publisher ) {
            return new User( publisher );
        } else {
            return null;
        }
    }

    public String getTarget() {
        return internalDocument.getTarget();
    }

    public Date getPublicationStartDatetime() {
        return internalDocument.getPublicationStartDatetime();
    }

    public void setPublicationStartDatetime( Date datetime ) {
        internalDocument.setPublicationStartDatetime( datetime );
    }

    public Date getArchivedDatetime() {
        return internalDocument.getArchivedDatetime();
    }

    public void setArchivedDatetime( Date datetime ) {
        internalDocument.setArchivedDatetime( datetime );
    }

    public void setPublisher( User user ) {
        internalDocument.setPublisher( user.getInternal() );
    }

    /**
     * @return An array of Sections, an empty arrya if no one found.
     */
    public Section[] getSections() {
        SectionDomainObject[] sectionDomainObjects = internalDocument.getSections();
        Section[] sections = new Section[sectionDomainObjects.length];
        for ( int i = 0; i < sectionDomainObjects.length; i++ ) {
            SectionDomainObject sectionDomainObject = sectionDomainObjects[i];
            sections[i] = new Section( sectionDomainObject );
        }
        return sections;
    }

    public void setSections( Section[] sections ) {
        SectionDomainObject[] internalSections = new SectionDomainObject[sections.length];
        for ( int i = 0; i < sections.length; i++ ) {
            Section section = sections[i];
            internalSections[i] = section.internalSection;
        }
        internalDocument.setSections( internalSections );
    }

    public Date getModifiedDatetime() {
        return internalDocument.getModifiedDatetime();
    }

    public void setModifiedDatetime( Date date ) {
        internalDocument.setModifiedDatetime( date );
    }

    public Date getCreatedDatetime() {
        return internalDocument.getCreatedDatetime();
    }

    public void addSection( Section section ) {
        internalDocument.addSection( section.internalSection );
    }

    /** @deprecated Use {@link #setPublicationStatus} instead. */
    public void setStatus( int status ) {
        internalDocument.setPublicationStatus( new PublicationStatus(status) );
    }

    public void setLanguage( Language language ) {
        internalDocument.setLanguageIso639_2( language.getIsoCode639_2() );
    }

    public void setPublicationEndDatetime( Date datetime ) {
        internalDocument.setPublicationEndDatetime( datetime );
    }

    public Date getPublicationEndDatetime() {
        return internalDocument.getPublicationEndDatetime();
    }

    /** @deprecated Use {@link #getPublicationStatus} instead. */
    public int getStatus() {
        return internalDocument.getPublicationStatus().status ;
    }

    public void setVisibleInMenusForUnauthorizedUsers( boolean visibleInMenusForUnauthorizedUsers ) {
        internalDocument.setVisibleInMenusForUnauthorizedUsers( visibleInMenusForUnauthorizedUsers );
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return internalDocument.isVisibleInMenusForUnauthorizedUsers();
    }

    public boolean isLinkableByOtherUsers() {
        return internalDocument.isLinkableByOtherUsers();
    }

    public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
        internalDocument.setLinkableByOtherUsers(linkableByOtherUsers);
    }

    public PublicationStatus getPublicationStatus() {
        return internalDocument.getPublicationStatus();
    }

    public void setPublicationStatus(PublicationStatus publicationStatus) {
        internalDocument.setPublicationStatus(publicationStatus) ;
    }

    /**
        @since 3.0
     */
    public static class PublicationStatus {
        public static final PublicationStatus NEW = new PublicationStatus(STATUS_NEW);
        public static final PublicationStatus APPROVED = new PublicationStatus(STATUS_PUBLICATION_APPROVED);
        public static final PublicationStatus DISAPPROVED = new PublicationStatus(STATUS_PUBLICATION_DISAPPROVED);

        private final int status;

        private PublicationStatus(int status) {
            this.status = status;
        }

        public String toString() {
            return ""+status ;
        }

        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            return status == ( (PublicationStatus) o ).status;

        }

        public int hashCode() {
            return status;
        }
    }

    public static class LifeCyclePhase {
        public static final LifeCyclePhase NEW = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.NEW);
        public static final LifeCyclePhase DISAPPROVED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.DISAPPROVED);
        public static final LifeCyclePhase PUBLISHED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.PUBLISHED);
        public static final LifeCyclePhase UNPUBLISHED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.UNPUBLISHED);
        public static final LifeCyclePhase ARCHIVED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.ARCHIVED);
        public static final LifeCyclePhase APPROVED = new LifeCyclePhase(DocumentDomainObject.LifeCyclePhase.APPROVED);

        private DocumentDomainObject.LifeCyclePhase phase ;

        private LifeCyclePhase( DocumentDomainObject.LifeCyclePhase phase ) {
            this.phase = phase;
        }

        public String toString() {
            return phase.toString() ;
        }
    }

    public abstract static class Comparator extends ChainableReversibleNullComparator {

        public int compare( Object o1, Object o2 ) {
            final Document d1 = (Document)o1;
            final Document d2 = (Document)o2;
            try {
                return compareDocuments( d1, d2 );
            } catch ( NullPointerException npe ) {
                NullPointerException nullPointerException = new NullPointerException("Tried sorting on null fields! You need to call .nullsFirst() or .nullsLast() on your Comparator.");
                nullPointerException.initCause(npe);
                throw nullPointerException;
            }
        }

        protected abstract int compareDocuments( Document d1, Document d2 );

        public final static Comparator ID = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getId() - d2.getId();
            }
        };

        public final static Comparator HEADLINE = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getHeadline().compareToIgnoreCase( d2.getHeadline() );
            }
        };

        public final static Comparator CREATED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getCreatedDatetime().compareTo( d2.getCreatedDatetime() );
            }
        };

        public final static Comparator MODIFIED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getModifiedDatetime().compareTo( d2.getModifiedDatetime() );
            }
        };

        public final static Comparator PUBLICATION_START_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getPublicationStartDatetime().compareTo( document2.getPublicationStartDatetime() );
            }
        };

        public final static Comparator PUBLICATION_END_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getPublicationEndDatetime().compareTo( document2.getPublicationEndDatetime() );
            }
        };

        public final static Comparator ARCHIVED_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getArchivedDatetime().compareTo( document2.getArchivedDatetime() );
            }
        };

    }
}

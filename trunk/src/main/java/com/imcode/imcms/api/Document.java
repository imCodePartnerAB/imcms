package com.imcode.imcms.api;

import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleGetter;
import imcode.server.user.RoleId;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.util.ChainableReversibleNullComparator;
import com.imcode.util.CountingIterator;

public class Document implements Serializable {

    private final DocumentDomainObject internalDocument;
    ContentManagementSystem contentManagementSystem;

    private final static Logger log = Logger.getLogger( Document.class.getName() );

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
                result.put(role, internalDocument.getPermissionSets().getRestricted1());
            } else if ( DocumentPermissionSetTypeDomainObject.RESTRICTED_2.equals(documentPermissionSetType) ) {
                result.put(role, internalDocument.getPermissionSets().getRestricted2());
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

    /** Whether the document is archived. **/
    public boolean isArchived() {
        return internalDocument.isArchived();
    }

    /** Whether the document is published and not archived. **/
    public boolean isActive() {
        return internalDocument.isActive();
    }

    /** Whether the document is published. **/
    public boolean isPublished() {
        return internalDocument.isPublished();
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

    public void setKeywords(I18nLanguage language, Set keywords) {
        internalDocument.setKeywords(language, keywords);
    }

    public DocumentPermissionSet getPermissionSetRestrictedOne() {
        DocumentPermissionSetDomainObject restrictedOne = internalDocument.getPermissionSets().getRestricted1() ;
        return new DocumentPermissionSet( restrictedOne );
    }

    public DocumentPermissionSet getPermissionSetRestrictedTwo() {
        DocumentPermissionSetDomainObject restrictedTwo = internalDocument.getPermissionSets().getRestricted2() ;
        return new DocumentPermissionSet( restrictedTwo );
    }

    public String getHeadline() {
        return internalDocument.getHeadline();
    }

    public String getMenuText() {
        return internalDocument.getMenuText();
    }

    public String getAlias() {
        return  internalDocument.getAlias();
    }

    public void setAlias(String alias) {
        internalDocument.setAlias(alias);
    }

    public String getName() {
        return internalDocument.getName();
    }

    public String getMenuImageURL() {
        return internalDocument.getMenuImage();
    }

    public void setHeadline(I18nLanguage language, String headline ) {
        internalDocument.setHeadline(language, headline );
    }

    public void setMenuText(I18nLanguage language,  String menuText ) {
        internalDocument.setMenuText(language, menuText );
    }

    public void setMenuImageURL(I18nLanguage language, String imageUrl ) {
        internalDocument.setMenuImage(language, imageUrl );
    }

    public User getCreator() {
        int creatorId = internalDocument.getCreatorId();
        return contentManagementSystem.getUserService().getUser(creatorId) ;
    }

    public void setCreator( User creator ) {
        internalDocument.setCreator( creator.getInternal() );
    }

    public void addCategory( Category category ) {
        internalDocument.addCategoryId( category.getId() );
    }

    public void removeCategory( Category category ) {
        internalDocument.removeCategoryId( category.getId() );
    }

    /**
     * @return An array of Categories, an empty if no one found.
     */
    public Category[] getCategories() {
        Set categories = contentManagementSystem.getInternal().getCategoryMapper().getCategories(internalDocument.getCategoryIds());
        CategoryDomainObject[] categoryDomainObjects = (CategoryDomainObject[]) categories.toArray(new CategoryDomainObject[categories.size()]);
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
        CategoryMapper categoryMapper = contentManagementSystem.getInternal().getCategoryMapper();
        Set categoriesOfType = categoryMapper.getCategoriesOfType(categoryType.getInternal(), internalDocument.getCategoryIds());
        CategoryDomainObject[] categories = (CategoryDomainObject[]) categoriesOfType.toArray(new CategoryDomainObject[categoriesOfType.size()]);
        return getCategoryArrayFromCategoryDomainObjectArray( categories );
    }

    public User getPublisher() {
        Integer publisherId = internalDocument.getPublisherId();
        if ( null != publisherId ) {
            return contentManagementSystem.getUserService().getUser(publisherId.intValue()) ;
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

    public Date getModifiedDatetime() {
        return internalDocument.getModifiedDatetime();
    }

    public void setModifiedDatetime( Date date ) {
        internalDocument.setModifiedDatetime( date );
    }

    public Date getCreatedDatetime() {
        return internalDocument.getCreatedDatetime();
    }

    /** @deprecated Use {@link #setPublicationStatus} instead. */
    public void setStatus( int status ) {
        internalDocument.setPublicationStatus( new PublicationStatus(status) );
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
        internalDocument.setLinkedForUnauthorizedUsers( visibleInMenusForUnauthorizedUsers );
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return internalDocument.isLinkedForUnauthorizedUsers();
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
    public static class PublicationStatus implements Serializable {
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
        
        public Integer asInt() {
        	return status;
        }
    }

    public static class LifeCyclePhase implements Serializable {
        public static final LifeCyclePhase NEW = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.NEW);
        public static final LifeCyclePhase DISAPPROVED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.DISAPPROVED);
        public static final LifeCyclePhase PUBLISHED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.PUBLISHED);
        public static final LifeCyclePhase UNPUBLISHED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.UNPUBLISHED);
        public static final LifeCyclePhase ARCHIVED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.ARCHIVED);
        public static final LifeCyclePhase APPROVED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.APPROVED);

        private imcode.server.document.LifeCyclePhase phase ;

        private LifeCyclePhase( imcode.server.document.LifeCyclePhase phase ) {
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

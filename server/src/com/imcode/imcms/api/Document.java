package com.imcode.imcms.api;

import com.imcode.util.ChainableReversibleNullComparator;
import com.imcode.util.CountingIterator;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.document.*;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleGetter;
import imcode.server.user.RoleId;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * The base class for all document types, such as {@link TextDocument}, {@link UrlDocument}, {@link FileDocument} etc.
 * In charge of document information such as headline, alias, publication status. Publication, archivation and setting as
 * unpublished dates.
 * Assignment of {@link Category}, {@link Section} and keywords.
 * Holds informations about document creationg and modification, the dates and {@link User}.
 */
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

    /**
     * Returns the unique identifier of this document in imcms
     * @return int, the id of this document
     */
    public int getId() {
        return internalDocument.getId();
    }

    /**
     * Returns a map of roles mapped to their permission set for this document.
     * For example Role "Users" has a permission set of type{@link DocumentPermissionSetType#RESTRICTED_1}, and some
     * privileges there.
     * @return map of roles {@link Role} -> {@link DocumentPermissionSet} instances.
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

    /**
     * Tests if the document is archived.
     * @return true if the document is archived, false otherwise.
     */
    public boolean isArchived() {
        return internalDocument.isArchived();
    }

    /**
     * Tests if the document is published and not archived.
     * @return true if the document is published and not archived, false otherwise.
     */
    public boolean isActive() {
        return internalDocument.isActive();
    }

    /**
     * Tests if the document is published.
     * @return true if the document is published, false otherwise.
     */
    public boolean isPublished() {
        return internalDocument.isPublished();
    }

    /**
     * Tests if search is disabled for this document.
     * Note that {@link DocumentService#getDocument(String)} will still find the document,
     * however the {@link DocumentService#getDocuments(SearchQuery)} won't.
     * @return true if search is disabled for this document, false otherwise.
     */
    public boolean isSearchDisabled() {
        return internalDocument.isSearchDisabled();
    }

    /**
     * Sets whether search is disabled for this document.
     * @param searchDisabled boolean, true to disable search, false to enable
     */
    public void setSearchDisabled(boolean searchDisabled) {
        internalDocument.setSearchDisabled(searchDisabled);
    }

    /**
     * Returns the most privileged DocumentPermissionSet of this document for current user.
     * The lowest priveled DocumentPermissionSet returned here is of type {@link DocumentPermissionSetType#NONE}
     * @return the most privileged DocumentPermissionSet of this document for current user or {@link DocumentPermissionSetType#NONE}
     */
    public DocumentPermissionSet getDocumentPermissionSetForUser() {
        return new DocumentPermissionSet( contentManagementSystem.getCurrentUser().getInternal().getPermissionSetFor( internalDocument ) );
    }

    /**
     * Checks whether this and the given Objects are the same.
     * Uses id as a criteria of equality.
     * @param o Object to compare with
     * @return true if the parameter is of class Document or it's subclass and their id attributes are the same.
     */
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

    /**
     * Returns the id of this document.
     * @return id of this document
     */
    public int hashCode() {
        return internalDocument.hashCode();
    }

    /**
     * Returns keywords of this document.
     * @return a Set of keywords assigned to this document
     */
    public Set getKeywords() {
        return internalDocument.getKeywords();
    }

    /**
     * Sets keywords for this document.
     * @param keywords a Set of keywords for this document.
     */
    public void setKeywords(Set keywords) {
        internalDocument.setKeywords(keywords);
    }

    /**
     * Returns this document's {@link DocumentPermissionSet} of type {@link DocumentPermissionSetType#RESTRICTED_1}
     * @return can't be null, this document's {@link DocumentPermissionSet} of type {@link DocumentPermissionSetType#RESTRICTED_1}
     */
    public DocumentPermissionSet getPermissionSetRestrictedOne() {
        DocumentPermissionSetDomainObject restrictedOne = internalDocument.getPermissionSets().getRestricted1() ;
        return new DocumentPermissionSet( restrictedOne );
    }

    /**
     * Returns this document's {@link DocumentPermissionSet} of type {@link DocumentPermissionSetType#RESTRICTED_2}
     * @return can't be null, this document's {@link DocumentPermissionSet} of type {@link DocumentPermissionSetType#RESTRICTED_2}
     */
    public DocumentPermissionSet getPermissionSetRestrictedTwo() {
        DocumentPermissionSetDomainObject restrictedTwo = internalDocument.getPermissionSets().getRestricted2() ;
        return new DocumentPermissionSet( restrictedTwo );
    }

    /**
     * Returns this document's headline.
     * @return headline or an empty String
     */
    public String getHeadline() {
        return internalDocument.getHeadline();
    }

    /**
     * Returns this document's menu text.
     * @return menu text or an empty String
     */
    public String getMenuText() {
        return internalDocument.getMenuText();
    }

    /**
     * Returns this document's alias.
     * @return this document's alias or null if doesn't exist
     */
    public String getAlias() {
        return  internalDocument.getAlias();
    }

    /**
     * Sets this document's alias
     * @param alias new alias or null to remove
     */
    public void setAlias(String alias) {
        internalDocument.setAlias(alias);
    }

    /**
     * Returns this document's alias if there's one, id otherwise.
     * @return id or alias, if no alias then id
     */
    public String getName() {
        return internalDocument.getName();
    }

    /**
     * Returns this document's menu image url
     * @return this document's menu image url, or an empty String if doesn't exist
     */
    public String getMenuImageURL() {
        return internalDocument.getMenuImage();
    }

    /**
     * Sets this document's headline.
     * @param headline new headline
     */
    public void setHeadline( String headline ) {
        internalDocument.setHeadline( headline );
    }

    /**
     * Sets this document's menu text
     * @param menuText new menu text
     */
    public void setMenuText( String menuText ) {
        internalDocument.setMenuText( menuText );
    }

    /**
     * Sets this document's menu image url
     * @param imageUrl new menu image url
     */
    public void setMenuImageURL( String imageUrl ) {
        internalDocument.setMenuImage( imageUrl );
    }

    /**
     * Returns the creator of this document
     * @return User or null if doesn't exist
     */
    public User getCreator() {
        int creatorId = internalDocument.getCreatorId();
        return contentManagementSystem.getUserService().getUser(creatorId) ;
    }

    /**
     * Returns the last user who modified this document.
     * @return a user who modified document or null if there is no such data or user can not be found.
     */
    public User getModifier() {
        Integer modifierId = internalDocument.getModifierId();
        return modifierId == null ? null : contentManagementSystem.getUserService().getUser(modifierId);
    }

    /**
     * Sets the creator of this document.
     * @param creator a User to be the new creator.
     */
    public void setCreator( User creator ) {
        internalDocument.setCreator( creator.getInternal() );
    }

    /**
     * Returns the languages set for this document.
     * @return {@link Language} set for this document
     */
    public Language getLanguage() {
        return Language.getLanguageByISO639_2( internalDocument.getLanguageIso639_2() );
    }

    /**
     * Adds a {@link Category} to this document.
     * @param category a category to add
     */
    public void addCategory( Category category ) {
        internalDocument.addCategoryId( category.getId() );
    }

    /**
     * Removes a {@link Category} from this document.
     * @param category a category to remove
     */
    public void removeCategory( Category category ) {
        internalDocument.removeCategoryId( category.getId() );
    }

    /**
     * Returns categories assigned to this document.
     * @return An array of Categories, an empty if none assigned.
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
     * Sets a {@link DocumentPermissionSetType} for a {@link Role} for this document
     * @param role a Role
     * @param documentPermissionSetType one of DocumentPermissionSetType constants
     * @since 3.0
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
     * Returns a {@link DocumentPermissionSetType} for the given {@link Role}
     * @return {@link DocumentPermissionSetType} for the given {@link Role} or {@link DocumentPermissionSetType#NONE} if none exist
     * @since 3.0
    */
    public DocumentPermissionSetType getPermissionSetTypeForRole( Role role ) {
        return new DocumentPermissionSetType(internalDocument.getDocumentPermissionSetTypeForRoleId(role.getInternal().getId())) ;
    }

    /**
     * Returns categories of the provided type assigned to this document.
     * @param categoryType CategoryType to get categories of
     * @return an array of Categories, empty array if no one found.
     */
    public Category[] getCategoriesOfType( CategoryType categoryType ) {
        CategoryMapper categoryMapper = contentManagementSystem.getInternal().getCategoryMapper();
        Set categoriesOfType = categoryMapper.getCategoriesOfType(categoryType.getInternal(), internalDocument.getCategoryIds());
        CategoryDomainObject[] categories = (CategoryDomainObject[]) categoriesOfType.toArray(new CategoryDomainObject[categoriesOfType.size()]);
        return getCategoryArrayFromCategoryDomainObjectArray( categories );
    }

    /**
     * Returns the publisher of this document
     * @return publisher User, or null if none
     */
    public User getPublisher() {
        Integer publisherId = internalDocument.getPublisherId();
        if ( null != publisherId ) {
            return contentManagementSystem.getUserService().getUser(publisherId.intValue()) ;
        } else {
            return null;
        }
    }

    /**
     * Returns a String represending html link target, such as '_blank' etc.
     * @return String representing html link target attribute
     */
    public String getTarget() {
        return internalDocument.getTarget();
    }

    /**
     * Returns the {@link java.util.Date} this document was published or the date it will be.
     * @return publication Date for/of this document
     */
    public Date getPublicationStartDatetime() {
        return internalDocument.getPublicationStartDatetime();
    }

    /**
     * Sets publication date for this document
     * @param datetime new publication {@link java.util.Date} or null to remove
     */
    public void setPublicationStartDatetime( Date datetime ) {
        internalDocument.setPublicationStartDatetime( datetime );
    }

    /**
     * Returns the archivation Date for this document.
     * @return archivation Date or null if not set.
     */
    public Date getArchivedDatetime() {
        return internalDocument.getArchivedDatetime();
    }

    /**
     * Sets archivation Date for this document.
     * @param datetime new archivation date
     */
    public void setArchivedDatetime( Date datetime ) {
        internalDocument.setArchivedDatetime( datetime );
    }

    /**
     * Sets this document's publisher
     * @param user a {@link User} to be this document's publisher
     */
    public void setPublisher( User user ) {
        internalDocument.setPublisher( user.getInternal() );
    }

    /**
     * Returns the sections assigned to this document
     * @return An array of {@link Section}, an empty array if none found.
     */
    public Section[] getSections() {
        Set sectionIds = internalDocument.getSectionIds();
        Section[] sections = new Section[sectionIds.size()];
        DocumentService documentService = contentManagementSystem.getDocumentService();
        for ( CountingIterator iterator = new CountingIterator(sectionIds.iterator()); iterator.hasNext(); ) {
            Integer sectionId = (Integer) iterator.next();
            sections[iterator.getCount() - 1] = documentService.getSection(sectionId.intValue()) ;
        }
        return sections;
    }

    /**
     * Sets sections for this document.
     * @param sections an array of {@link Section} for this document
     */
    public void setSections( Section[] sections ) {
        Set sectionIds = new HashSet();
        for ( int i = 0; i < sections.length; i++ ) {
            Section section = sections[i];
            int sectionId = section.getId();
            sectionIds.add(new Integer(sectionId)) ;
        }
        internalDocument.setSectionIds( sectionIds );
    }

    /**
     * Returns the last {@link java.util.Date} this document was modified.
     * @return the last date this document was modified
     */
    public Date getModifiedDatetime() {
        return internalDocument.getModifiedDatetime();
    }

    /**
     * Sets a modification date for this document.
     * @param date new modification date for this document
     */
    public void setModifiedDatetime( Date date ) {
        internalDocument.setModifiedDatetime( date );
    }

    /**
     * Returns the creation date of this document.
     * @return creation date
     */
    public Date getCreatedDatetime() {
        return internalDocument.getCreatedDatetime();
    }

    /**
     * Adds a {@link Section} to this document
     * @param section to add, can't be null
     */
    public void addSection( Section section ) {
        internalDocument.addSectionId( section.getId() );
    }

    /** @deprecated Use {@link #setPublicationStatus} instead. */
    public void setStatus( int status ) {
        internalDocument.setPublicationStatus( new PublicationStatus(status) );
    }

    /**
     * Sets the language of this document
     * @param language language for this document
     */
    public void setLanguage( Language language ) {
        internalDocument.setLanguageIso639_2( language.getIsoCode639_2() );
    }

    /**
     * Sets publication end date for this document
     * @param datetime publication end date
     */
    public void setPublicationEndDatetime( Date datetime ) {
        internalDocument.setPublicationEndDatetime( datetime );
    }

    /**
     * Returns publication date of this document
     * @return publication end date or null if not set
     */
    public Date getPublicationEndDatetime() {
        return internalDocument.getPublicationEndDatetime();
    }

    /** @deprecated Use {@link #getPublicationStatus} instead. */
    public int getStatus() {
        return internalDocument.getPublicationStatus().status ;
    }

    /**
     * Sets whether this document's link is visible in menus for anauthorized users
     * @param visibleInMenusForUnauthorizedUsers true to allow unauthorized users see this document's link in menus, false not to
     */
    public void setVisibleInMenusForUnauthorizedUsers( boolean visibleInMenusForUnauthorizedUsers ) {
        internalDocument.setLinkedForUnauthorizedUsers( visibleInMenusForUnauthorizedUsers );
    }

    /**
     * Tests if this document is visible in menus for anauthorized users
     * @return document visibility in menus for unauthorized users
     */
    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return internalDocument.isLinkedForUnauthorizedUsers();
    }

    /**
     * Tests whether other users can add this document to a menu.
     * Note that if a user can edit this document, they can also add it to a menu.
     * @return whether other users can add this document to a menu.
     */
    public boolean isLinkableByOtherUsers() {
        return internalDocument.isLinkableByOtherUsers();
    }

    /**
     * Sets whether other users can add this document to a menu.
     * Note that users that can edit this document can link it to a menu too too
     * @param linkableByOtherUsers true to allow, false not to
     */
    public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
        internalDocument.setLinkableByOtherUsers(linkableByOtherUsers);
    }

    /**
     * Returns the {@link PublicationStatus} of this document.
     * @return publication status of this document, defaults to {@link PublicationStatus#NEW}
     */
    public PublicationStatus getPublicationStatus() {
        return internalDocument.getPublicationStatus();
    }

    /**
     * Sets {@link PublicationStatus} of this document.
     * @param publicationStatus new publication status
     */
    public void setPublicationStatus(PublicationStatus publicationStatus) {
        internalDocument.setPublicationStatus(publicationStatus) ;
    }

    /**
     * Represents publication status of a {@link Document}
        @since 3.0
     */
    public static class PublicationStatus implements Serializable {
        /**
         * New publication status
         */
        public static final PublicationStatus NEW = new PublicationStatus(STATUS_NEW);

        /**
         * Approved publication status
         */
        public static final PublicationStatus APPROVED = new PublicationStatus(STATUS_PUBLICATION_APPROVED);

        /**
         * Disapproved publication status
         */
        public static final PublicationStatus DISAPPROVED = new PublicationStatus(STATUS_PUBLICATION_DISAPPROVED);

        private final int status;

        private PublicationStatus(int status) {
            this.status = status;
        }

        /**
         * Int values of this status as a string
         * @return Int values of this status as a string
         */
        public String toString() {
            return ""+status ;
        }

        /**
         * Compares two statuses
         * @param o Status to compare with
         * @return true if this and the argument have the same status
         */
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            return status == ( (PublicationStatus) o ).status;

        }

        /**
         * Returns int value of this PublicationStatus
         * @return status
         */
        public int hashCode() {
            return status;
        }
    }


    /**
     * Represents document's life cycle phase
     * @see <a href="http://doc.imcms.net/4.0/239" target="_blank">Page information</a>
     */
    public static class LifeCyclePhase implements Serializable {

        /**
         * New phase
         */
        public static final LifeCyclePhase NEW = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.NEW);

        /**
         * Disapproved phase
         */
        public static final LifeCyclePhase DISAPPROVED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.DISAPPROVED);

        /**
         * Published phase
         */
        public static final LifeCyclePhase PUBLISHED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.PUBLISHED);

        /**
         * Unpublished phase
         */
        public static final LifeCyclePhase UNPUBLISHED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.UNPUBLISHED);

        /**
         * Archived phase
         */
        public static final LifeCyclePhase ARCHIVED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.ARCHIVED);

        /**
         * Approved phase
         */
        public static final LifeCyclePhase APPROVED = new LifeCyclePhase(imcode.server.document.LifeCyclePhase.APPROVED);

        private imcode.server.document.LifeCyclePhase phase ;

        private LifeCyclePhase( imcode.server.document.LifeCyclePhase phase ) {
            this.phase = phase;
        }

        /**
         * Returns string representation of this life cycle phase
         * @return string representation in form of internal life cycle phase name, e.g. "published"
         */
        public String toString() {
            return phase.toString() ;
        }
    }

    /**
     * Abstract class in charge of {@link Document} comparison with a number of static comparators extending it for comparison by
     * different document attributes.
     */
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

        /**
         * Document id comparator
         */
        public final static Comparator ID = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getId() - d2.getId();
            }
        };

        /**
         * Case insensitive document headline comparator
         */
        public final static Comparator HEADLINE = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getHeadline().compareToIgnoreCase( d2.getHeadline() );
            }
        };

        /**
         * Creation date comparator
         */
        public final static Comparator CREATED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getCreatedDatetime().compareTo( d2.getCreatedDatetime() );
            }
        };

        /**
         * Modification date comparator
         */
        public final static Comparator MODIFIED_DATETIME = new Comparator() {
            protected int compareDocuments( Document d1, Document d2 ) {
                return d1.getModifiedDatetime().compareTo( d2.getModifiedDatetime() );
            }
        };

        /**
         * Publication start date comparator
         */
        public final static Comparator PUBLICATION_START_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getPublicationStartDatetime().compareTo( document2.getPublicationStartDatetime() );
            }
        };

        /**
         * Publication end date comparator
         */
        public final static Comparator PUBLICATION_END_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getPublicationEndDatetime().compareTo( document2.getPublicationEndDatetime() );
            }
        };

        /**
         * Archivation date comparator
         */
        public final static Comparator ARCHIVED_DATETIME = new Comparator() {
            protected int compareDocuments( Document document1, Document document2 ) {
                return document1.getArchivedDatetime().compareTo( document2.getArchivedDatetime() );
            }
        };

    }
}

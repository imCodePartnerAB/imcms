package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import com.imcode.util.ChainableReversibleNullComparator;
import imcode.server.Imcms;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.user.RoleGetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;

public class Document implements Serializable {

    private final static Logger log = LogManager.getLogger(Document.class.getName());
    private static final long serialVersionUID = -6934849355968513148L;
    private final DocumentDomainObject internalDocument;
    ContentManagementSystem contentManagementSystem;

    protected Document(DocumentDomainObject document, ContentManagementSystem contentManagementSystem) {
        this.internalDocument = document;
        this.contentManagementSystem = contentManagementSystem;
    }

    public DocumentDomainObject getInternal() {
        return internalDocument;
    }

    public int getId() {
        return internalDocument.getId();
    }

    /**
     * Whether the document is archived. *
     */
    public boolean isArchived() {
        return internalDocument.isArchived();
    }

    /**
     * Whether the document is published and not archived. *
     */
    public boolean isActive() {
        return internalDocument.isActive();
    }

    /**
     * Whether the document is published. *
     */
    public boolean isPublished() {
        return internalDocument.isPublished();
    }

    public boolean isSearchDisabled() {
        return internalDocument.isSearchDisabled();
    }

    public void setSearchDisabled(boolean searchDisabled) {
        internalDocument.setSearchDisabled(searchDisabled);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Document)) {
            return false;
        }
        final Document document = (Document) o;
        return internalDocument.equals(document.internalDocument);
    }

    public Language getLanguage() {
        return internalDocument.getLanguage();
    }

    public int getVersionNo() {
        return internalDocument.getVersionNo();
    }

    public int hashCode() {
        return internalDocument.hashCode();
    }

    public Set<String> getKeywords() {
        return internalDocument.getKeywords();
    }

    public void setKeywords(Set<String> keywords) {
        internalDocument.setKeywords(keywords);
    }

    public String getHeadline() {
        return internalDocument.getHeadline();
    }

    public void setHeadline(String headline) {
        internalDocument.setHeadline(headline);
    }

    public String getMenuText() {
        return internalDocument.getMenuText();
    }

    public void setMenuText(String menuText) {
        internalDocument.setMenuText(menuText);
    }

    public String getAlias() {
        return internalDocument.getAlias();
    }

    public void setAlias(String alias) {
        internalDocument.setAlias(alias);
    }

    public String getName() {
        return internalDocument.getName();
    }

    public User getCreator() {
        int creatorId = internalDocument.getCreatorId();
        return contentManagementSystem.getUserService().getUser(creatorId);
    }

    public void setCreator(User creator) {
        internalDocument.setCreator(creator.getInternal());
    }

    public void addCategory(Category category) {
        internalDocument.addCategory(category);
    }

    /**
     * @deprecated - use better {@link com.imcode.imcms.domain.dto.DocumentDTO#setCategories(Set)}
     */
    @Deprecated
    public void addCategory(com.imcode.imcms.api.Category category) {
        internalDocument.addCategory(category);
    }

    @SuppressWarnings("unused")
    public void setPermissionSetTypeForRole(Role role, Meta.Permission permission) {
        this.internalDocument.setDocumentPermissionSetTypeForRoleId(role.getInternal().getId(), permission);
    }

    @SuppressWarnings("unused")
    public Meta.Permission getPermissionSetIdForRole(Role role) {
        return this.internalDocument.getDocumentPermissionSetTypeForRoleId(role.getInternal().getId());
    }

    @SuppressWarnings("unused")
    public RestrictedPermission getDocumentPermissionSetForUser() {
        return Imcms.getServices().getAccessService().getPermission(Imcms.getUser(), getId());
    }

    /**
     * @deprecated - use better {@link DocumentDTO#getRestrictedPermissions()}
     */
    @Deprecated
    public DocumentPermissionSet getPermissionSetRestrictedOne() {
        return new DocumentPermissionSet(hasEditTextOption(Meta.Permission.RESTRICTED_1));
    }

    /**
     * @deprecated - use better {@link DocumentDTO#getRestrictedPermissions()}
     */
    @Deprecated
    public DocumentPermissionSet getPermissionSetRestrictedTwo() {
        return new DocumentPermissionSet(hasEditTextOption(Meta.Permission.RESTRICTED_2));
    }

    private boolean hasEditTextOption(Meta.Permission permission) {
        final Set<RestrictedPermissionJPA> permissions = internalDocument.getMeta().getRestrictedPermissions();

        final Optional<RestrictedPermissionJPA> permissionOptional = permissions.stream()
                .filter(permissionJPA -> permissionJPA.getPermission() == permission)
                .findFirst();

        return permissionOptional.orElse(new RestrictedPermissionJPA()).isEditText();
    }

    /**
     * @deprecated - use better {@link DocumentDTO#getRoleIdToPermission()}
     */
    @Deprecated
    public Map<Role, DocumentPermissionSet> getRolesMappedToPermissions() {
        final Map<Role, DocumentPermissionSet> rolesMappedToPermissions = new HashMap<>();

        final RoleGetter roleGetter = contentManagementSystem.getInternal().getRoleGetter();

        final RoleIdToDocumentPermissionSetTypeMappings permissionSetTypes =
                internalDocument.getRoleIdsMappedToDocumentPermissionSetTypes();

        Arrays.stream(permissionSetTypes.getMappings())
                .forEach(mapping -> {
                    final Role role = new Role(roleGetter.getRole(mapping.getRoleId()));
                    rolesMappedToPermissions.put(role, null);
                });

        return rolesMappedToPermissions;
    }

    /**
     * @return An array of Categories, an empty if no one found.
     * @deprecated - use better {@link CategoryService#getAll()}
     */
    @SuppressWarnings("unused")
    @Deprecated
    public com.imcode.imcms.api.Category[] getCategories() {
        Set<CategoryDomainObject> categories = contentManagementSystem.getInternal().getCategoryMapper().getCategories(internalDocument.getCategories());
        CategoryDomainObject[] categoryDomainObjects = categories.toArray(new CategoryDomainObject[categories.size()]);
        return getCategoryArrayFromCategoryDomainObjectArray(categoryDomainObjects);
    }

    private com.imcode.imcms.api.Category[] getCategoryArrayFromCategoryDomainObjectArray(
            CategoryDomainObject[] categoryDomainObjects) {

        return Arrays.stream(categoryDomainObjects)
                .map(com.imcode.imcms.api.Category::new)
                .toArray(com.imcode.imcms.api.Category[]::new);
    }

    public User getPublisher() {
        Integer publisherId = internalDocument.getPublisherId();
        if (null != publisherId) {
            return contentManagementSystem.getUserService().getUser(publisherId);
        } else {
            return null;
        }
    }

    public void setPublisher(User user) {
        internalDocument.setPublisher(user.getInternal());
    }

    public String getTarget() {
        return internalDocument.getTarget();
    }

    public Date getPublicationStartDatetime() {
        return internalDocument.getPublicationStartDatetime();
    }

    public void setPublicationStartDatetime(Date datetime) {
        internalDocument.setPublicationStartDatetime(datetime);
    }

    public Date getArchivedDatetime() {
        return internalDocument.getArchivedDatetime();
    }

    public void setArchivedDatetime(Date datetime) {
        internalDocument.setArchivedDatetime(datetime);
    }

    public Date getModifiedDatetime() {
        return internalDocument.getModifiedDatetime();
    }

    public void setModifiedDatetime(Date date) {
        internalDocument.setModifiedDatetime(date);
    }

    public Date getCreatedDatetime() {
        return internalDocument.getCreatedDatetime();
    }

    public Date getPublicationEndDatetime() {
        return internalDocument.getPublicationEndDatetime();
    }

    @SuppressWarnings("unused")
    public void setPublicationEndDatetime(Date datetime) {
        internalDocument.setPublicationEndDatetime(datetime);
    }

    /**
     * @deprecated Use {@link #getPublicationStatus} instead.
     */
    @Deprecated
    public int getStatus() {
        return internalDocument.getPublicationStatus().status;
    }

    /**
     * @deprecated Use {@link #setPublicationStatus} instead.
     */
    @Deprecated
    public void setStatus(int status) {
        internalDocument.setPublicationStatus(new PublicationStatus(status));
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return internalDocument.isVisible() || internalDocument.isLinkedForUnauthorizedUsers();
    }

    public void setVisibleInMenusForUnauthorizedUsers(boolean visibleInMenusForUnauthorizedUsers) {
        internalDocument.setLinkedForUnauthorizedUsers(visibleInMenusForUnauthorizedUsers);
    }

    public boolean isVisibleInMenusForAuthorizedUsers(User user) {
        return internalDocument.isLinkedForUnauthorizedUsers() || user.getInternal().canAccess(internalDocument);
    }

    public boolean isLinkableByOtherUsers() {
        return internalDocument.isLinkableByOtherUsers();
    }

    public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
        internalDocument.setLinkableByOtherUsers(linkableByOtherUsers);
    }

    public boolean isCacheForUnauthorizedUsers() {
        return internalDocument.isCacheForUnauthorizedUsers();
    }

    public void setCacheForUnauthorizedUsers(boolean cacheForUnauthorizedUsers) {
        internalDocument.setCacheForUnauthorizedUsers(cacheForUnauthorizedUsers);
    }

    public boolean isCacheForAuthorizedUsers() {
        return internalDocument.isCacheForAuthorizedUsers();
    }

    public void setCacheForAuthorizedUsers(boolean cacheForAuthorizedUsers) {
        internalDocument.setCacheForAuthorizedUsers(cacheForAuthorizedUsers);
    }

    public PublicationStatus getPublicationStatus() {
        return internalDocument.getPublicationStatus();
    }

    public void setPublicationStatus(PublicationStatus publicationStatus) {
        internalDocument.setPublicationStatus(publicationStatus);
    }

    @SuppressWarnings("unused")
    public void removeCategory(Category category) {
        internalDocument.removeCategoryId(category.getId());
    }

    /**
     * @deprecated Use {@link #removeCategory} instead.
     */
    @Deprecated
    public void removeCategory(com.imcode.imcms.api.Category category) {
        internalDocument.removeCategoryId(category.getId());
    }

    /**
     * @return an array of Categories, empty array if no one found.
     * @deprecated - use better
     * {@link CategoryService#getCategoriesByCategoryType(Integer)}
     */
    @SuppressWarnings("unused")
    @Deprecated
    public com.imcode.imcms.api.Category[] getCategoriesOfType(CategoryType categoryType) {
        CategoryMapper categoryMapper = contentManagementSystem.getInternal().getCategoryMapper();
        Set<CategoryDomainObject> categoriesOfType = categoryMapper.getCategoriesOfType(categoryType.getInternal(), internalDocument.getCategories());
        CategoryDomainObject[] categories = categoriesOfType.toArray(new CategoryDomainObject[categoriesOfType.size()]);
        return getCategoryArrayFromCategoryDomainObjectArray(categories);
    }

    @SuppressWarnings("serial")
    public abstract static class Comparator extends ChainableReversibleNullComparator<Document> {

        @SuppressWarnings("unused")
        public final static Comparator ID = new Comparator() {
            protected int compareDocuments(Document d1, Document d2) {
                return d1.getId() - d2.getId();
            }
        };
        @SuppressWarnings("unused")
        public final static Comparator HEADLINE = new Comparator() {
            protected int compareDocuments(Document d1, Document d2) {
                return d1.getHeadline().compareToIgnoreCase(d2.getHeadline());
            }
        };
        @SuppressWarnings("unused")
        public final static Comparator CREATED_DATETIME = new Comparator() {
            protected int compareDocuments(Document d1, Document d2) {
                return d1.getCreatedDatetime().compareTo(d2.getCreatedDatetime());
            }
        };
        @SuppressWarnings("unused")
        public final static Comparator MODIFIED_DATETIME = new Comparator() {
            protected int compareDocuments(Document d1, Document d2) {
                return d1.getModifiedDatetime().compareTo(d2.getModifiedDatetime());
            }
        };
        @SuppressWarnings("unused")
        public final static Comparator PUBLICATION_START_DATETIME = new Comparator() {
            protected int compareDocuments(Document document1, Document document2) {
                return document1.getPublicationStartDatetime().compareTo(document2.getPublicationStartDatetime());
            }
        };
        @SuppressWarnings("unused")
        public final static Comparator PUBLICATION_END_DATETIME = new Comparator() {
            protected int compareDocuments(Document document1, Document document2) {
                return document1.getPublicationEndDatetime().compareTo(document2.getPublicationEndDatetime());
            }
        };
        @SuppressWarnings("unused")
        public final static Comparator ARCHIVED_DATETIME = new Comparator() {
            protected int compareDocuments(Document document1, Document document2) {
                return document1.getArchivedDatetime().compareTo(document2.getArchivedDatetime());
            }
        };

        public int compare(Document d1, Document d2) {
            try {
                return compareDocuments(d1, d2);
            } catch (NullPointerException npe) {
                NullPointerException nullPointerException = new NullPointerException("Tried sorting on null fields! You need to call .nullsFirst() or .nullsLast() on your Comparator.");
                nullPointerException.initCause(npe);
                throw nullPointerException;
            }
        }

        protected abstract int compareDocuments(Document d1, Document d2);
    }

    /**
     * @since 3.0
     */
    public static class PublicationStatus implements Serializable {

        private static final int STATUS_NEW = 0;
        public static final PublicationStatus NEW = new PublicationStatus(STATUS_NEW);
        private static final int STATUS_PUBLICATION_DISAPPROVED = 1;
        public static final PublicationStatus DISAPPROVED = new PublicationStatus(STATUS_PUBLICATION_DISAPPROVED);
        private static final int STATUS_PUBLICATION_APPROVED = 2;
        public static final PublicationStatus APPROVED = new PublicationStatus(STATUS_PUBLICATION_APPROVED);
        private static final long serialVersionUID = -7360962799114550616L;
        private final int status;

        private PublicationStatus(int status) {
            this.status = status;
        }

        public static PublicationStatus of(int id) {
            switch (id) {
                case STATUS_NEW:
                    return NEW;
                case STATUS_PUBLICATION_APPROVED:
                    return APPROVED;
                case STATUS_PUBLICATION_DISAPPROVED:
                    return DISAPPROVED;
                default:
                    throw new IllegalArgumentException("Illegal status id: " + id);
            }
        }

        public String toString() {
            return "" + status;
        }

        public boolean equals(Object o) {
            return o == this || (o instanceof PublicationStatus && equals((PublicationStatus) o));
        }

        private boolean equals(PublicationStatus that) {
            return this.status == that.status;
        }

        public int hashCode() {
            return status;
        }

        public int asInt() {
            return status;
        }

        public Meta.PublicationStatus asEnum() {
            return Meta.PublicationStatus.values()[status];
        }
    }

}

package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentDomainObject;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Document implements Serializable {

    private final static Logger log = Logger.getLogger(Document.class.getName());
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

    public DocumentLanguage getLanguage() {
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

    public String getMenuImageURL() {
        return internalDocument.getMenuImage();
    }

    public void setMenuImageURL(String imageUrl) {
        internalDocument.setMenuImage(imageUrl);
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

    @SuppressWarnings("unused")
    public void setPermissionSetTypeForRole(Role role, Meta.Permission permission) {
        this.internalDocument.setDocumentPermissionSetTypeForRoleId(role.getInternal().getId(), permission);
    }

    /**
     * @return An array of Categories, an empty if no one found.
     */
    public Category[] getCategories() {
        Set<CategoryDomainObject> categories = contentManagementSystem.getInternal().getCategoryMapper().getCategories(internalDocument.getCategories());
        CategoryDomainObject[] categoryDomainObjects = categories.toArray(new CategoryDomainObject[categories.size()]);
        return getCategoryArrayFromCategoryDomainObjectArray(categoryDomainObjects);
    }

    private Category[] getCategoryArrayFromCategoryDomainObjectArray(CategoryDomainObject[] categoryDomainObjects) {
        Category[] categories = new Category[categoryDomainObjects.length];

        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject categoryDomainObject = categoryDomainObjects[i];
            categories[i] = new CategoryDTO(categoryDomainObject);
        }
        return categories;
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
    public int getStatus() {
        return internalDocument.getPublicationStatus().status;
    }

    /**
     * @deprecated Use {@link #setPublicationStatus} instead.
     */
    public void setStatus(int status) {
        internalDocument.setPublicationStatus(new PublicationStatus(status));
    }

    public boolean isVisibleInMenusForUnauthorizedUsers() {
        return internalDocument.isLinkedForUnauthorizedUsers();
    }

    public void setVisibleInMenusForUnauthorizedUsers(boolean visibleInMenusForUnauthorizedUsers) {
        internalDocument.setLinkedForUnauthorizedUsers(visibleInMenusForUnauthorizedUsers);
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
        internalDocument.setPublicationStatus(publicationStatus);
    }

    @SuppressWarnings("unused")
    public void removeCategory(Category category) {
        internalDocument.removeCategoryId(category.getId());
    }

    /**
     * @return an array of Categories, empty array if no one found.
     */
    @SuppressWarnings("unused")
    public Category[] getCategoriesOfType(CategoryType categoryType) {
        CategoryMapper categoryMapper = contentManagementSystem.getInternal().getCategoryMapper();
        Set<CategoryDomainObject> categoriesOfType = categoryMapper.getCategoriesOfType(categoryType.getInternal(), internalDocument.getCategories());
        CategoryDomainObject[] categories = categoriesOfType.toArray(new CategoryDomainObject[categoriesOfType.size()]);
        return getCategoryArrayFromCategoryDomainObjectArray(categories);
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

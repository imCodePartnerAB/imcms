package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Meta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocumentDTO extends Document implements Serializable {

    protected static final long serialVersionUID = -1197329246115859534L;

    protected Meta.DocumentType type;

    private Integer id;
    private String target;
    private String alias;
    private List<CommonContentDTO> commonContents;
    private Meta.PublicationStatus publicationStatus;
    private AuditDTO published;
    private AuditDTO archived;
    private AuditDTO publicationEnd;
    private AuditDTO modified;
    private AuditDTO created;
    private Meta.DisabledLanguageShowMode disabledLanguageShowMode;
    private AuditDTO currentVersion;
    private Set<String> keywords;
    private boolean searchDisabled;
    private Set<CategoryDTO> categories;
    private Set<RestrictedPermissionDTO> restrictedPermissions;
    private Map<String, String> properties;
    private Map<Integer, Meta.Permission> roleIdToPermission;

    public DocumentDTO(Document from) {
        super(from);
    }

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @ConstructorProperties({"commonContents", "categories", "restrictedPermissions"})
    public DocumentDTO(List<CommonContentDTO> commonContents,
                       Set<CategoryDTO> categories,
                       Set<RestrictedPermissionDTO> restrictedPermissions) {
        this.categories = categories;
        this.restrictedPermissions = restrictedPermissions;
        this.commonContents = commonContents;
    }

    public List<CommonContent> getCommonContents() {
        return (commonContents == null) ? null : new ArrayList<>(commonContents);
    }

    public void setCommonContents(List<CommonContent> commonContents) {
        this.commonContents = (commonContents == null) ? null
                : commonContents.stream().map(CommonContentDTO::new).collect(Collectors.toList());
    }

    public Set<Category> getCategories() {
        return (categories == null) ? null : new HashSet<>(categories);
    }

    public void setCategories(Set<Category> categories) {
        this.categories = (categories == null) ? null
                : categories.stream().map(CategoryDTO::new).collect(Collectors.toSet());
    }

    public Set<RestrictedPermission> getRestrictedPermissions() {
        return (this.restrictedPermissions == null) ? null : new TreeSet<>(this.restrictedPermissions);
    }

    public void setRestrictedPermissions(Set<RestrictedPermission> restrictedPermissions) {
        this.restrictedPermissions = (restrictedPermissions == null) ? null
                : restrictedPermissions.stream()
                .map(RestrictedPermissionDTO::new)
                .collect(Collectors.toCollection(TreeSet::new));
    }

}

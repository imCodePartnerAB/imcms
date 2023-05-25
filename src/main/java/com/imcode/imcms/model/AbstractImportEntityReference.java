package com.imcode.imcms.model;

import com.imcode.imcms.persistence.entity.ImportCategoryReferenceJPA;
import com.imcode.imcms.persistence.entity.ImportCategoryTypeReferenceJPA;
import com.imcode.imcms.persistence.entity.ImportRoleReferenceJPA;
import com.imcode.imcms.persistence.entity.ImportTemplateReferenceJPA;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class AbstractImportEntityReference implements Serializable {
	@Serial
	private static final long serialVersionUID = 8299642540619409566L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;

	@Column(name = "name", nullable = false, unique = true)
	protected String name;

	@Column(name = "linked_entity_id")
	protected Integer linkedEntityId;

	@Transient
	protected ImportEntityReferenceType type;

	public AbstractImportEntityReference(String name, ImportEntityReferenceType type) {
		this(null, name, null, type);
	}

	public AbstractImportEntityReference(Integer id, String name, Integer linkedEntityId, ImportEntityReferenceType type) {
		setId(id);
		setName(name);
		setLinkedEntityId(linkedEntityId);
		setType(type);
	}

	public AbstractImportEntityReference(AbstractImportEntityReference from) {
		if (from == null) return;

		setId(from.getId());
		setName(from.getName());
		setLinkedEntityId(from.getLinkedEntityId());
		setType(from.getType());
	}

	public static <T extends AbstractImportEntityReference> AbstractImportEntityReference of(T entityReference) {
		return switch (entityReference.getType()) {
			case ROLE -> new ImportRoleReferenceJPA(entityReference);
			case CATEGORY -> new ImportCategoryReferenceJPA(entityReference);
			case TEMPLATE -> new ImportTemplateReferenceJPA(entityReference);
			case CATEGORY_TYPE -> new ImportCategoryTypeReferenceJPA(entityReference);
		};
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractImportEntityReference> T toTyped() {
		return switch (getType()) {
			case ROLE -> (T) new ImportRoleReferenceJPA(this);
			case CATEGORY -> (T) new ImportCategoryReferenceJPA(this);
			case TEMPLATE -> (T) new ImportTemplateReferenceJPA(this);
			case CATEGORY_TYPE -> (T) new ImportCategoryTypeReferenceJPA(this);
		};
	}
}

package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.AbstractImportEntityReference;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serial;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "import_category_type_references")
public class ImportCategoryTypeReferenceJPA extends AbstractImportEntityReference {
	@Serial
	private static final long serialVersionUID = -7688116188809147552L;

	public ImportCategoryTypeReferenceJPA() {
		this.type = ImportEntityReferenceType.CATEGORY_TYPE;
	}

	public ImportCategoryTypeReferenceJPA(AbstractImportEntityReference from) {
		super(from);
	}

	public ImportCategoryTypeReferenceJPA(String name) {
		super(name, ImportEntityReferenceType.CATEGORY_TYPE);
	}

}

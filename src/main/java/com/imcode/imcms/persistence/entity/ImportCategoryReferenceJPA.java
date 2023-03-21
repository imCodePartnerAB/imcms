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
@Table(name = "import_category_references")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ImportCategoryReferenceJPA extends AbstractImportEntityReference {
	@Serial
	private static final long serialVersionUID = -6608715953857760262L;

	public ImportCategoryReferenceJPA() {
		this.type = ImportEntityReferenceType.CATEGORY;
	}

	public ImportCategoryReferenceJPA(AbstractImportEntityReference from) {
		super(from);
	}

	public ImportCategoryReferenceJPA(String name) {
		super(name, ImportEntityReferenceType.CATEGORY);
	}

}

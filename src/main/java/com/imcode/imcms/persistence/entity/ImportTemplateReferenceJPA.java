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
@Table(name = "import_template_references")
public class ImportTemplateReferenceJPA extends AbstractImportEntityReference {
	@Serial
	private static final long serialVersionUID = -3599874247238796468L;

	public ImportTemplateReferenceJPA() {
		this.type = ImportEntityReferenceType.TEMPLATE;
	}

	public ImportTemplateReferenceJPA(AbstractImportEntityReference from) {
		super(from);
	}

	public ImportTemplateReferenceJPA(String name) {
		super(name, ImportEntityReferenceType.TEMPLATE);
	}

}

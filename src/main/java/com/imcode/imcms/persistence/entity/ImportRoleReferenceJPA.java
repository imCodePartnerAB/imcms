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
@Table(name = "import_role_references")

public class ImportRoleReferenceJPA extends AbstractImportEntityReference {
	@Serial
	private static final long serialVersionUID = 7295610521979409269L;

	public ImportRoleReferenceJPA() {
		this.type = ImportEntityReferenceType.ROLE;
	}

	public ImportRoleReferenceJPA(AbstractImportEntityReference from) {
		super(from);
	}

	public ImportRoleReferenceJPA(String name) {
		super(name, ImportEntityReferenceType.ROLE);
	}

}

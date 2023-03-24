package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.ImportDocumentStatus;
import com.imcode.imcms.model.BasicImportDocumentInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "basic_import_documents_info")
public class BasicImportDocumentInfoJPA extends BasicImportDocumentInfo {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "meta_id")
	private Integer metaId;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ImportDocumentStatus status;

	public BasicImportDocumentInfoJPA(BasicImportDocumentInfo from) {
		super(from);
	}

	public BasicImportDocumentInfoJPA(Integer id, ImportDocumentStatus status) {
		this.id = id;
		this.status = status;
	}
}

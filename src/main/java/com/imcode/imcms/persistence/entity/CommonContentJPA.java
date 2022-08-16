package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.DocumentMetadata;
import com.imcode.imcms.model.Language;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Content common to all document types.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "imcms_doc_i18n_meta")
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommonContentJPA extends CommonContent {

	private static final int META_HEADLINE_MAX_LENGTH = 255;
	private static final int META_TEXT_MAX_LENGTH = 1000;
	private static final long serialVersionUID = 9218236106612630843L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "doc_id")
	private Integer docId;

	@Column(name = "alias")
	private String alias;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id", referencedColumnName = "id")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private LanguageJPA language;

	/**
	 * Doc's headline label. Mainly used as HTML page title.
	 */
	@Column(name = "headline")
	private String headline;

	/**
	 * Doc's metadata. Used in HTML meta tags.
	 */
	@CollectionTable(name = "imcms_doc_metadata", joinColumns = @JoinColumn(name = "imcms_doc_i18n_meta_id"))
	@ElementCollection(fetch = FetchType.LAZY, targetClass = DocumentMetadataJPA.class)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<DocumentMetadataJPA> documentMetadataList = new ArrayList<>();

	/**
	 * Menu item label.
	 * Used when a doc is included in other doc's menu (as a menu item).
	 */
	@Column(name = "menu_text", length = META_TEXT_MAX_LENGTH)
	private String menuText;

	/**
	 * Flag indicates is current language enabled for document
	 */
	@Column(name = "is_enabled")
	private boolean isEnabled;

	/**
	 * Related document version number
	 */
	@Column(name = "version_no", nullable = false)
	private Integer versionNo;

	public CommonContentJPA(Integer docId, String alias, LanguageJPA language, String headline, String menuText,
	                        Boolean isEnabled, Integer versionNo) {
		this.docId = docId;
		this.alias = alias;
		this.language = language;
		this.headline = headline;
		this.menuText = menuText;
		this.isEnabled = isEnabled;
		this.versionNo = versionNo;
	}

	public CommonContentJPA(CommonContent from) {
		super(from);
	}

	@Override
	public void setDocumentMetadataList(List<? extends DocumentMetadata> documentMetadataList) {
		this.documentMetadataList = (documentMetadataList == null) ? Collections.emptyList() :
				documentMetadataList.stream().map(DocumentMetadataJPA::new).collect(Collectors.toList());
	}

	@Override
	public void setLanguage(Language language) {
		this.language = (language == null) ? null : new LanguageJPA(language);
	}
}

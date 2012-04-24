package com.imcode
package imcms.dao

import imcode.server.document.DocumentDomainObject
import org.apache.commons.lang.StringUtils
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.{DocumentProperty, Meta, I18nMeta, I18nLanguage}
import com.imcode.imcms.mapping.orm.{FileReference, HtmlReference, Include, TemplateNames, UrlReference}
import imcode.server.user.UserDomainObject
import java.util.Date

@Transactional(rollbackFor = Array(classOf[Throwable]))
class MetaDao extends HibernateSupport {

  private val META_HEADLINE_MAX_LENGTH = 255
  private val META_TEXT_MAX_LENGTH = 1000

  def getMeta(docId: JInteger): Meta = hibernate.get(docId)

  /**  Updates doc's access and modified date-time. */
  def touch(doc: DocumentDomainObject, user: UserDomainObject): Unit = touch(doc, new Date, user)

  def touch(doc: DocumentDomainObject, dt:Date, user: UserDomainObject): Unit =
    touch(doc.getIdValue, doc.getVersionNo, doc.getModifiedDatetime, user.getId)

  def touch(docId: JInteger, docVersionNo: JInteger, userId: JInteger): Unit =
    touch(docId, docVersionNo, new Date, userId)

  def touch(docId: JInteger, docVersionNo: JInteger, dt: Date, userId: JInteger) {
    hibernate.bulkUpdateByNamedParams(
      "UPDATE Meta m SET m.modifiedDatetime = :modifiedDt WHERE m.id = :docId",

      "modifiedDt" -> dt,
      "docId" -> docId
    )

    hibernate.bulkUpdateByNamedParams(
      """UPDATE DocumentVersion v SET v.modifiedDt = :modifiedDt, v.modifiedBy = :modifiedBy
         WHERE v.docId = :docId AND v.no = :docVersionNo""",

      "modifiedDt" -> dt,
      "modifiedBy" -> userId,
      "docId" -> docId,
      "docVersionNo" -> docVersionNo
    )
  }

  def getDocumentIdByAlias(alias: String): JInteger = hibernate.getByNamedQueryAndNamedParams(
    "DocumentProperty.getDocumentIdByAlias",

    "name" -> DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS,
    "value" -> alias.toLowerCase
  )

  def getI18nMeta(docId: JInteger, language: I18nLanguage): I18nMeta =
    hibernate.getByNamedQueryAndNamedParams[I18nMeta](
      "I18nMeta.getByDocIdAndLanguageId", "docId" -> docId, "languageId" -> language.getId
    ) |> ? getOrElse new I18nMeta |< { i18nMeta =>
      i18nMeta.setDocId(docId)
      i18nMeta.setLanguage(language)
      i18nMeta.setHeadline("")
      i18nMeta.setMenuText("")
      i18nMeta.setMenuImageURL("")
    }


  def getI18nMetas(docId: JInteger): JList[I18nMeta] = hibernate.listByNamedQueryAndNamedParams(
    "I18nMeta.getByDocId", "docId" -> docId
  )

  def deleteI18nMeta(docId: JInteger, languageId: JInteger) = hibernate.bulkUpdateByNamedQueryAndNamedParams(
    "I18nMeta.deleteByDocIdAndLanguageId", "docId" -> docId, "languageId" -> languageId
  )

  def saveI18nMeta(i18nMeta: I18nMeta): I18nMeta = {
    val headline = i18nMeta.getHeadline
    val text = i18nMeta.getMenuText

    val headlineThatFitsInDB = headline.take(java.lang.Math.min(headline.length, META_HEADLINE_MAX_LENGTH - 1))
    val textThatFitsInDB = text.take(java.lang.Math.min(text.length, META_TEXT_MAX_LENGTH - 1))

    i18nMeta.setHeadline(headlineThatFitsInDB)
    i18nMeta.setMenuText(textThatFitsInDB)

    hibernate.saveOrUpdate(i18nMeta)
  }


  def insertPropertyIfNotExists(docId: JInteger, name: String, value: String): Boolean =
    hibernate.getByNamedQueryAndNamedParams[DocumentProperty](
      "DocumentProperty.getProperty", "docId" -> docId, "name" -> name
    ) |> ? getOrElse new DocumentProperty |< { property =>
      property.setDocId(docId)
      property.setName(name)
    } match {
      case property if StringUtils.isBlank(property.getValue) =>
        property.setValue(value)
        hibernate.saveOrUpdate(property)
        true

      case _ => false
    }


  def saveMeta(meta: Meta) = hibernate.saveOrUpdate(meta)


  def deleteIncludes(docId: JInteger) = hibernate.bulkUpdate("delete from Include i where i.metaId = ?", docId)


  def saveInclude(include: Include) = hibernate.saveOrUpdate(include)


  def deleteHtmlReference(docId: JInteger, docVersionNo: JInteger) = hibernate.bulkUpdate(
    "delete from HtmlReference r where r.docId = ? AND r.docVersionNo = ?", docId, docVersionNo
  )



  def deleteUrlReference(docId: JInteger, docVersionNo: JInteger) = hibernate.bulkUpdate(
    "delete from UrlReference r where r.docId = ? AND r.docVersionNo = ?", docId, docVersionNo
  )



  def saveTemplateNames(templateNames: TemplateNames) = hibernate.merge(templateNames)


  def getIncludes(docId: JInteger): JList[Include] =
    hibernate.listByQuery("select i from Include i where i.metaId = ?", docId)


  def getTemplateNames(docId: JInteger): TemplateNames = hibernate.get(docId)


  def deleteTemplateNames(docId: JInteger) = hibernate.bulkUpdateByNamedParams(
    "DELETE FROM TemplateNames n WHERE n.docId = :docId", "docId" -> docId
  )


  def getFileReferences(docId: JInteger, docVersionNo: JInteger): JList[FileReference] =
    hibernate.listByNamedQueryAndNamedParams(
      "FileDoc.getReferences", "docId" -> docId, "docVersionNo" -> docVersionNo
    )


  def saveFileReference(fileRef: FileReference) = hibernate.saveOrUpdate(fileRef)


  def deleteFileReferences(docId: JInteger, docVersionNo: JInteger) = hibernate.bulkUpdateByNamedQueryAndNamedParams(
    "FileDoc.deleteAllReferences", "docId" -> docId, "docVersionNo" -> docVersionNo
  )


  def getHtmlReference(docId: JInteger, docVersionNo: JInteger): HtmlReference = hibernate.getByNamedQueryAndNamedParams(
    "HtmlDoc.getReference", "docId" -> docId, "docVersionNo" -> docVersionNo
  )


  def saveHtmlReference(reference: HtmlReference) = hibernate.saveOrUpdate(reference)


  def getUrlReference(docId: JInteger, docVersionNo: JInteger): UrlReference = hibernate.getByNamedQueryAndNamedParams(
    "UrlDoc.getReference", "docId" -> docId, "docVersionNo" -> docVersionNo
  )


  def saveUrlReference(reference: UrlReference) = hibernate.merge(reference)


  def getAllAliases(): JList[String] = hibernate.listByNamedQueryAndNamedParams(
    "DocumentProperty.getAllAliases",

    "name" -> DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS
  )



  def getAliasProperty(alias: String): DocumentProperty  = hibernate.getByNamedQueryAndNamedParams(
    "DocumentProperty.getAliasProperty",

    "name" -> DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS,
    "value" -> alias
  )


  def getDocIdByAliasOpt(alias: String) = getAliasProperty(alias) |> ? map(_.getDocId.toInt)


  def deleteDocument(docId: JInteger): Unit = hibernate.withSession { session =>
    List(
      "DELETE FROM document_categories WHERE meta_id = ?",
      "DELETE FROM imcms_text_doc_menu_items WHERE to_doc_id = ?",
      "DELETE FROM imcms_text_doc_menu_items WHERE menu_id IN (SELECT doc_id FROM imcms_text_doc_menus WHERE doc_id = ?)",
      "DELETE FROM imcms_text_doc_menus WHERE doc_id = ?",
      "DELETE FROM text_docs WHERE meta_id = ?",
      "DELETE FROM imcms_text_doc_texts WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_images WHERE doc_id = ?",
      "DELETE FROM roles_rights WHERE meta_id = ?",
      "DELETE FROM user_rights WHERE meta_id = ?",
      "DELETE FROM imcms_url_docs WHERE doc_id = ?",
      "DELETE FROM fileupload_docs WHERE meta_id = ?",
      "DELETE FROM imcms_html_docs WHERE doc_id = ?",
      "DELETE FROM new_doc_permission_sets_ex WHERE meta_id = ?",
      "DELETE FROM new_doc_permission_sets WHERE meta_id = ?",
      "DELETE FROM doc_permission_sets_ex WHERE meta_id = ?",
      "DELETE FROM doc_permission_sets WHERE meta_id = ?",
      "DELETE FROM includes WHERE meta_id = ?",
      "DELETE FROM includes WHERE included_meta_id = ?",
      "DELETE FROM imcms_text_doc_texts_history WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_images_history WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_menu_items_history WHERE to_doc_id = ?",
      "DELETE FROM imcms_text_doc_menu_items_history WHERE menu_id IN (SELECT menu_id FROM imcms_text_doc_menus_history WHERE doc_id = ?)",
      "DELETE FROM imcms_text_doc_menus_history WHERE doc_id = ?",
      "DELETE FROM document_properties WHERE meta_id = ?",
      "DELETE FROM imcms_doc_i18n_meta WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_contents WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_content_loops WHERE doc_id = ?",
      "DELETE FROM imcms_doc_languages WHERE doc_id = ?",
      "DELETE FROM imcms_doc_keywords WHERE doc_id = ?",
      "DELETE FROM imcms_doc_versions WHERE doc_id = ?",
      "DELETE FROM meta WHERE meta_id = ?"
    ) foreach { session.createSQLQuery(_).setParameter(0, docId).executeUpdate() }
  }


  def getAllDocumentIds(): JList[JInteger] = hibernate.listByNamedQuery("Meta.getAllDocumentIds")


  def getDocumentIdsInRange(min: JInteger, max: JInteger): JList[JInteger] = hibernate.listByNamedQueryAndNamedParams(
    "Meta.getDocumentIdsInRange", "min" -> min, "max" -> max
  )


  def getMaxDocumentId(): JInteger = hibernate.getByNamedQuery("Meta.getMaxDocumentId")


  def getMinDocumentId(): JInteger = hibernate.getByNamedQuery("Meta.getMinDocumentId")


  // todo: check
  def getMinMaxDocumentIds(): Array[JInteger] =
    hibernate.getByNamedQuery("Meta.getMinMaxDocumentIds")


  def getEnabledLanguages(docId: JInteger) = sys.error("Not implemented")
}





/*
public class MetaDao extends HibernateTemplate {

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

	/**
	 * @return Meta.
	 */
	@Transactional
	public Meta getMeta(Integer docId) {
		return (Meta)get(Meta.class, docId);
	}

	@Transactional
	public Integer getDocumentIdByAlias(String alias) {
		return (Integer)getSession().getNamedQuery("DocumentProperty.getDocumentIdByAlias")
			.setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
			.setParameter("value", alias.toLowerCase())
			.uniqueResult();
	}

	@Transactional
	public I18nMeta getI18nMeta(Integer docId, I18nLanguage language) {
		I18nMeta labels = (I18nMeta)getSession().getNamedQuery("I18nMeta.getByDocIdAndLanguageId")
                .setParameter("docId", docId)
                .setParameter("languageId", language.getId())
                .uniqueResult();

        if (labels == null) {
            labels = new I18nMeta();
            labels.setDocId(docId);
            labels.setLanguage(language);
            labels.setHeadline("");
            labels.setMenuText("");
            labels.setMenuImageURL("");
        }

        return labels;
	}


	@Transactional
	public List<I18nMeta> getI18nMetas(Integer docId) {
        return (List<I18nMeta>)findByNamedQueryAndNamedParam("I18nMeta.getByDocId", "docId", docId);
	}


	@Transactional
	public void deleteI18nMeta(Integer docId, Integer LanguageId) {
		getSession().getNamedQuery("I18nMeta.deleteByDocIdAndLanguageId")
                .setParameter("docId", docId)
                .setParameter("languageId", LanguageId)
                .executeUpdate();
	}

    //@Transactional
    public I18nMeta saveI18nMeta(I18nMeta i18nMeta) {
        String headline = i18nMeta.getHeadline();
        String text = i18nMeta.getMenuText();

        String headlineThatFitsInDB = headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        String textThatFitsInDB = text.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));

        i18nMeta.setHeadline(headlineThatFitsInDB);
        i18nMeta.setMenuText(textThatFitsInDB);

        saveOrUpdate(i18nMeta);

        return i18nMeta;
    }

    //@Transactional
    public boolean insertPropertyIfNotExists(Integer docId, String name, String value) {
        DocumentProperty property = (DocumentProperty)getSession()
                .getNamedQuery("DocumentProperty.getProperty")
                .setParameter("docId", docId)
                .setParameter("name", name)
                .uniqueResult();

        if (property != null) {
            if (StringUtils.isNotBlank(property.getValue())) {
                return false;
            }
        } else {
            property = new DocumentProperty();
            property.setDocId(docId);
            property.setName(name);
        }

        property.setValue(value);

        saveOrUpdate(property);

        return true;
    }





    //@Transactional
	public void saveMeta(Meta meta) {
		saveOrUpdate(meta);
	}





    //@Transactional
	public int deleteIncludes(Integer docId) {
		return bulkUpdate("delete from Include i where i.metaId = ?", docId);
	}

 	@Transactional
	public void saveInclude(Include include) {
		saveOrUpdate(include);
	}


 	@Transactional
	public void deleteHtmlReference(Integer docId, Integer docVersionNo) {
		bulkUpdate("delete from HtmlReference r where r.docId = ? AND r.docVersionNo = ?", new Object [] {docId, docVersionNo});
	}

 	@Transactional
	public void deleteUrlReference(Integer docId, Integer docVersionNo) {
		bulkUpdate("delete from UrlReference r where r.docId = ? AND r.docVersionNo = ?", new Object [] {docId, docVersionNo});
	}

	@Transactional
	public void saveTemplateNames(TemplateNames templateNames) {
		merge(templateNames);
	}

	@Transactional
	public Collection<Include> getIncludes(Integer documentId) {
		return (Collection<Include>) find("select i from Include i where i.metaId = ?", documentId);
	}

	@Transactional
	public TemplateNames getTemplateNames(Integer docId) {
        return (TemplateNames)get(TemplateNames.class, docId);
	}

    //@Transactional
    public int deleteTemplateNames(Integer docId) {
        return getSession().createQuery("DELETE FROM TemplateNames n WHERE n.docId = :docId")
                .setParameter("docId", docId)
                .executeUpdate();
    }

	@Transactional
	public Collection<FileReference> getFileReferences(Integer docId, Integer docVersionNo) {
		return findByNamedQueryAndNamedParam("FileDoc.getReferences", new String [] {"docId", "docVersionNo"},
                new Object [] {docId, docVersionNo});
	}

	@Transactional
	public FileReference saveFileReference(FileReference fileRef) {
		saveOrUpdate(fileRef);

		return fileRef;
	}

	@Transactional
	public int deleteFileReferences(Integer docId, Integer docVersionNo) {
        return getSession().getNamedQuery("FileDoc.deleteAllReferences")
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .executeUpdate();
	}

	@Transactional
	public HtmlReference getHtmlReference(Integer docId, Integer docVersionNo) {
		return (HtmlReference)getSession().getNamedQuery("HtmlDoc.getReference")
		    .setParameter("docId", docId)
            .setParameter("docVersionNo", docVersionNo)
		    .uniqueResult();
	}

	@Transactional
	public HtmlReference saveHtmlReference(HtmlReference reference) {
		saveOrUpdate(reference);

		return reference;
	}

	@Transactional
	public UrlReference getUrlReference(Integer docId, Integer docVersionNo) {
		return (UrlReference)getSession().getNamedQuery("UrlDoc.getReference")
		    .setParameter("docId", docId)
            .setParameter("docVersionNo", docVersionNo)
		    .uniqueResult();
	}

	@Transactional
	public UrlReference saveUrlReference(UrlReference reference) {
		merge(reference);

		return reference;
	}






	@Transactional
	public List<String> getAllAliases() {
		return findByNamedQueryAndNamedParam(
				"DocumentProperty.getAllAliases", "name",
				DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
	}


	@Transactional
	public DocumentProperty getAliasProperty(String alias) {
		return (DocumentProperty)getSession().getNamedQuery("DocumentProperty.getAliasProperty")
			.setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
			.setParameter("value", alias)
			.uniqueResult();
	}


	@Transactional
	public void deleteDocument(final Integer metaId) {
		String[] sqls = {
			"DELETE FROM document_categories WHERE meta_id = ?",
			"DELETE FROM imcms_text_doc_menu_items WHERE to_doc_id = ?",
			"DELETE FROM imcms_text_doc_menu_items WHERE menu_id IN (SELECT doc_id FROM imcms_text_doc_menus WHERE doc_id = ?)",
			"DELETE FROM imcms_text_doc_menus WHERE doc_id = ?",
			"DELETE FROM text_docs WHERE meta_id = ?",
			"DELETE FROM imcms_text_doc_texts WHERE doc_id = ?",
			"DELETE FROM imcms_text_doc_images WHERE doc_id = ?",
			"DELETE FROM roles_rights WHERE meta_id = ?",
			"DELETE FROM user_rights WHERE meta_id = ?",
			"DELETE FROM imcms_url_docs WHERE doc_id = ?",
			"DELETE FROM fileupload_docs WHERE meta_id = ?",
			"DELETE FROM imcms_html_docs WHERE doc_id = ?",
			"DELETE FROM new_doc_permission_sets_ex WHERE meta_id = ?",
			"DELETE FROM new_doc_permission_sets WHERE meta_id = ?",
			"DELETE FROM doc_permission_sets_ex WHERE meta_id = ?",
			"DELETE FROM doc_permission_sets WHERE meta_id = ?",
			"DELETE FROM includes WHERE meta_id = ?",
			"DELETE FROM includes WHERE included_meta_id = ?",
			"DELETE FROM imcms_text_doc_texts_history WHERE doc_id = ?",
			"DELETE FROM imcms_text_doc_images_history WHERE doc_id = ?",
			"DELETE FROM imcms_text_doc_menu_items_history WHERE to_doc_id = ?",
			"DELETE FROM imcms_text_doc_menu_items_history WHERE menu_id IN (SELECT menu_id FROM imcms_text_doc_menus_history WHERE doc_id = ?)",
			"DELETE FROM imcms_text_doc_menus_history WHERE doc_id = ?",
			"DELETE FROM document_properties WHERE meta_id = ?",
			"DELETE FROM imcms_doc_i18n_meta WHERE doc_id = ?",
            "DELETE FROM imcms_text_doc_contents WHERE doc_id = ?",
            "DELETE FROM imcms_text_doc_content_loops WHERE doc_id = ?",
            "DELETE FROM imcms_doc_languages WHERE doc_id = ?",
            "DELETE FROM imcms_doc_keywords WHERE doc_id = ?",
            "DELETE FROM imcms_doc_versions WHERE doc_id = ?",
			"DELETE FROM meta WHERE meta_id = ?",
		};

		Session session = getSession();

		for (String sql: sqls) {
			int i = session.createSQLQuery(sql).setParameter(0, metaId).executeUpdate();
		}
	}

	@Transactional
	public List<Integer> getAllDocumentIds() {
		return (List<Integer>)getSession().getNamedQuery("Meta.getAllDocumentIds")
			.listByNamedParams();
	}

	@Transactional
	public List<Integer> getDocumentIdsInRange(Integer min, Integer max) {
		return (List<Integer>)getSession().getNamedQuery("Meta.getDocumentIdsInRange")
			.setParameter("min", min)
			.setParameter("max", max)
			.listByNamedParams();
	}

	@Transactional
	public Integer getMaxDocumentId() {
		return (Integer)getSession().getNamedQuery("Meta.getMaxDocumentId")
			.uniqueResult();
	}

	@Transactional
	public Integer getMinDocumentId() {
		return (Integer)getSession().getNamedQuery("Meta.getMinDocumentId")
			.uniqueResult();
	}

	@Transactional
	public Integer[] getMinMaxDocumentIds() {
	    Object[] tuple = (Object[]) getSession().getNamedQuery("Meta.getMinMaxDocumentIds")
	        .uniqueResult();

	    return new Integer[] {
	            (Integer) tuple[0],
	            (Integer) tuple[1]
	    };
	}

    // TODO: REMOVE!!!
    // TEMP!!!
    @Override
    public Session getSession() {
        return super.getSession();
    }

    //@Transactional
    public List<I18nLanguage> getEnabledLanguages(Integer docId) {
        //getSession().createQuery("SELECT FROM ")
        return null;
    }

*/
package com.imcode
package imcms.dao

import imcode.server.document.DocumentDomainObject
import org.apache.commons.lang.StringUtils
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.{DocumentProperty, Meta, I18nMeta, I18nLanguage}
import com.imcode.imcms.mapping.orm.{FileReference, HtmlReference, Include, TemplateNames, UrlReference}
import imcode.server.user.UserDomainObject
import java.util.Date
import imcode.server.document.textdocument.DocRef

@Transactional(rollbackFor = Array(classOf[Throwable]))
class MetaDao extends HibernateSupport {

  private val META_HEADLINE_MAX_LENGTH = 255
  private val META_TEXT_MAX_LENGTH = 1000

  def getMeta(docId: Int) = hibernate.get[Meta](docId)

  /**  Updates doc's access and modified date-time. */
  def touch(docRef: DocRef, user: UserDomainObject): Unit = touch(docRef, user, new Date)
  def touch(docRef: DocRef, user: UserDomainObject, date: Date): Unit =
    touch(docRef.getDocId, docRef.getDocVersionNo, user.getId, date)

  private def touch(docId: Int, docVersionNo: Int, userId: Int, dt: Date) {
    hibernate.bulkUpdateByNamedParams(
      "UPDATE Meta m SET m.modifiedDatetime = :modifiedDt WHERE m.id = :docId",

      "modifiedDt" -> dt,
      "docId" -> docId
    )

    hibernate.bulkUpdateByNamedParams(
      """|UPDATE DocumentVersion v SET v.modifiedDt = :modifiedDt, v.modifiedBy = :modifiedBy
         |WHERE v.docId = :docId AND v.no = :docVersionNo""".stripMargin,

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

  def getI18nMeta(docId: Int, language: I18nLanguage): I18nMeta =
    hibernate.getByNamedQueryAndNamedParams[I18nMeta](
      "I18nMeta.getByDocIdAndLanguageId", "docId" -> docId, "languageId" -> language.getId
    ) |> opt getOrElse I18nMeta.builder() |> {
      _.docId(docId)
       .language(language)
       .headline("")
       .menuText("")
       .menuImageURL("")
       .build()
    }


  def getI18nMetas(docId: Int): JList[I18nMeta] = hibernate.listByNamedQueryAndNamedParams(
    "I18nMeta.getByDocId", "docId" -> docId
  )

  def deleteI18nMeta(docId: Int, languageId: Int) = hibernate.bulkUpdateByNamedQueryAndNamedParams(
    "I18nMeta.deleteByDocIdAndLanguageId", "docId" -> docId, "languageId" -> languageId
  )

  def saveI18nMeta(i18nMeta: I18nMeta): I18nMeta = {
    val headline = i18nMeta.getHeadline
    val text = i18nMeta.getMenuText

    val headlineThatFitsInDB = headline.take(java.lang.Math.min(headline.length, META_HEADLINE_MAX_LENGTH - 1))
    val textThatFitsInDB = text.take(java.lang.Math.min(text.length, META_TEXT_MAX_LENGTH - 1))

    I18nMeta.builder(i18nMeta) |> {
      _.headline(headlineThatFitsInDB)
       .menuText(textThatFitsInDB)
       .build()
    } |> hibernate.saveOrUpdate
  }


  def insertPropertyIfNotExists(docId: Int, name: String, value: String): Boolean =
    hibernate.getByNamedQueryAndNamedParams[DocumentProperty](
      "DocumentProperty.getProperty", "docId" -> docId, "name" -> name
    ) |> opt getOrElse new DocumentProperty |>> { property =>
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


  def deleteIncludes(docId: Int) = hibernate.bulkUpdate("delete from Include i where i.metaId = ?", docId)


  def saveInclude(include: Include) = hibernate.saveOrUpdate(include)


  def deleteHtmlReference(docRef: DocRef) = hibernate.bulkUpdate(
    "delete from HtmlReference r where r.docRef = ?", docRef
  )



  def deleteUrlReference(docRef: DocRef) = hibernate.bulkUpdate(
    "delete from UrlReference r where r.docRef = ?", docRef
  )



  def saveTemplateNames(templateNames: TemplateNames) = hibernate.merge(templateNames)


  def getIncludes(docId: Int): JList[Include] =
    hibernate.listByQuery("select i from Include i where i.metaId = ?", docId)


  def getTemplateNames(docId: Int) = hibernate.get[TemplateNames](docId)


  def deleteTemplateNames(docId: Int) = hibernate.bulkUpdateByNamedParams(
    "DELETE FROM TemplateNames n WHERE n.docId = :docId", "docId" -> docId
  )


  def getFileReferences(docRef: DocRef): JList[FileReference] =
    hibernate.listByNamedQueryAndNamedParams(
      "FileDoc.getReferences", "docRef" -> docRef
    )


  def saveFileReference(fileRef: FileReference) = hibernate.saveOrUpdate(fileRef)


  def deleteFileReferences(docRef: DocRef) = hibernate.bulkUpdateByNamedQueryAndNamedParams(
    "FileDoc.deleteAllReferences", "docRef" -> docRef
  )


  def getHtmlReference(docRef: DocRef): HtmlReference = hibernate.getByNamedQueryAndNamedParams(
    "HtmlDoc.getReference", "docRef" -> docRef
  )


  def saveHtmlReference(reference: HtmlReference) = hibernate.saveOrUpdate(reference)


  def getUrlReference(docRef: DocRef): UrlReference = hibernate.getByNamedQueryAndNamedParams(
    "UrlDoc.getReference", "docRef" -> docRef
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


  def getDocIdByAliasOpt(alias: String) = getAliasProperty(alias) |> opt map(_.getDocId.toInt)


  def deleteDocument(docId: Int): Unit = hibernate.withCurrentSession { session =>
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


  def getDocumentIdsInRange(min: Int, max: Int): JList[JInteger] = hibernate.listByNamedQueryAndNamedParams(
    "Meta.getDocumentIdsInRange", "min" -> min, "max" -> max
  )


  def getMaxDocumentId(): Int = hibernate.getByNamedQuery("Meta.getMaxDocumentId")


  def getMinDocumentId(): Int = hibernate.getByNamedQuery("Meta.getMinDocumentId")


  def getMinMaxDocumentIds(): Array[JInteger] =
    hibernate.getByNamedQuery("Meta.getMinMaxDocumentIds")


  def getEnabledLanguages(docId: Int) = sys.error("Not implemented")
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
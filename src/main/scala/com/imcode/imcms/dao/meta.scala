package com.imcode
package imcms.dao

import imcode.server.document.DocumentDomainObject
import org.apache.commons.lang.StringUtils
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.{DocumentProperty, Meta, I18nMeta, I18nLanguage}
import com.imcode.imcms.mapping.orm.{FileReference, HtmlReference, Include, TemplateNames, UrlReference}
import java.util.Date
import imcode.server.user.UserDomainObject

class MetaDao extends SpringHibernateTemplate {

  val META_HEADLINE_MAX_LENGTH = 255
  val META_TEXT_MAX_LENGTH = 1000

  @Transactional
  def getMeta(docId: JInteger) = hibernateTemplate.get(classOf[Meta], docId)

  /**  Updates doc's access and modified date-time. */
  @Transactional
  def touch(doc: DocumentDomainObject, user: UserDomainObject) = withSession { session =>
    session.createQuery("UPDATE Meta m SET m.modifiedDatetime = :modifiedDt WHERE m.id = :docId")
      .setParameter("modifiedDt", doc.getModifiedDatetime)
      .setParameter("docId", doc.getIdValue)
      .executeUpdate()

    session.createQuery(
      """UPDATE DocumentVersion v SET v.modifiedDt = :modifiedDt, v.modifiedBy = :modifiedBy
         WHERE v.docId = :docId AND v.no = :docVersionNo""")
      .setParameter("modifiedDt", doc.getModifiedDatetime)
      .setParameter("modifiedBy", Int box user.getId)
      .setParameter("docId", doc.getIdValue)
      .setParameter("docVersionNo", doc.getVersionNo)
      .executeUpdate()
  }

  @Transactional
  def getDocumentIdByAlias(alias: String) = withSession {
    _.getNamedQuery("DocumentProperty.getDocumentIdByAlias")
     .setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
     .setParameter("value", alias.toLowerCase())
     .uniqueResult().asInstanceOf[JInteger]
  }

  @Transactional
  def getI18nMeta(docId: JInteger, language: I18nLanguage) =
    ?(withSession {
      _.getNamedQuery("I18nMeta.getByDocIdAndLanguageId")
       .setParameter("docId", docId)
       .setParameter("languageId", language.getId())
       .uniqueResult().asInstanceOf[I18nMeta]
    }) getOrElse letret(new I18nMeta) { i18nMeta =>
      i18nMeta.setDocId(docId);
      i18nMeta.setLanguage(language);
      i18nMeta.setHeadline("");
      i18nMeta.setMenuText("");
      i18nMeta.setMenuImageURL("")
    }


  @Transactional
  def getI18nMetas(docId: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("I18nMeta.getByDocId", "docId", docId).asInstanceOf[JList[I18nMeta]]


  @Transactional
  def deleteI18nMeta(docId: JInteger, languageId: JInteger) = withSession {
    _.getNamedQuery("I18nMeta.deleteByDocIdAndLanguageId")
     .setParameter("docId", docId)
     .setParameter("languageId", languageId)
     .executeUpdate()
  }

  @Transactional
  def saveI18nMeta(i18nMeta: I18nMeta) = letret(i18nMeta) { _ =>
    val headline = i18nMeta.getHeadline
    val text = i18nMeta.getMenuText

    val headlineThatFitsInDB = headline.take(java.lang.Math.min(headline.length, META_HEADLINE_MAX_LENGTH - 1))
    val textThatFitsInDB = text.take(java.lang.Math.min(text.length, META_TEXT_MAX_LENGTH - 1))

    i18nMeta.setHeadline(headlineThatFitsInDB)
    i18nMeta.setMenuText(textThatFitsInDB)

    hibernateTemplate.saveOrUpdate(i18nMeta)
  }

  @Transactional
  def insertPropertyIfNotExists(docId: JInteger, name: String, value: String) =
    ?(withSession {
      _.getNamedQuery("DocumentProperty.getProperty")
       .setParameter("docId", docId)
       .setParameter("name", name)
       .uniqueResult().asInstanceOf[DocumentProperty]
    }) getOrElse letret(new DocumentProperty) { property =>
      property.setDocId(docId)
      property.setName(name)
    } match {
      case property if StringUtils.isBlank(property.getValue) =>
        property.setValue(value)
        hibernateTemplate.saveOrUpdate(property)
        true

      case _ => false
    }

  @Transactional
  def saveMeta(meta: Meta) = hibernateTemplate.saveOrUpdate(meta)

  @Transactional
  def deleteIncludes(docId: JInteger) =
    hibernateTemplate.bulkUpdate("delete from Include i where i.metaId = ?", docId)

  @Transactional
  def saveInclude(include: Include) = hibernateTemplate.saveOrUpdate(include)

  @Transactional
  def deleteHtmlReference(docId: JInteger, docVersionNo: JInteger) = hibernateTemplate.
    bulkUpdate("delete from HtmlReference r where r.docId = ? AND r.docVersionNo = ?", Array[AnyRef](docId, docVersionNo))


  @Transactional
  def deleteUrlReference(docId: JInteger, docVersionNo: JInteger) = hibernateTemplate.
    bulkUpdate("delete from UrlReference r where r.docId = ? AND r.docVersionNo = ?", Array[AnyRef](docId, docVersionNo))


  @Transactional
  def saveTemplateNames(templateNames: TemplateNames) = hibernateTemplate.merge(templateNames)

  @Transactional
  def getIncludes(docId: JInteger) = hibernateTemplate.find("select i from Include i where i.metaId = ?", docId).
    asInstanceOf[JList[Include]]

  @Transactional
  def getTemplateNames(docId: JInteger) = hibernateTemplate.get(classOf[TemplateNames], docId)

  @Transactional
  def deleteTemplateNames(docId: JInteger) = withSession {
    _.createQuery("DELETE FROM TemplateNames n WHERE n.docId = :docId")
     .setParameter("docId", docId)
     .executeUpdate()
  }

  @Transactional
  def getFileReferences(docId: JInteger, docVersionNo: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("FileDoc.getReferences",
      Array("docId", "docVersionNo"),
      Array[AnyRef](docId, docVersionNo)).asInstanceOf[JList[FileReference]]

  @Transactional
  def saveFileReference(fileRef: FileReference) = letret(fileRef) {
    hibernateTemplate.saveOrUpdate
  }

  @Transactional
  def deleteFileReferences(docId: JInteger, docVersionNo: JInteger) = withSession {
    _.getNamedQuery("FileDoc.deleteAllReferences")
     .setParameter("docId", docId)
     .setParameter("docVersionNo", docVersionNo)
     .executeUpdate()
  }

  @Transactional
  def getHtmlReference(docId: JInteger, docVersionNo: JInteger) = withSession {
    _.getNamedQuery("HtmlDoc.getReference")
     .setParameter("docId", docId)
     .setParameter("docVersionNo", docVersionNo)
     .uniqueResult().asInstanceOf[HtmlReference]
  }

  @Transactional
  def saveHtmlReference(reference: HtmlReference) = letret(reference) {
    hibernateTemplate.saveOrUpdate
  }

  @Transactional
  def getUrlReference(docId: JInteger, docVersionNo: JInteger) = withSession {
    _.getNamedQuery("UrlDoc.getReference")
     .setParameter("docId", docId)
     .setParameter("docVersionNo", docVersionNo)
     .uniqueResult().asInstanceOf[UrlReference]
  }

  @Transactional
  def saveUrlReference(reference: UrlReference) = hibernateTemplate.merge(reference)

  @Transactional
  def getAllAliases() = hibernateTemplate.findByNamedQueryAndNamedParam(
    "DocumentProperty.getAllAliases", "name",
    DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS).asInstanceOf[JList[String]]


  @Transactional
  def getAliasProperty(alias: String) = withSession {
    _.getNamedQuery("DocumentProperty.getAliasProperty")
     .setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
     .setParameter("value", alias)
     .uniqueResult().asInstanceOf[DocumentProperty]
  }


  @Transactional
  def deleteDocument(docId: JInteger) = withSession { session =>
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

  @Transactional
  def getAllDocumentIds() = withSession{
    _.getNamedQuery("Meta.getAllDocumentIds").list.asInstanceOf[JList[JInteger]]
  }

  @Transactional
  def getDocumentIdsInRange(min: JInteger, max: JInteger) = withSession {
    _.getNamedQuery("Meta.getDocumentIdsInRange")
     .setParameter("min", min)
     .setParameter("max", max)
     .list.asInstanceOf[JList[JInteger]]
  }

  @Transactional
  def getMaxDocumentId() = withSession {
    _.getNamedQuery("Meta.getMaxDocumentId").uniqueResult().asInstanceOf[JInteger]
  }

  @Transactional
  def getMinDocumentId() = withSession {
    _.getNamedQuery("Meta.getMinDocumentId").uniqueResult().asInstanceOf[JInteger]
  }

  @Transactional
  def getMinMaxDocumentIds() = withSession {
    _.getNamedQuery("Meta.getMinMaxDocumentIds")
     .uniqueResult()
     .asInstanceOf[Array[AnyRef]]
  } map {
    _.asInstanceOf[JInteger]
  }

  @Transactional
  def getEnabledLanguages(docId: JInteger) = error("Not implemented")
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

    @Transactional
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

    @Transactional
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





    @Transactional
	public void saveMeta(Meta meta) {
		saveOrUpdate(meta);
	}





    @Transactional
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

    @Transactional
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
			.list();
	}

	@Transactional
	public List<Integer> getDocumentIdsInRange(Integer min, Integer max) {
		return (List<Integer>)getSession().getNamedQuery("Meta.getDocumentIdsInRange")
			.setParameter("min", min)
			.setParameter("max", max)
			.list();
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

    @Transactional
    public List<I18nLanguage> getEnabledLanguages(Integer docId) {
        //getSession().createQuery("SELECT FROM ")
        return null;
    }

*/
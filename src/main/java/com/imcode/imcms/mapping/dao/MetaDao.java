package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.DocRef;
import com.imcode.imcms.mapping.DocVersionRef;
import com.imcode.imcms.mapping.orm.*;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import scala.Option;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Transactional
public class MetaDao {

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

    @PersistenceContext
    private EntityManager entityManager;

    public DocMeta getMeta(int docId) {
        return entityManager.find(DocMeta.class, docId);
    }

    /**
     * Updates doc's access and modified date-time.
     */
    public void touch(DocRef docIdentity, UserDomainObject user) {
        touch(docIdentity, user, new Date());
    }

    public void touch(DocRef docIdentity, UserDomainObject user, Date date) {
        touch(docIdentity.getDocId(), docIdentity.getDocVersionNo(), user.getId(), date);
    }

    public void touch(DocVersionRef docIdentity, UserDomainObject user) {
        touch(docIdentity, user, new Date());
    }

    public void touch(DocVersionRef docIdentity, UserDomainObject user, Date date) {
        touch(docIdentity.getDocId(), docIdentity.getDocVersionNo(), user.getId(), date);
    }

    private void touch(int docId, int docVersionNo, int userId, Date dt) {
        entityManager.createQuery("UPDATE Meta m SET m.modifiedDatetime = :modifiedDt WHERE m.id = :docId")
                .setParameter("modifiedDt", dt)
                .setParameter("docId", docId)
                .executeUpdate();

        entityManager.createQuery("UPDATE DocVersion v SET v.modifiedDt = :modifiedDt, v.modifiedBy = :modifiedBy WHERE v.docId = :docId AND v.no = :docVersionNo")
                .setParameter("modifiedDt", dt)
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .executeUpdate();
    }


    public Integer getDocumentIdByAlias(String alias) {
        return entityManager.createQuery("DocumentProperty.getDocumentIdByAlias", Integer.class)
                .setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
                .setParameter("value", alias.toLowerCase())
                .getSingleResult();
    }


    public DocCommonContent getDocAppearance(DocRef docRef) {
        return entityManager.createQuery("DocumentProperty.getDocumentIdByAlias", DocCommonContent.class)
                .setParameter("docId", docRef.getDocId())
                .setParameter("languageCode", docRef.getDocLanguageCode())
                .getSingleResult();
    }


    public List<DocCommonContent> getAppearance(int docId) {
        return entityManager.createQuery("DocAppearance.getByDocId", DocCommonContent.class)
                .setParameter("docId", docId)
                .getResultList();
    }


    public int deleteAppearance(DocRef docRef) {
        DocCommonContent dcc = getDocAppearance(docRef);

        if (dcc == null) return 0;

        entityManager.remove(dcc);

        return 1;
    }


    public DocCommonContent saveAppearance(DocCommonContent docAppearance) {
        String headline = docAppearance.getHeadline();
        String text = docAppearance.getMenuText();

        String headlineThatFitsInDB = headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        String textThatFitsInDB = headline.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));

        docAppearance.setHeadline(headlineThatFitsInDB);
        docAppearance.setMenuText(textThatFitsInDB);

        return entityManager.merge(docAppearance);
    }


    public boolean insertPropertyIfNotExists(int docId, String name, String value) {
        DocProperty property = entityManager.createNamedQuery("DocumentProperty.getProperty", DocProperty.class)
                .setParameter("docId", docId)
                .setParameter("name", name)
                .getSingleResult();

        if (property == null) {
            property = new DocProperty();
            property.setDocId(docId);
            property.setName(name);
            property.setValue(value);
            entityManager.persist(property);
            return true;
        } else if (StringUtils.isBlank(property.getValue())) {
            property.setValue(value);
            return true;
        }

        return false;
    }


    public DocMeta saveMeta(DocMeta meta) {
        return entityManager.merge(meta);
    }


    public int deleteIncludes(int docId) {
        return entityManager.createQuery("delete Include i where i.metaId = ?1")
                .setParameter(1, docId)
                .executeUpdate();
    }


    public Include saveInclude(Include include) {
        return entityManager.merge(include);
    }


    public int deleteHtmlReference(DocRef docIdentity) {
        return entityManager.createQuery("delete from HtmlReference r where r.docIdentity = :docIdentity")
                .setParameter("docIdentity", docIdentity)
                .executeUpdate();
    }


    public int deleteUrlReference(DocRef docIdentity) {
        return entityManager.createQuery("delete from UrlReference r where r.docIdentity = :docIdentity")
                .setParameter("docIdentity", docIdentity)
                .executeUpdate();
    }

    public TemplateNames saveTemplateNames(TemplateNames templateNames) {
        return entityManager.merge(templateNames);
    }


    public List<Include> getIncludes(int docId) {
        return entityManager.createQuery("select i from Include i where i.metaId = ?1", Include.class)
                .setParameter(1, docId)
                .getResultList();
    }


    public TemplateNames getTemplateNames(int docId) {
        return entityManager.find(TemplateNames.class, docId);
    }


    public int deleteTemplateNames(int docId) {
        return entityManager.createQuery("DELETE FROM TemplateNames n WHERE n.docId = :docId")
                .setParameter("docId", docId)
                .executeUpdate();
    }


    public List<FileDocItem> getFileDocItems(DocRef docIdentity) {
        return entityManager.createNamedQuery("FileDoc.getReferences", FileDocItem.class)
                .setParameter("docIdentity", docIdentity)
                .getResultList();
    }


    public FileDocItem saveFileReference(FileDocItem fileDocItem) {
        return entityManager.merge(fileDocItem);
    }


    public int deleteFileReferences(DocRef docIdentity) {
        return entityManager.createNamedQuery("FileDoc.deleteAllReferences")
                .setParameter("docIdentity", docIdentity)
                .executeUpdate();
    }


    public HtmlDocContent getHtmlDocContent(DocRef docIdentity) {
        return entityManager.createNamedQuery("HtmlDoc.getReference", HtmlDocContent.class)
                .setParameter("docIdentity", docIdentity)
                .getSingleResult();
    }


    public HtmlDocContent saveHtmlReference(HtmlDocContent reference) {
        return entityManager.merge(reference);
    }


    public UrlDocContent getUrlDocContent(DocRef docIdentity) {
        return entityManager.createNamedQuery("UrlDoc.getReference", UrlDocContent.class)
                .setParameter("docIdentity", docIdentity)
                .getSingleResult();
    }


    public UrlDocContent saveUrlReference(UrlDocContent reference) {
        return entityManager.merge(reference);
    }


    public List<String> getAllAliases() {
        return entityManager.createNamedQuery("DocumentProperty.getAllAliases", String.class)
                .setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
                .getResultList();
    }


    public DocProperty getAliasProperty(String alias) {
        return entityManager.createNamedQuery("DocumentProperty.getAliasProperty", DocProperty.class)
                .setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
                .setParameter("value", alias)
                .getSingleResult();

    }

    public Option<Integer> getDocIdByAliasOpt(String alias) {
        DocProperty property = getAliasProperty(alias);

        return Option.apply(property.getDocId());
    }


    public void deleteDocument(int docId) {
        List<String> queries = Arrays.asList(
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
        );

        for (String query : queries) {
            entityManager.createNativeQuery(query).setParameter(0, docId).executeUpdate();
        }
    }


    public List<Integer> getAllDocumentIds() {
        return entityManager.createNamedQuery("Meta.getAllDocumentIds", Integer.class).getResultList();
    }


    public List<Integer> getDocumentIdsInRange(int min, int max) {
        return entityManager.createNamedQuery("Meta.getDocumentIdsInRange", Integer.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }


    public Integer getMaxDocumentId() {
        return entityManager.createNamedQuery("Meta.getMaxDocumentId", Integer.class).getSingleResult();
    }


    public Integer getMinDocumentId() {
        return entityManager.createNamedQuery("Meta.getMinDocumentId", Integer.class).getSingleResult();
    }


    public Integer[] getMinMaxDocumentIds() {
//    hibernate.getByNamedQuery[Array[Object]]("Meta.getMinMaxDocumentIds") |> { pair =>
//      Array(pair(0).asInstanceOf[JInteger], pair(1).asInstanceOf[JInteger])
//    }
        throw new NotImplementedException();
    }


    public void getEnabledLanguages(int docId) {
        throw new NotImplementedException();
    }
}
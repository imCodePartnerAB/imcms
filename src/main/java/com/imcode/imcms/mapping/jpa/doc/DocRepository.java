package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.DocVersionRef;
import com.imcode.imcms.mapping.jpa.doc.content.FileDocItem;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.mapping.jpa.doc.content.UrlDocContent;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import scala.Option;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class DocRepository {

    @Inject
    private MetaRepository metaRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @PersistenceContext
    private EntityManager entityManager;

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


    public Integer getDocIdByAlias(String alias) {
        return entityManager.createQuery("DocumentProperty.getDocumentIdByAlias", Integer.class)
                .setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
                .setParameter("value", alias.toLowerCase())
                .getSingleResult();
    }



    public void insertPropertyIfNotExists(int docId, String name, String value) {
        Property property = propertyRepository.findByDocIdAndName(docId, name);

        if (property == null) {
            property = new Property();
            property.setDocId(docId);
            property.setName(name);
            property.setValue(value);
        } else if (StringUtils.isBlank(property.getValue())) {
            property.setValue(value);
        }
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


    public Property getAliasProperty(String alias) {
        return entityManager.createNamedQuery("DocumentProperty.getAliasProperty", Property.class)
                .setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
                .setParameter("value", alias)
                .getSingleResult();

    }

    public Option<Integer> getDocIdByAliasOpt(String alias) {
        Property property = getAliasProperty(alias);

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
        return metaRepository.findAllIds();
    }


    public List<Integer> getDocumentIdsInRange(int min, int max) {
        return metaRepository.findIdsBetween(min, max);
    }


    public Integer getMaxDocumentId() {
        return metaRepository.findMaxId();
    }


    public Integer getMinDocumentId() {
        return metaRepository.findMinId();
    }

    public Integer[] getMinMaxDocumentIds() {
        return metaRepository.findMinAndMaxId();
    }
}
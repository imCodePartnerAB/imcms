package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContentRepository;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Transactional
public class DocRepository {

    private final MetaRepository metaRepository;
    private final PropertyRepository propertyRepository;
    private final HtmlDocContentRepository htmlDocContentRepository;
    private final DocumentUrlRepository documentUrlRepository;
    private final DocumentFileRepository documentFileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = LogManager.getLogger(getClass());

    public DocRepository(MetaRepository metaRepository, PropertyRepository propertyRepository,
                         HtmlDocContentRepository htmlDocContentRepository,
                         DocumentUrlRepository documentUrlRepository, DocumentFileRepository documentFileRepository) {
        this.metaRepository = metaRepository;
        this.propertyRepository = propertyRepository;
        this.htmlDocContentRepository = htmlDocContentRepository;
        this.documentUrlRepository = documentUrlRepository;
        this.documentFileRepository = documentFileRepository;
    }

    public void touch(VersionRef docIdentity, UserDomainObject user) {
        touch(docIdentity, user, new Date());
    }

    public void touch(VersionRef docIdentity, UserDomainObject user, Date date) {
        touch(docIdentity.getDocId(), docIdentity.getNo(), user.getId(), date);
    }

    private void touch(int docId, int docVersionNo, int userId, Date dt) {
        entityManager.createQuery("UPDATE Meta m SET m.modifiedDatetime = :modifiedDt WHERE m.id = :docId")
                .setParameter("modifiedDt", dt)
                .setParameter("docId", docId)
                .executeUpdate();

        entityManager.createQuery("UPDATE Version v SET v.modifiedDt = :modifiedDt, v.modifiedBy.id = :modifiedBy WHERE v.docId = :docId AND v.no = :docVersionNo")
                .setParameter("modifiedBy", userId)
                .setParameter("modifiedDt", dt)
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .executeUpdate();
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

    public void deleteHtmlDocContent(DocRef docIdentity) {
        htmlDocContentRepository.deleteByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());
    }

    public List<DocumentFileJPA> getFileDocContent(DocRef docIdentity) {
        return documentFileRepository.findByDocId(docIdentity.getId());
    }


    public DocumentFileJPA saveFileDocFile(DocumentFileJPA fileDocItem) {
        return entityManager.merge(fileDocItem);
    }

    public void deleteFileDocContent(DocRef docIdentity) {
        List<DocumentFileJPA> documentFile = documentFileRepository.findByDocId(docIdentity.getId());

        if (documentFile.size() == 0) {
            return;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<DocumentFileJPA> query = cb.createCriteriaDelete(DocumentFileJPA.class);
        query.where(query.from(DocumentFileJPA.class).get("id").in(documentFile.stream().map(DocumentFileJPA::getId).collect(Collectors.toList())));

        entityManager.createQuery(query).executeUpdate();
    }

    public HtmlDocContent getHtmlDocContent(DocRef docIdentity) {
        return htmlDocContentRepository.findByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());
    }


    public HtmlDocContent saveHtmlDocContent(HtmlDocContent content) {
        return entityManager.merge(content);
    }

    public DocumentUrlJPA getUrlDocContent(DocRef docIdentity) {
        return documentUrlRepository.findByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());
    }


    public DocumentUrlJPA saveUrlDocContent(DocumentUrlJPA reference) {
        return entityManager.merge(reference);
    }

    public void deleteDocument(int docId) {
        Stream.of(
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
                "DELETE FROM fileupload_docs WHERE doc_id = ?",
                "DELETE FROM imcms_html_docs WHERE doc_id = ?",
                "DELETE FROM includes WHERE meta_id = ?",
                "DELETE FROM includes WHERE included_meta_id = ?",
                "DELETE FROM imcms_text_doc_texts_history WHERE doc_id = ?",
                "DELETE FROM imcms_text_doc_images_history WHERE doc_id = ?",
                "DELETE FROM imcms_text_doc_menu_items_history WHERE to_doc_id = ?",
                "DELETE FROM imcms_text_doc_menu_items_history WHERE menu_id IN (SELECT menu_id FROM imcms_text_doc_menus_history WHERE doc_id = ?)",
                "DELETE FROM imcms_text_doc_menus_history WHERE doc_id = ?",
                "DELETE FROM document_properties WHERE meta_id = ?",
                "DELETE FROM imcms_doc_i18n_meta WHERE doc_id = ?",
                "DELETE FROM imcms_text_doc_contents WHERE loop_id IN (SELECT id FROM imcms_text_doc_content_loops WHERE doc_id = ?)",
                "DELETE FROM imcms_text_doc_content_loops WHERE doc_id = ?",
                "DELETE FROM imcms_doc_keywords WHERE doc_id = ?",
                "DELETE FROM imcms_doc_versions WHERE doc_id = ?",
                "DELETE FROM meta WHERE meta_id = ?"
        ).forEach(query -> entityManager.createNativeQuery(query).setParameter(1, docId).executeUpdate());
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
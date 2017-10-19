package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.content.*;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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
    private final UrlDocContentRepository urlDocContentRepository;
    private final FileDocFileRepository fileDocFileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Logger logger = Logger.getLogger(getClass());

    @Inject
    public DocRepository(MetaRepository metaRepository, PropertyRepository propertyRepository,
                         HtmlDocContentRepository htmlDocContentRepository,
                         UrlDocContentRepository urlDocContentRepository, FileDocFileRepository fileDocFileRepository) {
        this.metaRepository = metaRepository;
        this.propertyRepository = propertyRepository;
        this.htmlDocContentRepository = htmlDocContentRepository;
        this.urlDocContentRepository = urlDocContentRepository;
        this.fileDocFileRepository = fileDocFileRepository;
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


    public void deleteUrlDocContent(DocRef docIdentity) {
        urlDocContentRepository.deleteByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());
    }

    public List<FileDocFile> getFileDocContent(DocRef docIdentity) {
        return fileDocFileRepository.findByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());
    }


    public FileDocFile saveFileDocFile(FileDocFile fileDocItem) {
        return entityManager.merge(fileDocItem);
    }

    public void deleteFileDocContent(DocRef docIdentity) {
        List<FileDocFile> fileDocFile = fileDocFileRepository.findByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());

        if (fileDocFile.size() == 0) {
            return;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<FileDocFile> query = cb.createCriteriaDelete(FileDocFile.class);
        query.where(query.from(FileDocFile.class).get("id").in(fileDocFile.stream().map(FileDocFile::getId).collect(Collectors.toList())));

        entityManager.createQuery(query).executeUpdate();
    }

    public HtmlDocContent getHtmlDocContent(DocRef docIdentity) {
        return htmlDocContentRepository.findByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());
    }


    public HtmlDocContent saveHtmlDocContent(HtmlDocContent content) {
        return entityManager.merge(content);
    }

    public UrlDocContent getUrlDocContent(DocRef docIdentity) {
        return urlDocContentRepository.findByDocIdAndVersionNo(docIdentity.getId(), docIdentity.getVersionNo());
    }


    public UrlDocContent saveUrlDocContent(UrlDocContent reference) {
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
                "DELETE FROM imcms_text_doc_contents WHERE loop_id IN (SELECT id FROM imcms_text_doc_content_loops WHERE doc_id = ?)",
                "DELETE FROM imcms_text_doc_content_loops WHERE doc_id = ?",
                "DELETE FROM imcms_doc_languages WHERE doc_id = ?",
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
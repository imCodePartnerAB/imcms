package com.imcode.imcms.mapping;

import com.imcode.imcms.DocIdentityCleanerVisitor;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.*;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.*;
import com.imcode.imcms.dao.*;
import com.imcode.imcms.mapping.orm.DefaultDocumentVersion;

/**
 * Used internally by DocumentMapper. Must not be used directly.
 *
 * Instantiated and initialized using spring framework. 
 */
public class DocumentSaver {

    private DocumentMapper documentMapper;
    
    private MetaDao metaDao;

    private DocumentVersionDao documentVersionDao;

    private ContentLoopDao contentLoopDao;

    private TextDao textDao;

    private ImageDao imageDao;

    private MenuDao menuDao;
    
    private DocumentPermissionSetMapper documentPermissionSetMapper = new DocumentPermissionSetMapper();
    
    /**
     * Saves edited text-document text and non-saved enclosing content loop if any.
     * If text is enclosed into unsaved content loop then the loop must also exist in document.
     *
     * @see com.imcode.imcms.servlet.admin.SaveText
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     *
     * @throws IllegalStateException if a text refers non-existing content loop.
     * 
     * TODO: Update doc modified dt
     */
    @Transactional     
    public void saveText(TextDocumentDomainObject doc, TextDomainObject text, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        createEnclosingContentLoopIfNecessary(doc, text);

    	new DocumentStoringVisitor(Imcms.getServices()).saveTextDocumentText(doc, text, user);
    }


    /**
     * Saves edited text-document text and non-saved enclosing content loop if any.
     * If text is enclosed into unsaved content loop then the loop must also exist in document.
     *
     * @see com.imcode.imcms.servlet.admin.SaveText
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     *
     * @throws IllegalStateException if a text refers non-existing content loop.
     *
     * TODO: Update doc modified dt
     */
    @Transactional
    public void saveMenu(TextDocumentDomainObject doc, MenuDomainObject menu, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        menu.setDocId(doc.getId());
        menu.setDocVersionNo(doc.getVersion().getNo());

        new DocumentStoringVisitor(Imcms.getServices()).updateTextDocumentMenu(doc, menu, user);
    }


    /**
     * Saves changed text-document image(s).
     * If an image is enclosed into unsaved content loop then this content loop is also saved.
     *
     * @see com.imcode.imcms.servlet.admin.ChangeImage
     * 
     * @param doc
     * @param images images with the same 'no' for every language.
     * @param user
     * @throws NoPermissionInternalException
     * @throws DocumentSaveException
     *
     * TODO: Update doc modified dt
     */
    @Transactional
    public void saveImages(TextDocumentDomainObject doc, Collection<ImageDomainObject> images, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        for (ImageDomainObject image: images) {
            createEnclosingContentLoopIfNecessary(doc, image);
            storingVisitor.saveTextDocumentImage(doc, image, user);
        }
    }


    @Transactional
    public void saveImage(TextDocumentDomainObject doc, ImageDomainObject image, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        createEnclosingContentLoopIfNecessary(doc, image);

        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        storingVisitor.saveTextDocumentImage(doc, image, user);
    }
    

    /**
     * Creates content loop if item references non-saved enclosing content loop.
     *
     * @param doc
     * @param item
     */
    @Transactional
    public ContentLoop createEnclosingContentLoopIfNecessary(TextDocumentDomainObject doc, DocContentLoopItem item) {
        ContentLoop loop = null;
        Integer loopNo = item.getContentLoopNo();
        
        if (loopNo != null) {
            Integer contentNo = item.getContentNo();

            if (contentNo == null) {
                throw new IllegalStateException(String.format(
                        "Content loop's context no is not set. Doc id: %s, item :%s, content loop no: %s.",  doc.getId(), item, loopNo));
            }

            loop = doc.getContentLoop(loopNo);

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Content loop does not exists. Doc id: %s, item :%s, content loop no: %s.",  doc.getId(), item, loopNo));                
            }

            if (!loop.contentExists(contentNo)) {
                throw new IllegalStateException(String.format(
                        "Content does not exists. Doc id: %s, item :%s, content loop no: %s.",  doc.getId(), item, loopNo));                                
            }

            loop = contentLoopDao.saveLoop(loop);
        }

        return loop;
    }


    @Transactional
    public void changeDocumentDefaultVersion(Integer docId, Integer newDocDefaultVersionNo, UserDomainObject user) {
        DefaultDocumentVersion defaultVersion = documentVersionDao.getDefaultVersionORM(docId);

        if (defaultVersion == null) {
            defaultVersion = new DefaultDocumentVersion();
            defaultVersion.setDocId(docId);
            defaultVersion.setNo(newDocDefaultVersionNo);
        } else {
            defaultVersion.setNo(newDocDefaultVersionNo);
        }

        documentVersionDao.saveDefaultVersionORM(defaultVersion);
    }



    /**
     * @param docs
     * @param user
     * @return saved document id.
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    //todo: add security check.
    @Transactional
    public DocumentVersion makeDocumentVersion(final Meta meta, Map<I18nLanguage, DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentVersion nextVersion = documentVersionDao.createVersion(meta.getId(), user.getId());

        // saves labels
        for (DocumentDomainObject doc: docs.values()) {
            doc.accept(new DocIdentityCleanerVisitor());
            DocumentLabels labels = doc.getLabels();

            labels.setId(null);
            labels.setDocVersionNo(nextVersion.getNo());
            metaDao.saveLabels(labels);

        }

        // save content    
        DocumentVersionCreationVisitor docCreationVisitor = new DocumentVersionCreationVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc: docs.values()) {
            doc.setVersion(nextVersion);

            doc.setMeta(meta);
            doc.accept(docCreationVisitor);

            // Only text doc has i18n content.
            if (!(doc instanceof TextDocumentDomainObject)) break; 
        }

        return nextVersion;
    }


//    /**
//     * Creates working document copy as a new version.
//     */
//    // TODO: Should throw NoPermissionToEditDocumentException ?
//    // TODO: Add history for texts and images
//    @Transactional
//    public DocumentVersion makeDocumentVersion(Integer docId, UserDomainObject user)
//    throws DocumentSaveException {
//    	try {
//            Meta meta = metaDao.getMeta(docId);
//
//            DocumentVersion documentVersion = documentVersionDao.createVersion(docId, user.getId());
//            Integer docVersionNo = documentVersion.getNo();
//
//            for (DocumentLabels labels:  metaDao.getLabels(docId, DocumentVersion.WORKING_VERSION_NO)) {
//                labels = labels.clone();
//                labels.setId(null);
//                labels.setDocVersionNo(docVersionNo);
//
//                metaDao.saveLabels(labels);
//            }
//
//    		if (meta.getDocumentType() == DocumentTypeDomainObject.TEXT_ID) {
//                for (ContentLoop loop: contentLoopDao.getLoops(docId, DocumentVersion.WORKING_VERSION_NO)) {
//                    loop = loop.clone();
//                    loop.setId(null);
//                    loop.setDocVersionNo(docVersionNo);
//
//                    contentLoopDao.saveLoop(loop);
//                }
//
//                for (TextDomainObject text: textDao.getTexts(docId, DocumentVersion.WORKING_VERSION_NO)) {
//                    text = text.clone();
//                    text.setId(null);
//                    text.setDocVersionNo(docVersionNo);
//
//                    textDao.saveText(text);
//                }
//
//                for (ImageDomainObject image: imageDao.getImages(docId, DocumentVersion.WORKING_VERSION_NO)) {
//                    image = image.clone();
//                    image.setId(null);
//                    image.setDocVersionNo(docVersionNo);
//
//                    imageDao.saveImage(image);
//                }
//
//                for (MenuDomainObject menu: menuDao.getMenus(docId, DocumentVersion.WORKING_VERSION_NO)) {
//                    menu = menu.clone();
//                    menu.setId(null);
//                    menu.setDocVersionNo(docVersionNo);
//
//                    menuDao.saveMenu(menu);
//                }
//    		} else if (meta.getDocumentType() == DocumentTypeDomainObject.FILE_ID) {
//
//            }
//
//            return documentVersion;
//    	} catch (RuntimeException e) {
//    		throw new DocumentSaveException(e);
//    	}
//    }



    @Transactional
    public void updateDocument(DocumentDomainObject document, DocumentDomainObject oldDocument, UserDomainObject user) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        checkDocumentForSave(document);

        //document.loadAllLazilyLoaded();

        Date lastModifiedDatetime = Utility.truncateDateToMinutePrecision(document.getActualModifiedDatetime());
        Date modifiedDatetime = Utility.truncateDateToMinutePrecision(document.getModifiedDatetime());
        boolean modifiedDatetimeUnchanged = lastModifiedDatetime.equals(modifiedDatetime);
        if (modifiedDatetimeUnchanged) {
            document.setModifiedDatetime(documentMapper.getClock().getCurrentDate());
        }

        if (user.canEditPermissionsFor(oldDocument)) {
            newUpdateDocumentRolePermissions(document, user, oldDocument);
            documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(document, user, oldDocument);
        }

        DocumentSavingVisitor savingVisitor = new DocumentSavingVisitor(oldDocument, documentMapper.getImcmsServices(), user);

        saveMeta(document);

        document.accept(savingVisitor);
    }    

//    private Integer saveNewDocument(UserDomainObject user, DocumentDomainObject document, boolean copying)
//            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
//
//        checkDocumentForSave(document);
//
//        //document.loadAllLazilyLoaded();
//
//        documentMapper.setCreatedAndModifiedDatetimes(document, new Date());
//
//        boolean inheritRestrictedPermissions = !user.isSuperAdminOrHasFullPermissionOn(document) && !copying;
//        if (inheritRestrictedPermissions) {
//            document.getPermissionSets().setRestricted1(document.getPermissionSetsForNewDocuments().getRestricted1());
//            document.getPermissionSets().setRestricted2(document.getPermissionSetsForNewDocuments().getRestricted2());
//        }
//
//        newUpdateDocumentRolePermissions(document, user, null);
//
//        // Update permissions
//        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(document, user, null);
//
//        document.accept(new DocIdentityCleanerVisitor());
//
//        Meta meta = saveMeta(document);
//        Integer docId = meta.getId();
//
//        metaDao.insertPropertyIfNotExists(docId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, docId.toString());
//
//        DocumentVersion version = documentVersionDao.createVersion(meta.getId(), user.getId());
//        document.setVersion(version);
//
//        document.accept(new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user));
//
//        return docId;
//    }


//    /**
//     * @param docs
//     * @param user
//     * @return saved document id.
//     * @throws NoPermissionToAddDocumentToMenuException
//     * @throws DocumentSaveException
//     */
//    @Transactional
//    public Integer copyDocument(List<DocumentDomainObject> docs, UserDomainObject user, String copyHeadlineSuffix)
//            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
//
//        int docsCount = docs.size();
//        DocumentDomainObject firstDoc = docs.get(0);
//        List<DocumentDomainObject> restDocs = docsCount == 1
//                ? new LinkedList<DocumentDomainObject>()
//                : docs.subList(1, docsCount);
//
//
//        for (DocumentDomainObject doc: docs) {
//            DocumentLabels labels = doc.getLabels();
//            labels.setHeadline(labels.getHeadline() + copyHeadlineSuffix);
//            labelsColl.add(doc.getLabels());
//        }
//
//
//        // save meta and all labels
//        Collection<DocumentLabels> labelsColl = new LinkedList<DocumentLabels>();
//
//        for (DocumentDomainObject doc: docs) {
//            DocumentLabels labels = doc.getLabels();
//            labels.setHeadline(labels.getHeadline() + copyHeadlineSuffix);
//            labelsColl.add(doc.getLabels());
//        }
//
//        Integer docId = saveNewDocument(user, firstDoc, labelsColl, true);
//
//        // save rest docs fields in case of a text document.
//        int docsCount = docs.size();
//        if (firstDoc.getDocumentTypeId() == DocumentTypeDomainObject.TEXT_ID && docsCount > 1) {
//            DocumentCreatingVisitor visitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);
//
//            for (DocumentDomainObject doc: docs.subList(1, docsCount)) {
//                doc.setId(docId);
//                visitor.updateTextDocumentTexts((TextDocumentDomainObject) doc, null, user);
//                visitor.updateTextDocumentImages((TextDocumentDomainObject) doc, null, user);
//            }
//        }
//
//        return docId;
//    }


//    @Transactional
//    public Integer saveNewDocument(UserDomainObject user,
//                                   DocumentDomainObject document, Collection<DocumentLabels> labelsColl, boolean copying)
//            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
//
//        Integer docId = saveNewDocument(user, document, copying);
//
//        for (DocumentLabels l: labelsColl) {
//            l.setId(null);
//            l.setDocId(document.getId());
//            l.setDocVersionNo(document.getVersion().getNo());
//
//            metaDao.saveLabels(l);
//        }
//
//        return docId;
//    }



//            DocumentDomainObject doc = getCustomDocument(docId, docVersionNo, language);
//            if (doc != null) {
//                doc.setAlias(null);
//                makeDocumentLookNew(doc, user);
//                DocumentLabels labels = doc.getLabels();
//                labels.setHeadline(labels.getHeadline() + copyHeadlineSuffix);
//
//                // todo: ??? move to makeDocLookNew
//                doc.accept(new DocIdentityCleanerVisitor());
//
//                docs.put(language, doc);
//            }

    @Transactional
    public Integer copyDocument(Meta meta, Map<I18nLanguage, DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        DocumentDomainObject firstDoc = docs.values().iterator().next();

        checkDocumentForSave(firstDoc);

        documentMapper.setCreatedAndModifiedDatetimes(firstDoc, new Date());

        newUpdateDocumentRolePermissions(firstDoc, user, null);

        // Update permissions
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(firstDoc, user, null);

        Meta copyMeta = meta.clone();
        
        copyMeta.setId(null);
        copyMeta = saveMeta(copyMeta);
        Integer copyDocId = copyMeta.getId();

        metaDao.insertPropertyIfNotExists(copyDocId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, copyDocId.toString());

        DocumentVersion copyDocVersion = documentVersionDao.createVersion(copyDocId, user.getId());

        for (DocumentDomainObject doc: docs.values()) {
            DocumentLabels labels = doc.getLabels();
            labels.setId(null);
            labels.setDocId(copyDocId);
            labels.setDocVersionNo(copyDocVersion.getNo());

            metaDao.saveLabels(labels);
        }

        DocumentCreatingVisitor docCreatingVisitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);

        for (DocumentDomainObject doc: docs.values()) {
            doc.setMeta(copyMeta);
            doc.setVersion(copyDocVersion);

            doc.accept(docCreatingVisitor);

            // Only text doc has i18n content.
            if (!(doc instanceof TextDocumentDomainObject)) break;
        }

        return copyDocId;
    }

    
    @Transactional
    public Integer saveNewDocument(DocumentDomainObject doc, UserDomainObject user, boolean copying)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        checkDocumentForSave(doc);

        documentMapper.setCreatedAndModifiedDatetimes(doc, new Date());

        boolean inheritRestrictedPermissions = !user.isSuperAdminOrHasFullPermissionOn(doc) && !copying;
        if (inheritRestrictedPermissions) {
            doc.getPermissionSets().setRestricted1(doc.getPermissionSetsForNewDocuments().getRestricted1());
            doc.getPermissionSets().setRestricted2(doc.getPermissionSetsForNewDocuments().getRestricted2());
        }

        newUpdateDocumentRolePermissions(doc, user, null);

        // Update permissions
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(doc, user, null);

        // ensure meta is null.
        doc.getMeta().setId(null);

        Meta newMeta = saveMeta(doc);
        Integer docId = newMeta.getId();

//        DocumentProperty property = new DocumentProperty();
//        property.setDocId(docId);
//        property.setName();

        metaDao.insertPropertyIfNotExists(docId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, docId.toString());

        DocumentVersion version = documentVersionDao.createVersion(newMeta.getId(), user.getId());

        doc.setMeta(newMeta);
        doc.setVersion(version);
        doc.accept(new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user));

        return docId;
    }    
    
    
    /**
     * @return saved document meta.
     */
    private Meta saveMeta(DocumentDomainObject document) {
    	Meta meta = document.getMeta();
    	
    	//meta.setPublicationStatusInt(document.getPublicationStatus().asInt());
    	
    	if (meta.getId() == null) {
        	meta.setDocumentType(document.getDocumentTypeId());
        	meta.setActivate(1);
    	}
    	
    	metaDao.saveMeta(meta);
    	
    	return meta;
    }


    /**
     * @return saved document meta.
     */
    private Meta saveMeta(Meta meta) {
    	if (meta.getId() == null) {
        	meta.setActivate(1);
    	}

    	metaDao.saveMeta(meta);

    	return meta;
    }
    

    /**
     * Various non security checks. 
     * 
     * @param document
     * @throws NoPermissionInternalException
     * @throws DocumentSaveException
     */
    private void checkDocumentForSave(DocumentDomainObject document) throws NoPermissionInternalException, DocumentSaveException {

        documentMapper.getCategoryMapper().checkMaxDocumentCategoriesOfType(document);
        checkIfAliasAlreadyExist(document);

    }
    
    
    /**
     * Update meta roles to permissions set mapping.
     * Modified copy of legacy updateDocumentRolePermissions method.  
     * NB! Compared to legacy this method does not update database.
     */
    private void newUpdateDocumentRolePermissions(DocumentDomainObject document, UserDomainObject user,
            DocumentDomainObject oldDocument) {

    	// Original (old) and modified or new document permission set type mapping.
		RoleIdToDocumentPermissionSetTypeMappings mappings = new RoleIdToDocumentPermissionSetTypeMappings();
		
		// Copy original document' roles to mapping with NONE(4) permissions-set assigned
		if (null != oldDocument) {
			RoleIdToDocumentPermissionSetTypeMappings.Mapping[] oldDocumentMappings = oldDocument.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings();
			for ( int i = 0; i < oldDocumentMappings.length; i++ ) {
				RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = oldDocumentMappings[i];
				mappings.setPermissionSetTypeForRole(mapping.getRoleId(), DocumentPermissionSetTypeDomainObject.NONE);
			}
		}
		
		// Copy modified or new document' roles to mapping
		RoleIdToDocumentPermissionSetTypeMappings.Mapping[] documentMappings = document.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings() ;
		for ( int i = 0; i < documentMappings.length; i++ ) {
			RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = documentMappings[i];
			mappings.setPermissionSetTypeForRole(mapping.getRoleId(), mapping.getDocumentPermissionSetType());
		}
		
		RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappingsArray = mappings.getMappings();
		Map<Integer, Integer> roleIdToPermissionSetIdMap = document.getMeta().getRoleIdToPermissionSetIdMap();
		
		for ( int i = 0; i < mappingsArray.length; i++ ) {
			RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = mappingsArray[i];
			RoleId roleId = mapping.getRoleId();
			DocumentPermissionSetTypeDomainObject documentPermissionSetType = mapping.getDocumentPermissionSetType();
			
			if (null == oldDocument
					|| user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(documentPermissionSetType, roleId, oldDocument)) {
				
				// According to schema design NONE value can not be save into table 
				if (documentPermissionSetType.equals(DocumentPermissionSetTypeDomainObject.NONE)) {
					roleIdToPermissionSetIdMap.remove(roleId.intValue());
				} else {
					roleIdToPermissionSetIdMap.put(roleId.intValue(), documentPermissionSetType.getId());
				}
			}
		}
	}

    private void checkIfAliasAlreadyExist(DocumentDomainObject document) throws AliasAlreadyExistsInternalException {
    	String alias = document.getAlias();
    	
    	if (alias != null) {
    		DocumentProperty property = metaDao.getAliasProperty(alias);
    		if (property != null) {
    			Integer documentId = document.getId();
    			
    			if (!property.getDocId().equals(documentId)) {
                    throw new AliasAlreadyExistsInternalException(
                    		String.format("Alias %s is allready given to document %d.", alias, documentId));    				
    			}			
    		}
    	}
    }

	public DocumentMapper getDocumentMapper() {
		return documentMapper;
	}

	public void setDocumentMapper(DocumentMapper documentMapper) {
		this.documentMapper = documentMapper;
	}

	public MetaDao getMetaDao() {
		return metaDao;
	}

	public void setMetaDao(MetaDao metaDao) {
		this.metaDao = metaDao;
	}

    public ContentLoopDao getContentLoopDao() {
        return contentLoopDao;
    }

    public void setContentLoopDao(ContentLoopDao contentLoopDao) {
        this.contentLoopDao = contentLoopDao;
    }

    public DocumentVersionDao getDocumentVersionDao() {
        return documentVersionDao;
    }

    public void setDocumentVersionDao(DocumentVersionDao documentVersionDao) {
        this.documentVersionDao = documentVersionDao;
    }

    public TextDao getTextDao() {
        return textDao;
    }

    public void setTextDao(TextDao textDao) {
        this.textDao = textDao;
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public MenuDao getMenuDao() {
        return menuDao;
    }

    public void setMenuDao(MenuDao menuDao) {
        this.menuDao = menuDao;
    }
}
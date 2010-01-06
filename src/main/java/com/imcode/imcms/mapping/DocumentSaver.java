package com.imcode.imcms.mapping;

import com.imcode.imcms.DocIdentityCleanerVisitor;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
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
 * This class is instantiated using spring framework.
 * 
 * Used by DocumentMapper. API must not be invoked directly.  
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
        Integer loopNo = text.getLoopNo();

        if (loopNo != null) {
            ContentLoop loop = doc.getContentLoop(loopNo);

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Text no: %s in document id: %s references non-existing content loop no: %s.", text.getNo(), doc.getId(), loopNo));
            }

            Integer contentIndex = text.getContentIndex();

            if (contentIndex == null) {
                throw new IllegalStateException(String.format(
                        "Text's loop context index is not set. Doc id: %s, text no: content loop no: %s.",  doc.getId(), text.getNo(),loopNo));
            }

            if (loop.getId() == null) {
                loop = contentLoopDao.saveContentLoop(loop);
                
                doc.getContentLoops().put(loopNo, loop);
            }
        }

    	new DocumentStoringVisitor(Imcms.getServices()).saveTextDocumentText(doc, text, user);
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
        ImageDomainObject image = images.iterator().next();

        if (image != null) {
            Integer loopNo = image.getLoopNo();

            if (loopNo != null) {
                ContentLoop loop = doc.getContentLoop(loopNo);

                if (loop == null) {
                    throw new IllegalStateException(String.format(
                            "Image no: %s in document id: %s references non-existing content loop no: %s.", image.getNo(), doc.getId(), loopNo));
                }

                Integer contentIndex = image.getContentIndex();

                if (contentIndex == null) {
                    throw new IllegalStateException(String.format(
                            "Image's loop context index is not set. Doc id: %s, text no: content loop no: %s.",  doc.getId(), image.getNo(),loopNo));
                }

                if (loop.getId() == null) {
                    loop = contentLoopDao.saveContentLoop(loop);

                    doc.getContentLoops().put(loopNo, loop);
                }
            }
        }

        DocumentStoringVisitor storingVisitor = new DocumentStoringVisitor(Imcms.getServices());

        for (ImageDomainObject img: images) {
            storingVisitor.saveTextDocumentImage(doc, img, user);
        }
    }


    @Transactional
    public void setDocumentActiveVersion(Integer docId, Integer docVersionNo) {
        DefaultDocumentVersion activeVersion = documentVersionDao.getDefaultVersionORM(docId);

        if (activeVersion == null) {
            activeVersion = new DefaultDocumentVersion();
            activeVersion.setDocId(docId);
            activeVersion.setNo(docVersionNo);
        } else {
            activeVersion.setNo(docVersionNo);
        }

        documentVersionDao.saveDefaultVersionORM(activeVersion);
    }


    /**
     * Creates working document copy as a new version. 
     */
    // TODO: Should throw NoPermissionToEditDocumentException ?
    // TODO: Add history for texts and images
    @Transactional    
    public void makeDocumentVersion(Integer docId, UserDomainObject user)
    throws DocumentSaveException {
    	try {
            Meta meta = metaDao.getMeta(docId);

            DocumentVersion documentVersion = documentVersionDao.createVersion(docId, user.getId());
            Integer docVersionNo = documentVersion.getNo();

            for (DocumentLabels labels:  metaDao.getLabels(docId, DocumentVersion.WORKING_VERSION_NO)) {
                labels = labels.clone();
                labels.setDocVersionNo(docVersionNo);

                metaDao.saveLabels(labels);
            }

    		if (meta.getDocumentType() == DocumentTypeDomainObject.TEXT_ID) {
                for (ContentLoop loop: contentLoopDao.getContentLoops(docId, DocumentVersion.WORKING_VERSION_NO)) {
                    loop = loop.clone();
                    loop.setId(null);
                    loop.setDocVersionNo(docVersionNo);

                    for (Content content: loop.getContents()) {
                        content.setId(null);
                        content.setLoopId(null);
                    }

                    contentLoopDao.saveContentLoop(loop);
                }

                for (TextDomainObject text: textDao.getTexts(docId, DocumentVersion.WORKING_VERSION_NO)) {
                    text = text.clone();
                    text.setId(null);
                    text.setDocVersionNo(docVersionNo);

                    textDao.saveText(text);
                }

                for (ImageDomainObject image: imageDao.getImages(docId, DocumentVersion.WORKING_VERSION_NO)) {
                    image = image.clone();
                    image.setId(null);
                    image.setDocVersionNo(docVersionNo);

                    imageDao.saveImage(image);
                }

                for (MenuDomainObject menu: menuDao.getMenus(docId, DocumentVersion.WORKING_VERSION_NO)) {
                    menu = menu.clone();
                    menu.setId(null);
                    menu.setDocVersionNo(docVersionNo);

                    menuDao.saveMenu(menu);
                }
                
    		}
    	} catch (RuntimeException e) {
    		throw new DocumentSaveException(e);
    	}
    }
    
    
    /**
     * Updates published or working document.
     */
    @Transactional
    public void updateDocument(DocumentDomainObject document, DocumentDomainObject oldDocument,
                      final UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
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

//    /**
//     * Creates working document version from existing document.
//     * <p/>
//     * Actually only texts and images are copied into new document.
//     *
//     * @param document an instance of {@link TextDocumentDomainObject}
//     * @return working version of a document.
//     */
//    @Transactional
//    public DocumentDomainObject createWorkingDocumentFromExisting(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
//
//        //checkDocumentForSave(document);
//        //document.loadAllLazilyLoaded();
//
//        //TODO: refactor - very ugly
//        // save document id
//        Meta meta = document.getMeta();
//        Integer documentId = meta.getId();
//
//        //TODO: refactor - very ugly
//        // clone document, reset its dependencies meta id and assign its documentId again
//        TextDocumentDomainObject textDocument = (TextDocumentDomainObject) document.clone();
//        textDocument.setDependenciesMetaIdToNull();
//        textDocument.setId(documentId);
//
//        /*
//        //try {
//            Date lastModifiedDatetime = Utility.truncateDateToMinutePrecision(document.getActualModifiedDatetime());
//            Date modifiedDatetime = Utility.truncateDateToMinutePrecision(document.getModifiedDatetime());
//            boolean modifiedDatetimeUnchanged = lastModifiedDatetime.equals(modifiedDatetime);
//            if (modifiedDatetimeUnchanged) {
//                document.setModifiedDatetime(documentMapper.getClock().getCurrentDate());
//            }
//
//            saveMeta(documentId, document);
//
//            document.accept(new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user));
//        //} finally {
//        //    documentMapper.invalidateDocument(document);
//        //}
//        */
//
//        DocumentVersion documentVersion = documentVersionDao.createVersion(documentId, user.getId());
//        textDocument.setVersion(documentVersion);
//
//        DocumentCreatingVisitor visitor = new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user);
//
//        visitor.updateTextDocumentTexts(textDocument, null, user);
//        visitor.updateTextDocumentImages(textDocument, null, user);
//        visitor.updateTextDocumentContentLoops(textDocument, null, user);
//
//        return document;
//    }


    /**
     *
     * @param user
     * @param document
     * @param copying
     * @return saved document id.
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    @Deprecated
    @Transactional
    public Integer saveNewDocument(UserDomainObject user,
                         DocumentDomainObject document, boolean copying) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        checkDocumentForSave(document);

        //document.loadAllLazilyLoaded();                
        
        documentMapper.setCreatedAndModifiedDatetimes(document, new Date());

        boolean inheritRestrictedPermissions = !user.isSuperAdminOrHasFullPermissionOn(document) && !copying;
        if (inheritRestrictedPermissions) {
            document.getPermissionSets().setRestricted1(document.getPermissionSetsForNewDocuments().getRestricted1());
            document.getPermissionSets().setRestricted2(document.getPermissionSetsForNewDocuments().getRestricted2());
        }
        
        newUpdateDocumentRolePermissions(document, user, null);

        // Update permissions
        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(document, user, null);

        document.accept(new DocIdentityCleanerVisitor());

        Meta meta = saveMeta(document);
        Integer docId = meta.getId();

        metaDao.insertPropertyIfNotExists(docId, DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, docId.toString());
        
        DocumentVersion version = documentVersionDao.createVersion(meta.getId(), user.getId());
        document.setVersion(version);        
                
        document.accept(new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user));

        return docId;
    }


    /**
     *
     * @param docs
     * @param user
     * @return saved document id.
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    @Transactional
    public Integer copyDocument(List<DocumentDomainObject> docs, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        // save meta and all labels
        Collection<DocumentLabels> labels = new LinkedList<DocumentLabels>();

        for (DocumentDomainObject doc: docs) {
            labels.add(doc.getLabels());
        }

        DocumentDomainObject firstDoc = docs.get(0);
        Integer savedDocId = saveNewDocument(user, firstDoc, labels, true);

        // save rest docs fields in case of a text document.
        int docsCount = docs.size();
        if (firstDoc.getDocumentTypeId() == DocumentTypeDomainObject.TEXT_ID && docsCount > 1) {
            Integer docId = firstDoc.getId();
            for (DocumentDomainObject doc: docs.subList(1, docsCount)) {
                 doc.accept(new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user));
            }
        }

        return savedDocId;
    }


    @Transactional
    public Integer saveNewDocument(UserDomainObject user,
                         DocumentDomainObject document, Collection<DocumentLabels> labels, boolean copying) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Integer docId = saveNewDocument(user, document, copying);

        for (DocumentLabels l: labels) {
            l.setId(null);
            l.setDocId(document.getId());
            l.setDocVersionNo(document.getVersion().getNo());
            metaDao.saveLabels(l);
        }

        return docId;
    }


    @Transactional
    public void updateDocument(DocumentDomainObject document, Collection<DocumentLabels> labels, UserDomainObject user) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        updateDocument(document, (DocumentDomainObject)null, user);

        // ???
        metaDao.deleteLabels(document.getId(), document.getVersion().getNo());

        for (DocumentLabels l: labels) {
            l.setId(null);
            l.setDocId(document.getId());
            l.setDocVersionNo(document.getVersion().getNo());
            metaDao.saveLabels(l);    
        }
        
    }
    
    
    /**
     * Temporary method
     * Copies data from attributes to meta and stores meta.
     * 
     * @return saved document meta.
     */
    private Meta saveMeta(DocumentDomainObject document) {
    	Meta meta = document.getMeta();
    	
    	meta.setPublicationStatusInt(document.getPublicationStatus().asInt());
    	
    	if (meta.getId() == null) {
        	meta.setDocumentType(document.getDocumentTypeId());
        	meta.setActivate(1);
    	} 
    	
    	//for update
        //private static final int META_HEADLINE_MAX_LENGTH = 255;
        //private static final int META_TEXT_MAX_LENGTH = 1000;
        //String headlineThatFitsInDB = headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        //String textThatFitsInDB = text.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));
    	
    	// Converted from legacy queries:
    	// Should be handled separately from meta???
    	//meta.getRoleIdToPermissionSetIdMap();
    	//meta.getDocPermisionSetEx().clear();
    	//meta.getDocPermisionSetExForNew().clear();    	
    	//meta.getPermissionSetBitsMap().clear();
    	//meta.getPermissionSetBitsForNewMap().clear();    	    	
    	
    	// WHAT TO DO WITH THIS on copy save and on base save?    	
    	//meta.setCategoryIds(document.getCategoryIds());
    	//meta.setProperties(document.getProperties());
    	
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
    			
    			if (!property.getDocumentId().equals(documentId)) {
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
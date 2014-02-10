package com.imcode.imcms.mapping;

import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.dao.*;
import com.imcode.imcms.mapping.orm.*;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.imcode.imcms.dao.TextDocDao;

public class TextDocumentInitializer {

    private final static Logger LOG = Logger.getLogger(TextDocumentInitializer.class);

    private DocumentGetter documentGetter;

    private MetaDao metaDao;
    
    private TextDocDao textDocDao;

    /**
	 * Initializes text document.
     */
    public void initialize(TextDocumentDomainObject document) {
        initContentLoops(document);
        initTexts(document);
        initImages(document);
        initMenus(document);
        initIncludes(document);
        initTemplateNames(document);
    }
    
    public void initTexts(TextDocumentDomainObject document) {
    	Collection<TextDocText> texts = textDocDao.getTexts(document.getIdentity());

    	for (TextDocText text: texts) {
            Integer no = text.getNo();

            document.setText(no, OrmToApi.toApi(text));
    	}
    }
    
    
    public void initIncludes(TextDocumentDomainObject document) {
    	Collection<Include> includes = metaDao.getIncludes(document.getMeta().getId());
    	
    	Map<Integer, Integer> includesMap = new HashMap<Integer, Integer>();
    	
    	for (Include include: includes) {
    		includesMap.put(include.getIndex(), include.getIncludedDocumentId());
    	}
    	
    	document.setIncludesMap(includesMap);
    }
    
    
    public void initTemplateNames(TextDocumentDomainObject document) {
    	TemplateNames templateNames = metaDao.getTemplateNames(document.getMeta().getId());
    	
    	if (templateNames == null) {
    		templateNames = new TemplateNames();
    	}
    	
    	document.setTemplateNames(templateNames);
    }    
    
    
    public void initImages(TextDocumentDomainObject document) {
    	Collection<TextDocImage> images = textDocDao.getImagesInAllLanguages(document.getIdentity());
    	
    	for (TextDocImage image: images) {
    		document.setImage(image.getNo(), image);
    	}
    }


    public void initMenus(TextDocumentDomainObject document) {
    	Collection<MenuDomainObject> menus = textDocDao.getMenus(document.getIdentity());
    	Map<Integer, MenuDomainObject> menusMap = new HashMap<>();

    	for (MenuDomainObject menu: menus) {
    		initMenuItems(menu, documentGetter);
	    	
	    	menusMap.put(menu.getNo(), menu);
    	}
    	
    	document.setMenus(menusMap);
    }   
    
    private void initMenuItems(MenuDomainObject menu, DocumentGetter documentGetter) {
    	
    	for (Map.Entry<Integer, MenuItemDomainObject> entry: menu.getItemsMap().entrySet()) {
    		Integer referencedDocumentId = entry.getKey();
    		MenuItemDomainObject menuItem = entry.getValue();
    		GetterDocumentReference gtr = new GetterDocumentReference(referencedDocumentId, documentGetter);
    		
    		menuItem.setDocumentReference(gtr);
    	}
    }


    /**
     * @throws IllegalStateException if a content loop is empty i.e. does not have a contents. 
     */
	public void initContentLoops(TextDocumentDomainObject document) {
		List<TextDocLoop> loops = textDocDao.getLoops(document.getIdentity());
		Map<Integer, ContentLoop> loopsMap = new HashMap<>();
		
		for (TextDocLoop loop: loops) {
			loopsMap.put(loop.getNo(), OrmToApi.toApi(loop));
		}
		
		document.setContentLoops(loopsMap);
	}

	public MetaDao getMetaDao() {
		return metaDao;
	}

	public void setMetaDao(MetaDao metaDao) {
		this.metaDao = metaDao;
	}

	public TextDocDao getTextDocDao() {
		return textDocDao;
	}

	public void setTextDocDao(TextDocDao textDocDao) {
		this.textDocDao = textDocDao;
	}

	public DocumentGetter getDocumentGetter() {
		return documentGetter;
	}

	public void setDocumentGetter(DocumentGetter documentGetter) {
		this.documentGetter = documentGetter;
	}	
}
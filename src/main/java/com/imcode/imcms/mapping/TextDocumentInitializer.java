package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.db.Database;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.orm.OrmTextDocument;
import com.imcode.imcms.dao.MenuDao;

public class TextDocumentInitializer {

    private final static Logger LOG = Logger.getLogger(TextDocumentInitializer.class);

   private final DocumentGetter documentGetter;

    public TextDocumentInitializer(Database database, DocumentGetter documentGetter, Collection documentIds) {
        this.documentGetter = documentGetter;
    }
    
    // TODO: refactor
    public void initialize(TextDocumentDomainObject document) {
        Integer documentId = new Integer(document.getId()) ;
        OrmTextDocument orm = (OrmTextDocument)document.getMeta().getOrmDocument();
        
        initTexts(document);
        initImages(document);
        initMenus(document);
           		
		document.setIncludesMap(orm.getIncludesMap());
		document.setTemplateNames(orm.getTemplateNames());
    }
    
    private void initTexts(TextDocumentDomainObject document) {
    	OrmTextDocument orm = (OrmTextDocument)document.getMeta().getOrmDocument();
    	Set<TextDomainObject> textsSet = orm.getTexts();    	    
    	Map<I18nLanguage, Map<Integer, TextDomainObject>> textsMap = new HashMap<I18nLanguage, Map<Integer,TextDomainObject>>();
    	
    	for (TextDomainObject text: textsSet) {
    		I18nLanguage language = text.getLanguage();
    		Map<Integer, TextDomainObject> indexMap = textsMap.get(language);
    		
    		if (indexMap == null) {
    			indexMap = new HashMap<Integer, TextDomainObject>();
    			textsMap.put(language, indexMap);
    		}  
    		
    		indexMap.put(text.getIndex(), text);
    	}
    	
    	document.setTexts(textsMap);
    }
    
    
    private void initImages(TextDocumentDomainObject document) {
    	OrmTextDocument orm = (OrmTextDocument)document.getMeta().getOrmDocument();
    	Set<ImageDomainObject> imagesSet = orm.getImages();    	    
    	Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();
    	
    	for (ImageDomainObject image: imagesSet) {
    		I18nLanguage language = image.getLanguage();
    		Map<Integer, ImageDomainObject> indexMap = imagesMap.get(language);
    		
    		if (indexMap == null) {
    			indexMap = new HashMap<Integer, ImageDomainObject>();
    			imagesMap.put(language, indexMap);
    		}  
    		
    		indexMap.put(image.getIndex(), setImageSource(image));
    	}
    	
    	document.setImages(imagesMap);
    }
    
    
    private void initMenus(TextDocumentDomainObject document) {
    	OrmTextDocument orm = (OrmTextDocument)document.getMeta().getOrmDocument();
    	Map<Integer, MenuDomainObject> menusMap = orm.getMenus();	    
    	Set<Integer> destinationDocumentIds = new HashSet<Integer>();
    	BatchDocumentGetter batchDocumentGetter = new BatchDocumentGetter(destinationDocumentIds, documentGetter);
    	
    	for (MenuDomainObject menu: menusMap.values()) {
	    	for (Map.Entry<Integer, MenuItemDomainObject> entry: menu.getItemsMap().entrySet()) {
	    		Integer destinationDocumentId = entry.getKey();
	    		MenuItemDomainObject menuItem = entry.getValue();
	    		
	    		GetterDocumentReference gtr = new GetterDocumentReference(destinationDocumentId, batchDocumentGetter);
	    		menuItem.setDocumentReference(gtr);
	    		menuItem.setTreeSortKey(new TreeSortKeyDomainObject(menuItem.getTreeSortIndex()));
	    		
	    		destinationDocumentIds.add(destinationDocumentId);
	    	}		
    	}
    	
    	document.setMenusMap(menusMap);
    }    
    
    // Temporary used by ImageDaoImpl
    // after refactoring make it private
	public static ImageDomainObject setImageSource(ImageDomainObject image) {
		if (image == null) {
			return null;
		}
		
		String url = image.getImageUrl();
		
		if (!StringUtils.isBlank(url)) {
			ImageSource imageSource = new ImagesPathRelativePathImageSource(url);
			image.setSource(imageSource);	
			image.setImageUrl(url);
			image.setType(imageSource.getTypeId());
		}
				
		return image;
	}  
	
	// Reserved for future use:
	public void loadMenus(TextDocumentDomainObject document) {
		MenuDao menuDao = (MenuDao)Imcms.getServices().getSpringBean("menuDao");
		List<MenuDomainObject> menus = menuDao.getMenus(document.getId());
		
	    Set<Integer> destinationDocumentIds = new HashSet<Integer>();
	    BatchDocumentGetter batchDocumentGetter = new BatchDocumentGetter(destinationDocumentIds, documentGetter);
	    Map<Integer, MenuDomainObject> menusMap = new HashMap<Integer, MenuDomainObject>();
	    
	    for (MenuDomainObject menu: menus) {
	    	menusMap.put(menu.getIndex(), menu);
	    	
	    	for (Map.Entry<Integer, MenuItemDomainObject> entry: menu.getItemsMap().entrySet()) {
	    		Integer destinationDocumentId = entry.getKey();
	    		MenuItemDomainObject menuItem = entry.getValue();
	    		
	    		GetterDocumentReference gtr = new GetterDocumentReference(destinationDocumentId, batchDocumentGetter);
	    		menuItem.setDocumentReference(gtr);
	    		
	    		destinationDocumentIds.add(destinationDocumentId);
	    	}
	    }
	    
	    document.setMenusMap(menusMap);
	}	
}
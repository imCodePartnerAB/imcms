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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.db.Database;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.ContentLoopDao;
import com.imcode.imcms.dao.ImageDao;
import com.imcode.imcms.dao.MenuDao;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;

// TODO: Move all separated across dao-s methods into the TextDocumentDao class.  
public class TextDocumentInitializer {

    private final static Logger LOG = Logger.getLogger(TextDocumentInitializer.class);

    private DocumentGetter documentGetter;
    
    private MetaDao metaDao;
    
    private TextDao textDao;
    
    private MenuDao menuDao;
    
    private ImageDao imageDao;
    
    private ContentLoopDao contentLoopDao;
    
    // TODO: refactor
    public void initialize(TextDocumentDomainObject document) {
        initTexts(document);
        initImages(document);
        initMenus(document);
        initIncludes(document);
        initTemplateNames(document);
        initContentLoops(document);
    }
    
    private void initTexts(TextDocumentDomainObject document) {
     	Meta meta = document.getMeta();
    	
    	Collection<TextDomainObject> texts = textDao.getTexts(meta.getId(), meta.getVersion().getVersion());    	    
    	Map<I18nLanguage, Map<Integer, TextDomainObject>> textsMap = new HashMap<I18nLanguage, Map<Integer,TextDomainObject>>();
    	
    	for (TextDomainObject text: texts) {
    		I18nLanguage language = text.getLanguage();
    		Map<Integer, TextDomainObject> indexMap = textsMap.get(language);
    		
    		if (indexMap == null) {
    			indexMap = new HashMap<Integer, TextDomainObject>();
    			textsMap.put(language, indexMap);
    		}  
    		
    		indexMap.put(text.getIndex(), text);
    	}
    	
    	document.setAllTexts(textsMap);
    }
    
    
    private void initIncludes(TextDocumentDomainObject document) {
    	Collection<Include> includes = metaDao.getIncludes(document.getMeta().getId());
    	
    	Map<Integer, Integer> includesMap = new HashMap<Integer, Integer>();
    	
    	for (Include include: includes) {
    		includesMap.put(include.getIndex(), include.getIncludedDocumentId());
    	}
    	
    	document.setIncludesMap(includesMap);
    }
    
    
    private void initTemplateNames(TextDocumentDomainObject document) {
    	TemplateNames templateNames = metaDao.getTemplateNames(document.getMeta().getId());
    	
    	//if (templateNames == null) {
    	//	templateNames = new TemplateNames();
    	//}
    	
    	document.setTemplateNames(templateNames);
    }    
    
    
    private void initImages(TextDocumentDomainObject document) {
    	Meta meta = document.getMeta();
    	
    	Collection<ImageDomainObject> images = imageDao.getImages(meta.getId(), meta.getVersion().getVersion());
    	
    	Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();
    	
    	for (ImageDomainObject image: images) {
    		I18nLanguage language = image.getLanguage();
    		Map<Integer, ImageDomainObject> indexMap = imagesMap.get(language);
    		
    		if (indexMap == null) {
    			indexMap = new HashMap<Integer, ImageDomainObject>();
    			imagesMap.put(language, indexMap);
    		}  
    		
    		indexMap.put(image.getIndex(), setImageSource(image));
    	}
    	
    	document.setAllImages(imagesMap);
    }
    
    
    private void initMenus(TextDocumentDomainObject document) {
    	Collection<MenuDomainObject> menus = menuDao.getMenus(document.getMeta().getId());	
    	Map<Integer, MenuDomainObject> menusMap = new HashMap<Integer, MenuDomainObject>();
    	
    	//Set<Integer> destinationDocumentIds = new HashSet<Integer>();
    	//BatchDocumentGetter batchDocumentGetter = new BatchDocumentGetter(destinationDocumentIds, documentGetter);
    	
    	for (MenuDomainObject menu: menus) {
	    	for (Map.Entry<Integer, MenuItemDomainObject> entry: menu.getItemsMap().entrySet()) {
	    		Integer destinationDocumentId = entry.getKey();
	    		MenuItemDomainObject menuItem = entry.getValue();
	    		
	    		//GetterDocumentReference gtr = new GetterDocumentReference(destinationDocumentId, batchDocumentGetter);
	    		GetterDocumentReference gtr = new GetterDocumentReference(destinationDocumentId, documentGetter);
	    		
	    		menuItem.setDocumentReference(gtr);
	    		menuItem.setTreeSortKey(new TreeSortKeyDomainObject(menuItem.getTreeSortIndex()));
	    		
	    		//destinationDocumentIds.add(destinationDocumentId);
	    	}
	    	
	    	menusMap.put(menu.getIndex(), menu);
    	}
    	
    	document.setMenusMap(menusMap);
    }    
    
    // Temporary used by ImageDao
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
	
	
	private void initContentLoops(TextDocumentDomainObject document) {
		List<ContentLoop> loops = contentLoopDao.getContentLoops(document.getMeta().getId());
		Map<Integer, ContentLoop> loopsMap = new HashMap<Integer, ContentLoop>();
		
		for (ContentLoop loop: loops) {
			loopsMap.put(loop.getIndex(), loop);
		}
		
		document.setContentLoopsMap(loopsMap);
	}

	public MetaDao getMetaDao() {
		return metaDao;
	}

	public void setMetaDao(MetaDao metaDao) {
		this.metaDao = metaDao;
	}

	public TextDao getTextDao() {
		return textDao;
	}

	public void setTextDao(TextDao textDao) {
		this.textDao = textDao;
	}

	public MenuDao getMenuDao() {
		return menuDao;
	}

	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}

	public ImageDao getImageDao() {
		return imageDao;
	}

	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}

	public ContentLoopDao getContentLoopDao() {
		return contentLoopDao;
	}

	public void setContentLoopDao(ContentLoopDao contentLoopDao) {
		this.contentLoopDao = contentLoopDao;
	}

	public DocumentGetter getDocumentGetter() {
		return documentGetter;
	}

	public void setDocumentGetter(DocumentGetter documentGetter) {
		this.documentGetter = documentGetter;
	}	
}
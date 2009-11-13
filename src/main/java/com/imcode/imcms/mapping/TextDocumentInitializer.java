package com.imcode.imcms.mapping;

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

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.DocumentVersionSelector;
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

    /** Set to documentMapper */
    private DocumentGetter documentGetter;

    private MetaDao metaDao;
    
    private TextDao textDao;
    
    private MenuDao menuDao;
    
    private ImageDao imageDao;
    
    private ContentLoopDao contentLoopDao;
    
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
    	Collection<TextDomainObject> texts = textDao.getTexts(document.getId(), document.getVersion().getNo(), document.getLanguage().getId());

    	for (TextDomainObject text: texts) {
            Integer no = text.getNo();

            document.setText(no, text);
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
    	
    	//if (templateNames == null) {
    	//	templateNames = new TemplateNames();
    	//}
    	
    	document.setTemplateNames(templateNames);
    }    
    
    
    public void initImages(TextDocumentDomainObject document) {
    	Collection<ImageDomainObject> images = imageDao.getImages(document.getId(), document.getVersion().getNo(), document.getLanguage().getId());
    	
    	for (ImageDomainObject image: images) {
    		document.setImage(image.getNo(), image);
    	}
    }


    /**
     * Working document menuitems initialized to refer to working documents.
     * Published and other documents initialized to refer to published documents. 
     *
     * @param document document to initialzie.
     */
    public void initMenus(TextDocumentDomainObject document) {
    	Collection<MenuDomainObject> menus = menuDao.getMenus(document.getMeta().getId());	
    	Map<Integer, MenuDomainObject> menusMap = new HashMap<Integer, MenuDomainObject>();
    	DocumentVersionSelector versionSelector = document.getVersion().getNo() == 0
    		? DocumentVersionSelector.WORKING_SELECTOR
    		: DocumentVersionSelector.PUBLISHED_SELECTOR;
    				
    	
    	for (MenuDomainObject menu: menus) {
    		initMenuItems(menu, documentGetter, versionSelector);
	    	
	    	menusMap.put(menu.getIndex(), menu);
    	}
    	
    	document.setMenusMap(menusMap);
    }   
    
    private void initMenuItems(MenuDomainObject menu, DocumentGetter documentGetter,
    		DocumentVersionSelector versionSelector) {
    	
    	for (Map.Entry<Integer, MenuItemDomainObject> entry: menu.getItemsMap().entrySet()) {
    		Integer destinationDocumentId = entry.getKey();
    		MenuItemDomainObject menuItem = entry.getValue();
    		GetterDocumentReference gtr = new GetterDocumentReference(destinationDocumentId, versionSelector);
    		
    		menuItem.setDocumentReference(gtr);
    		menuItem.setTreeSortKey(new TreeSortKeyDomainObject(menuItem.getTreeSortIndex()));
    	}    	
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
	
	
	public void initContentLoops(TextDocumentDomainObject document) {
		List<ContentLoop> loops = contentLoopDao.getContentLoops(document.getMeta().getId(), document.getVersion().getNo());
		Map<Integer, ContentLoop> loopsMap = new HashMap<Integer, ContentLoop>();
		
		for (ContentLoop loop: loops) {
			loopsMap.put(loop.getIndex(), loop);
			// Loops should have at lest one content.
			List<Content> contents = loop.getContents();
			if (contents.size() == 0) {
				Content content = new Content();
				
				content.setLoopId(loop.getId());
				content.setOrderIndex(0);
				content.setIndex(0);
				
				contents.add(content);
			}
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
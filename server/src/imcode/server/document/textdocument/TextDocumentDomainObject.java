package imcode.server.document.textdocument;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.util.LazilyLoadedObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.UnhandledException;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.ImageDao;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.mapping.DocumentMenusMap;

public class TextDocumentDomainObject extends DocumentDomainObject {
	
	/** 
	 * Modified text indexes. 
	 * 
	 * Every modified text can be saved to history. 
	 * This controlled by setting boolean flag.
	 * 
	 * Required when saving only particular set of text fields.
	 * 
	 * TODO: Move to thread local or make separate call
	 */
	private Map<Integer, Boolean> modifiedTextIndexes = new TreeMap<Integer, Boolean>();

    /**
     * Holds map of loaded images.
     * 
     * Latter can be loaded as lazy object in loadAllLazyLoaded.
     * 
     * For now loaded only by demand.
     */
    private Map<I18nLanguage, Map<Integer, ImageDomainObject>> images
    		= new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();
    
    /**
     * Holds map of loaded texts.
     * 
     * Latter can be loaded as lazy object in loadAllLazyLoaded.
     * 
     * For now loaded only by demand.
     */
    private Map<I18nLanguage, Map<Integer, TextDomainObject>> texts    
    		= new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();
    
    
    private LazilyLoadedObject<CopyableHashMap> includes = new LazilyLoadedObject<CopyableHashMap>(new CopyableHashMapLoader());
    private LazilyLoadedObject<DocumentMenusMap> menus = new LazilyLoadedObject<DocumentMenusMap>(new LazilyLoadedObject.Loader<DocumentMenusMap>() {
        public DocumentMenusMap load() {
            return new DocumentMenusMap();
        }
    });
    private LazilyLoadedObject<TemplateNames> templateNames = new LazilyLoadedObject<TemplateNames>(new LazilyLoadedObject.Loader<TemplateNames>() {
        public TemplateNames load() {
            return new TemplateNames();
        }
    });

    public TextDocumentDomainObject() {
        this(ID_NEW) ;
    }

    public TextDocumentDomainObject(int documentId) {
        setId(documentId);
    }

    public void loadAllLazilyLoaded() {
        super.loadAllLazilyLoaded();

        // TODO i18n: Do not load here !!!
        // Implement exactley as images!!
        // texts.load();
        
        //loadAllImages();
        
        //images.load();        
        includes.load();
        menus.load();
        templateNames.load();
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
               
        // TODO i18n: Clone texts
        // TODO i18n: Clone images        
        //clone.texts = (LazilyLoadedObject) texts.clone();
        //clone.images = (LazilyLoadedObject) images.clone();
        clone.texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();
        clone.images = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();        
        
        clone.includes = (LazilyLoadedObject) includes.clone();
        clone.menus = (LazilyLoadedObject) menus.clone() ;
        clone.templateNames = (LazilyLoadedObject) templateNames.clone() ;
        return clone;
    }

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.TEXT ;
    }

    public Set getChildDocumentIds() {
        Set childDocuments = new HashSet() ;
        for ( Iterator iterator = getMenus().values().iterator(); iterator.hasNext(); ) {
            MenuDomainObject menu = (MenuDomainObject)iterator.next();
            MenuItemDomainObject[] menuItems = menu.getMenuItems() ;
            for ( int i = 0; i < menuItems.length; i++ ) {
                MenuItemDomainObject menuItem = menuItems[i];
                childDocuments.add( new Integer(menuItem.getDocumentId()) ) ;
            }
        }
        return childDocuments ;
    }

    /**
     * Returns images map for current language.
     */
    private Map<Integer, ImageDomainObject> getImagesMap() {
    	I18nLanguage language = I18nSupport.getCurrentLanguage();
    	
    	return getImagesMap(language);
    }
    
    /**
     * Returns all images for language specified in case this language is enabled.
     * 
     * Populates images map with values from the database if language is enabled 
     * on a first run.  
     */
    private synchronized Map<Integer, ImageDomainObject> getImagesMap(I18nLanguage language) {
    	Map<Integer, ImageDomainObject> map = images.get(language);
    	
    	if (map == null) {
    		map = new HashMap<Integer, ImageDomainObject>();
						
    		if (getMeta().getI18nMeta(language).getEnabled()) { 
				ImageDao dao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
				
				List<ImageDomainObject> items = dao.getImages(getId(), language.getId());
				
				for (ImageDomainObject item: items) {
					if (item.getSource() instanceof NullImageSource
							&& getMeta().getMissingI18nShowRule() == Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE
							&& !language.equals(I18nSupport.getDefaultLanguage())) {
						int index = Integer.parseInt(item.getName());
						
						item = dao.getDefaultImage(getId(), index);
						
						item.setLanguage(language);
					}
					
					map.put(Integer.parseInt(item.getName()), item);
				}
    		}
			
			images.put(language, map);
    	}
    	
        return map;
    }    

    public Integer getIncludedDocumentId( int includeIndex ) {
        return (Integer)getIncludesMap().get( new Integer( includeIndex ) );
    }

    private Map getIncludesMap() {
        return (Map) includes.get();
    }

    public MenuDomainObject getMenu( int menuIndex ) {
        Map menusMap = (Map) menus.get();
        MenuDomainObject menu = (MenuDomainObject) menusMap.get( new Integer( menuIndex ) );
        if (null == menu) {
            menu = new MenuDomainObject() ;
            setMenu( menuIndex, menu );
        }
        return menu;
    }

    
    public TextDomainObject getText( int textFieldIndex ) {
    	I18nLanguage language = I18nSupport.getCurrentLanguage();
    	
    	TextDomainObject item = getText(language, textFieldIndex);
    	
        return item;
    }
    
    
	public synchronized TextDomainObject getText(I18nLanguage language, 
			int index) {
		
		if (language == null) {
			throw new IllegalArgumentException("language argument " +
					"can not be null.");			
		}		
		
		Map<Integer, TextDomainObject> map = getTextsMap(language);
		
		TextDomainObject item = map.get(index);				
		
		if (item == null) {
			if (getMeta().getMissingI18nShowRule() == Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE
					&& !language.equals(I18nSupport.getDefaultLanguage())) {
			
				TextDao dao = (TextDao)Imcms.getServices().getSpringBean("textDao");
						
				item = dao.getText(getId(), index, 
					I18nSupport.getDefaultLanguage().getId()); 
			}
			
			if (item == null) {
				item = new TextDomainObject();
			
				item.setId(null);
				item.setMetaId(getId());
				item.setLanguage(language);
				item.setIndex(index);
				item.setText("");
				item.setType(TextDomainObject.TEXT_TYPE_HTML);
			}
				
			map.put(index, item);
		}
				
		return item;
	}    

    /**
     * Returns texts map for current language.
     */
    private Map<Integer, TextDomainObject> getTextsMap() {
    	I18nLanguage language = I18nSupport.getCurrentLanguage();
    	
    	return getTextsMap(language);
    }
    
    /**
     * Returns all texts for language specified.
     */
    private synchronized Map<Integer, TextDomainObject> getTextsMap(
    		I18nLanguage language) {
    	Map<Integer, TextDomainObject> map = texts.get(language);
    	
    	if (map == null) {
    		map = new HashMap<Integer, TextDomainObject>();
    		
    		if (getMeta().getI18nMeta(language).getEnabled()) {
						
				TextDao dao = (TextDao)Imcms.getServices().getSpringBean("textDao");
				
				List<TextDomainObject> items = dao.getTexts(getId(), language.getId());
				
				for (TextDomainObject item: items) {
					map.put(item.getIndex(), item);
				}
    		}
			
			texts.put(language, map);
    	}
    	
        return map;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitTextDocument(this) ;
    }

    /**
     * Removes all image.
     */
    public synchronized void removeAllImages() {
        images.clear();
    }

    public void removeAllIncludes() {
        getIncludesMap().clear();
    }

    public void removeAllMenus() {
        getMenusMap().clear();
    }

    private Map getMenusMap() {
        return (Map) menus.get();
    }

    public void removeAllTexts() {
        texts.clear();
    }

    public void setInclude( int includeIndex, int includedDocumentId ) {
        getIncludesMap().put( new Integer( includeIndex ), new Integer( includedDocumentId ) );
    }

    public void setMenu( int menuIndex, MenuDomainObject menu ) {
        getMenusMap().put( new Integer( menuIndex ), menu );
    }
    
        
    /**
     * Sets text to currently active language.
     */    
    public void setText( int textIndex, TextDomainObject text ) {
    	setText(I18nSupport.getCurrentLanguage(), textIndex, text);
    }
    
    /**
     * Sets image.
     */   
    public void setText(I18nLanguage language, int imageIndex,
    		TextDomainObject text) {
    	
    	Map<Integer, TextDomainObject> map = getTextsMap(language);
    	
    	map.put(imageIndex, text);
    }    
    

    public Map getIncludes() {
        return Collections.unmodifiableMap( getIncludesMap() );
    }

    public Map<Integer, MenuDomainObject> getMenus() {
        return Collections.unmodifiableMap( getMenusMap() );
    }

    public String getTemplateName() {
        return getTemplateNames().getTemplateName();
    }

    private TemplateNames getTemplateNames() {
        return (TemplateNames) templateNames.get();
    }

    public int getTemplateGroupId() {
        return getTemplateNames().getTemplateGroupId();
    }

    public Map<Integer, TextDomainObject> getTexts() {
        return Collections.unmodifiableMap( getTextsMap() );
    }

    public void setTemplateName( String templateName ) {
        getTemplateNames().setTemplateName(templateName);
    }

    public void setTemplateGroupId( int v ) {
        getTemplateNames().setTemplateGroupId(v);
    }

    public String getDefaultTemplateName() {
        return getTemplateNames().getDefaultTemplateName();
    }

    public void setDefaultTemplateId( String defaultTemplateId ) {
        getTemplateNames().setDefaultTemplateName(defaultTemplateId);
    }

    public void removeInclude( int includeIndex ) {
        getIncludesMap().remove( new Integer( includeIndex )) ;
    }

    public void setLazilyLoadedMenus(LazilyLoadedObject menus) {
        this.menus = menus;
    }

    public void setLazilyLoadedImages(LazilyLoadedObject images) {
    	// TODO i18n: rewrite or remove latter.
        //this.images = images ;
    }

    public void setLazilyLoadedIncludes(LazilyLoadedObject includes) {
        this.includes = includes ;
    }

    public void setLazilyLoadedTexts(LazilyLoadedObject texts) {
    	// TODO i18n: rewrite or remove latter.
        //this.texts = texts;
    }

    public String getDefaultTemplateNameForRestricted1() {
        return getTemplateNames().getDefaultTemplateNameForRestricted1();
    }

    public String getDefaultTemplateNameForRestricted2() {
        return getTemplateNames().getDefaultTemplateNameForRestricted2();
    }

    public void setDefaultTemplateIdForRestricted1(String defaultTemplateIdForRestricted1) {
        getTemplateNames().setDefaultTemplateNameForRestricted1(defaultTemplateIdForRestricted1);
    }

    public void setDefaultTemplateIdForRestricted2(String defaultTemplateIdForRestricted2) {
        getTemplateNames().setDefaultTemplateNameForRestricted2(defaultTemplateIdForRestricted2);
    }

    public void setLazilyLoadedTemplateIds(LazilyLoadedObject templateIds) {
        this.templateNames = templateIds;
    }

    private static class CopyableHashMapLoader implements LazilyLoadedObject.Loader<CopyableHashMap> {

        public CopyableHashMap load() {
            return new CopyableHashMap();
        }
    }

    public static class TemplateNames implements LazilyLoadedObject.Copyable<TemplateNames>, Cloneable {
        private String templateName;
        private int templateGroupId;
        private String defaultTemplateName;
        private String defaultTemplateNameForRestricted1 ;
        private String defaultTemplateNameForRestricted2 ;

        public TemplateNames copy() {
            return (TemplateNames) clone() ;
        }

        public Object clone() {
            try {
                return super.clone();
            } catch ( CloneNotSupportedException e ) {
                throw new UnhandledException(e);
            }
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public int getTemplateGroupId() {
            return templateGroupId;
        }

        public void setTemplateGroupId(int templateGroupId) {
            this.templateGroupId = templateGroupId;
        }

        public String getDefaultTemplateName() {
            return defaultTemplateName;
        }

        public void setDefaultTemplateName(String defaultTemplateName) {
            this.defaultTemplateName = defaultTemplateName;
        }

        public String getDefaultTemplateNameForRestricted1() {
            return defaultTemplateNameForRestricted1;
        }

        public void setDefaultTemplateNameForRestricted1(String defaultTemplateNameForRestricted1) {
            this.defaultTemplateNameForRestricted1 = defaultTemplateNameForRestricted1;
        }

        public String getDefaultTemplateNameForRestricted2() {
            return defaultTemplateNameForRestricted2;
        }

        public void setDefaultTemplateNameForRestricted2(String defaultTemplateNameForRestricted2) {
            this.defaultTemplateNameForRestricted2 = defaultTemplateNameForRestricted2;
        }
    }

    
	public Map<Integer, Boolean> getModifiedTextIndexes() {
		return modifiedTextIndexes;
	}
	
	public void addModifiedTextIndex(int index, boolean saveToHistory) {
		modifiedTextIndexes.put(index, saveToHistory);
	}
	
	public void removeModifiedTextIndex(int index) {
		modifiedTextIndexes.remove(index);
	}
	
	public void removeAllModifiedTextIndexs() {
		modifiedTextIndexes.clear();
	}
	
    /**
     * @return Image id mapped to image for current language.
     */
    public Map<Integer, ImageDomainObject> getImages() {
        return Collections.unmodifiableMap( getImagesMap() );
    }
    
	/**
	 * Sets images.
	 * This method currently used by only administration interface.
	 */		
    public synchronized void setImages(int imageIndex, 
    		Collection<ImageDomainObject> images) {
    	
    	for (ImageDomainObject image: images) {
    		I18nLanguage language = image.getLanguage();
    		
    		setImage(language, imageIndex, image);
    	}
    }	
	
    /**
     * Sets image to currently active language.
     * 
     * Not in use.
     */
    public void setImage(int imageIndex, ImageDomainObject image) {
    	setImage(I18nSupport.getCurrentLanguage(), imageIndex, image);
    }
    
    /**
     * Sets image.
     */   
    public void setImage(I18nLanguage language, int imageIndex,
    		ImageDomainObject image) {
    	
    	Map<Integer, ImageDomainObject> imagesMap = getImagesMap(language);
    	
    	imagesMap.put(imageIndex, image);
    }	
	
    /**
     * Returns all images. 
     */
	public Map<I18nLanguage, Map<Integer, ImageDomainObject>> getAllImages() {
		return images;
	}
	
	/**
	 * Returns i18n-ed image for language bound to the current thread. 
	 * 
	 * If image for that language is not exists
	 * then returns image according to language rule set in meta. 
	 *  
	 * @return ImageDomainObject for language bound to the current thread. 
	 */
	public ImageDomainObject getImage(int imageIndex) {
		I18nLanguage language = I18nSupport.getCurrentLanguage();
		ImageDomainObject image = getImage(language, imageIndex);
					
		return image;
	}
	
	/**
	 * Returns image for language specified.
	 */
	public synchronized ImageDomainObject getImage(I18nLanguage language, int imageIndex) {
		if (language == null) {
			throw new IllegalArgumentException("language argument " +
					"can not be null.");			
		}		
		
		Map<Integer, ImageDomainObject> imagesMap = getImagesMap(language);
		
		ImageDomainObject image = imagesMap.get(imageIndex);
		
		if (image == null) {
			if (getMeta().getMissingI18nShowRule() == Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE
					&& !language.equals(I18nSupport.getDefaultLanguage())) {			
			
				ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
						
				image = imageDao.getDefaultImage(getId(), imageIndex);
			}
			
			if (image == null) {
				image = new ImageDomainObject();
				
				image.setId(null);
				image.setMetaId(getId());
				image.setLanguage(language);
				image.setName("" + imageIndex);
			}

			imagesMap.put(imageIndex, image);
		}
				
		return image;
	}	
	
	/**
	 * Returns languages mapped to images.
	 * This method is used by administration interface.
	 * 
	 * @return languages mapped to images. 
	 */	
/*	public synchronized Map<I18nLanguage, ImageDomainObject> getI18nImageMap(int imageIndex) {
		Map<I18nLanguage, ImageDomainObject> i18nImageMap = images.get(imageIndex);
		
		if (i18nImageMap == null) {
			ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
		        
			i18nImageMap = imageDao.getImagesMap(getId(), imageIndex);
			
			images.put(imageIndex, i18nImageMap);
		}
		
		return i18nImageMap;
	}	
	
*/
  
	/*
    private synchronized void loadAllImages() {
    	I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
    	
		ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");

		images = imageDao.getImagesMap(getId());
    }
    
    private synchronized void loadAllTexts() {
    	I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
    	
		TextDao textDao = (TextDao)Imcms.getServices().getSpringBean("textDao");

		texts = textDao.getAll(getId());
    } 
    */   
}
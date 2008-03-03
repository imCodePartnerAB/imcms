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

    private LazilyLoadedObject<CopyableHashMap> texts = new LazilyLoadedObject<CopyableHashMap>(new CopyableHashMapLoader());
    //private LazilyLoadedObject<CopyableHashMap> images = new LazilyLoadedObject<CopyableHashMap>(new CopyableHashMapLoader());
    
    // Map of image index to image map with key of language code. 
    
    /**
     * Holds map of loaded images.
     * 
     * Latter can be loaded as lazy object in loadAllLazyLoaded.
     * 
     * For now loaded only on demand.
     */
    private Map<I18nLanguage, Map<Integer, ImageDomainObject>> images
    		= new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();
    
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

        // TODO i18n: Do not laad here !!!
        // Implement exactley as images!!
        texts.load();
        
        //loadAllImages();
               
        //images.load();        
        includes.load();
        menus.load();
        templateNames.load();
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        clone.texts = (LazilyLoadedObject) texts.clone();
        
        // TODO i18n: Clone images
        //clone.images = (LazilyLoadedObject) images.clone();
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
     * Returns all images for language specified.
     * 
     * Populates images map with values from the database 
     * on a first run.  
     */
    private synchronized Map<Integer, ImageDomainObject> getImagesMap(I18nLanguage language) {
    	Map<Integer, ImageDomainObject> imagesMap = images.get(language);
    	
    	if (imagesMap == null) {
			imagesMap = new HashMap<Integer, ImageDomainObject>();
						
			ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
			
			List<ImageDomainObject> _images = imageDao.getImages(getId(), language.getId());
			
			for (ImageDomainObject image: _images) {
				imagesMap.put(Integer.parseInt(image.getName()), image);
			}			
			
			images.put(language, imagesMap);
    	}
    	
        return imagesMap;
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
        return (TextDomainObject)getTextsMap().get( new Integer( textFieldIndex ) );
    }

    private Map getTextsMap() {
        return (Map)texts.get();
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitTextDocument(this) ;
    }

    /**
     * Removes all images  
     * TODO: find when it is used.
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
        getTextsMap().clear();
    }

    public void setInclude( int includeIndex, int includedDocumentId ) {
        getIncludesMap().put( new Integer( includeIndex ), new Integer( includedDocumentId ) );
    }

    public void setMenu( int menuIndex, MenuDomainObject menu ) {
        getMenusMap().put( new Integer( menuIndex ), menu );
    }

    public void setText( int textIndex, TextDomainObject text ) {
        getTextsMap().put( new Integer( textIndex ), text );
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
        this.texts = texts;
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
	 * If image for that language is not exists (examined by checking its source)
	 * then returns image according to language rule set in meta. 
	 *  
	 * @return ImageDomainObject for language bound to the current thread. 
	 */
	public ImageDomainObject getImage(int imageIndex) {
		I18nLanguage language = I18nSupport.getCurrentLanguage();
		ImageDomainObject image = getImage(language, imageIndex);
			
		if (image.getSource() instanceof NullImageSource) {
			if (getMeta().getMissingI18nShowRule() == Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE
					&& !language.equals(I18nSupport.getDefaultLanguage())) {
				image = getImage(I18nSupport.getDefaultLanguage(), imageIndex);
			}
		}
		
		return image;
	}
	
	/**
	 * Returns i18n-ed image. 
	 * 
	 * If image does not exists returns default image.
	 */
	public synchronized ImageDomainObject getImage(I18nLanguage language, int imageIndex) {
		if (language == null) {
			throw new IllegalArgumentException("language argument " +
					"can not be null.");			
		}		
		
		Map<Integer, ImageDomainObject> imagesMap = getImagesMap(language);
		
		ImageDomainObject image = imagesMap.get(imageIndex);
		
		if (image == null) {
			ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
						
			image = imageDao.getDefaultImage(getId(), imageIndex); 
			
			if (image == null) {
				image = new ImageDomainObject();
			} 
			
			image.setId(null);
			image.setMetaId(getId());
			image.setLanguage(language);
			image.setName("" + imageIndex);
				
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
  
    private synchronized void loadAllImages() {
		ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");

		images = imageDao.getImagesMap(getId());
    }
}
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

import org.apache.commons.lang.UnhandledException;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.ImageDao;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.mapping.DocumentMenusMap;

//TODO: Refactor out text and image loading logic into lazy load object and optimize:
// getTextsMap, getImagesMap, getText, getImage
public class TextDocumentDomainObject extends DocumentDomainObject {
	
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
    
    
    /**
     * Returns images map from the database.
     */
    public static Map<Integer, ImageDomainObject> getOriginalImagesMap(I18nLanguage language, Integer metaId) {
    	Map<Integer, ImageDomainObject> map = new HashMap<Integer, ImageDomainObject>(); 
    	
   		ImageDao dao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
   		List<ImageDomainObject> images = dao.getDocumentImagesByLanguage(metaId, language.getId());
    		
		for (ImageDomainObject image: images) {
			int index = Integer.parseInt(image.getName()); 
			map.put(index, image);
		}
		
    	return map;
    }
        
    /**
     * Returns texts map from the database.
     */
    public static Map<Integer, TextDomainObject> getOrigianlTextsMap(I18nLanguage language, Integer metaId) {
    	Map<Integer, TextDomainObject> map = new HashMap<Integer, TextDomainObject>(); 
    	
   		TextDao dao = (TextDao)Imcms.getServices().getSpringBean("textDao");
   		List<TextDomainObject> texts = dao.getTexts(metaId, language.getId());
    		
		for (TextDomainObject text: texts) {
			map.put(text.getIndex(), text);
		}
		
    	return map;
    }    
    

    public TextDocumentDomainObject() {
        this(ID_NEW) ;
    }

    public TextDocumentDomainObject(int documentId) {
        setId(documentId);
    }

    public void loadAllLazilyLoaded() {
        super.loadAllLazilyLoaded();

        loadTexts();
        loadImages();
        
        includes.load();
        menus.load();
        templateNames.load();
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        
        //clone.texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>(texts);
        //clone.images = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>(images);
        
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
     * This method loads images when invoked for the first time.  
     */
    
    // TODO: Refactor out into lazy loaded object and optimize 
    private Map<Integer, ImageDomainObject> getImagesMap(I18nLanguage language) {
    	Map<Integer, ImageDomainObject> map = images.get(language);    	
    	
    	if (map == null) {
    		Integer metaId = getId();
    		
   			map = getOriginalImagesMap(language, metaId);
   			
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
    	
    	TextDomainObject text = getText(language, textFieldIndex);
    	
        return text;
    }
    
    
	public TextDomainObject getText(I18nLanguage language, 
			int index) {
		
		getMenu(0);
		
		if (language == null) {
			throw new IllegalArgumentException("language argument " +
					"can not be null.");			
		}		
		
		Map<Integer, TextDomainObject> map = getTextsMap(language);
		
		return map.get(index);
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
    public Map<Integer, TextDomainObject> getTextsMap(I18nLanguage language) {
    	Map<Integer, TextDomainObject> map = texts.get(language);
    	
    	if (map == null) {
    		Meta meta = getMeta();
    		Integer metaId = meta.getMetaId();
    		map = getOrigianlTextsMap(language, metaId);    		
    			
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
        // we can not use images.clear() since all clones share same images;

        images = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();
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
        // we can not use texts.clear() since all clones share same texts;

        texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();
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
     * Sets text.
     */   
    public void setText(I18nLanguage language, int index,
    		TextDomainObject text) {

    	Integer metaId = getId();    	
    	Map<Integer, TextDomainObject> map = getTextsMap(language);
    	TextDomainObject existingText = map.get(index);
    	boolean modified = true;
    	Long id = null;
    	
    	if (existingText != null) {
    		String oldValue = existingText.getText();
    		String newValue = text.getText();    		
    		int newType = text.getType(); 
    		int oldType = existingText.getType(); 
    		
    		if (oldValue == null) oldValue = "";
    		
    		id = existingText.getId();
    		modified = oldType != newType || !oldValue.equals(newValue);
    	}
    	
    	text.setId(id);
    	text.setMetaId(metaId);
    	text.setIndex(index);
    	text.setLanguage(language);
    	text.setModified(modified);
    	
    	map.put(index, text);    	
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
    
    public Map<Integer, TextDomainObject> getTexts(I18nLanguage language) {
        return Collections.unmodifiableMap( getTextsMap(language) );
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
        // There is no lazy loaders for images
        //this.images = images ;
    }

    public void setLazilyLoadedIncludes(LazilyLoadedObject includes) {
        this.includes = includes ;
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
	
    /**
     * @return Image id mapped to image for current language.
     */
    public Map<Integer, ImageDomainObject> getImages() {
        return Collections.unmodifiableMap( getImagesMap() );
    }
    
    /**
     * @return Image id mapped to image for language specified.
     */
    public Map<Integer, ImageDomainObject> getImages(I18nLanguage language) {
        return Collections.unmodifiableMap( getImagesMap(language) );
    }    
    
	/**
	 * Sets images.
	 * This method is only used by administration interface.
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
    public void setImage(I18nLanguage language, int index,
    		ImageDomainObject image) {
    	
    	Integer metaId = getId();    	
    	Map<Integer, ImageDomainObject> map = getImagesMap(language);
    	
    	ImageDomainObject existingImage = map.get(index);
    	
    	if (existingImage != null) {    		
    		image.setId(existingImage.getId());
    	} else {    		
        	image.setId(null);        	
    	}

    	image.setMetaId(metaId);
    	image.setLanguage(language);
    	image.setName("" + index);
    	
    	map.put(index, image);
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
	public ImageDomainObject getImage(I18nLanguage language, int index) {
		if (language == null) {
			throw new IllegalArgumentException("language argument " +
					"can not be null.");			
		}		
		
		Map<Integer, ImageDomainObject> map = getImagesMap(language);		
		
		return map.get(index);
	}

    private void loadTexts() {
        for (I18nLanguage language: I18nSupport.getLanguages()) {
            getTextsMap(language);
        }
    }

    private void loadImages() {
        for (I18nLanguage language: I18nSupport.getLanguages()) {
            getImagesMap(language);
        }
    }

    @Override
    public void cloneShared() {
        cloneTexts();
        cloneImages();
    }

    private void cloneImages() {
        int metaId = getId();
        Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesClone
    		= new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();

        for(Map.Entry<I18nLanguage, Map<Integer, ImageDomainObject>> languageEntry: images.entrySet()) {
            Map<Integer, ImageDomainObject> imagesMap = languageEntry.getValue();
            Map<Integer, ImageDomainObject> imagesMapClone = new HashMap<Integer, ImageDomainObject>();

            imagesClone.put(languageEntry.getKey(), imagesMapClone);

            for (Map.Entry<Integer, ImageDomainObject> imagesEntry: imagesMap.entrySet()) {
                ImageDomainObject image = imagesEntry.getValue().clone();
                //image.setModified(true);
                image.setId(null);
                //image.setMetaId(metaId);

                imagesMapClone.put(imagesEntry.getKey(), image);
            }
        }

        images = imagesClone;
    }

    private void cloneTexts() {
        int metaId = getId();
        Map<I18nLanguage, Map<Integer, TextDomainObject>> textsClone    
    		= new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();

        for(Map.Entry<I18nLanguage, Map<Integer, TextDomainObject>> languageEntry: texts.entrySet()) {
            Map<Integer, TextDomainObject> textsMap = languageEntry.getValue();
            Map<Integer, TextDomainObject> textsMapClone = new HashMap<Integer, TextDomainObject>();

            textsClone.put(languageEntry.getKey(), textsMapClone);

            for (Map.Entry<Integer, TextDomainObject> textsEntry: textsMap.entrySet()) {
                TextDomainObject text = textsEntry.getValue().clone();
                text.setId(null);
                //text.setMetaId(metaId);
                text.setModified(true);

                textsMapClone.put(textsEntry.getKey(), text);
            }
        }

        texts = textsClone;
    }
}
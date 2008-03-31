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

        // TODO i18n: Do not load here
        // texts.load();
        // images.load();
        
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
        
        //clone.texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();
        //clone.images = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();        
        
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
    private synchronized Map<Integer, ImageDomainObject> getImagesMap(I18nLanguage language) {
    	Map<Integer, ImageDomainObject> map = images.get(language);    	
    	
    	if (map == null) {
    		I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
    		Map<Integer, ImageDomainObject> defaultMap = images.get(defaultLanguage);
    		
    		if (defaultMap == null) {
    			defaultMap = getOriginalImagesMap(defaultLanguage);				
				images.put(defaultLanguage, defaultMap);
    		}
    		
    		if (language.equals(defaultLanguage)) {
    			map = defaultMap;
    		} else {
    			map = getOriginalImagesMap(language);
    			
        		Meta meta = getMeta();
        		boolean disabled = !meta.getI18nMeta(language).getEnabled(); 
        		boolean allowsSubstitionWithDefault 
        				= meta.getMissingI18nShowRule() == Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE;

        		if (allowsSubstitionWithDefault) {
    				for (Map.Entry<Integer, ImageDomainObject> entry: map.entrySet()) {
    					int index = entry.getKey();
    					ImageDomainObject image = entry.getValue();
    					
    					if (disabled || image.getSource() instanceof NullImageSource) {
    						ImageDomainObject defaultImage = getImage(defaultLanguage, index);
    						
    						image = createSubstitutionImage(defaultImage, language);
    						
        					entry.setValue(image);
    					}    					
    				}
        		} else if (disabled){
        			int metaId = getId();
    				for (Map.Entry<Integer, ImageDomainObject> entry: map.entrySet()) {
    					int index = entry.getKey();
    					ImageDomainObject image = createSubstitutionImage(metaId,index, language);
 
   						entry.setValue(image);
    				}        			
        		}
        		
        		images.put(language, map);
    		}
    	}
    	
        return map;
    }    
    
    /**
     * Creates substitution image. 
     */
    private static ImageDomainObject createSubstitutionImage(int metaId, int index, I18nLanguage language) {    	
    	ImageDomainObject image = new ImageDomainObject();
    	
    	image.setMetaId(metaId);    	
    	image.setName("" + index);
    	image.setLanguage(language);
    	image.setSubstitution(true);
    	
    	return image;
    }
    
    
    /**
     * Creates substitution text. 
     */
    public static TextDomainObject createSubstitutionText(int metaId, int index, I18nLanguage language) {    	
    	TextDomainObject text = new TextDomainObject();
    	
    	text.setMetaId(metaId);
    	text.setIndex(index);
    	text.setLanguage(language);
    	text.setSubstitution(true);
    	
    	return text;
    }    
    
    /**
     * Creates substitution text. 
     */
    public static TextDomainObject createSubstitutionText(
    		TextDomainObject originalText, I18nLanguage newLanguage) {
    	
    	TextDomainObject text = originalText.clone();
    	
    	text.setId(null);
    	text.setLanguage(newLanguage);
    	text.setSubstitution(true);
    	
    	return text;
    }    
    
    
    /**
     * Create substitution image from original image.
     * 
     * @param originalImage original image.
     * @param newLanguage cloned image language. 
     */
    private static ImageDomainObject createSubstitutionImage(ImageDomainObject 
    		originalImage, I18nLanguage newLanguage) {
    	
    	ImageDomainObject newImage = originalImage.clone();
    	
    	newImage.setId(null);
    	newImage.setSubstitution(true);
    	newImage.setLanguage(newLanguage);
    	
    	return newImage;
    } 
    
    
    
    /**
     * Returns original images map.
     */
    private Map<Integer, ImageDomainObject> getOriginalImagesMap(I18nLanguage language) {
    	Map<Integer, ImageDomainObject> map = new HashMap<Integer, ImageDomainObject>(); 
    	
   		ImageDao dao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
   		List<ImageDomainObject> images = dao.getImages(getId(), language.getId());
    		
		for (ImageDomainObject image: images) {
			int index = Integer.parseInt(image.getName()); 
			map.put(index, image);
		}
		
    	return map;
    }
    
    
    /**
     * Returns original texts map.
     */
    private Map<Integer, TextDomainObject> getOriginalTextsMap(I18nLanguage language) {
    	Map<Integer, TextDomainObject> map = new HashMap<Integer, TextDomainObject>(); 
    	
   		TextDao dao = (TextDao)Imcms.getServices().getSpringBean("textDao");
   		List<TextDomainObject> texts = dao.getTexts(getId(), language.getId());
    		
		for (TextDomainObject text: texts) {
			map.put(text.getIndex(), text);
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
		TextDomainObject text = map.get(index);				
		
		if (text == null) {
			I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
			Map<Integer, TextDomainObject> defaultMap = getTextsMap(defaultLanguage);			
			TextDomainObject defaultText = defaultMap.get(index);
			
			if (defaultText == null) {
				defaultText = createSubstitutionText(getId(), index, defaultLanguage);
				defaultMap.put(index, defaultText);
			}
			
			if (language.equals(defaultLanguage)) {
				text = defaultText;
			} else {
				// if allows substitution
        		if (getMeta().getMissingI18nShowRule() 
        				== Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE) {
				
        			text = createSubstitutionText(defaultText, language);
        		}
        		
        		map.put(index, text);				
			}
		}
				
		return text;
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
    		I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
    		
    		Map<Integer, TextDomainObject> defaultMap = texts.get(defaultLanguage);
    		
    		if (defaultMap == null) {
    			defaultMap = getOriginalTextsMap(defaultLanguage);				
    			texts.put(defaultLanguage, defaultMap);
    		}
    		
    		if (language.equals(defaultLanguage)) {
    			map = defaultMap;
    		} else {
    			map = getOriginalTextsMap(language);
    		
    			Meta meta = getMeta();
    			boolean disabled = !meta.getI18nMeta(language).getEnabled(); 
    			boolean allowsSubstitionWithDefault 
    				= meta.getMissingI18nShowRule() == Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE;
    			int metaId = getId();
    			
				for (Map.Entry<Integer, TextDomainObject> entry: map.entrySet()) {
					int index = entry.getKey();
					TextDomainObject text = entry.getValue();
					
					if (disabled) {
						if (allowsSubstitionWithDefault) {
							TextDomainObject defaultText = getText(defaultLanguage, index);
							
							text = createSubstitutionText(defaultText, language);
						} else {
							text = createSubstitutionText(metaId, index, language);
						}
					} else if (text.getText().length() == 0) {
						if (allowsSubstitionWithDefault) {
							TextDomainObject defaultText = getText(defaultLanguage, index);
							
							text = createSubstitutionText(defaultText, language);
						} 						
					}
					
					entry.setValue(text);
				}
    			
    			texts.put(language, map);
    		}
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
	public synchronized ImageDomainObject getImage(I18nLanguage language, int index) {
		if (language == null) {
			throw new IllegalArgumentException("language argument " +
					"can not be null.");			
		}		
		
		Map<Integer, ImageDomainObject> map = getImagesMap(language);		
		ImageDomainObject image = map.get(index);
		
		if (image == null) {
			I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
			Map<Integer, ImageDomainObject> defaultMap = getImagesMap(defaultLanguage);			
			ImageDomainObject defaultImage = defaultMap.get(index);
			
			if (defaultImage == null) {
				defaultImage = createSubstitutionImage(getId(), index, defaultLanguage);
				defaultMap.put(index, defaultImage);
			}
			
			if (language.equals(defaultLanguage)) {
				image = defaultImage;
			} else {
				// if allows substitution
        		if (getMeta().getMissingI18nShowRule() 
        				== Meta.MissingI18nShowRule.SHOW_IN_DEFAULT_LANGUAGE) {
				
        			createSubstitutionImage(defaultImage, language);
        		}
        		
        		map.put(index, image);				
			}
		}
		
		return image;
	}	
}
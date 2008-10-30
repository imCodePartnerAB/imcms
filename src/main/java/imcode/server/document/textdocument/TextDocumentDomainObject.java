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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Include;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.ImageDao;
import com.imcode.imcms.dao.IncludeDao;
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
    
    private List<Include> includes = new LinkedList<Include>();        
    
    private Map<Integer, Integer> includesMap = new HashMap<Integer, Integer>();
    
    private TemplateNames templateNames = new TemplateNames();
        
    private Map<Integer, MenuDomainObject> menusMap = new HashMap<Integer, MenuDomainObject>();  
    
    
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
    
    
    /**
     * Returns texts map from the database.
     */
    public static Map<Integer, Include> getOrigianlIncludesMap(Integer metaId) {
    	Map<Integer, Include> map = new HashMap<Integer, Include>(); 
    	
   		IncludeDao dao = (IncludeDao)Imcms.getServices().getSpringBean("includeDao");
   		List<Include> includes = dao.getDocumentIncludes(metaId);
    		
		for (Include include: includes) {
			map.put(include.getIndex(), include);
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
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        
        //clone.texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>(texts);
        //clone.images = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>(images);        
        //clone.includes =
        
        //clone.menus = (LazilyLoadedObject) menus.clone() ;
        
        clone.templateNames = (TemplateNames) templateNames.clone() ;
        
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
        return includesMap.get(includeIndex);
    }
    

    public MenuDomainObject getMenu(int menuIndex) {
        MenuDomainObject menu = menusMap.get(menuIndex);
        /*
        if (null == menu) {
            menu = new MenuDomainObject() ;
            setMenu( menuIndex, menu );
        }
        */
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

    // TODO: refactor
    public void removeAllIncludes() {
    	// we can not use includes.clear() since all clones share same data
    	
        setIncludes(new LinkedList<Include>());
    }

    public void removeAllMenus() {
        menusMap.clear();
    }

    public void removeAllTexts() {
        // we can not use texts.clear() since all clones share same texts;

        texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();
    }

    
    // TODO: refactor
    public void setInclude( int includeIndex, int includedDocumentId ) {
    	Include include = null; 
    	
    	for (Include i: includes) {
    		if (i.getIndex().equals(includeIndex)) {
    			include = i;
    			break;
    		}
    	}
    	
    	if (include == null) {
    		include = new Include();
    		include.setMetaId(getId());    		
    		include.setIndex(includeIndex);
    		
    		includes.add(include);
    	}
    	
    	include.setIncludedMetaId(includedDocumentId);
    	
    	setIncludes(includes);
    }
    

    public void setMenu( int menuIndex, MenuDomainObject menu ) {
        menusMap.put(menuIndex, menu);
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
    
    /**
     * TODO: Refactor remove
     * @return
     */
    @Deprecated
    public Map getIncludesMap() {
        return Collections.unmodifiableMap(includesMap);
    }

    public Map<Integer, MenuDomainObject> getMenus() {
        return Collections.unmodifiableMap(menusMap);
    }

    public String getTemplateName() {
        return templateNames.getTemplateName();
    }

    public int getTemplateGroupId() {
        return templateNames.getTemplateGroupId();
    }

    public Map<Integer, TextDomainObject> getTexts() {
        return Collections.unmodifiableMap( getTextsMap() );
    }
    
    public Map<Integer, TextDomainObject> getTexts(I18nLanguage language) {
        return Collections.unmodifiableMap( getTextsMap(language) );
    }    

    public void setTemplateName( String templateName ) {
    	templateNames.setTemplateName(templateName);
    }

    public void setTemplateGroupId( int v ) {
    	templateNames.setTemplateGroupId(v);
    }

    public String getDefaultTemplateName() {
        return templateNames.getDefaultTemplateName();
    }

    public void setDefaultTemplateId( String defaultTemplateId ) {
    	templateNames.setDefaultTemplateName(defaultTemplateId);
    }

    public void removeInclude( int includeIndex ) {
    	int size = includes.size();
    	
    	for (int i = 0; i < size; i++) {
    		Include include = includes.get(i);
    		
    		if (include.getIndex().equals(includeIndex)) {
    			includes.remove(i);
    			setIncludes(includes);
    			break;
    		}
    	}
    }

    public String getDefaultTemplateNameForRestricted1() {
        return templateNames.getDefaultTemplateNameForRestricted1();
    }

    public String getDefaultTemplateNameForRestricted2() {
        return templateNames.getDefaultTemplateNameForRestricted2();
    }

    public void setDefaultTemplateIdForRestricted1(String defaultTemplateIdForRestricted1) {
    	templateNames.setDefaultTemplateNameForRestricted1(defaultTemplateIdForRestricted1);
    }

    public void setDefaultTemplateIdForRestricted2(String defaultTemplateIdForRestricted2) {
    	templateNames.setDefaultTemplateNameForRestricted2(defaultTemplateIdForRestricted2);
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
        cloneIncludes();
        cloneTemplateNames();
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
    
    private void cloneTemplateNames() {
    	templateNames = (TemplateNames)templateNames.clone();
    }
    
    private void cloneIncludes() {
    	List<Include> includesClone = new LinkedList<Include>();
    	
    	for (Include include: includes) {
        	Include includeClone = (Include)include.clone();
        	
        	includeClone.setId(null);
        	includeClone.setMetaId(null);
        	
        	includesClone.add(includeClone);
    	}
    	
    	setIncludes(includesClone);
    }    
    
    
    /**
     * Temporal solution. Includes set by TextDocumentInitializer.
     * TODO: refactor.  
     */
    public void setIncludes(List<Include> includes) {
    	this.includes = includes;
    	this.includesMap = new HashMap<Integer, Integer>();
    	
		for (Include include: includes) {
			includesMap.put(include.getIndex(), include.getIncludedMetaId());
		}
    }

	public List<Include> getIncludes() {
		return includes;
	}

	public TemplateNames getTemplateNames() {
		return templateNames;
	}

	public void setTemplateNames(TemplateNames templateNames) {
		this.templateNames = templateNames;
	}

	public void setMenusMap(Map<Integer, MenuDomainObject> menusMap) {
		this.menusMap = menusMap;
	}
}
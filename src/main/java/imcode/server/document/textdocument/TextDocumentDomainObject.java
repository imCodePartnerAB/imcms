package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;

public class TextDocumentDomainObject extends DocumentDomainObject {
		
	/**
     * Images.
     */
    private Map<I18nLanguage, Map<Integer, ImageDomainObject>> images
    		= new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>();
    
    /**
     * Texts.
     */
    private Map<I18nLanguage, Map<Integer, TextDomainObject>> texts    
    		= new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();
    
    private Map<Integer, Integer> includesMap = new HashMap<Integer, Integer>();
    
    private TemplateNames templateNames = new TemplateNames();
        
    private Map<Integer, MenuDomainObject> menusMap = new HashMap<Integer, MenuDomainObject>();  
        
    public TextDocumentDomainObject() {
        this(ID_NEW) ;
    }

    public TextDocumentDomainObject(int documentId) {
        setId(documentId);
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        
        //clone.texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>(texts);
        //clone.images = new HashMap<I18nLanguage, Map<Integer, ImageDomainObject>>(images);        
        //clone.includes =
        
        //clone.menus = (LazilyLoadedObject) menus.clone() ;
        
        //clone.templateNames = (TemplateNames) templateNames.clone() ;
        
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
   			map = new HashMap<Integer, ImageDomainObject>();   		
      		images.put(language, map);
    	}
    	
        return map;
    }    
    
    public Integer getIncludedDocumentId( int includeIndex ) {
        return includesMap.get(includeIndex);
    }
    

    public MenuDomainObject getMenu(int menuIndex) {
        MenuDomainObject menu = menusMap.get(menuIndex);
        
        //TODO: REPLACE with const MenuDomainObject()???
        
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
    
    /**
     * @return TextDomainObject or null if text can not be found.
     */
	public TextDomainObject getText(I18nLanguage language, int index) {
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
    		map = new HashMap<Integer, TextDomainObject>();
    			
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
    	includesMap = new HashMap<Integer, Integer>();
    }

    public void removeAllMenus() {
        menusMap = new HashMap<Integer, MenuDomainObject>();
    }

    public void removeAllTexts() {
        // we can not use texts.clear() since all clones share same texts;

        texts = new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();
    }

    
    // TODO: refactor
    public void setInclude( int includeIndex, int includedDocumentId ) {
    	includesMap.put(includeIndex, includedDocumentId);
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
    public void setText(I18nLanguage language, int index, TextDomainObject text) {        	
    	Map<Integer, TextDomainObject> map = getTextsMap(language);
    	TextDomainObject oldText = map.get(index);
    	TextDomainObject newText = text.clone();
    	
    	if (oldText != null) {
    		newText.setId(oldText.getId());
    	}
    	
    	newText.setIndex(index);
    	newText.setLanguage(language);
    	newText.setModified(true);
    	
    	map.put(index, newText);
    } 
    
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
    	includesMap.remove(includeIndex);
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
	 * 
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
    public void setImage(I18nLanguage language, int index, ImageDomainObject image) {    	
    	Map<Integer, ImageDomainObject> map = getImagesMap(language);

    	ImageDomainObject oldImage = map.get(index);
    	ImageDomainObject newImage = image.clone();
    	
    	if (oldImage != null) {
    		newImage.setIndex(oldImage.getIndex());
    	} 
    	
    	newImage.setLanguage(language);
    	newImage.setIndex(index);
    	
    	map.put(index, newImage);
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
    
    @Override
    public void cloneSharedForNewDocument() {
        cloneTextsForNewDocument();
        cloneImagesForNewDocument();
        cloneIncludesForNewDocument();
        cloneTemplateNamesForNewDocument();
        cloneMenusForNewDocument();
    }
    
    private void cloneMenusForNewDocument() {
    	Map<Integer, MenuDomainObject> menusClone = new HashMap<Integer, MenuDomainObject>();
    	
    	for (Map.Entry<Integer, MenuDomainObject> entry: menusMap.entrySet()) {
    		MenuDomainObject menu = entry.getValue();
    		MenuDomainObject menuClone = menu.clone();
    		
    		menuClone.setId(null);
    		menuClone.setMetaId(null);
    		
    		menusClone.put(entry.getKey(), menuClone);
    	}
    	
    	menusMap = menusClone;
    }

    private void cloneImagesForNewDocument() {
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

    private void cloneTextsForNewDocument() {
        Map<I18nLanguage, Map<Integer, TextDomainObject>> textsClone    
    		= new HashMap<I18nLanguage, Map<Integer, TextDomainObject>>();

        for(Map.Entry<I18nLanguage, Map<Integer, TextDomainObject>> languageEntry: texts.entrySet()) {
            Map<Integer, TextDomainObject> textsMap = languageEntry.getValue();
            Map<Integer, TextDomainObject> textsMapClone = new HashMap<Integer, TextDomainObject>();

            textsClone.put(languageEntry.getKey(), textsMapClone);

            for (Map.Entry<Integer, TextDomainObject> textsEntry: textsMap.entrySet()) {
                TextDomainObject text = textsEntry.getValue().clone();
                //text.setId(null);
                //text.setMetaId(metaId);
                text.setModified(true);

                textsMapClone.put(textsEntry.getKey(), text);
            }
        }

        texts = textsClone;
    }
    
    private void cloneTemplateNamesForNewDocument() {
    	templateNames = (TemplateNames)templateNames.clone();
    }
    
    private void cloneIncludesForNewDocument() {
    	Map<Integer, Integer> newIncludesMap = new HashMap<Integer, Integer>(includesMap);
    	includesMap = newIncludesMap;
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

	public void setIncludesMap(Map<Integer, Integer> includesMap) {
		this.includesMap = includesMap;
	}

	public void setTexts(Map<I18nLanguage, Map<Integer, TextDomainObject>> texts) {
		this.texts = texts;
	}
	
	public void setImages(Map<I18nLanguage, Map<Integer, ImageDomainObject>> images) {
		this.images = images;
	}	
}
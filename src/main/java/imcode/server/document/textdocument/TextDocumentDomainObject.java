package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.orm.TemplateNames;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TextDocumentDomainObject extends DocumentDomainObject {

    /**
     * Content loop unique item key.
     */
    private static final class ContentLoopItemKey {
        
        public final int loopNo;

        public final int contentIndex;

        public final int itemNo;

        private final int hashCode;

        public ContentLoopItemKey(int loopNo, int contentIndex, int itemNo) {
            this.loopNo = loopNo;
            this.contentIndex = contentIndex;
            this.itemNo = itemNo;

            this.hashCode = new HashCodeBuilder(17, 31).
                append(loopNo).
                append(contentIndex).
                append(itemNo).
                toHashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ContentLoopItemKey) && ((ContentLoopItemKey)o).hashCode() == hashCode();
        }
   }
		
	/** Images outside of loops. */
    private Map<Integer, ImageDomainObject> images = new HashMap<Integer, ImageDomainObject>();
    
    /** Texts outside of loops. */
    private Map<Integer, TextDomainObject> texts = new HashMap<Integer, TextDomainObject>();
    
    /** Texts in loops. */
    private Map<ContentLoopItemKey, TextDomainObject> loopTexts
    		= new HashMap<ContentLoopItemKey, TextDomainObject>();


    /** Images in loops. */
    private Map<ContentLoopItemKey, ImageDomainObject> loopImages
    		= new HashMap<ContentLoopItemKey, ImageDomainObject>();
    
    
    /**
     * Includes map.
     * 
     * Map key is an included doc's order index in this document.
     * 
     * Map value is an included doc's id.
     */
    private Map<Integer, Integer> includesMap = new HashMap<Integer, Integer>();
    
    /**
     * Menus map.
     * 
     * Map index is a menu's no in this document.
     */
    private Map<Integer, MenuDomainObject> menus = new HashMap<Integer, MenuDomainObject>();  
    
    /** Template names. */
    private TemplateNames templateNames = new TemplateNames();
    
    /**
     * Content loops.
     * 
     * Map key is a content's no in this document.
     */
    private Map<Integer, ContentLoop> contentLoops = new HashMap<Integer, ContentLoop>();
                
    
    public TextDocumentDomainObject() {
        this(ID_NEW) ;
    }

    public TextDocumentDomainObject(int documentId) {
        setId(documentId);
    }

    @Override
    public TextDocumentDomainObject clone() {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        
        clone.images = cloneImages();
        clone.loopImages = cloneLoopImages();
        clone.includesMap = cloneIncludesMap();
        clone.menus = cloneMenusMap();
        clone.templateNames = cloneTemplateNames();
        clone.texts = cloneTexts();
        clone.loopTexts = cloneLoopTexts();
        clone.contentLoops = cloneContentLoopsMap();
                
        return clone;
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitTextDocument(this) ;
    }    

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.TEXT ;
    }

    public Set getChildDocumentIds() {
        Set childDocuments = new HashSet();
        
        for (MenuDomainObject menu:  getMenus().values()) {
            for (MenuItemDomainObject menuItem:  menu.getMenuItems()) {
                childDocuments.add(menuItem.getDocumentId()) ;
            }
        }
        
        return childDocuments ;
    }
    
    public Integer getIncludedDocumentId( int includeIndex ) {
        return includesMap.get(includeIndex);
    }
    

    public MenuDomainObject getMenu(int menuIndex) {
        MenuDomainObject menu = menus.get(menuIndex);
        
        if (null == menu) {
            menu = new MenuDomainObject() ;
            setMenu( menuIndex, menu );
        }
        
        return menu;
    }

    
    public TextDomainObject getText(int no) {
        return texts.get(no);
    }


    /**
     * @return TextDomainObject or null if text can not be found.
     */
	public TextDomainObject getText(Integer loopNo, Integer contentIndex, Integer textNo) {
        return loopTexts.get(new ContentLoopItemKey(loopNo, contentIndex, textNo));
	}

    /**
     * Removes all image.
     */
    public synchronized void removeAllImages() {
        images = new HashMap<Integer, ImageDomainObject>();
        loopImages = new HashMap<ContentLoopItemKey, ImageDomainObject>();
    }

    public void removeAllIncludes() {
    	includesMap = new HashMap<Integer, Integer>();
    }

    public void removeAllMenus() {
        menus = new HashMap<Integer, MenuDomainObject>();
    }
    
    public void removeAllContentLoops() {
    	contentLoops = new HashMap<Integer, ContentLoop>();
    }

    public void removeAllTexts() {
        texts = new HashMap<Integer, TextDomainObject>();
        loopTexts = new HashMap<ContentLoopItemKey, TextDomainObject>();
    }

    
    public void setInclude( int includeIndex, int includedDocumentId ) {
    	includesMap.put(includeIndex, includedDocumentId);
    }
    

    public void setMenu( int menuIndex, MenuDomainObject menu ) {
    	MenuDomainObject newMenu = menu.clone();
    	MenuDomainObject oldMenu = menus.get(menuIndex);
    	
    	if (oldMenu != null) {
    		newMenu.setId(oldMenu.getId());
            newMenu.setSortOrder(oldMenu.getSortOrder());
    	} else {
    		newMenu.setId(null);
    	}
    	
    	newMenu.setIndex(menuIndex);
        newMenu.setMetaId(getMeta().getId());
        newMenu.setDocVersionNo(getVersion().getNo());
    	    	
        menus.put(menuIndex, newMenu);
    }


    public TextDomainObject setText(Integer no, TextDomainObject text) {
        Meta meta = getMeta();
        Integer documentVersion = getVersion().getNo();
        Integer metaId = meta.getId();

        Integer loopNo = text.getLoopNo();
        Integer contentIndex = text.getContentIndex();

        if ((loopNo != null && contentIndex == null) || (loopNo == null && contentIndex != null)) {
            throw new IllegalStateException(String.format(
                "Invalid text. Both loop no and content index must be set or not set (null). Meta  id :%s, document version: %s, loop no: %s, content index: %s, text no: %s."
                ,meta.getId(), documentVersion, loopNo, contentIndex, no)
            );
        }

        ContentLoopItemKey key = loopNo == null ? null : new ContentLoopItemKey(loopNo, contentIndex, no);
        
        TextDomainObject oldText = key == null ? texts.get(no) : loopTexts.get(key);
        TextDomainObject newText = text.clone();

        if (oldText != null) {
            newText.setId(oldText.getId());
        } else {
            newText.setId(null);
        }

        newText.setDocId(metaId);
        newText.setDocVersionNo(documentVersion);
        newText.setNo(no);
        newText.setLanguage(getLanguage());

        if (key == null) texts.put(no, newText); else loopTexts.put(key, newText);

        return newText.clone();
    } 
    
    public Map<Integer, Integer> getIncludesMap() {
        return Collections.unmodifiableMap(includesMap);
    }

    public String getTemplateName() {
        return templateNames.getTemplateName();
    }

    public int getTemplateGroupId() {
        return templateNames.getTemplateGroupId();
    }

    public Map<Integer, TextDomainObject> getTexts() {
        return texts;
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
     * @return images outside ot content loops.
     */
    public Map<Integer, ImageDomainObject> getImages() {
        return images;
    }

     
    public ImageDomainObject setImage(int no, ImageDomainObject image) {
        Meta meta = getMeta();
        Integer documentVersion = getVersion().getNo();
        Integer metaId = meta.getId();

        Integer loopNo = image.getLoopNo();
        Integer contentIndex = image.getContentIndex();

        if ((loopNo != null && contentIndex == null) || (loopNo == null && contentIndex != null)) {
            throw new IllegalStateException(String.format(
                "Invalid image. Both loop no and content index must be set or not set (null). Meta  id :%s, document version: %s, loop no: %s, content index: %s, image no: %s."
                ,meta.getId(), documentVersion, loopNo, contentIndex, no)
            );
        }

        ContentLoopItemKey key = loopNo == null ? null : new ContentLoopItemKey(loopNo, contentIndex, no);

        ImageDomainObject oldImage = key == null ? images.get(no) : loopImages.get(key);
        ImageDomainObject newImage = image.clone();

        if (oldImage != null) {
            newImage.setId(oldImage.getId());
        } else {
            newImage.setId(null);
        }

        newImage.setDocId(metaId);
        newImage.setDocVersionNo(documentVersion);
        newImage.setNo(no);
        newImage.setLanguage(getLanguage());

        if (key == null) images.put(no, newImage); else loopImages.put(key, newImage);

        return newImage.clone();
    }	
	
	/**
	 * @return image outside of content loop.
	 */
	public ImageDomainObject getImage(int no) {
		return images.get(no);
	}
    
    private Map<Integer, MenuDomainObject> cloneMenusMap() {
    	Map<Integer, MenuDomainObject> menusClone = new HashMap<Integer, MenuDomainObject>();
    	
    	for (Map.Entry<Integer, MenuDomainObject> entry: menus.entrySet()) {
    		MenuDomainObject menu = entry.getValue();
    		MenuDomainObject menuClone = menu.clone();
    		
    		menusClone.put(entry.getKey(), menuClone);
    	}
    	
    	return menusClone;
    }
    
    
    private Map<Integer, ImageDomainObject> cloneImages() {
        Map<Integer, ImageDomainObject> imagesClone = new HashMap<Integer, ImageDomainObject>();

        for (Map.Entry<Integer, ImageDomainObject> imagesEntry: images.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }


    private Map<ContentLoopItemKey, ImageDomainObject> cloneLoopImages() {
        Map<ContentLoopItemKey, ImageDomainObject> imagesClone
    		= new HashMap<ContentLoopItemKey, ImageDomainObject>();

        for (Map.Entry<ContentLoopItemKey, ImageDomainObject> imagesEntry: loopImages.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }

    private Map<Integer, TextDomainObject> cloneTexts() {
        Map<Integer, TextDomainObject> textsClone = new HashMap<Integer, TextDomainObject>();

        for (Map.Entry<Integer, TextDomainObject> textsEntry: texts.entrySet()) {
            TextDomainObject text = textsEntry.getValue().clone();

            textsClone.put(textsEntry.getKey(), text);
        }

        return textsClone;
    }

    private Map<ContentLoopItemKey, TextDomainObject> cloneLoopTexts() {
        Map<ContentLoopItemKey, TextDomainObject> textsClone = new HashMap<ContentLoopItemKey, TextDomainObject>();

        for (Map.Entry<ContentLoopItemKey, TextDomainObject> textsEntry: loopTexts.entrySet()) {
            TextDomainObject text = textsEntry.getValue().clone();

            textsClone.put(textsEntry.getKey(), text);
        }

        return textsClone;
    }
    
    private TemplateNames cloneTemplateNames() {
    	TemplateNames templateNamesClone = templateNames.clone();

    	return templateNamesClone; 
    }
    
    private Map<Integer, Integer> cloneIncludesMap() {
    	Map<Integer, Integer> includesMapClone = new HashMap<Integer, Integer>(includesMap);
    	
    	return includesMapClone;
    }    

    private Map<Integer, ContentLoop> cloneContentLoopsMap() {
    	Map<Integer, ContentLoop> contentLoopsMapClone = new HashMap<Integer, ContentLoop>();
    	
    	for (Map.Entry<Integer, ContentLoop> entry: contentLoops.entrySet()) {
    		contentLoopsMapClone.put(entry.getKey(), entry.getValue().clone());
    	}
    	
    	return contentLoopsMapClone;
    }    

	public TemplateNames getTemplateNames() {
		return templateNames;
	}

	public void setTemplateNames(TemplateNames templateNames) {
		this.templateNames = templateNames;
	}

    public Map<Integer, MenuDomainObject> getMenus() {
        return menus;
    }

	public void setMenus(Map<Integer, MenuDomainObject> menus) {
		this.menus = menus;
	}

	public void setIncludesMap(Map<Integer, Integer> includesMap) {
		this.includesMap = includesMap;
	}

	public Map<Integer, ContentLoop> getContentLoops() {
		return contentLoops;
	}

	public void setContentLoops(Map<Integer, ContentLoop> contentLoops) {
		this.contentLoops = contentLoops;
	}	
	
	public ContentLoop getContentLoop(int no) {
		return contentLoops.get(no);
	}
	
	/**
	 * Sets content loop clone passed to the method. 
	 * 
	 * @param no content loop no in this document.
	 * @param contentLoop content loop to set.
     * 
	 * @return clone of a ContentLoop set to this document.
	 */
	public ContentLoop setContentLoop(int no, ContentLoop contentLoop) {
		ContentLoop oldContentLoop = getContentLoop(no);
		ContentLoop newContentLoop = contentLoop.clone();
		
		Meta meta = getMeta();		
		
		Integer metaId = meta == null ? null : meta.getId();
		Long loopId = null;
		DocumentVersion documentVersion = getVersion();
        Integer versionNumber = documentVersion == null ? null :  documentVersion.getNo();
		
		if (oldContentLoop != null) {
			loopId = oldContentLoop.getId();
			versionNumber = oldContentLoop.getDocVersionNo();
		}
		
		newContentLoop.setDocId(metaId);
		newContentLoop.setDocVersionNo(versionNumber);
		newContentLoop.setId(loopId);		
		newContentLoop.setNo(no);
		
		for (Content content: newContentLoop.getContents()) {
			content.setLoopId(loopId);
			content.setId(null);
		}
		
		contentLoops.put(no, newContentLoop);
		
		return newContentLoop.clone();
	}

    public Map<ContentLoopItemKey, TextDomainObject> getLoopTexts() {
        return loopTexts;
    }

    public void setLoopTexts(Map<ContentLoopItemKey, TextDomainObject> loopTexts) {
        this.loopTexts = loopTexts;
    }

    public Map<ContentLoopItemKey, ImageDomainObject> getLoopImages() {
        return loopImages;
    }

    public void setLoopImages(Map<ContentLoopItemKey, ImageDomainObject> loopImages) {
        this.loopImages = loopImages;
    }
}
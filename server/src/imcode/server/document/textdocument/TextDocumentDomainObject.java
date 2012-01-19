package imcode.server.document.textdocument;

import com.imcode.imcms.mapping.DocumentMenusMap;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.util.LazilyLoadedObject;
import org.apache.commons.lang.UnhandledException;

import java.util.*;

public class TextDocumentDomainObject extends DocumentDomainObject {
	
	/** 
	 * Modified text indexes. 
	 * 
	 * Every modified text can be saved to history. 
	 * This controlled by setting boolean flag.
	 * 
	 * Required when saving only particular set of text fields.
	 */
	private Map<Integer, Boolean> modifiedTextIndexes = new TreeMap<Integer, Boolean>();

    private LazilyLoadedObject<CopyableHashMap> texts = new LazilyLoadedObject<CopyableHashMap>(new CopyableHashMapLoader());
    private LazilyLoadedObject<CopyableHashMap> images = new LazilyLoadedObject<CopyableHashMap>(new CopyableHashMapLoader());
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

        texts.load();
        images.load();
        includes.load();
        menus.load();
        templateNames.load();
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        clone.texts = (LazilyLoadedObject) texts.clone();
        clone.images = (LazilyLoadedObject) images.clone();
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

    public ImageDomainObject getImage( int imageIndex ) {
        ImageDomainObject image = (ImageDomainObject)getImagesMap().get( new Integer( imageIndex )) ;
        if (null == image) {
            image = new ImageDomainObject() ;
        }
        return image ;
    }

    private Map getImagesMap() {
        return (Map) images.get();
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

    public void removeAllImages() {
        getImagesMap().clear();
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

    /**
     * @return Map<Integer, {@link ImageDomainObject} *
     */
    public Map<Integer, ImageDomainObject> getImages() {
        return Collections.unmodifiableMap( getImagesMap() );
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

    public void setImage( int imageIndex, ImageDomainObject image ) {
        image.setImageIndex(imageIndex);
        getImagesMap().put( new Integer( imageIndex ), image ) ;
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
        this.images = images ;
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
}
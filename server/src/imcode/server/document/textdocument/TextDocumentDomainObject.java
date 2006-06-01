package imcode.server.document.textdocument;

import com.imcode.imcms.mapping.DocumentMenusMap;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.util.LazilyLoadedObject;
import org.apache.commons.lang.UnhandledException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TextDocumentDomainObject extends DocumentDomainObject {

    private LazilyLoadedObject texts = new LazilyLoadedObject(new CopyableHashMapLoader());
    private LazilyLoadedObject images = new LazilyLoadedObject(new CopyableHashMapLoader());
    private LazilyLoadedObject includes = new LazilyLoadedObject(new CopyableHashMapLoader());
    private LazilyLoadedObject menus = new LazilyLoadedObject(new LazilyLoadedObject.Loader() {
        public LazilyLoadedObject.Copyable load() {
            return new DocumentMenusMap();
        }
    });
    private LazilyLoadedObject templateIds = new LazilyLoadedObject(new LazilyLoadedObject.Loader() {
        public LazilyLoadedObject.Copyable load() {
            return new TemplateIds();
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
        templateIds.load();
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        clone.texts = (LazilyLoadedObject) texts.clone();
        clone.images = (LazilyLoadedObject) images.clone();
        clone.includes = (LazilyLoadedObject) includes.clone();
        clone.menus = (LazilyLoadedObject) menus.clone() ;
        clone.templateIds = (LazilyLoadedObject) templateIds.clone() ;
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

    public Map getMenus() {
        return Collections.unmodifiableMap( getMenusMap() );
    }

    public int getTemplateId() {
        return getTemplateIds().getTemplateId();
    }

    private TemplateIds getTemplateIds() {
        return (TemplateIds) templateIds.get();
    }

    public int getTemplateGroupId() {
        return getTemplateIds().getTemplateGroupId();
    }

    /**
     * @return Map<Integer, {@link TextDomainObject}>
     */
    public Map getTexts() {
        return Collections.unmodifiableMap( getTextsMap() );
    }

    public void setTemplateId( int v ) {
        getTemplateIds().setTemplateId(v);
    }

    public void setTemplateGroupId( int v ) {
        getTemplateIds().setTemplateGroupId(v);
    }

    public void setImage( int imageIndex, ImageDomainObject image ) {
        getImagesMap().put( new Integer( imageIndex ), image ) ;
    }

    public Integer getDefaultTemplateId() {
        return getTemplateIds().getDefaultTemplateId();
    }

    public void setDefaultTemplateId( Integer defaultTemplateId ) {
        getTemplateIds().setDefaultTemplateId(defaultTemplateId);
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

    public Integer getDefaultTemplateIdForRestricted1() {
        return getTemplateIds().getDefaultTemplateIdForRestricted1();
    }

    public Integer getDefaultTemplateIdForRestricted2() {
        return getTemplateIds().getDefaultTemplateIdForRestricted2();
    }

    public void setDefaultTemplateIdForRestricted1(Integer defaultTemplateIdForRestricted1) {
        getTemplateIds().setDefaultTemplateIdForRestricted1(defaultTemplateIdForRestricted1);
    }

    public void setDefaultTemplateIdForRestricted2(Integer defaultTemplateIdForRestricted2) {
        getTemplateIds().setDefaultTemplateIdForRestricted2(defaultTemplateIdForRestricted2);
    }

    public void setLazilyLoadedTemplateIds(LazilyLoadedObject templateIds) {
        this.templateIds = templateIds;
    }

    private static class CopyableHashMapLoader implements LazilyLoadedObject.Loader {

        public LazilyLoadedObject.Copyable load() {
            return new CopyableHashMap();
        }
    }

    public static class TemplateIds implements LazilyLoadedObject.Copyable, Cloneable {
        private int templateId;
        private int templateGroupId;
        private Integer defaultTemplateId;
        private Integer defaultTemplateIdForRestricted1 ;
        private Integer defaultTemplateIdForRestricted2 ;

        public LazilyLoadedObject.Copyable copy() {
            return (LazilyLoadedObject.Copyable) clone() ;
        }

        public Object clone() {
            try {
                return super.clone();
            } catch ( CloneNotSupportedException e ) {
                throw new UnhandledException(e);
            }
        }

        public int getTemplateId() {
            return templateId;
        }

        public void setTemplateId(int templateId) {
            this.templateId = templateId;
        }

        public int getTemplateGroupId() {
            return templateGroupId;
        }

        public void setTemplateGroupId(int templateGroupId) {
            this.templateGroupId = templateGroupId;
        }

        public Integer getDefaultTemplateId() {
            return defaultTemplateId;
        }

        public void setDefaultTemplateId(Integer defaultTemplateId) {
            this.defaultTemplateId = defaultTemplateId;
        }

        public Integer getDefaultTemplateIdForRestricted1() {
            return defaultTemplateIdForRestricted1;
        }

        public void setDefaultTemplateIdForRestricted1(Integer defaultTemplateIdForRestricted1) {
            this.defaultTemplateIdForRestricted1 = defaultTemplateIdForRestricted1;
        }

        public Integer getDefaultTemplateIdForRestricted2() {
            return defaultTemplateIdForRestricted2;
        }

        public void setDefaultTemplateIdForRestricted2(Integer defaultTemplateIdForRestricted2) {
            this.defaultTemplateIdForRestricted2 = defaultTemplateIdForRestricted2;
        }
    }


}
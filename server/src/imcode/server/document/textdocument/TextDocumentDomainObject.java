package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.TemplateDomainObject;

import java.util.*;

public class TextDocumentDomainObject extends DocumentDomainObject {

    private TemplateDomainObject template;
    private int templateGroupId;
    private TemplateDomainObject defaultTemplate;
    private TreeMap texts = new TreeMap();
    private TreeMap images = new TreeMap();
    private TreeMap includes = new TreeMap();
    private TreeMap menus = new TreeMap();

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        clone.texts = (TreeMap)texts.clone();
        clone.images = (TreeMap)images.clone();
        clone.includes = (TreeMap)includes.clone();
        clone.menus = deepCloneMenus() ;
        return clone;
    }

    private TreeMap deepCloneMenus() throws CloneNotSupportedException {
        TreeMap menusClone = new TreeMap() ;
        for ( Iterator iterator = menus.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer menuIndex = (Integer)entry.getKey();
            MenuDomainObject menu = (MenuDomainObject)entry.getValue();
            menusClone.put(menuIndex, menu.clone()) ;
        }
        return menusClone ;
    }

    public Set getChildDocuments() {
        Set childDocuments = new HashSet() ;
        for ( Iterator iterator = getMenus().values().iterator(); iterator.hasNext(); ) {
            MenuDomainObject menu = (MenuDomainObject)iterator.next();
            MenuItemDomainObject[] menuItems = menu.getMenuItems() ;
            for ( int i = 0; i < menuItems.length; i++ ) {
                MenuItemDomainObject menuItem = menuItems[i];
                childDocuments.add( menuItem.getDocument() ) ;
            }
        }
        return childDocuments ;
    }

    public ImageDomainObject getImage( int imageIndex ) {
        ImageDomainObject image = (ImageDomainObject)images.get( new Integer( imageIndex )) ;
        if (null == image) {
            image = new ImageDomainObject() ;
        }
        return image ;
    }

    public Integer getIncludedDocumentId( int includeIndex ) {
        return (Integer)includes.get( new Integer( includeIndex ) );
    }

    public MenuDomainObject getMenu( int menuIndex ) {
        MenuDomainObject menu = (MenuDomainObject)menus.get( new Integer( menuIndex ) );
        if (null == menu) {
            menu = new MenuDomainObject() ;
            setMenu( menuIndex, menu );
        }
        return menu;
    }

    public TextDomainObject getText( int textFieldIndex ) {
        return (TextDomainObject)texts.get( new Integer( textFieldIndex ) );
    }

    public void initDocument( DocumentMapper documentMapper ) {
        // lazily loaded
    }

    public void accept( DocumentVisitor documentVisitor ) {
        documentVisitor.visitTextDocument(this) ;
    }

    public void removeAllImages() {
        images.clear();
    }

    public void removeAllIncludes() {
        includes.clear();
    }

    public void removeAllMenus() {
        menus.clear();
    }

    public void removeAllTexts() {
        texts.clear();
    }

    public void setInclude( int includeIndex, int includedDocumentId ) {
        includes.put( new Integer( includeIndex ), new Integer( includedDocumentId ) );
    }

    public void setMenu( int menuIndex, MenuDomainObject menu ) {
        menus.put( new Integer( menuIndex ), menu );
    }

    public void setText( int textIndex, TextDomainObject text ) {
        texts.put( new Integer( textIndex ), text );
    }

    public int getDocumentTypeId() {
        return DOCTYPE_TEXT;
    }

    /**
     * @return Map<Integer, {@link ImageDomainObject} *
     */
    public Map getImages() {
        return Collections.unmodifiableMap( images );
    }

    public Map getIncludes() {
        return Collections.unmodifiableMap( includes );
    }

    public Map getMenus() {
        return Collections.unmodifiableMap( menus );
    }

    public TemplateDomainObject getTemplate() {
        return template;
    }

    public int getTemplateGroupId() {
        return templateGroupId;
    }

    /**
     * @return Map<Integer, {@link TextDomainObject}>
     */
    public Map getTexts() {
        return Collections.unmodifiableMap( texts );
    }

    public void setImages( Map images ) {
        this.images = new TreeMap(images);
    }

    public void setTemplate( TemplateDomainObject v ) {
        this.template = v;
    }

    public void setTemplateGroupId( int v ) {
        this.templateGroupId = v;
    }

    public void setImage( int imageIndex, ImageDomainObject image ) {
        images.put( new Integer( imageIndex ), image ) ;
    }

    public TemplateDomainObject getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate( TemplateDomainObject defaultTemplate ) {
        this.defaultTemplate = defaultTemplate;
    }
}
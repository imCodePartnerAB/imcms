package imcode.server.document.textdocument;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.ApplicationServer;
import imcode.server.document.*;
import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class TextDocumentDomainObject extends DocumentDomainObject {

    private class LazilyLoadedTextDocumentAttributes implements Serializable, Cloneable {

        private TemplateDomainObject template;
        private int templateGroupId;
        private int defaultTemplateIdForRestrictedPermissionSetOne;
        private int defaultTemplateIdForRestrictedPermissionSetTwo;
        private Map texts = new HashMap();
        private Map images = new HashMap();
        private Map includes = new HashMap();
        private Map menus = new HashMap();

        public Object clone() throws CloneNotSupportedException {
            LazilyLoadedTextDocumentAttributes clone = (LazilyLoadedTextDocumentAttributes)super.clone();
            clone.texts = new HashMap( texts );
            clone.images = new HashMap( images );
            clone.includes = new HashMap( includes );
            clone.menus = new HashMap( menus );
            return clone;
        }
    }

    private LazilyLoadedTextDocumentAttributes lazilyLoadedTextDocumentAttributes = null;

    private synchronized LazilyLoadedTextDocumentAttributes getLazilyLoadedTextDocumentAttributes() {
        if ( null == lazilyLoadedTextDocumentAttributes ) {
            lazilyLoadedTextDocumentAttributes = new LazilyLoadedTextDocumentAttributes();
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            documentMapper.initLazilyLoadedTextDocumentAttributes( this );
        }
        return lazilyLoadedTextDocumentAttributes;
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject)super.clone();
        if ( null != lazilyLoadedTextDocumentAttributes ) {
            clone.lazilyLoadedTextDocumentAttributes = (LazilyLoadedTextDocumentAttributes)lazilyLoadedTextDocumentAttributes.clone();
        }
        return clone;
    }

    protected void loadAllLazilyLoadedDocumentTypeSpecificAttributes() {
        getLazilyLoadedTextDocumentAttributes();
    }

    public TemplateDomainObject getTemplate() {
        return getLazilyLoadedTextDocumentAttributes().template;
    }

    public void setTemplate( TemplateDomainObject v ) {
        this.getLazilyLoadedTextDocumentAttributes().template = v;
    }

    public int getTemplateGroupId() {
        return getLazilyLoadedTextDocumentAttributes().templateGroupId;
    }

    public void setTemplateGroupId( int v ) {
        this.getLazilyLoadedTextDocumentAttributes().templateGroupId = v;
    }

    public void setDefaultTemplateIdForRestrictedPermissionSetOne( int defaultTemplateIdForRestrictedPermissionSetOne ) {
        this.getLazilyLoadedTextDocumentAttributes().defaultTemplateIdForRestrictedPermissionSetOne = defaultTemplateIdForRestrictedPermissionSetOne;
    }

    public int getDefaultTemplateIdForRestrictedPermissionSetOne() {
        return getLazilyLoadedTextDocumentAttributes().defaultTemplateIdForRestrictedPermissionSetOne;
    }

    public void setDefaultTemplateIdForRestrictedPermissionSetTwo( int defaultTemplateIdForRestrictedPermissionSetTwo ) {
        this.getLazilyLoadedTextDocumentAttributes().defaultTemplateIdForRestrictedPermissionSetTwo = defaultTemplateIdForRestrictedPermissionSetTwo;
    }

    public int getDefaultTemplateIdForRestrictedPermissionSetTwo() {
        return getLazilyLoadedTextDocumentAttributes().defaultTemplateIdForRestrictedPermissionSetTwo;
    }

    public int getDocumentTypeId() {
        return DOCTYPE_TEXT;
    }

    public void processNewDocumentInformation( DocumentComposer documentComposer,
                                               DocumentComposer.NewDocumentParentInformation newDocumentParentInformation,
                                               UserDomainObject user, HttpServletRequest request,
                                               HttpServletResponse response ) throws IOException {
        documentComposer.processNewTextDocumentInformation( this, newDocumentParentInformation, request, response, user );
    }

    public void saveDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveTextDocument( this, user );
    }

    public void saveNewDocument( DocumentMapper documentMapper, UserDomainObject user ) {
        documentMapper.saveNewTextDocument( this, user );
    }

    public void initDocument( DocumentMapper documentMapper ) {
        // lazily loaded
    }

    public void setText( int textFieldIndex, TextDomainObject text ) {
        getLazilyLoadedTextDocumentAttributes().texts.put( new Integer( textFieldIndex ), text );
    }

    public TextDomainObject getText( int textFieldIndex ) {
        return (TextDomainObject)getLazilyLoadedTextDocumentAttributes().texts.get( new Integer( textFieldIndex ) );
    }

    /**
     * @return Map<Integer, {@link TextDomainObject}>
     */
    public Map getTexts() {
        return Collections.unmodifiableMap( getLazilyLoadedTextDocumentAttributes().texts );
    }

    public void removeAllTexts() {
        getLazilyLoadedTextDocumentAttributes().texts.clear();
    }

    public void setImages( Map images ) {
        this.getLazilyLoadedTextDocumentAttributes().images = images;
    }

    /**
     * @return Map<Integer, {@link ImageDomainObject} *
     */
    public Map getImages() {
        return Collections.unmodifiableMap( getLazilyLoadedTextDocumentAttributes().images );
    }

    public void removeAllImages() {
        getLazilyLoadedTextDocumentAttributes().images.clear();
    }

    public void setInclude( int includeIndex, int includedDocumentId ) {
        getLazilyLoadedTextDocumentAttributes().includes.put( new Integer( includeIndex ), new Integer( includedDocumentId ) );
    }

    public Integer getIncludedDocumentId( int includeIndex ) {
        return (Integer)getLazilyLoadedTextDocumentAttributes().includes.get( new Integer( includeIndex ) );
    }

    public Map getIncludes() {
        return Collections.unmodifiableMap( getLazilyLoadedTextDocumentAttributes().includes );
    }

    public void removeAllIncludes() {
        getLazilyLoadedTextDocumentAttributes().includes.clear();
    }

    public MenuDomainObject getMenu( int menuIndex ) {
        MenuDomainObject menu = (MenuDomainObject)getLazilyLoadedTextDocumentAttributes().menus.get( new Integer( menuIndex ) );
        if (null == menu) {
            menu = new MenuDomainObject( 0, MenuDomainObject.MENU_SORT_ORDER__DEFAULT ) ;
            setMenu( menuIndex, menu );
        }
        return menu;
    }

    public void setMenu( int menuIndex, MenuDomainObject menu ) {
        getLazilyLoadedTextDocumentAttributes().menus.put( new Integer( menuIndex ), menu );
    }

    public Map getMenus() {
        return Collections.unmodifiableMap( getLazilyLoadedTextDocumentAttributes().menus );
    }

    public ImageDomainObject getImage( int imageIndex ) {
        return (ImageDomainObject)getLazilyLoadedTextDocumentAttributes().images.get( new Integer( imageIndex )) ;
    }

}
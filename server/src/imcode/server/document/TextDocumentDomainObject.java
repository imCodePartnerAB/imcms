/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:25:56
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TextDocumentDomainObject extends DocumentDomainObject {

    private class LazilyLoadedTextDocumentAttributes implements Serializable, Cloneable {

        private TemplateDomainObject template;
        private int templateGroupId;
        private int defaultTemplateIdForRestrictedPermissionSetOne;
        private int defaultTemplateIdForRestrictedPermissionSetTwo;
        private Map texts = new HashMap();
        private Map images = new HashMap();
        private Map includes = new HashMap();

        public Object clone() throws CloneNotSupportedException {
            LazilyLoadedTextDocumentAttributes clone = (LazilyLoadedTextDocumentAttributes)super.clone();
            clone.texts = new HashMap( texts );
            clone.images = new HashMap( images );
            clone.includes = new HashMap( includes );
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
        getLazilyLoadedTextDocumentAttributes() ;
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

    public void setText( int textFieldIndex, TextDocumentDomainObject.Text text ) {
        getLazilyLoadedTextDocumentAttributes().texts.put( new Integer( textFieldIndex ), text );
    }

    public TextDocumentDomainObject.Text getText( int textFieldIndex ) {
        return (TextDocumentDomainObject.Text)getLazilyLoadedTextDocumentAttributes().texts.get( new Integer( textFieldIndex ) );
    }

    /**
     * @return Map<Integer, {@link TextDocumentDomainObject.Text}>
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

    public static class Text implements Serializable {

        String text;
        int type;

        /* Text-types. */

        /**
         * Plain text, with linebreaks. *
         */
        public final static int TEXT_TYPE_PLAIN = 0;

        /**
         * HTML-code. *
         */
        public final static int TEXT_TYPE_HTML = 1;

        /**
         * Create a text for a text-page.
         *
         * @param text The text
         * @param type The type of the text.
         */
        public Text( String text, int type ) {
            setText( text );
            setType( type );
        }

        /**
         * Gets the value of text
         *
         * @return the value of text
         */
        public String getText() {
            return this.text;
        }

        /**
         * Sets the value of text
         *
         * @param text Value to assign to text
         */
        public void setText( String text ) {
            this.text = text;
        }

        /**
         * Gets the value of type
         *
         * @return the value of type
         */
        public int getType() {
            return this.type;
        }

        /**
         * Sets the value of type
         *
         * @param type Value to assign to type
         */
        public void setType( int type ) {
            switch ( type ) {
                case TEXT_TYPE_PLAIN:
                case TEXT_TYPE_HTML:
                    this.type = type;
                    break;
                default:
                    throw new IllegalArgumentException( "Illegal text-type." );
            }
        }

        /**
         * Equivalent to getMenuText()
         */
        public String toString() {
            return getText();
        }

        public String toHtmlString() {
            String result = getText();
            if ( getType() == TEXT_TYPE_PLAIN ) {
                String[] vp = new String[]{
                    "&", "&amp;",
                    "<", "&lt;",
                    ">", "&gt;",
                    "\"", "&quot;",
                    "\r\n", "\n",
                    "\r", "\n",
                    "\n", "<BR>\n",
                };
                result = Parser.parseDoc( result, vp );
            }
            return result;
        }

    }

    public static class Image implements Serializable {

        private String url;
        private String name;
        private int width;
        private int height;
        private int border;
        private String align;
        private String alternateText;
        private String lowResolutionUrl;
        private int verticalSpace;
        private int horizontalSpace;
        private String target;
        private String linkUrl;

        public Image() {

        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getBorder() {
            return border;
        }

        public String getAlign() {
            return align;
        }

        public String getAlternateText() {
            return alternateText;
        }

        public String getLowResolutionUrl() {
            return lowResolutionUrl;
        }

        public int getVerticalSpace() {
            return verticalSpace;
        }

        public int getHorizontalSpace() {
            return horizontalSpace;
        }

        public String getTarget() {
            return target;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setUrl( String image_ref ) {
            this.url = image_ref;
        }

        public void setName( String image_name ) {
            this.name = image_name;
        }

        public void setWidth( int image_width ) {
            this.width = image_width;
        }

        public void setHeight( int image_height ) {
            this.height = image_height;
        }

        public void setBorder( int image_border ) {
            this.border = image_border;
        }

        public void setAlign( String image_align ) {
            this.align = image_align;
        }

        public void setAlternateText( String alt_text ) {
            this.alternateText = alt_text;
        }

        public void setLowResolutionUrl( String low_scr ) {
            this.lowResolutionUrl = low_scr;
        }

        public void setVerticalSpace( int v_space ) {
            this.verticalSpace = v_space;
        }

        public void setHorizontalSpace( int h_space ) {
            this.horizontalSpace = h_space;
        }

        public void setTarget( String target ) {
            this.target = target;
        }

        public void setLinkUrl( String image_ref_link ) {
            this.linkUrl = image_ref_link;
        }

    }

}
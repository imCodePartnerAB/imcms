/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-28
 * Time: 20:25:56
 */
package imcode.server.document;

import com.imcode.imcms.servlet.admin.DocumentComposer;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TextDocumentDomainObject extends DocumentDomainObject {

    private int menuSortOrder;
    private TemplateDomainObject template;
    private int templateGroupId;
    private int defaultTemplateIdForRestrictedPermissionSetOne;
    private int defaultTemplateIdForRestrictedPermissionSetTwo;
    private Map texts = new HashMap();

    public int getMenuSortOrder() {
        return menuSortOrder;
    }

    public void setMenuSortOrder( int v ) {
        this.menuSortOrder = v;
    }

    public TemplateDomainObject getTemplate() {
        return template;
    }

    public void setTemplate( TemplateDomainObject v ) {
        this.template = v;
    }

    public int getTemplateGroupId() {
        return templateGroupId;
    }

    public void setTemplateGroupId( int v ) {
        this.templateGroupId = v;
    }

    public void setDefaultTemplateIdForRestrictedPermissionSetOne(
            int defaultTemplateIdForRestrictedPermissionSetOne ) {
        this.defaultTemplateIdForRestrictedPermissionSetOne = defaultTemplateIdForRestrictedPermissionSetOne;
    }

    public int getDefaultTemplateIdForRestrictedPermissionSetOne() {
        return defaultTemplateIdForRestrictedPermissionSetOne;
    }

    public void setDefaultTemplateIdForRestrictedPermissionSetTwo(
            int defaultTemplateIdForRestrictedPermissionSetTwo ) {
        this.defaultTemplateIdForRestrictedPermissionSetTwo = defaultTemplateIdForRestrictedPermissionSetTwo;
    }

    public int getDefaultTemplateIdForRestrictedPermissionSetTwo() {
        return defaultTemplateIdForRestrictedPermissionSetTwo;
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

    public void saveDocument( DocumentMapper documentMapper ) {
        documentMapper.saveTextDocument( this );
    }

    public void saveNewDocument( DocumentMapper documentMapper ) {
        documentMapper.saveNewTextDocument( this );
    }

    public void initDocumentFromDb( DocumentMapper documentMapper ) {
        documentMapper.initTextDocumentFromDb( this );
    }

    public void setText( int textFieldIndex, TextDocumentDomainObject.Text text ) {
        texts.put( new Integer( textFieldIndex ), text );
    }

    public TextDocumentDomainObject.Text getText( int textFieldIndex ) {
        return (TextDocumentDomainObject.Text)texts.get( new Integer( textFieldIndex ) );
    }

    /**
     * @return Map<Integer, {@link TextDocumentDomainObject.Text}>
     */
    public Map getTexts() {
        return Collections.unmodifiableMap( texts );
    }

    public void removeAllTexts() {
        texts.clear() ;
    }

    public static class Text {

        protected String text;
        protected int type;

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
         * @param type The type of the text. Either
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

}
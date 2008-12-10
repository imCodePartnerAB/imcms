package imcode.server.document.textdocument;

import imcode.util.Parser;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TextDomainObject implements Serializable {

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

    public TextDomainObject(String text) {
        this(text, TEXT_TYPE_PLAIN);
    }

    /**
     * Create a text for a text-page.
     *
     * @param text The text
     * @param type The type of the text.
     */
    public TextDomainObject( String text, int type ) {
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
     * Equivalent to getText()
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
                "\n", "<br />\n",
            };
            result = Parser.parseDoc( result, vp );
        }
        return result;
    }

    public boolean equals( Object obj ) {
        if ( !( obj instanceof TextDomainObject ) ) {
            return false;
        }
        final TextDomainObject o = (TextDomainObject)obj;
        return new EqualsBuilder().append(text, o.getText())
                .append(type, o.getType()).isEquals();
        }

    public int hashCode() {
        return new HashCodeBuilder().append(type)
                .append(text).toHashCode();
    }
}
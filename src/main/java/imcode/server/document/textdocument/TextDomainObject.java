package imcode.server.document.textdocument;

import com.imcode.imcms.api.ContentLoopRef;
import imcode.util.Parser;

import java.io.Serializable;
import java.util.Objects;

/**
 * Text doc's text field.
 */
public class TextDomainObject implements Serializable, Cloneable {

    /**
     * Plain text, with linebreaks.
     */
    public final static int TEXT_TYPE_PLAIN = 0;

    /**
     * HTML-code.
     */
    public final static int TEXT_TYPE_HTML = 1;

    public enum Type {
        PLAIN_TEXT, HTML
    }

    public static final class Builder {
        private TextDomainObject textDomainObject;

        public Builder() {
            textDomainObject = new TextDomainObject();
        }

        public Builder(TextDomainObject textDomainObject) {
            this.textDomainObject = textDomainObject.clone();
        }

        public TextDomainObject build() {
            return textDomainObject.clone();
        }

        public Builder contentRef(ContentLoopRef contentLoopRef) {
            textDomainObject.contentLoopRef = contentLoopRef;
            return this;
        }

        public Builder text(String text) {
            textDomainObject.text = text;
            return this;
        }

        public Builder type(int type) {
            textDomainObject.type = type;
            return this;
        }

        public Builder format(Type type) {
            textDomainObject.setFormatType(type);
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private volatile String text;

    private volatile int type;

    private volatile ContentLoopRef contentLoopRef;

    public TextDomainObject() {
        this("");
    }

    public TextDomainObject(String text) {
        this(text, TEXT_TYPE_PLAIN);
    }

    /**
     * Create a text for a text-page.
     *
     * @param text The text
     * @param type The type of the text.
     */
    public TextDomainObject(String text, int type) {
        setText(text);
        setType(type);
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
    public void setText(String text) {
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
    public void setType(int type) {
        switch (type) {
            case TEXT_TYPE_PLAIN:
            case TEXT_TYPE_HTML:
                this.type = type;
                break;
            default:
                throw new IllegalArgumentException("Illegal text-type.");
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
        if (getType() == TEXT_TYPE_PLAIN) {
            String[] vp = new String[]{
                    "&", "&amp;",
                    "<", "&lt;",
                    ">", "&gt;",
                    "\"", "&quot;",
                    "\r\n", "\n",
                    "\r", "\n",
                    "\n", "<br />\n",
            };
            result = Parser.parseDoc(result, vp);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof TextDomainObject && equals((TextDomainObject) obj));
    }

    private boolean equals(TextDomainObject that) {
        return Objects.equals(text, that.text)
                && Objects.equals(type, that.type)
                && Objects.equals(contentLoopRef, that.contentLoopRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type, contentLoopRef);
    }


    @Override
    public TextDomainObject clone() {
        try {
            return (TextDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public ContentLoopRef getContentLoopRef() {
        return contentLoopRef;
    }

    public void setContentLoopRef(ContentLoopRef contentLoopRef) {
        this.contentLoopRef = contentLoopRef;
    }

    public Type getFormatType() {
        return Type.values()[type];
    }

    public void setFormatType(Type type) {
        this.type = type.ordinal();
    }
}
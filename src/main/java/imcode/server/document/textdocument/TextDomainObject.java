package imcode.server.document.textdocument;

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
    /**
     * WYSIWYG Editor
     */
    public static final int TEXT_TYPE_EDITOR = 2;

    private volatile String text;
    private volatile int type;

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

    public static Builder builder() {
        return new Builder();
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
            case TEXT_TYPE_EDITOR:
                this.type = type;
                break;
            default:
                throw new IllegalArgumentException(String.format("Illegal text-type: %d.", type));
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
        return Objects.equals(text, that.text) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type);
    }

    @Override
    public TextDomainObject clone() {
        try {
            return (TextDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
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

        public Builder text(String text) {
            textDomainObject.text = text;
            return this;
        }

        public Builder type(int type) {
            textDomainObject.type = type;
            return this;
        }
    }
}
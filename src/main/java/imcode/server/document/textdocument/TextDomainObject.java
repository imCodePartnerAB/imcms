package imcode.server.document.textdocument;

import imcode.util.Parser;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.imcode.imcms.api.I18nLanguage;

/**
 * Text doc's text field.
 */
@Entity(name = "Text")
@Table(name = "imcms_text_doc_texts")
public class TextDomainObject implements Serializable, Cloneable {

    public static final class Builder {
        private final TextDomainObject tdo = new TextDomainObject();

        public Builder() {
        }

        public TextDomainObject build() {
            TextDomainObject tdo = new TextDomainObject();

            tdo.id = this.tdo.id;
            tdo.docIdentity = this.tdo.docIdentity;
            tdo.no = this.tdo.no;
            tdo.language = this.tdo.language;
            tdo.contentLoopIdentity = this.tdo.contentLoopIdentity;

            return tdo;
        }

        public Builder id(Long id) {
            tdo.id = id;
            return this;
        }

        public Builder docIdentity(DocIdentity docIdentity) {
            tdo.docIdentity = docIdentity;
            return this;
        }

        public Builder no(Integer no) {
            tdo.no = no;
            return this;
        }

        public Builder language(I18nLanguage language) {
            tdo.language = language;
            return this;
        }

        public Builder contentLoopIdentity(ContentLoopIdentity contentLoopIdentity) {
            tdo.contentLoopIdentity = contentLoopIdentity;
            return this;
        }

        public Builder text(String text) {
            tdo.text = text;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Plain text, with linebreaks. */
    public final static int TEXT_TYPE_PLAIN = 0;

    /** HTML-code. */
    public final static int TEXT_TYPE_HTML = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Text filed no in a document. */
    private Integer no;

    String text;

    int type;

    private DocIdentity docIdentity;
    private ContentLoopIdentity contentLoopIdentity;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private I18nLanguage language;

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
        if (!(obj instanceof TextDomainObject)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final TextDomainObject o = (TextDomainObject) obj;

        return new EqualsBuilder()
                .append(text, o.getText())
                .append(type, o.getType())
                .append(no, o.getNo())
                .append(language, o.getLanguage()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(33, 31)
                .append(text)
                .append(type)
                .append(no)
                .append(language).toHashCode();
    }


    @Override
    public TextDomainObject clone() {
        try {
            return (TextDomainObject)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public void setLanguage(I18nLanguage language) {
        this.language = language;
    }

    @Deprecated
    public Integer getIndex() {
        return getNo();
    }

    @Deprecated
    public void setIndex(Integer index) {
        setNo(index);
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public DocIdentity getDocIdentity() {
        return docIdentity;
    }

    public void setDocIdentity(DocIdentity docIdentity) {
        this.docIdentity = docIdentity;
    }

    public ContentLoopIdentity getContentLoopIdentity() {
        return contentLoopIdentity;
    }

    public void setContentLoopIdentity(ContentLoopIdentity contentRef) {
        this.contentLoopIdentity = contentRef;
    }
}
package imcode.server.document.textdocument;

import imcode.util.Parser;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.imcode.imcms.api.I18nLanguage;

@Entity(name="I18nText")
@Table(name="texts")
@NamedQueries({
	@NamedQuery(name="Text.getByMetaIdAndLanguageId", 
			query="select t from I18nText t where t.metaId = :metaId and t.language.id = :languageId"),
	@NamedQuery(name="Text.getByMetaIdAndIndexAndLanguageId", 
			query="select t from I18nText t where t.metaId = :metaId and t.index = :index and t.language.id = :languageId")

})
public class TextDomainObject implements Serializable, Cloneable {

	@Override
	protected TextDomainObject clone() {
		try {
			return (TextDomainObject)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="counter")
	private Long id;
	
	@Column(name="meta_id")
	private Integer metaId;
	
	/**
	 *   
	 */
	@Transient 
	private boolean substitution;
	
    /**
     * 'name' is a legacy identifier. 
     * Actually it is a part of natural key. 
     */	
	@Column(name="name")
	Integer index;
		
    String text;        
    
    int type;        
    
    /**
     * i18n support 
     */
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="language_id")    
    private I18nLanguage language;    

    /* Text-types. */

    /**
     * Plain text, with linebreaks. *
     */
    public final static int TEXT_TYPE_PLAIN = 0;

    /**
     * HTML-code. *
     */
    public final static int TEXT_TYPE_HTML = 1;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public I18nLanguage getLanguage() {
		return language;
	}

	public void setLanguage(I18nLanguage language) {
		this.language = language;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public boolean isSubstitution() {
		return substitution;
	}

	public void setSubstitution(boolean temporary) {
		this.substitution = temporary;
	}
}
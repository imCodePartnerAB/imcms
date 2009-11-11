package imcode.server.document.textdocument;

import imcode.util.Parser;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.mapping.DocumentStoringVisitor;

/**
 * Document text field. 
 */
@Entity(name="Text")
@Table(name="imcms_text_doc_texts")
@NamedQueries({
	
	// Unique result
	@NamedQuery(name="Text.getByMetaIdAndDocumentVersionAndLanguageId", 
			query="SELECT t FROM Text t WHERE t.metaId = :documentId AND t.documentVersion = :documentVersiob AND t.language.id = :languageId"),
	
	// Unique result
	@NamedQuery(name="Text.getByDocumentIdAndDocumentVersionAndIndexAndLanguageId", 
			query="SELECT t FROM Text t WHERE t.metaId = :documentId AND t.documentVersion = :documentVersion AND t.no = :no AND t.language.id = :languageId"),
	
	// Collection			
	@NamedQuery(name="Text.getByDocumentIdAndDocumentVersion", 
			query="SELECT t FROM Text t WHERE t.metaId = :documentId AND t.documentVersion = :documentVersion")
})
public class TextDomainObject implements Serializable, Cloneable {
	
    /**
     * Plain text, with linebreaks.
     */
    public final static int TEXT_TYPE_PLAIN = 0;

    /**
     * HTML-code. *
     */
    public final static int TEXT_TYPE_HTML = 1;

    @Override
	public TextDomainObject clone() {
		try {
			return (TextDomainObject)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	 

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="meta_id")
	private Integer metaId;
	
	@Column(name="doc_version_number")
	private Integer documentVersion;	
	
    /** Text filed no in a document. */
	private Integer no;

    @Column(name="loop_no")
    private Integer loopNo;

    @Column(name="loop_content_index")
    private Integer contentIndex;
	
	/**
	 * Altered if text was modified.
     *
     * todo: move to document domain object modified texts list.
	 * 
	 * @see TextDocumentDomainObject.setText
	 * @see DocumentStoringVisitor.updateTextDocumentTexts 
	 */                                           
	@Transient
	private boolean modified;
		
    String text;        
    
    int type;        
    
    /**
     * i18n support 
     */
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="language_id")    
    private I18nLanguage language;    

    /* Text-types. */

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

	@Override 
    public boolean equals( Object obj ) {
        if ( !( obj instanceof TextDomainObject ) ) {
            return false;
        }
        
        if (this == obj) {
        	return true;
        }
        
        final TextDomainObject o = (TextDomainObject)obj;
        
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

	/**
	 * @return if text was modified. 
	 */
	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public Integer getDocumentVersion() {
		return documentVersion;
	}

	public void setDocumentVersion(Integer documentVersion) {
		this.documentVersion = documentVersion;
	}

    public Integer getLoopNo() {
        return loopNo;
    }

    public void setLoopNo(Integer loopNo) {
        this.loopNo = loopNo;
    }

    public Integer getContentIndex() {
        return contentIndex;
    }

    public void setContentIndex(Integer contentIndex) {
        this.contentIndex = contentIndex;
    }
}
package com.imcode.imcms.api;

import imcode.server.document.textdocument.TextDomainObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="texts_history")
public class TextHistory {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="counter")
	private Long id;
	
	@Column(name="meta_id")
	private Integer metaId;	
	
    /**
     * 
     */	
	@Column(name="name")
	private Integer index;
		
	private String text;        
    
	private Integer type;
    
    @Column(name="user_id")
    private Integer userId;
    
    public TextHistory() {}
    
    public TextHistory(TextDomainObject textDO) {
    	//setMetaId(textDO.getMetaId());
    	setIndex(textDO.getIndex());
    	setText(textDO.getText());
    	setLanguage(textDO.getLanguage());
    }
    
    /**
     * i18n support 
     */
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="language_id")    
    private I18nLanguage language;    

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
    	this.type = type;
    }

    /**
     * Equivalent to getText()
     */
    public String toString() {
        return getText();
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
}
package com.imcode.imcms.api;

import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="imcms_text_doc_texts_history")
public class TextHistory {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="counter")
	private Long id;

	@Column(name="doc_id")
	private Integer docId;

	@Column(name="doc_version_no")
	private Integer docVersionNo;

	private Integer no;

	private String text;

	private Integer type;

    @Column(name="content_loop_no")
    private Integer contentLoopNo;

    @Column(name="content_no")
    private Integer contentNo;

    /**
     * i18n support
     */
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="language_id", referencedColumnName="id")
    private I18nLanguage language;


    @Column(name="user_id")
    private Integer userId;

    
    @Column(name="modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;
    
    public TextHistory() {}
    
    public TextHistory(TextDomainObject textDO, UserDomainObject user) {
    	setDocId(textDO.getDocId());
        setDocVersionNo(textDO.getDocVersionNo());
    	setNo(textDO.getNo());
    	setText(textDO.getText());
    	setLanguage(textDO.getLanguage());
        setContentLoopNo(textDO.getContentLoopNo());
        setContentNo(textDO.getContentNo());
        setUserId(user.getId());
        setModifiedDt(new Date());
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

	public I18nLanguage getLanguage() {
		return language;
	}

	public void setLanguage(I18nLanguage language) {
		this.language = language;
	}

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getDocVersionNo() {
        return docVersionNo;
    }

    public void setDocVersionNo(Integer docVersionNo) {
        this.docVersionNo = docVersionNo;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getContentLoopNo() {
        return contentLoopNo;
    }

    public void setContentLoopNo(Integer contentLoopNo) {
        this.contentLoopNo = contentLoopNo;
    }

    public Integer getContentNo() {
        return contentNo;
    }

    public void setContentNo(Integer contentNo) {
        this.contentNo = contentNo;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }
}
package com.imcode.imcms.mapping.orm;

import imcode.server.document.textdocument.ContentLoopRef;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "imcms_text_doc_texts_history")
public class TextHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer no;

    private String text;

    private Integer type;

    private ContentLoopRef contentLoopRef;

    private I18nDocRef i18nDocRef;

    @Column(name = "user_id")
    private Integer userId;


    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public TextHistory() {
    }

    public TextHistory(TextDomainObject textDO, UserDomainObject user) {
        setType(textDO.getType());
        setI18nDocRef(textDO.getI18nDocRef());
        setNo(textDO.getNo());
        setText(textDO.getText());
        setContentLoopRef(textDO.getContentLoopRef());
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
        this.type = type;
    }

    /**
     * Equivalent to getText()
     */
    public String toString() {
        return getText();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }

    public ContentLoopRef getContentLoopRef() {
        return contentLoopRef;
    }

    public void setContentLoopRef(ContentLoopRef contentLoopRef) {
        this.contentLoopRef = contentLoopRef;
    }

    public I18nDocRef getI18nDocRef() {
        return i18nDocRef;
    }

    public void setI18nDocRef(I18nDocRef i18nDocRef) {
        this.i18nDocRef = i18nDocRef;
    }
}
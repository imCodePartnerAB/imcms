package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Text")
@Table(name = "imcms_text_doc_texts")
public class TextDocText implements Serializable, Cloneable {

    public enum Type {
        PLAIN_TEXT, HTML
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Text filed no in a document.
     */
    @NotNull
    private Integer no;

    private String text;

    @NotNull
    private Type type;

    @NotNull
    private I18nDocRef i18nDocRef;

    private ContentLoopRef contentLoopRef;

    public TextDocText() {
        this("", Type.PLAIN_TEXT);
    }

    public TextDocText(String text) {
        this(text, Type.PLAIN_TEXT);
    }

    /**
     * Create a text for a text-page.
     *
     * @param text The text
     * @param type The format of the text.
     */
    public TextDocText(String text, Type type) {
        setText(text);
        setType(type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof TextDocText && equals((TextDocText) obj));
    }

    private boolean equals(TextDocText that) {
        return Objects.equals(id, that.id)
                && Objects.equals(text, that.text)
                && Objects.equals(type, that.type)
                && Objects.equals(no, that.no)
                && Objects.equals(i18nDocRef, that.i18nDocRef)
                && Objects.equals(contentLoopRef, that.contentLoopRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, type, no, i18nDocRef, contentLoopRef);
    }


    @Override
    public TextDocText clone() {
        try {
            return (TextDocText) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public I18nDocRef getI18nDocRef() {
        return i18nDocRef;
    }

    public void setI18nDocRef(I18nDocRef i18nDocRef) {
        this.i18nDocRef = i18nDocRef;
    }

    public ContentLoopRef getContentLoopRef() {
        return contentLoopRef;
    }

    public void setContentLoopRef(ContentLoopRef contentLoopRef) {
        this.contentLoopRef = contentLoopRef;
    }
}
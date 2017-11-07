package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Table(name = "imcms_text_doc_texts_history")
public class TextHistory extends TextBase {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public TextHistory(Text text, User modifiedBy) {
        setVersion(text.getVersion());
        setLanguage(text.getLanguage());
        setIndex(text.getIndex());
        setText(text.getText());
        setType(text.getType());
        setLoopEntryRef(text.getLoopEntryRef());
        setModifiedBy(modifiedBy);
        setModifiedDt(new Date());
    }

}

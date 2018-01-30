package com.imcode.imcms.model;

import com.imcode.imcms.mapping.jpa.User;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public abstract class TextHistory extends Text {

    public TextHistory(TextHistory from) {
        super(from);

        setModifiedBy(from.getModifiedBy());
        setModifiedDt(from.getModifiedDt());
    }

    public abstract User getModifiedBy();

    public abstract void setModifiedBy(User modifiedBy);

    public abstract Date getModifiedDt();

    public abstract void setModifiedDt(Date modifiedDt);
}

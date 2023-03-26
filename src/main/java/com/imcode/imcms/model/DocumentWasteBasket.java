package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
public abstract class DocumentWasteBasket implements Serializable {

    protected DocumentWasteBasket(DocumentWasteBasket from) {
        setMetaId(from.getMetaId());
        setAddedDatetime(from.getAddedDatetime());
        setAddedBy(from.getAddedBy());
    }

    public abstract Integer getMetaId();

    public abstract void setMetaId(Integer metaId);

    public abstract Date getAddedDatetime();

    public abstract void setAddedDatetime(Date addedDatetime);

    public abstract UserData getAddedBy();

    public abstract void setAddedBy(UserData addedBy);

}

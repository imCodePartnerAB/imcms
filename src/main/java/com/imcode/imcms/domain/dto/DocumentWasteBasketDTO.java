package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.imcode.imcms.model.DocumentWasteBasket;
import com.imcode.imcms.model.UserData;
import imcode.server.user.UserDomainObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class DocumentWasteBasketDTO extends DocumentWasteBasket {

    private Integer metaId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addedDatetime;

    private UserData addedBy;

    public DocumentWasteBasketDTO() {}

    public DocumentWasteBasketDTO(DocumentWasteBasket from) {
        super(from);
    }

    @Override
    @JsonDeserialize(as = UserDomainObject.class)
    public void setAddedBy(UserData addedBy) {
        this.addedBy = addedBy;
    }
}

package com.imcode.imcms.domain.dto;

import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.TextHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TextHistoryDTO extends TextHistory {
    private final AuditDTO modified = new AuditDTO();
    private Integer index;
    private String text;
    private Type type;
    private String langCode;
    private LoopEntryRefDTO loopEntryRef;
    private UserDTO modifiedBy;

    public TextHistoryDTO(TextHistory from) {
        super(from);
    }

    @Override
    public Integer getDocId() {
        return null;
    }

    @Override
    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefDTO(loopEntryRef);
    }

    @Override
    public Date getModifiedDt() {
        return modified.getFormattedDate();
    }

    @Override
    public void setModifiedDt(Date modifiedDt) {
        this.modified.setDateTime(modifiedDt);
    }

    @Override
    public Integer getModifierId() {
        return this.modifiedBy.getId();
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = new UserDTO(modifiedBy);
    }
}

package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.TextHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TextHistoryDTO extends TextHistory {

    private Integer docId;
    private AuditDTO modified;
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
    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefDTO(loopEntryRef);
    }

    @Override
    public Date getModifiedDt() {
        if (this.modified == null) {
            return null;
        }

        return this.modified.getFormattedDate();
    }

    @Override
    public void setModifiedDt(Date modifiedDt) {
        this.modified = Optional.ofNullable(this.modified).orElse(new AuditDTO());
        this.modified.setDateTime(modifiedDt);
    }

    @Override
    public Integer getModifierId() {
        return this.modifiedBy.getId();
    }

    public void setModifiedBy(UserDTO modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}

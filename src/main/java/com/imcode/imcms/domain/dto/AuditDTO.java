package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.imcms.persistence.entity.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import static imcode.util.DateConstants.*;

@Data
@NoArgsConstructor
public class AuditDTO implements Serializable {

    private static final long serialVersionUID = -1101899199352381698L;

    private Integer id;

    private String by;

    private String date;

    private String time;

    @JsonIgnore
    public Date getFormattedDate() {
        try {
            return DATETIME_DOC_FORMAT.parse(date + " " + time);
        } catch (Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public void setDateTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }

        setDate(DATE_FORMAT.format(dateTime));
        setTime(TIME_FORMAT.format(dateTime));
    }

    public static AuditDTO fromVersion(Version version) {
        final AuditDTO versionAudit = new AuditDTO();
        versionAudit.setDateTime(version.getCreatedDt());
        versionAudit.setId(version.getNo());
        versionAudit.setBy(version.getModifiedBy().getLogin());

        return versionAudit;
    }

}

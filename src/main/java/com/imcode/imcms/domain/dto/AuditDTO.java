package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import static imcode.util.DateConstants.DATETIME_DOC_FORMAT;
import static imcode.util.DateConstants.DATE_FORMAT;
import static imcode.util.DateConstants.TIME_FORMAT;

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

}

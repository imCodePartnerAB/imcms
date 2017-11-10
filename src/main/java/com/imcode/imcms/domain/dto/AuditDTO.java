package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import static imcode.util.DateConstants.DATETIME_DOC_FORMAT;

@Data
@NoArgsConstructor
public class AuditDTO implements Serializable {

    private static final long serialVersionUID = -1101899199352381698L;

    private int id;

    private String by;

    private String date;

    private String time;

    @JsonIgnore
    public Date getFormattedDate() {
        try {
            return DATETIME_DOC_FORMAT.parse(date + " " + time);
        } catch (ParseException e) {
            return null;
        }
    }

}

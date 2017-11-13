package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import static imcode.util.DateConstants.*;

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

    @JsonIgnore
    public void setDateTime(Date dateTime) {
        setDate(DATE_FORMAT.format(dateTime));
        setTime(TIME_FORMAT.format(dateTime));
    }

}

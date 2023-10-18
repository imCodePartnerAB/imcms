package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.imcms.persistence.entity.Version;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

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

    public AuditDTO(Integer id, String by, Date date){
        this.id = id;
        this.by = by;
        setDateTime(date);
    }

    @JsonIgnore
    @SneakyThrows
    public Date getFormattedDate() {
        if (null == date || null == time) {
            return null;
        } else {
            String fixedTime = time.matches(TIME_NO_SECONDS_REGEX) ? time + ":00" : time;
            return DATETIME_DOC_FORMAT.get().parse(date + " " + fixedTime);
        }
    }

    @JsonIgnore
    public void setDateTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }

        setDate(DATE_FORMAT.get().format(dateTime));
        setTime(TIME_FORMAT.get().format(dateTime));
    }

}

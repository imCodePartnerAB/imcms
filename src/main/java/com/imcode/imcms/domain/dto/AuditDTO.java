package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AuditDTO implements Serializable {

    private static final long serialVersionUID = -1101899199352381698L;

    private int id;

    private String by;

    private String date;

    private String time;

}

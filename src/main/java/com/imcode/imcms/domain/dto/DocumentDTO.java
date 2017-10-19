package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class DocumentDTO implements Serializable {

    private static final long serialVersionUID = -2317764204932918145L;

    private int id;

    private String title;

    private String type;

    private String target;

    private String alias;

}

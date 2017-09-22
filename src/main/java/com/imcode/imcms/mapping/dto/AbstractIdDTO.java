package com.imcode.imcms.mapping.dto;

import java.io.Serializable;

public abstract class AbstractIdDTO implements Serializable {

    private Integer id;

    protected AbstractIdDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}

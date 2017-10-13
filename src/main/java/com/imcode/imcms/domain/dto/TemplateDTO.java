package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDTO implements Serializable {

    private static final long serialVersionUID = 441290133487733989L;

    private int id;

    private String name;

    private boolean hidden;

}

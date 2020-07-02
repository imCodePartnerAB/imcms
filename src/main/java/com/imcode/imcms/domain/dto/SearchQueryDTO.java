package com.imcode.imcms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dmizem from Ubrainians for imCode on 19.10.17.
 */
@Data
@NoArgsConstructor
public class SearchQueryDTO implements Serializable {

    private static final long serialVersionUID = -3236215724457387659L;

    private String term;

    private Integer userId;

    private List<Integer> categoriesId;

    private PageRequestDTO page;

    public SearchQueryDTO(String term) {
        this.term = term;
    }
}

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

    private SearchRange searchRange = SearchRange.ALL;

    private Integer userId;

    private List<Integer> categoriesId;

    private PageRequestDTO page;

    /**
     * Restrict search relative to this role.
     */
    private Integer roleId;

    private Boolean linkableByOtherUsers;

    public SearchQueryDTO(String term) {
        this.term = term;
    }

    public enum SearchRange {
        ALL,
        BASIC
    }
}

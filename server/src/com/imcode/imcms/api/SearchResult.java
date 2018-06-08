package com.imcode.imcms.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class SearchResult<T> {

    private final List<T> result;
    private final int totalCount;
    private final int nextSkip;

    public SearchResult(List<T> result, int totalCount) {
        this(result, totalCount, 0);
    }

    public SearchResult(List<T> result, int totalCount, int nextSkip) {
        this.result = result;
        this.totalCount = totalCount;
        this.nextSkip = nextSkip;
    }

    public static <T> SearchResult<T> empty() {
        return new SearchResult<>(Collections.emptyList(), 0);
    }

    public static <T> SearchResult<T> of(List<T> result, int totalCount) {
        return new SearchResult<>(result, totalCount);
    }

}

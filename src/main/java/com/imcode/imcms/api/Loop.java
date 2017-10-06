package com.imcode.imcms.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public final class Loop {

    private final Map<Integer, Boolean> entries;

    public Loop(Map<Integer, Boolean> entries) {
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }
}

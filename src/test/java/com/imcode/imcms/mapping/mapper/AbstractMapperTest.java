package com.imcode.imcms.mapping.mapper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public abstract class AbstractMapperTest<FROM, TO> {

    @Autowired
    private Function<FROM, TO> mapper;

    abstract FROM getOrigin();

    abstract TO getExpectedResult();

    @Test
    public void expectedEqualsMapResult() throws Exception {
        assertEquals(getExpectedResult(), mapper.apply(getOrigin()));
    }
}

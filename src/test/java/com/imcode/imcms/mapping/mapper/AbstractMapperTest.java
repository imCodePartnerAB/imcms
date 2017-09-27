package com.imcode.imcms.mapping.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

public abstract class AbstractMapperTest<FROM, TO> {

    @Autowired
    protected Function<FROM, TO> mapper;

    protected FROM origin;
    protected TO expectedResult;

    protected void setUp(FROM origin, TO expectedResult) {
        this.origin = origin;
        this.expectedResult = expectedResult;
    }

    @Test
    public void expectedEqualsMapResult() throws Exception {
        Assert.assertEquals(expectedResult, mapper.apply(origin));
    }
}

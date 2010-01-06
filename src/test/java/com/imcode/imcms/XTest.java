package com.imcode.imcms;

import org.testng.annotations.Test;

@Test
public class XTest {

    @Test
    public void x1() {}


    @Test(dependsOnMethods = {"x1"})
    public void x2() {
        throw new RuntimeException("gug1");
    }
}

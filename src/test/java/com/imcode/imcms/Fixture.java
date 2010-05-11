package com.imcode.imcms;

import clojure.lang.RT;
import clojure.lang.Var;
import imcode.server.Imcms;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static java.lang.System.out;

@Test
public class Fixture {

    public Fixture() throws Exception{
        RT.load("com/imcode/imcms/runtime");
        RT.load("com/imcode/imcms/project");
    }

    @BeforeSuite
    public void initImcms() throws Exception {
        Var initFn = RT.var("com.imcode.imcms.project", "init-imcms");

        initFn.invoke();
    }
}
package com.imcode.imcms;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;

/**
 *
 */
@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({MappersTests.A.class, MappersTests.B.class})
public class MappersTests {

    public MappersTests() {
        System.out.println("CONS MappersTests");
    }

    @Before public void setUp() {
        System.out.println("SETUP MappersTests");
    }

    @BeforeClass public static void setUpClass() {
        System.out.println("SETUP CLASS MappersTests");
    }

    public static class A{

        @BeforeClass public static void setUpClass() {
            System.out.println("SETUP CLASS A");
        }

        public A() {
            System.out.println("CONS A");
        }

        @Test public void doA1() {
            System.out.println("a1");
        }

        @Test public void doA2() {
            System.out.println("a2");
        }

        @Before public void setUp() {
            System.out.println("SETUP A");
        }
    }

    public static class B {

        @BeforeClass public static void setUpClass() {
            System.out.println("SETUP CLASS B");
        }

        public B() {
            System.out.println("CONS B");
        }

        @Test public void doB1() {
            System.out.println("b1");
        }

        @Test public void doB2() {
            System.out.println("b2");
        }

        @Before public void setUp() {
            System.out.println("SETUP B");
        }
    }

}

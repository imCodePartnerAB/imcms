package com.imcode.imcms.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(value=Parameterized.class)
public class LoginPasswordManagerTest {

    private LoginPasswordManager manager;

    public LoginPasswordManagerTest(String sharedSalt) {
        manager = new LoginPasswordManager(sharedSalt);
    }

    @Parameterized.Parameters
    static public Collection<String[]> parameters() {
        return Arrays.asList(new String[][] {{null}, {""}, {"secret"}});
    }

    @Test
    public void encryptPassword() {
        String password = "abcdefg";
        String encryptedPassword = manager.encryptPassword(password);
    }


    @Test
    public void validatePasswordUsingSamePassword() {
        String password = "abcdefg";
        String encryptedPassword = manager.encryptPassword(password);
        assertTrue("Password is valid", manager.validatePassword(password, encryptedPassword));
    }

    @Test
    public void validatePasswordUsingDifferentPassword() {
        String password = "abcdefg";
        String differentPassword = "abcdefgx";
        String encryptedPassword = manager.encryptPassword(password);
        assertFalse("Password is not valid", manager.validatePassword(differentPassword, encryptedPassword));
    }
}

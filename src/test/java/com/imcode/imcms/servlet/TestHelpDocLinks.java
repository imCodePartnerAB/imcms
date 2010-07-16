package com.imcode.imcms.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import com.imcode.imcms.test.mock.casual.TestLanguagePropertiesInUse;
import com.imcode.util.FileTreeTraverser;

public class TestHelpDocLinks extends TestCase {

    public void testHelpDocLinks() throws IOException {

        Set propertyKeysInUse = getPropertyKeysInUse();
        assertEquals(0, propertyKeysInUse.size());
    }


    private Set getPropertyKeysInUse() {
        TestLanguagePropertiesInUse.StringCollectingFileFilter stringCollectingFilter = new TestLanguagePropertiesInUse.StringCollectingFileFilter("m/openHelpW\\((\\d+?)\\)/");
        FileTreeTraverser fileTreeTraverser = new FileTreeTraverser(stringCollectingFilter);
        fileTreeTraverser.traverseDirectory( new File( "web" ) );
        //stringCollectingFilter.setStringPattern( "m/\"(\\S+)\"/" );
        //fileTreeTraverser.traverseDirectory( new File( "server" ) );
        Set propertyKeysInUse = stringCollectingFilter.getCollectedStrings();
        return propertyKeysInUse;
    }



}
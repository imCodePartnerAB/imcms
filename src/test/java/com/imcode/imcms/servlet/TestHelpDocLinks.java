package com.imcode.imcms.servlet;

import junit.framework.*;

import java.util.Set;
import java.util.HashSet;
import java.io.*;

import com.imcode.imcms.test.casual.TestLanguagePropertiesInUse;
import com.imcode.util.FileTreeTraverser;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.commons.lang.UnhandledException;

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
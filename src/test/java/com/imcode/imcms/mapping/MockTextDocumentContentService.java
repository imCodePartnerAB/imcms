package com.imcode.imcms.mapping;

import junit.framework.TestCase;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
public class MockTextDocumentContentService extends TestCase {

    private TextDocumentContentLoader contentLoader;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentLoader = new TextDocumentContentLoader();
    }

    public void testHtmlTagsWhiteList() {
        final Set<String> allowedTags = new HashSet<>();
        allowedTags.add("div");
        allowedTags.add("span");
        allowedTags.add("p");

        contentLoader.addHtmlTagsToWhiteList(allowedTags);
        final Set<String> htmlTagsWhitelist = contentLoader.getHtmlTagsWhitelist();
        assertTrue(CollectionUtils.isEqualCollection(allowedTags, htmlTagsWhitelist));
    }
}

package com.imcode.imcms.mapping;

import com.imcode.imcms.document.text.AllowedTagsCheckingResult;
import com.imcode.imcms.document.text.TextContentFilter;
import junit.framework.TestCase;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
public class MockTextDocumentContentService extends TestCase {

    private TextContentFilter textContentFilter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        textContentFilter = new TextContentFilter();
    }

    public void testHtmlTagsWhiteList() {
        final String[] allowedTags = {
                "div",
                "span",
                "p",
        };

        AllowedTagsCheckingResult checkingResult = textContentFilter.addHtmlTagsToWhiteList(allowedTags)
                .checkBadTags("<test-tag>this is a not allowed tag test</test-tag>");

        assertTrue(checkingResult.isFail());

        checkingResult = textContentFilter.checkBadTags("<div>this is allowed tag test</div>");

        assertTrue(checkingResult.isSuccess());
    }
}

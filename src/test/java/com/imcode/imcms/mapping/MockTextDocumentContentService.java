package com.imcode.imcms.mapping;

import com.imcode.imcms.document.text.AllowedTagsCheckingResult;
import com.imcode.imcms.document.text.TextContentFilter;
import junit.framework.TestCase;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

        final String[] badTagsArr = {
                "q",
                "img",
                "script"
        };

        final String badTag = "test-tag";
        final Set<String> badTags = new HashSet<>(Arrays.asList(badTagsArr));

        AllowedTagsCheckingResult checkingResult = textContentFilter.addHtmlTagsToWhiteList(allowedTags)
                .checkBadTags("<div>this is allowed tag test</div>");
        assertTrue(checkingResult.isSuccess());

        checkingResult = textContentFilter.checkBadTags("<" + badTag + ">this is a not allowed tag test</" + badTag + ">");
        assertTrue(checkingResult.isFail());
        assertNotNull(checkingResult.getBadTags());
        assertNotNull(checkingResult.getBadTags().iterator().next());
        assertTrue(checkingResult.getBadTags().iterator().next().equals(badTag));

        checkingResult = textContentFilter.checkBadTags(
                "<" + badTagsArr[0] + " class=\"some-class\">alalal</" + badTagsArr[0] + ">" +
                        "test text" +
                        "<" + badTagsArr[1] + " src=\"http://blabla.com\">" +
                        "test text" +
                        "<" + badTagsArr[2] + " src=\"http://blabla.com\"/>"
        );
        assertTrue(checkingResult.isFail());
        assertNotNull(checkingResult.getBadTags());
        assertNotNull(checkingResult.getBadTags().iterator().next());
        assertTrue(CollectionUtils.isEqualCollection(badTags, checkingResult.getBadTags()));
    }
}

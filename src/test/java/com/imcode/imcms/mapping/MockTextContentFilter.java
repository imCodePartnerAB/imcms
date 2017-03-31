package com.imcode.imcms.mapping;

import com.imcode.imcms.document.text.AllowedTagsCheckingResult;
import com.imcode.imcms.document.text.TextContentFilter;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
public class MockTextContentFilter {

    private final String[] allowedTags = {
            "div",
            "span",
            "p",
    };
    private final String[] badTagsArr = {
            "q",
            "img",
            "script"
    };
    private final Set<String> badTags = new HashSet<>(Arrays.asList(badTagsArr));
    private TextContentFilter textContentFilter;

    @Before
    public void setUp() throws Exception {
        textContentFilter = new TextContentFilter().addHtmlTagsToWhiteList(allowedTags);
    }

    @Test
    public void testBadTagsChecking() {
        testEmptyText();
        testTextWithoutTags();
        testTextWithSpecialSymbolButNotTag();
        testAllowedTag();
        testBadTag();
        testBadTags();
    }

    @Test
    public void testBadTags() {
        AllowedTagsCheckingResult checkingResult = textContentFilter.checkBadTags(
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

    @Test
    public void testBadTag() {
        final String badTag = "test-tag";
        AllowedTagsCheckingResult checkingResult = textContentFilter
                .checkBadTags("<" + badTag + ">this is a not allowed tag test</" + badTag + ">");

        assertTrue(checkingResult.isFail());
        assertNotNull(checkingResult.getBadTags());
        assertNotNull(checkingResult.getBadTags().iterator().next());
        assertTrue(checkingResult.getBadTags().iterator().next().equals(badTag));
    }

    @Test
    public void testAllowedTag() {
        AllowedTagsCheckingResult checkingResult = textContentFilter
                .checkBadTags("<div>this is allowed tag test</div>");

        assertTrue(checkingResult.isSuccess());
    }

    @Test
    public void testTextWithSpecialSymbolButNotTag() {
        AllowedTagsCheckingResult checkingResult = textContentFilter
                .checkBadTags("text with tags symbol \"<\" but this is not a tag ");

        assertTrue(checkingResult.isSuccess());
    }

    @Test
    public void testTextWithoutTags() {
        AllowedTagsCheckingResult checkingResult = textContentFilter.checkBadTags("text without tags");

        assertTrue(checkingResult.isSuccess());
    }

    @Test
    public void testEmptyText() {
        AllowedTagsCheckingResult checkingResult = textContentFilter.checkBadTags("");
        assertTrue(checkingResult.isSuccess());
    }
}

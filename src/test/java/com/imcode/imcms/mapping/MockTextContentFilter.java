package com.imcode.imcms.mapping;

import com.imcode.imcms.document.text.TextContentFilter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        final String textWithBadTags = "<" + badTagsArr[0] + " class=\"some-class\">alalal</" + badTagsArr[0] + ">"
                + "test text"
                + "<" + badTagsArr[1] + " src=\"http://blabla.com\">"
                + "test text"
                + "<" + badTagsArr[2] + " src=\"http://blabla.com\"/>";

        final String expectedCleanedText = "alalal"
                + "test text"
                + "test text";

        final String textAfterCleanup = textContentFilter.cleanText(textWithBadTags);
        assertEquals(expectedCleanedText, textAfterCleanup);
    }

    @Test
    public void testBadTag() {
        final String badTag = "test-tag";
        final String textWithBadTag = "<" + badTag + ">this is a not allowed tag test</" + badTag + ">";
        final String expectedCleanedText = "this is a not allowed tag test";
        final String textAfterCleanup = textContentFilter.cleanText(textWithBadTag);
        assertEquals(expectedCleanedText, textAfterCleanup);
    }

    @Test
    public void testAllowedTag() {
        final String textWithAllowedTag = "<div> this is allowed tag test </div>";
        final String textAfterCleanup = textContentFilter.cleanText(textWithAllowedTag);
        assertEquals(textWithAllowedTag, textAfterCleanup);
    }

    @Test
    public void testTextWithSpecialSymbolButNotTag() {
        final String textWithSpecialSymbolButNotTag = "text with tags symbol \"<\" but this is not a tag";
        final String textAfterCleanup = textContentFilter.cleanText(textWithSpecialSymbolButNotTag);
        assertEquals(textWithSpecialSymbolButNotTag, textAfterCleanup);
    }

    @Test
    public void testTextWithoutTags() {
        final String textWithoutTags = "text without tags";
        final String textAfterCleanup = textContentFilter.cleanText(textWithoutTags);
        assertEquals(textWithoutTags, textAfterCleanup);
    }

    @Test
    public void testEmptyText() {
        final String emptyText = "";
        final String textAfterCleanup = textContentFilter.cleanText(emptyText);
        assertEquals(emptyText, textAfterCleanup);
    }
}

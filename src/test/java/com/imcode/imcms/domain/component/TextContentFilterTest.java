package com.imcode.imcms.domain.component;

import com.imcode.imcms.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class})
public class TextContentFilterTest {

    private final String[] allowedTags = {
            "div",
            "span",
            "p",
    };
    private final String[] badTagsArr = {
            "iframe",
            "script"
    };

    @Autowired
    private TextContentFilter textContentFilter;

    @Before
    public void setUp() throws Exception {
        textContentFilter = textContentFilter.addHtmlTagsToWhiteList(allowedTags);
    }

    @Test
    public void testBadTags() {
        final String textWithBadTags = "<" + badTagsArr[0] + " class=\"some-class\">alalal</" + badTagsArr[0] + ">"
                + "test text"
                + "<" + badTagsArr[1] + " src=\"http://blabla.com\"></" + badTagsArr[1] + ">"
                + "<" + badTagsArr[1] + ">alert('!!!')</" + badTagsArr[1] + ">"
                + "test text";

        final String expectedCleanedText = "alalal"
                + "test text"
                + "test text";

        final String textAfterCleanup = textContentFilter.cleanText(textWithBadTags);
        assertEquals(expectedCleanedText, textAfterCleanup);
    }

    @Test
    public void testBadScriptTag() {
        final String textWithBadTag = "<script>this is a not allowed tag test</script>";
        final String expectedCleanedText = "";
        final String textAfterCleanup = textContentFilter.cleanText(textWithBadTag);
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

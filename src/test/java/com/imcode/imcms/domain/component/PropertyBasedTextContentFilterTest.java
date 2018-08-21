package com.imcode.imcms.domain.component;

import com.imcode.imcms.model.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class PropertyBasedTextContentFilterTest {

    private final String[] allowedTags = {
            "div",
            "span",
            "p",
            "b",
            "h1"
    };
    private final String[] badTagsArr = {
            "iframe",
            "script"
    };

    @InjectMocks
    private PropertyBasedTextContentFilter textContentFilter;

    @BeforeEach
    void setUp() {
        textContentFilter.addHtmlTagsToWhiteList(allowedTags);
    }

    @Test
    void testBadTags() {
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
    void testBadScriptTag() {
        final String textWithBadTag = "<script>this is a not allowed tag test</script>";
        final String expectedCleanedText = "";
        final String textAfterCleanup = textContentFilter.cleanText(textWithBadTag);
        assertEquals(expectedCleanedText, textAfterCleanup);
    }

    @Test
    void testBadTag() {
        final String badTag = "test-tag";
        final String textWithBadTag = "<" + badTag + ">this is a not allowed tag test</" + badTag + ">";
        final String expectedCleanedText = "this is a not allowed tag test";
        final String textAfterCleanup = textContentFilter.cleanText(textWithBadTag);
        assertEquals(expectedCleanedText, textAfterCleanup);
    }

    @Test
    void testAllowedTag() {
        final String textWithAllowedTag = "<div> this is allowed tag test </div>";
        final String textAfterCleanup = textContentFilter.cleanText(textWithAllowedTag);
        assertEquals(textWithAllowedTag, textAfterCleanup);
    }

    @Test
    void testTextWithSpecialSymbolButNotTag() {
        final String textWithSpecialSymbolButNotTag = "text with tags symbol \"<\" but this is not a tag";
        final String textAfterCleanup = textContentFilter.cleanText(textWithSpecialSymbolButNotTag);
        assertEquals(textWithSpecialSymbolButNotTag, textAfterCleanup);
    }

    @Test
    void testTextWithoutTags() {
        final String textWithoutTags = "text without tags";
        final String textAfterCleanup = textContentFilter.cleanText(textWithoutTags);
        assertEquals(textWithoutTags, textAfterCleanup);
    }

    @Test
    void testEmptyText() {
        final String emptyText = "";
        final String textAfterCleanup = textContentFilter.cleanText(emptyText);
        assertEquals(emptyText, textAfterCleanup);
    }

    @Test
    void cleanText_When_OkTextWithIllegalTextTogether_Expected_IllegalTextRemoved() {
        final String correctFirstPart = "some other <b>text</b> here";
        final String wrongPart = "<script>console.log('test');console.log('pshhh');</script>";
        final String correctTail = "and <h1>here</h1>";
        final String expected = correctFirstPart + correctTail;

        final String cleaned = textContentFilter.cleanText(correctFirstPart + wrongPart + correctTail);

        assertEquals(expected, cleaned);
    }

    @Test
    void cleanText_When_FilteringPolicyIsAllowAll_Expect_NothingChanged() {
        final String correctFirstPart = "some other <b>text</b> here";
        final String wrongPart = "<script>console.log('test');console.log('pshhh');</script>";
        final String correctTail = "and <h1>here</h1>";
        final String input = correctFirstPart + wrongPart + correctTail;

        final String cleaned = textContentFilter.cleanText(input, Text.HtmlFilteringPolicy.ALLOW_ALL);

        assertEquals(input, cleaned);
    }

    @Test
    void cleanText_When_FilteringPolicyIsRelaxed_Expect_IllegalTagsRemovedAndNotAllowedTagsUnwrapped() {
        final String illegal0 = "<head>illegal tag0!</head>";
        final String illegal1 = "<script>illegal tag1!</script>";
        final String illegal2 = "<embed>illegal tag2!</embed>";
        final String illegal3 = "<style>illegal tag3!</style>";

        final String legalContent = "<span>not allowed tag, should be unwrapped</span>";
        final String notAllowed0 = "<html>" + legalContent + "</html>";
        final String notAllowed1 = "<body>" + legalContent + "</body>";
        final String notAllowed2 = "<doctype>" + legalContent + "</doctype>";

        final String legalTag = "<div class=\"test-class\" style=\"display: block;\">legal stuff</div>";

        final String cleanMe = illegal0 + illegal1 + illegal2 + illegal3 + notAllowed0 + notAllowed1 + notAllowed2
                + legalTag;

        final String cleaned = textContentFilter.cleanText(cleanMe, Text.HtmlFilteringPolicy.RELAXED);

        final String expected = legalContent + legalContent + legalContent + legalTag;

        assertEquals(expected, cleaned);
    }

    @Test
    void cleanText_When_FilteringPolicyIsRestricted_Expect_IllegalTagsRemovedAndNotAllowedTagsUnwrappedAndIllegalAttributesRemoved() {
        final String illegal0 = "<script>illegal tag1!</script>";
        final String illegal1 = "<style>illegal tag3!</style>";

        final String legalContent = "<span>not allowed tag, should be unwrapped</span>";
        final String notAllowed0 = "<html>" + legalContent + "</html>";
        final String notAllowed1 = "<body>" + legalContent + "</body>";
        final String notAllowed2 = "<doctype>" + legalContent + "</doctype>";
        final String notAllowed3 = "<head>" + legalContent + "</head>";
        final String notAllowed4 = "<embed>" + legalContent + "</embed>";

        final String partiallyLegalTag = "<div class=\"test-class\" style=\"display: block;\">legal stuff</div>";
        final String legalTag = "<div class=\"test-class\">legal stuff</div>";

        final String cleanMe = illegal0 + illegal1 + notAllowed0 + notAllowed1 + notAllowed2 + notAllowed3 + notAllowed4
                + partiallyLegalTag;

        final String cleaned = textContentFilter.cleanText(cleanMe, Text.HtmlFilteringPolicy.RESTRICTED);

        final String expected = legalContent + legalContent + legalContent + legalContent + legalContent + legalTag;

        assertEquals(expected, cleaned);
    }
}

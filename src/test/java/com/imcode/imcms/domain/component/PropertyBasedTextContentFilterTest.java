package com.imcode.imcms.domain.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.imcode.imcms.model.Text.HtmlFilteringPolicy.*;
import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class PropertyBasedTextContentFilterTest {

    private final String[] badTagsArr = {
            "iframe",
            "script"
    };

    @InjectMocks
    private PropertyBasedTextContentFilter textContentFilter;

    @BeforeEach
    void setUp() {
        textContentFilter.init();
    }

    private void assertEqualsWithDifferentPolicies(String expectedText, String textForCleaning) {
        final String textAfterRelaxedCleanup = textContentFilter.cleanText(textForCleaning, RELAXED);
        assertEquals(expectedText, textAfterRelaxedCleanup);

        final String textAfterRestrictedCleanup = textContentFilter.cleanText(textForCleaning, RESTRICTED);
        assertEquals(expectedText, textAfterRestrictedCleanup);
    }

    @Test
    void cleanText_When_TagsNotAllowedWithDifferentPolicies_Expected_CorrectCleaning() {
        final String textWithBadTags = "<" + badTagsArr[0] + " class=\"some-class\">alalal</" + badTagsArr[0] + ">"
                + "test text"
                + "<" + badTagsArr[1] + " src=\"http://blabla.com\"></" + badTagsArr[1] + ">"
                + "<" + badTagsArr[1] + ">alert('!!!')</" + badTagsArr[1] + ">"
                + "test text";

        final String expectedCleanedText = "alalal"
                + "test text"
                + "test text";

        assertEqualsWithDifferentPolicies(expectedCleanedText, textWithBadTags);
    }

    @Test
    void cleanText_When_CustomTagWithDifferentPolicies_CorrectCleaning() {
        final String badTag = "test-tag";
        final String textWithBadTag = "<" + badTag + ">this is a not allowed tag test</" + badTag + ">";
        final String expectedCleanedText = "this is a not allowed tag test";

        assertEqualsWithDifferentPolicies(expectedCleanedText, textWithBadTag);
    }

    @Test
    void cleanText_When_TagsAreAllowedAndFilteringPolicyRelaxed_Expected_SameText() {
        final String textWithAllowedTag = "<i> this is allowed tag test </i>";
        final String textAfterCleanup = textContentFilter.cleanText(textWithAllowedTag, RELAXED);
        assertEquals(textWithAllowedTag, textAfterCleanup);
    }

    @Test
    void cleanText_When_SpecialSymbolFoundedWithDifferentPolicies_Expected_SymbolNotDeleted() {
        final String textWithSpecialSymbolButNotTag = "text with tags symbol \"<\" but this is not a tag";
        final String textAfterCleanup = textContentFilter.cleanText(textWithSpecialSymbolButNotTag);

        assertEqualsWithDifferentPolicies(textWithSpecialSymbolButNotTag, textAfterCleanup);
    }

    @Test
    void cleanText_When_TextWithoutTags_Expected_SameText() {
        final String textWithoutTags = "text without tags";
        final String textAfterCleanup = textContentFilter.cleanText(textWithoutTags);
        assertEqualsWithDifferentPolicies(textWithoutTags, textAfterCleanup);
    }

    @Test
    void cleanText_When_TextIsEmpty_Expected_SameText() {
        final String emptyText = "";
        final String textAfterCleanup = textContentFilter.cleanText(emptyText);
        assertEqualsWithDifferentPolicies(emptyText, textAfterCleanup);
    }

    @Test
    void cleanText_When_OkTextWithIllegalTextTogetherAndPolicyIsRelaxed_Expected_IllegalTextRemoved() {
        final String correctFirstPart = "some other \n<b>text</b> here";
        final String wrongPart = "<script>console.log('test');console.log('pshhh');</script>";
        final String correctTail = "and \n<i>here</i>";
        final String expected = correctFirstPart + correctTail;

        final String cleaned = textContentFilter.cleanText(correctFirstPart + wrongPart + correctTail, RELAXED);
        assertEquals(expected, cleaned);
    }

    @Test
    void cleanText_When_OkTextWithIllegalTextTogetherAndFilteringPolicyIsAllowAll_Expect_NothingChanged() {
        final String correctFirstPart = "some other <b>text</b> here";
        final String wrongPart = "<script>console.log('test');console.log('pshhh');</script>";
        final String correctTail = "and <h1>here</h1>";
        final String input = correctFirstPart + wrongPart + correctTail;

        final String cleaned = textContentFilter.cleanText(input, ALLOW_ALL);
        assertEquals(input, cleaned);
    }

    @Test
    void cleanText_When_DifferentPolicies_Expect_IllegalTagsRemovedAndNotAllowedTagsUnwrapped() {
        final String illegal0 = "<head>illegal tag0!</head>";
        final String illegal1 = "<script>illegal tag1!</script>";
        final String illegal2 = "<embed>illegal tag2!</embed>";
        final String illegal3 = "<style>illegal tag3!</style>";

        final String legalContent = "not allowed tag, should be unwrapped";
        final String notAllowed0 = "<html>" + legalContent + "</html>";
        final String notAllowed1 = "<body>" + legalContent + "</body>";
        final String notAllowed2 = "<doctype>" + legalContent + "</doctype>";
        final String notAllowed3 = "<div class=\"test-class\" style=\"display: block;\">" + legalContent + "</div>";
        final String notAllowed4 = "<span>" + legalContent + "</span>";

        final String cleanMe = illegal0 + illegal1 + illegal2 + illegal3 + notAllowed0 + notAllowed1 + notAllowed2
                + notAllowed3 + notAllowed4;

        final String expected = legalContent + legalContent + legalContent + legalContent + legalContent;

        assertEqualsWithDifferentPolicies(expected, cleanMe);
    }

    @Test
    void cleanText_When_ContainsLegalAndIllegalAttributesWithDifferentPolicies_Expected_RemoveIllegalAttributes() {
        final String legalAttribute = "align=\"center\"";
        final String illegalAttribute = "class=\"left\"";

        final String cleanMe = String.format("<p %s %s>Some text</p>", legalAttribute, illegalAttribute);
        final String expected = String.format("<p %s>Some text</p>", legalAttribute);

        assertEqualsWithDifferentPolicies(expected, cleanMe);
    }
}

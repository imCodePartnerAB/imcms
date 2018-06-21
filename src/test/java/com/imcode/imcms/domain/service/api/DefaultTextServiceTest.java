package com.imcode.imcms.domain.service.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTextServiceTest {

    @InjectMocks
    private DefaultTextService textService;

    @Test
    public void cleanScriptContent_With_EmptyString_Expect_EmptyStringReturned() {
        final String text = "";
        final String cleaned = textService.cleanScriptContent(text);

        assertEquals(text, cleaned);
    }

    @Test
    public void cleanScriptContent_With_LegalString_Expect_SameReturn() {
        final String text = "some legal stuff <div>and html</div><span idasda=\"test\">spanndnn</span>";
        final String cleaned = textService.cleanScriptContent(text);

        assertEquals(text, cleaned);
    }

    @Test
    public void cleanScriptContent_With_EmptyScriptTag_Expect_SameReturn() {
        final String text = "<script></script>";
        final String cleaned = textService.cleanScriptContent(text);

        assertEquals(text, cleaned);
    }

    @Test
    public void cleanScriptContent_With_NotEmptyButLegalScriptTag_Expect_SameReturn() {
        final String text = "<script>console.log('test')</script>";
        final String cleaned = textService.cleanScriptContent(text);

        assertEquals(text, cleaned);
    }

    @Test
    public void cleanScriptContent_With_ScriptTagWithBrTagInside_Expect_BrTagRemovedFromResult() {
        final String correctHead = "<script>console.log('test')";
        final String wrongPart = "<br />";
        final String correctTail = "</script>";
        final String wrongPartReplaced = "\n";
        final String expected = correctHead + wrongPartReplaced + correctTail;
        final String cleaned = textService.cleanScriptContent(correctHead + wrongPart + correctTail);

        assertEquals(expected, cleaned);
    }

    @Test
    public void cleanScriptContent_With_TextAndScriptTagWithBrTagInside_Expect_BrTagRemovedFromResult() {
        final String correctFirstPart = "some other <b>text</b> here<script>console.log('test')";
        final String wrongPart = "<br /><br ><br><br/>";
        final String correctTail = "</script>and <h1>here</h1>";
        final String wrongPartReplaced = "\n";
        final String expected = correctFirstPart + wrongPartReplaced + correctTail;
        final String cleaned = textService.cleanScriptContent(correctFirstPart + wrongPart + correctTail);

        assertEquals(expected, cleaned);
    }

    @Test
    public void cleanScriptContent_With_TextAndScriptTagWithManyBrTagsInside_Expect_BrTagRemovedFromResult() {
        final String correctFirstPart = "some other <b>text</b> here<script>console.log('test');";
        final String wrongPart = "<br /><br ><br><br/>";
        final String middleNoise = "console.log('pshhh');";
        final String correctTail = "</script>and <h1>here</h1>";
        final String wrongPartReplaced = "\n";
        final String expected = correctFirstPart + wrongPartReplaced + middleNoise + wrongPartReplaced + middleNoise
                + wrongPartReplaced + correctTail;

        final String cleaned = textService.cleanScriptContent(
                correctFirstPart + wrongPart + middleNoise + wrongPart + middleNoise + wrongPart + correctTail
        );

        assertEquals(expected, cleaned);
    }

    @Test
    public void cleanScriptContent_With_TextAndTwoScriptTagsWithManyBrTagsInside_Expect_BrTagRemovedOnlyInScriptTag() {
        final String correctFirstPart = "some other <b>text</b> here<script>console.log('test');";
        final String wrongPart = "<br /><br ><br><br/>";
        final String middleNoise = "console.log('pshhh');";
        final String correctTail = "</script>and <h1>here</h1>";
        final String wrongPartReplaced = "\n";
        final String expected = wrongPart
                + correctFirstPart + wrongPartReplaced + middleNoise + wrongPartReplaced + middleNoise + wrongPartReplaced + correctTail
                + wrongPart
                + correctFirstPart + wrongPartReplaced + middleNoise + wrongPartReplaced + middleNoise + wrongPartReplaced + correctTail
                + wrongPart;

        final String cleaned = textService.cleanScriptContent(
                wrongPart + correctFirstPart + wrongPart + middleNoise + wrongPart + middleNoise + wrongPart
                        + correctTail + wrongPart + correctFirstPart + wrongPart + middleNoise + wrongPart
                        + middleNoise + wrongPart + correctTail + wrongPart
        );

        assertEquals(expected, cleaned);
    }

    @Test
    public void cleanScriptContent_With_EscapedHtmlInside_Expect_ReturnedUnescaped() {
        final String correctFirstPart = "some other <b>text</b> here<script>console.log('test');";
        final String wrongPart = "<br /><br ><br><br/>";
        final String middleNoise = "console.log('pshhh');";
        final String escapedText = "&amp;";
        final String unescapedText = "&";
        final String correctTail = "</script>and <h1>here</h1>";
        final String cleaned = textService.cleanScriptContent(
                wrongPart + correctFirstPart + wrongPart + middleNoise + wrongPart + middleNoise + wrongPart
                        + correctTail + wrongPart + escapedText + correctFirstPart + wrongPart + middleNoise + wrongPart
                        + middleNoise + escapedText + wrongPart + correctTail + wrongPart
        );

        final String wrongPartReplaced = "\n";
        final String expected = wrongPart + correctFirstPart + wrongPartReplaced + middleNoise + wrongPartReplaced + middleNoise + wrongPartReplaced + correctTail + wrongPart
                + escapedText + correctFirstPart + wrongPartReplaced + middleNoise + wrongPartReplaced + middleNoise + unescapedText + wrongPartReplaced + correctTail + wrongPart;
        assertEquals(expected, cleaned);
    }
}

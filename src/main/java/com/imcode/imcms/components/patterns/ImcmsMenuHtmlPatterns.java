package com.imcode.imcms.components.patterns;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ImcmsMenuHtmlPatterns {
    private final String patternUlDataAttr = "<ul data-menu-index=\"%d\" data-doc-id=\"%d\"";
    private final String patternWithoutTagUlDataAttr = " data-menu-index=\"%d\" data-doc-id=\"%d\"";
    private final String patternStartUlClassAttr = "<ul class=\"%s %s %s %s--%d-%d\"";
    private final String patternStartSimpleUlClassAttr = " class=\"%s %s %s %s--%d-%d\"";
    private final String patternLiDataAttr = "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\"";
    private final String patternSimpleLiDataAttr = " %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\"";
    private final String patternLiClassAttr = "<li class=\"%s %s %s\"";
    private final String patternSimpleClassAttr = " class=\"%s %s %s\"";
}

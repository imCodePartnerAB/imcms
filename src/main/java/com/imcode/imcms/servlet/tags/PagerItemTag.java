package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.PagerItem;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Created by Shadowgun on 06.03.2015.
 */
public class PagerItemTag extends TagSupport {
    private PagerTag pagerTag;
    private PagerItem pagerItem;

    @Override
    public int doStartTag() {
        pagerTag = (PagerTag) findAncestorWithClass(this, PagerTag.class);
        assert pagerTag != null;
        if ((pagerItem = pagerTag.nextPagerItem()) != null)
            return EVAL_BODY_INCLUDE;
        else
            return SKIP_BODY;
    }

    @Override
    public int doAfterBody() {
        if ((pagerItem = pagerTag.nextPagerItem()) != null)
            return EVAL_BODY_AGAIN;
        else return SKIP_BODY;
    }

    @Override
    public int doEndTag() {
        return EVAL_PAGE;
    }
}

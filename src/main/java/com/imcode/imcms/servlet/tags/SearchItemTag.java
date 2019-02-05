package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.SearchItem;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Created by Shadowgun on 04.03.2015.
 */
public class SearchItemTag extends TagSupport {
    private SearchTag searchTag;
    private SearchItem searchItem;

    @Override
    public int doStartTag() {
        searchTag = (SearchTag) findAncestorWithClass(this, SearchTag.class);
        assert searchTag != null;
        if ((searchItem = searchTag.nextSearchItem()) != null)
            return EVAL_BODY_INCLUDE;
        else
            return SKIP_BODY;
    }

    @Override
    public int doAfterBody() {
        if ((searchItem = searchTag.nextSearchItem()) != null)
            return EVAL_BODY_AGAIN;
        else return SKIP_BODY;
    }

    @Override
    public int doEndTag() {
        return EVAL_PAGE;
    }
}

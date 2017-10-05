//package com.imcode.imcms.servlet.tags;
//
//import javax.servlet.jsp.JspException;
//import javax.servlet.jsp.JspTagException;
//import javax.servlet.jsp.tagext.TagSupport;
//
///**
// * @deprecated without replacement
// */
//@Deprecated
//public class LoopItemTag extends TagSupport {
//    public int doStartTag() throws JspException {
//        return EVAL_BODY_INCLUDE;
//    }
//
//    public int doEndTag() throws JspException {
//        LoopTag loopTag = (LoopTag) findAncestorWithClass(this, LoopTag.class);
//
//        if (loopTag == null) {
//            throw new JspTagException("loopitem must be enclosed in loop or loopentry.");
//        }
//
//        loopTag.invalidateCurrentEntry();
//        return EVAL_PAGE;
//    }
//}

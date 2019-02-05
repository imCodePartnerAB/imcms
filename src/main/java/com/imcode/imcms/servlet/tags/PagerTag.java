package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.PagerItem;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * Created by Shadowgun on 06.03.2015.
 */
public class PagerTag extends BodyTagSupport {
    private IPageableTag pageable;
    private int firstVisibleItemIndex = 0;
    private int visibleItemCount = 5;
    private int currentItemIndex = -1;
    private int showedItemIndex = -1;

    @Override
    public int doStartTag() {
        pageable = (IPageableTag) findAncestorWithClass(this, IPageableTag.class);

        Assert.notNull(pageable, "PagerTag must be nested to IPageableTag");

        calculatePagination();

        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        try {
            String bodyContentString = null != getBodyContent() ? getBodyContent().getString() : "";
            bodyContent = null;
            pageContext.getOut().write(bodyContentString);
        } catch (IOException | RuntimeException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public PagerItem nextPagerItem() {
        if (++currentItemIndex >= visibleItemCount)
            return null;
        int realItemIndex = currentItemIndex + firstVisibleItemIndex;
        PagerItem pagerItem = new PagerItem(
                buildPageLink(realItemIndex),
                realItemIndex,
                realItemIndex == showedItemIndex);
        pageContext.setAttribute("pagerItem", pagerItem);
        return pagerItem;
    }

    private void restoreDefault() {
        firstVisibleItemIndex = 0;
        visibleItemCount = 5;
        currentItemIndex = -1;
        showedItemIndex = -1;
    }

    private void calculatePagination() {
        int totalPageCount = (int) Math.ceil((float) pageable.size() / pageable.getTake());
        int halfVisibleWindow = visibleItemCount / 2;
        restoreDefault();
        showedItemIndex = pageable.getSkip() / pageable.getTake();
        firstVisibleItemIndex = 0;
        if (totalPageCount > visibleItemCount) {
            firstVisibleItemIndex = showedItemIndex >= halfVisibleWindow ?
                    showedItemIndex - halfVisibleWindow : firstVisibleItemIndex;
            firstVisibleItemIndex = totalPageCount - showedItemIndex > halfVisibleWindow ?
                    firstVisibleItemIndex : totalPageCount - visibleItemCount;
        } else visibleItemCount = totalPageCount;


        pageContext.setAttribute("firstPagerItem", new PagerItem(
                buildPageLink(0),
                0,
                false));
        pageContext.setAttribute("lastPagerItem", new PagerItem(
                buildPageLink(totalPageCount - 1),
                totalPageCount - 1,
                false));
    }

    private String buildPageLink(int position) {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        request.getParameterMap().remove("skip");
        request.getParameterMap().remove("take");
        String nativeQuery = request.getQueryString();
        nativeQuery = nativeQuery.replaceAll("&?skip=([0-9])|&?take=([0-9])", "");
        String query = String.format("skip=%s&take=%s", position * pageable.getTake(), pageable.getTake());
        return String.format("%s?%s",
                request.getServletPath(),
                nativeQuery.isEmpty() ? query : String.format("%s&%s", nativeQuery, query)
        );
    }

    public int getVisibleItemCount() {
        return visibleItemCount;
    }

    public void setVisibleItemCount(int visibleItemCount) {
        this.visibleItemCount = visibleItemCount;
    }
}

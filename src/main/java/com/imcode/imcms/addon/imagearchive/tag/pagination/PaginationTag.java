package com.imcode.imcms.addon.imagearchive.tag.pagination;

import com.imcode.imcms.addon.imagearchive.util.Pagination;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PaginationTag extends BodyTagSupport {
    private static final long serialVersionUID = 2467128112294864210L;

    private static final Log logger = LogFactory.getLog(PaginationTag.class);
    
    public static final String PAGE_MARKER = "[page]";
    private static final String ELLIPSE = "...";
    
    
    // presentation attributes
    private String contClass;
    private String contStyle;
    
    private String currentPageClass;
    private String currentPageStyle;
    
    private String nextText = "next";
    private String prevText = "previous";
    
    // control attributes
    private String pageUrl;
    private String onchange;
    private Pagination pag;
    private int currentPage;
    private int pageCount;
    private int pagesBeforeEllipse;
    private int capacity;

    
    @Override
    public int doAfterBody() throws JspException {
        if (pag != null) {
            currentPage = pag.getCurrentPage();
            pageCount = pag.getPageCount();
        }
        
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("<div ");
        renderStyle(contClass, contStyle, builder);
        builder.append(">");
        
        List<Page> pages = calculatePages();
        
        renderPagingControl((currentPage > 0), currentPage, prevText, builder);
        
        for (Page page : pages) {
            if (page.isEllipse()) {
                renderSpan(ELLIPSE, builder);
            } else if (page.isSelected()) {
                builder.append("<span ");
                renderStyle(currentPageClass, currentPageStyle, builder);
                builder.append(">");
                builder.append(page.getPage() + 1);
                builder.append("</span> ");
            } else {
                builder.append("<a ");
                
                if (pageUrl != null) {
                    builder.append("href=\"");
                    String url = request.getContextPath() + pageUrl.replace(PAGE_MARKER, Integer.toString(page.getPage() + 1));
                    builder.append(response.encodeURL(url));
                    builder.append("\" ");
                } else {
                    builder.append("href=\"#\"");
                    builder.append(String.format("onclick=\"(function(page){%s;})(%d);return false;\" ", onchange, page.getPage() + 1));
                }
                
                builder.append(">");
                builder.append(page.getPage() + 1);
                builder.append("</a> ");
            }
        }
        
        renderPagingControl((currentPage < (pageCount - 1)), currentPage + 2, nextText, builder);
        
        builder.append("</div> ");
        
        try {
            JspWriter out = bodyContent.getEnclosingWriter();
            out.print(builder.toString());
        } catch (IOException ex) {
            logger.fatal(ex.getMessage());
        }
        
        return SKIP_BODY;
    }
    
    private static void renderSpan(String content, StringBuilder builder) {
        builder.append("<span>");
        builder.append(content);
        builder.append("</span> ");
    }
    
    private void renderPagingControl(boolean enabled, int page, String controlText, StringBuilder builder) {
        if (!enabled) {
            builder.append("<span>");
            builder.append(StringEscapeUtils.escapeHtml(controlText));
            builder.append("</span> ");
        } else {
        	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            
            builder.append("<a ");
            
            if (pageUrl != null) {
                builder.append("href=\"");
                String url = request.getContextPath() + pageUrl.replace(PAGE_MARKER, Integer.toString(page));
                builder.append(response.encodeURL(url));
                builder.append("\" ");
            } else {
                builder.append("href=\"#\" ");
                builder.append(String.format("onclick=\"(function(page){%s})(%d);return false;\" ", onchange, page));
            }
            
            
            
            builder.append(">");
            builder.append(StringEscapeUtils.escapeHtml(controlText));
            builder.append("</a> ");
        }
    }
    
    private static void renderStyle(String styleClass, String style, StringBuilder builder) {
        if (styleClass != null) {
            builder.append("class=\"");
            builder.append(styleClass);
            builder.append("\" ");
        }
        if (style != null) {
            builder.append("style=\"");
            builder.append(style);
            builder.append(("\" "));
        }
    }
    
    private List<Page> calculatePages() {
        List<Page> pages = new ArrayList<Page>();
        
        if (pageCount < 1) {
            return pages;
        }
        
        currentPage = Math.min(currentPage, pageCount - 1);
        currentPage = Math.max(currentPage, 0);
        
        int centerPagesCount = capacity - ((pagesBeforeEllipse * 2) + 2);
        
        if ((centerPagesCount < 1) || ((centerPagesCount % 2) != 1)) {
            logger.fatal("PaginationTag misconfigured, wrong capacity or pagesBeforeEllipse");
        }
        
        int pagesBetweenEllipseAndCenter = (centerPagesCount - 1) / 2;
        int pagesBeforeCenter = pagesBetweenEllipseAndCenter + pagesBeforeEllipse + 1;
        
        int centerPage = pagesBeforeCenter;
        int pageOneAfterCenter = pagesBeforeCenter + 1;
        
        if (pageCount <= capacity) {
            for (int i = 0; i < pageCount; i++) {
                if (i == currentPage) {
                    pages.add(Page.selected(i));
                } else {
                    pages.add(Page.page(i));
                }
            }
        } else if (currentPage <= pagesBeforeCenter) {
            for (int i = 0; i < pageOneAfterCenter; i++) {
                if (i == currentPage) {
                    pages.add(Page.selected(i));
                } else {
                    pages.add(Page.page(i));
                }
            }
            
            int distFromEnd = (pageCount - 1) - pagesBeforeCenter;
            
            if (distFromEnd == pagesBeforeCenter) {
                for (int i = pageOneAfterCenter; i < pageCount; i++) {
                    pages.add(Page.page(i));
                }
            } else {
                for (int i = pageOneAfterCenter; i < (pageOneAfterCenter + pagesBetweenEllipseAndCenter); i++) {
                    pages.add(Page.page(i));
                }
                
                pages.add(Page.ellipse());
                
                for (int i = (pageCount - pagesBeforeEllipse); i < pageCount; i++) {
                    pages.add(Page.page(i));
                }
            }
        } else {
            for (int i = 0; i < pagesBeforeEllipse; i++) {
                pages.add(Page.page(i));
            }
            
            pages.add(Page.ellipse());
            
            centerPage = (pageCount - 1) - pagesBeforeCenter;
            
            int distFromEnd = (pageCount - 1) - currentPage;
            
            if (distFromEnd <= pagesBeforeCenter) {
                for (int i = (centerPage - pagesBetweenEllipseAndCenter); i < pageCount; i++) {
                    if (i == currentPage) {
                        pages.add(Page.selected(i));
                    } else {
                        pages.add(Page.page(i));
                    }
                }
            } else {
                for (int i = (currentPage - pagesBetweenEllipseAndCenter); i < (currentPage + pagesBetweenEllipseAndCenter + 1); i++) {
                    if (i == currentPage) {
                        pages.add(Page.selected(i));
                    } else {
                        pages.add(Page.page(i));
                    }
                }
                
                pages.add(Page.ellipse());
                
                for (int i = (pageCount - pagesBeforeEllipse); i < pageCount; i++) {
                    pages.add(Page.page(i));
                }
            }
        }
        
        return pages;
    }
    

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Pagination getPag() {
        return pag;
    }

    public void setPag(Pagination pag) {
        this.pag = pag;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getOnchange() {
        return onchange;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    public int getPagesBeforeEllipse() {
        return pagesBeforeEllipse;
    }

    public void setPagesBeforeEllipse(int pagesBeforeEllipse) {
        this.pagesBeforeEllipse = pagesBeforeEllipse;
    }

    public String getContClass() {
        return contClass;
    }

    public void setContClass(String contClass) {
        this.contClass = contClass;
    }

    public String getContStyle() {
        return contStyle;
    }

    public void setContStyle(String contStyle) {
        this.contStyle = contStyle;
    }

    public String getCurrentPageClass() {
        return currentPageClass;
    }

    public void setCurrentPageClass(String currentPageClass) {
        this.currentPageClass = currentPageClass;
    }

    public String getCurrentPageStyle() {
        return currentPageStyle;
    }

    public void setCurrentPageStyle(String currentPageStyle) {
        this.currentPageStyle = currentPageStyle;
    }

    public String getNextText() {
        return nextText;
    }

    public void setNextText(String nextText) {
        this.nextText = nextText;
    }

    public String getPrevText() {
        return prevText;
    }

    public void setPrevText(String prevText) {
        this.prevText = prevText;
    }
}

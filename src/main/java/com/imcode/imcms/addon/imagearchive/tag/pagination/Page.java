package com.imcode.imcms.addon.imagearchive.tag.pagination;

public final class Page {
    private final int page;
    private final boolean selected;
    private final boolean ellipse;

    public static Page page(int page) {
        return new Page(page, false, false);
    }
    
    public static Page selected(int page) {
        return new Page(page, true, false);
    }
    
    public static Page ellipse() {
        return new Page(0, false, true);
    }
    
    private Page(int page, boolean selected, boolean ellipse) {
        this.page = page;
        this.selected = selected;
        this.ellipse = ellipse;
    }

    public boolean isEllipse() {
        return ellipse;
    }

    public int getPage() {
        return page;
    }

    public boolean isSelected() {
        return selected;
    }
}
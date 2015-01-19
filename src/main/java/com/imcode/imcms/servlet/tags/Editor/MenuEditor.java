package com.imcode.imcms.servlet.tags.Editor;

import com.imcode.imcms.servlet.tags.Editor.BaseEditor;

/**
 * Created by Shadowgun on 30.12.2014.
 */
public class MenuEditor extends BaseEditor {
    private int no;
    @Override
    public String wrap(String content) {
        super.builder
                .addClass("menu")
                .addParam("no", no);
        return super.wrap(content);
    }

    public MenuEditor setNo(int no) {
        this.no = no;
        return this;
    }
}

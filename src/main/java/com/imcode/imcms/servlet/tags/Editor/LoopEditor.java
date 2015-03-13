package com.imcode.imcms.servlet.tags.Editor;

/**
 * Created by Shadowgun on 11.03.2015.
 */
public class LoopEditor extends BaseEditor {
    private int no;
    @Override
    public String wrap(String content) {
        super.builder
                .addClass("loop")
                .addParam("no", no);
        return super.wrap(content);
    }

    public LoopEditor setNo(int no) {
        this.no = no;
        return this;
    }
}

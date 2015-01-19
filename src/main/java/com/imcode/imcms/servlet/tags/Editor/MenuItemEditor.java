package com.imcode.imcms.servlet.tags.Editor;

/**
 * Created by Shadowgun on 12.01.2015.
 */
public class MenuItemEditor extends SupportEditor {
    private int id;
    private int position;

    @Override
    public String getWrapperPre() {
        super.builder
                .addClass("menu-item")
                .addParam("menu-item-id", id)
                .addParam("menu-item-position", position);

        return super.getWrapperPre();
    }

    public MenuItemEditor setId(int id) {
        this.id = id;
        return this;
    }

    public MenuItemEditor setPosition(int position) {
        this.position = position;
        return this;
    }
}

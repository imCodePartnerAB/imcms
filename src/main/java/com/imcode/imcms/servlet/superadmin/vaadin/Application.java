package com.imcode.imcms.servlet.superadmin.vaadin;

import clojure.lang.RT;

import clojure.lang.RT;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.*;

public class Application extends com.vaadin.Application {

    public void init() {
        try {
            RT.load("com/imcode/imcms/instance/superadmin");
            RT.var("com.imcode.imcms.instance.superadmin", "init").invoke(this);

            WebApplicationContext context = (WebApplicationContext) getContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
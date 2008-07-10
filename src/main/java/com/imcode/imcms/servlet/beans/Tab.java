package com.imcode.imcms.servlet.beans;

public class Tab {

    String name ;
    String text ;
    String uri ;

    public Tab( String name, String text, String uri ) {

        this.name = name;
        this.text = text;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text ;
    }

    public String getUri() {
        return uri;
    }
}

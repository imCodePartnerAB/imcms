package com.imcode.imcms.api.linker;

import java.util.List;

/**
 * Created by 3emluk on 29.07.16.
 */
public class StringLink {
    private String name;
    private String url;
    private List<String> args;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}

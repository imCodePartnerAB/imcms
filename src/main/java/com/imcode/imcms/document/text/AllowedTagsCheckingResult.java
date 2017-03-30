package com.imcode.imcms.document.text;

import java.util.Set;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
public class AllowedTagsCheckingResult {
    private boolean success;
    private Set<String> badTags;

    public AllowedTagsCheckingResult(boolean success, Set<String> badTags) {
        this.success = success;
        this.badTags = badTags;
    }

    public boolean isSuccess() {
        return success;
    }

    public Set<String> getBadTags() {
        return badTags;
    }

    public boolean isFail() {
        return !success;
    }
}

package com.imcode.imcms.servlet.tags;

/**
 * Created by Shadowgun on 06.03.2015.
 */
public interface IPageableTag {
    public Integer size();

    public Integer getSkip();

    public Integer getTake();
}

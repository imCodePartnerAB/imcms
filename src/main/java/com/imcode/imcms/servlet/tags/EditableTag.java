package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.servlet.tags.Editor.BaseEditor;

/**
 * Created by Shadowgun on 30.12.2014.
 */
public interface EditableTag {
    BaseEditor createEditor();
}

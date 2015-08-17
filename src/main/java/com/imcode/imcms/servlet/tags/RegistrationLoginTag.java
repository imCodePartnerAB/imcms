package com.imcode.imcms.servlet.tags;

/**
 * Created by Shadowgun on 19.02.2015.
 */
public class RegistrationLoginTag extends AbstractFormInputTag {
    public RegistrationLoginTag() {
        super("text", "login");

        setAttributes("");
    }

    @Override
    public void setAttributes(String attributes) {
        attributes += " required";

        super.setAttributes(attributes);
    }
}

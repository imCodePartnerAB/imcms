package com.imcode.imcms.servlet.tags;

/**
 * Created by Shadowgun on 19.02.2015.
 */
public class RegistrationEmailTag extends AbstractFormInputTag {
    public RegistrationEmailTag() {
        super("text", "email");

        setAttributes("");
    }

    @Override
    public void setAttributes(String attributes) {
        attributes += " required";

        super.setAttributes(attributes);
    }
}

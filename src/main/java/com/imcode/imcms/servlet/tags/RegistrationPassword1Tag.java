package com.imcode.imcms.servlet.tags;

/**
 * Created by Shadowgun on 19.02.2015.
 */
public class RegistrationPassword1Tag extends AbstractFormInputTag {
    public RegistrationPassword1Tag() {
        super("password", "password1");

        setAttributes("");
    }

    @Override
    public void setAttributes(String attributes) {
        attributes += " required  minlength=\"4\" maxlength=\"250\"";

        super.setAttributes(attributes);
    }
}

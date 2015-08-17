package com.imcode.imcms.servlet.tags;

/**
 * Created by Shadowgun on 19.02.2015.
 */
public class RegistrationPassword2Tag extends AbstractFormInputTag {
    public RegistrationPassword2Tag() {
        super("password", "password2");

        setAttributes("");
    }

    @Override
    public void setAttributes(String attributes) {
        attributes += " required equalTo=\"[name=password1]\"";

        super.setAttributes(attributes);
    }
}

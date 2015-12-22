package com.imcode.imcms.imagearchive.validator;

import com.imcode.imcms.imagearchive.command.CreateCategoryCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CreateCategoryValidator implements Validator {
    public void validate(Object target, Errors errors) {
        CreateCategoryCommand command = (CreateCategoryCommand) target;

        String categoryName = StringUtils.trimToNull(command.getCreateCategoryName());

        if (categoryName == null) {
            errors.rejectValue("createCategoryName", "archive.fieldEmptyError");

        } else if (categoryName.length() > 128) {
            errors.rejectValue("createCategoryName", "archive.fieldLengthError", new Object[]{128}, "???");

        }

        command.setCreateCategoryName(categoryName);
    }

    @SuppressWarnings("unchecked")
    public boolean supports(Class klass) {
        return CreateCategoryCommand.class.isAssignableFrom(klass);
    }
}

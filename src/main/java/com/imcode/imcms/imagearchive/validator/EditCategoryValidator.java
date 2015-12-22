package com.imcode.imcms.imagearchive.validator;

import com.imcode.imcms.imagearchive.command.EditCategoryCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EditCategoryValidator implements Validator {

    public void validate(Object target, Errors errors) {
        EditCategoryCommand command = (EditCategoryCommand) target;

        String categoryName = StringUtils.trimToNull(command.getEditCategoryName());

        if (categoryName == null) {
            errors.rejectValue("editCategoryName", "archive.fieldEmptyError");

        } else if (categoryName.length() > 128) {
            errors.rejectValue("editCategoryName", "archive.fieldLengthError", new Object[]{128}, "???");

        }

        command.setEditCategoryName(categoryName);
    }

    @SuppressWarnings("unchecked")
    public boolean supports(Class klass) {
        return EditCategoryCommand.class.isAssignableFrom(klass);
    }
}

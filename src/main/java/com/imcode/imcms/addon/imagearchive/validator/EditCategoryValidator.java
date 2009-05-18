package com.imcode.imcms.addon.imagearchive.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.imcode.imcms.addon.imagearchive.command.EditCategoryCommand;

public class EditCategoryValidator implements Validator {
    
    public void validate(Object target, Errors errors) {
        EditCategoryCommand command = (EditCategoryCommand) target;
        
        String categoryName = StringUtils.trimToNull(command.getEditCategoryName());
        
        if (categoryName == null) {
            errors.rejectValue("editCategoryName", "archive.fieldEmptyError");
            
        } else if (categoryName.length() > 128) {
            errors.rejectValue("editCategoryName", "archive.fieldLengthError", new Object[] { 128 }, "???");
            
        }
        
        command.setEditCategoryName(categoryName);
    }

    @SuppressWarnings("unchecked")
    public boolean supports(Class klass) {
        return EditCategoryCommand.class.isAssignableFrom(klass);
    }
}

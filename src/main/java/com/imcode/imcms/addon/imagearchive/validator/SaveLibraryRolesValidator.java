package com.imcode.imcms.addon.imagearchive.validator;

import com.imcode.imcms.addon.imagearchive.command.SaveLibraryRolesCommand;
import com.imcode.imcms.addon.imagearchive.util.ValidatorUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SaveLibraryRolesValidator implements Validator {
    public void validate(Object target, Errors errors) {
        SaveLibraryRolesCommand command = (SaveLibraryRolesCommand) target;
        
        ValidatorUtils.rejectValueIfLonger("libraryNm", 120, "archive.fieldLengthError", errors);
        String libraryNm = StringUtils.trimToNull(command.getLibraryNm());
        if (libraryNm == null) {
            errors.rejectValue("libraryNm", "archive.fieldEmptyError");
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return SaveLibraryRolesCommand.class.isAssignableFrom(clazz);
    }
}

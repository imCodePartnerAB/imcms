package com.imcode.imcms.addon.imagearchive.validator;

import com.imcode.imcms.addon.imagearchive.command.ChangeImageDataCommand;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.ValidatorUtils;
import com.imcode.imcms.api.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChangeImageDataValidator implements Validator {
    private Facade facade;
    private User user;

    public ChangeImageDataValidator(Facade facade, User user) {
        this.facade = facade;
        this.user = user;
    }
    
    
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return ChangeImageDataCommand.class.isAssignableFrom(clazz);
    }
    
    public void validate(Object target, Errors errors) {
        ChangeImageDataCommand command = (ChangeImageDataCommand) target;
        
        ValidatorUtils.rejectValueIfLonger("imageNm", 255, "archive.fieldLengthError", errors);
        ValidatorUtils.rejectValueIfLonger("description", 255, "archive.fieldLengthError", errors);
        ValidatorUtils.rejectValueIfLonger("artist", 255, "archive.fieldLengthError", errors);
        ValidatorUtils.rejectValueIfLonger("uploadedBy", 130, "archive.fieldLengthError", errors);
        ValidatorUtils.rejectValueIfLonger("copyright", 255, "archive.fieldLengthError", errors);
        
        List<Integer> categoryIds = command.getCategoryIds();
        if (!categoryIds.isEmpty() && !facade.getImageService().canUseCategories(user, categoryIds)) {
            errors.rejectValue("categories", "archive.categoryPermissionError");
        }
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        String licenseDt = StringUtils.trimToNull(command.getLicenseDt());
        if (licenseDt != null) {
            try {
                command.setLicenseDate(df.parse(licenseDt));
            } catch (ParseException ex) {
                errors.rejectValue("licenseDt", "archive.invalidStartDateError");
            }
        }
        
        String licenseEndDt = StringUtils.trimToNull(command.getLicenseEndDt());
        if (licenseEndDt != null) {
            try {
                command.setLicenseEndDate(df.parse(licenseEndDt));
            } catch (ParseException ex) {
                errors.rejectValue("licenseEndDt", "archive.invalidEndDateError");
            }
        }
    }
}

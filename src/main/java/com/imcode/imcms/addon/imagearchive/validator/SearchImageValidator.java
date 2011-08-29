package com.imcode.imcms.addon.imagearchive.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.imcode.imcms.addon.imagearchive.command.SearchImageCommand;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.ValidatorUtils;
import com.imcode.imcms.api.User;

public class SearchImageValidator implements Validator {
    private Facade facade;
    private User user;

    public SearchImageValidator(Facade facade, User user) {
        this.facade = facade;
        this.user = user;
    }
    
    
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return SearchImageCommand.class.isAssignableFrom(clazz);
    }
    
    public void validate(Object target, Errors errors) {
        SearchImageCommand command = (SearchImageCommand) target;
        
        short show = command.getShow();
        if (show < SearchImageCommand.SHOW_ALL || show > SearchImageCommand.SHOW_WITH_VALID_LICENCE) {
            command.setShow(SearchImageCommand.SHOW_ALL);
        }
        
        int categoryId = command.getCategoryId();
        
        if (categoryId != SearchImageCommand.CATEGORY_ALL && categoryId != SearchImageCommand.CATEGORY_NO_CATEGORY
                && !facade.getRoleService().hasAccessToCategory(user, categoryId, Roles.ALL_PERMISSIONS)) {
            errors.rejectValue("categoryId", "archive.searchImage.categoryPermissionError");
        } else if (user.isDefaultUser() && categoryId == SearchImageCommand.CATEGORY_NO_CATEGORY) {
            command.setCategoryId(SearchImageCommand.CATEGORY_ALL);
        }
        
        ValidatorUtils.rejectValueIfLonger("freetext", 120, "archive.fieldLengthError", errors);
        command.setFreetext(StringUtils.trimToNull(command.getFreetext()));
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        String licenseDt = StringUtils.trimToNull(command.getLicenseDt());
        Date licenseDate = null;
        if (licenseDt != null) {
            try {
                licenseDate = df.parse(licenseDt);
            } catch (ParseException ex) {
                errors.rejectValue("licenseDt", "archive.invalidStartDateError");
            }
        }
        command.setLicenseDate(licenseDate);
        
        String licenseEndDt = StringUtils.trimToNull(command.getLicenseEndDt());
        Date licenseEndDate = null;
        if (licenseEndDt != null) {
            try {
                licenseEndDate = df.parse(licenseEndDt);
            } catch (ParseException ex) {
                errors.rejectValue("licenseEndDt", "archive.invalidEndDateError");
            }
        }
        command.setLicenseEndDate(licenseEndDate);
        
        String activeDt = StringUtils.trimToNull(command.getActiveDt());
        Date activeDate = null;
        if (activeDt != null) {
            try {
                activeDate = df.parse(activeDt);
            } catch (ParseException ex) {
                errors.rejectValue("activeDt", "archive.invalidStartDateError");
            }
        }
        command.setActiveDate(activeDate);
        
        String activeEndDt = StringUtils.trimToNull(command.getActiveEndDt());
        Date activeEndDate = null;
        if (activeEndDt != null) {
            try {
                activeEndDate = df.parse(activeEndDt);
            } catch (ParseException ex) {
                errors.rejectValue("activeEndDt", "archive.invalidEndDateError");
            }
        }
        command.setActiveEndDate(activeEndDate);
        
        int resultsPerPage = command.getResultsPerPage();
        if (resultsPerPage < 10 || resultsPerPage > 100) {
            command.setResultsPerPage(SearchImageCommand.DEFAULT_PAGE_SIZE);
        }
        
        short sortBy = command.getSortBy();
        if (sortBy < SearchImageCommand.SORT_BY_ARTIST || sortBy > SearchImageCommand.SORT_BY_ENTRY_DATE) {
            command.setSortBy(SearchImageCommand.SORT_BY_ARTIST);
        }
        
        String artist = StringUtils.trimToNull(command.getArtist());
        command.setArtist(artist);
    }
}

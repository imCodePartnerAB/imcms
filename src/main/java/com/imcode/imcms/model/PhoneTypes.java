package com.imcode.imcms.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
public enum PhoneTypes implements PhoneType {
    OTHER(0, "Other"),
    HOME(1, "Home"),
    WORK(2, "Work"),
    MOBILE(3, "Mobile"),
    FAX(4, "Fax"),;

    private final static Logger log = LogManager.getLogger(PhoneTypes.class);
    @Setter
    private Integer id;
    @Setter
    private String name;

    PhoneTypes(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PhoneTypes getPhoneTypeById(int phoneTypeId) {
        try {
            return PhoneTypes.values()[phoneTypeId];
        } catch (Exception e) {
            log.error("Unsupported phone type id: " + phoneTypeId);
            return OTHER;
        }
    }

}

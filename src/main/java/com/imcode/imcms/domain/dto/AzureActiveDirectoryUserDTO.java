package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import lombok.Data;

import java.util.Set;

import static com.imcode.imcms.domain.component.AzureAuthenticationProvider.EXTERNAL_AUTHENTICATOR_AZURE_AD;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureActiveDirectoryUserDTO {

    /**
     * In UUID form
     */
    private String id;
    /**
     * Full name
     */
    private String displayName;
    /**
     * First name
     */
    private String givenName;
    /**
     * Second name
     */
    private String surname;
    private String jobTitle;
    private String mail;
    private String mobilePhone;
    private String officeLocation;
    private String preferredLanguage;
    private String userPrincipalName;
    private String[] businessPhones;

    private Set<AzureActiveDirectoryGroupDTO> userGroups;

    public UserDomainObject toDomainObject() {
        final UserDomainObject user = new UserDomainObject(-1);

        user.setExternalProviderId(EXTERNAL_AUTHENTICATOR_AZURE_AD);
        user.setLoginName(id);
        user.setImcmsExternal(true);
        user.setFirstName(givenName);
        user.setLastName(surname);
        user.setEmailAddress(mail);
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);

        return user;
    }

}

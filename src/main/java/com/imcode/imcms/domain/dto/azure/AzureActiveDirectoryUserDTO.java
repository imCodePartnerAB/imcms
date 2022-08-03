package com.imcode.imcms.domain.dto.azure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.model.ExternalUser;
import com.imcode.imcms.model.ExternalUserDTO;
import imcode.server.ImcmsConstants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider.EXTERNAL_AUTHENTICATOR_AZURE_AD;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureActiveDirectoryUserDTO implements ExternalUserDTO {

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

    public ExternalUser toExternalUser() {
        final ExternalUser user = new ExternalUser(EXTERNAL_AUTHENTICATOR_AZURE_AD);

        user.setLoginName(id);
        user.setFirstName(StringUtils.defaultString(givenName));
        user.setLastName(StringUtils.defaultString(surname));
        user.setPassword("");
        user.setEmailAddress(StringUtils.defaultString(mail));
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        user.setExternalRoles(new HashSet<>(userGroups));

        return user;
    }

}

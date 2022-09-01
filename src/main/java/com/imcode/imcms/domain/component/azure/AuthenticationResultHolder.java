package com.imcode.imcms.domain.component.azure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationResultHolder {

    @JsonProperty("token_type")
    protected String tokenType;

    @JsonProperty("access_token")
    protected String accessToken;

    @JsonProperty("expires_in")
    protected long expiresIn;

    @JsonIgnore
    private Date createdDate = new Date();

    @JsonIgnore
    public Date getExpiresOn() {
        final Date now = new Date();
        now.setTime(createdDate.getTime() + (expiresIn * 1000));
        return now;
    }
}

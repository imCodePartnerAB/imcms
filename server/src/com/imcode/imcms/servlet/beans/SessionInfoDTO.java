package com.imcode.imcms.servlet.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class SessionInfoDTO {
    private int userId;
    private String ip;
    private String sessionId;
    private String userAgent;
    private Date loginDate;
    private Date expireDate;
}

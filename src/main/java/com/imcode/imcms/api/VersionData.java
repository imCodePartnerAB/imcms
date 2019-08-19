package com.imcode.imcms.api;

import lombok.Data;

import java.io.Serializable;

@Data
public class VersionData implements Serializable {

    private String imcmsVersion;
    private String javaVersion;
    private String serverInfo;
    private String dbVersion;
    private String dbNameVersion;
}

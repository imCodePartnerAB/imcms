package com.imcode.imcms.domain.services.core;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Serhii from Ubrainians for Imcode
 * on 22.07.16.
 */
public class ServerSettings {

    public static final String JDBC_DRIVER = "JdbcDriver";
    public static final String JDBC_URL = "JdbcUrl";
    public static final String DB_USER = "User";
    public static final String DB_PASSWORD = "Password";
    public static final String DB_MAX_CONNECTIONS = "MaxConnectionCount";
    public static final String TEMPLATE_PATH = "TemplatePath";

    public static final List<String> NECESSARY_SETTINGS = Arrays.asList(
            JDBC_DRIVER,
            JDBC_URL,
            DB_USER,
            DB_PASSWORD,
            DB_MAX_CONNECTIONS,
            TEMPLATE_PATH
    );

//    public static final List<String>
}

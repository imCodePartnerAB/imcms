package com.imcode.imcms.domain.services.core;

import imcode.server.Imcms;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Created by Serhii from Ubrainians for Imcode
 * on 22.07.16.
 */
public class ServerSettingsChecker {

    private final static Logger LOG = LogManager.getLogger(ServerSettingsChecker.class);

    private static final String EMPTY_PROPERTY_MESSAGE = "Empty '%s' property! Check server settings!";

    public static void check() {
        LOG.info("Checking necessary server settings on empty values.");
        Properties serverProperties = Imcms.getServerProperties();

        for (String property : ServerSettings.NECESSARY_SETTINGS) {
            String value = serverProperties.getProperty(property);

            if (StringUtils.trimToNull(value) == null) {
                String message = "Necessary property '" + property + "' is not set! Shutting down ImCMS.";
                LOG.fatal(message);
                throw new RuntimeException(message);
            }
        }

        LOG.info("All necessary properties are set into some values.");
    }

    public static String getEmptyPropertyMessage(String property) {
        return String.format(EMPTY_PROPERTY_MESSAGE, property);
    }
}

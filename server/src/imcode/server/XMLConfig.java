package imcode.server;

import com.google.common.collect.ImmutableList;
import imcode.server.user.ldap.MappedRole;
import imcode.server.user.ldap.MappedRoles;
import imcode.server.user.ldap.jaxb.LdapElement;
import imcode.server.user.ldap.jaxb.RoleElement;
import imcode.server.user.ldap.jaxb.MappedRolesElement;
import imcode.server.user.ldap.jaxb.ServerElement;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;

public final class XMLConfig {

    private final ServerElement serverElement;

    private final MappedRoles ldapMappedRoles;

    private Logger logger = Logger.getLogger(getClass().getName());

    public XMLConfig(String configFilePath) {
        logger.info(String.format("Reading xml based configuration from %s.", configFilePath));

        if (configFilePath == null) {
            logger.fatal("Config file path is not provided (null)");
            throw new IllegalArgumentException("configFilePath must not be null");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(ServerElement.class);
            Unmarshaller um = context.createUnmarshaller();
            serverElement = (ServerElement)um.unmarshal(new InputStreamReader(new FileInputStream(configFilePath), "UTF-8"));
        } catch (FileNotFoundException e) {
            String errorMsg = String.format("Configuration file %s can not be found.", configFilePath);
            logger.fatal(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (IOException e) {
            String errorMsg = String.format("Failed to read configuration file %s.", configFilePath);
            logger.fatal(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (JAXBException e) {
            String errorMsg = String.format("Failed to parse configuration file %s.", configFilePath);
            logger.fatal(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }

        ldapMappedRoles = readLdapMappedRoles();
    }


    public MappedRoles getLdapMappedRoles() {
        return ldapMappedRoles;
    }


    private MappedRoles readLdapMappedRoles() {
        logger.info("Reading LDAP attributes mapped to roles.");
        ImmutableList.Builder<MappedRole> mappedRolesListBuilder = new ImmutableList.Builder<MappedRole>();
        LdapElement ldapElement = serverElement.getLdapElement();

        if (ldapElement != null) {
            MappedRolesElement rolesMappingElement = ldapElement.getMappedRolesElement();

            String defaultAttributeName = rolesMappingElement.getDefaultAttributeName().trim();
            logger.info("Default LDAP role mapping attribute name is "+defaultAttributeName+".");

            for (RoleElement roleElement: rolesMappingElement.getRolesElements()) {
                String roleName = roleElement.getName().trim();
                String attributeName = StringUtils.trimToEmpty(roleElement.getAttributeName());
                String attributeValue = StringUtils.trimToEmpty(roleElement.getAttributeValue());

                mappedRolesListBuilder.add(
                        new MappedRole(
                                roleName,
                                attributeName.isEmpty() ? defaultAttributeName : attributeName,
                                attributeValue
                        )
                );

                logger.info(String.format("Added LDAP mapping for role %s:  %s -> %s.",
                        roleName,
                        attributeName.isEmpty() ? defaultAttributeName : attributeName,
                        attributeValue));
            }
        }

        MappedRoles mappedRoles = new MappedRoles(mappedRolesListBuilder.build());

        if (mappedRoles.getAttributesNames().isEmpty()) {
            logger.info("No configuration provided for LDAP mapped roles.");
        }

        return mappedRoles;
    }
}
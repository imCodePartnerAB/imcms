package imcode.server;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import imcode.server.user.ldap.MappedRole;
import imcode.server.user.ldap.MappedRoles;
import imcode.server.user.ldap.jaxb.*;
import org.apache.commons.io.input.BOMInputStream;
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
            serverElement = (ServerElement)um.unmarshal(new InputStreamReader(
                    new BOMInputStream(new FileInputStream(configFilePath)), "UTF-8"));
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

        ImmutableCollection.Builder<MappedRole.MappedToAttribute> rolesMappedToAttributesBuilder =
                new ImmutableList.Builder<MappedRole.MappedToAttribute>();

        ImmutableCollection.Builder<MappedRole.MappedToAdGroup> rolesMappedToAdGroupsBuilder =
                new ImmutableList.Builder<MappedRole.MappedToAdGroup>();

        LdapElement ldapElement = serverElement.ldapElement();

        if (ldapElement != null) {
            MappedRolesElement rolesMappingElement = ldapElement.mappedRolesElement();

            for (RoleToAttributeElement el: rolesMappingElement.rolesToAttributesElements()) {
                String roleName = el.roleName().trim();
                String attributeName = el.attributeName().trim();
                String attributeValue = el.attributeValue().trim();

                rolesMappedToAttributesBuilder.add(new MappedRole.MappedToAttribute(roleName, attributeName, attributeValue));

                logger.info(String.format("Added LDAP role-to-attribute mapping. Role: %s, attribute: %s -> %s.",
                        roleName, attributeName, attributeValue));
            }

            for (RoleToAdGroupElement el: rolesMappingElement.rolesToAdGroupsElements()) {
                String roleName = el.roleName().trim();
                String groupDn = el.groupDn().trim();

                rolesMappedToAdGroupsBuilder.add(new MappedRole.MappedToAdGroup(roleName, groupDn));

                logger.info(String.format("Added AD role-to-ad-group mapping. Role: %s, group dn: %s.",
                        roleName, groupDn));
            }
        }

        MappedRoles mappedRoles = new MappedRoles(rolesMappedToAttributesBuilder.build(), rolesMappedToAdGroupsBuilder.build());

        if (mappedRoles.rolesNames().isEmpty()) {
            logger.info("No configuration provided for LDAP mapped roles.");
        }

        return mappedRoles;
    }
}
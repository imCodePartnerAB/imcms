package imcode.server;

import imcode.server.user.ldap.jaxb.ServerElement;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XMLConfigTest {

    @Test
    public void readConfig() throws Exception {
        JAXBContext context = JAXBContext.newInstance(ServerElement.class);
        Unmarshaller um = context.createUnmarshaller(

        );
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("test-server.xml");
        ServerElement serverElement2 = (ServerElement)um.unmarshal(new InputStreamReader(
                new BOMInputStream(in), "UTF-8"));

        serverElement2.ldapElement().mappedRolesElement().rolesToAdGroupsElements();
        serverElement2.ldapElement().mappedRolesElement().rolesToAttributesElements();
    }
}

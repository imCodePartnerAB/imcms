package com.imcode.imcms.api.linker;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.commons.collections.map.HashedMap;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created by 3emluk on 29.07.16.
 */
@Service
public  class LinkService {
    @Autowired
    ServletContext servletContext;
    private static Map<String,List<String>> linksMap = new HashedMap();

//TODO must be private
    public void initializeLinksMap(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {

            // Convert JSON string from file to Object


//            File tmp = new File(String.valueOf(Paths.get(servletContext.getRealPath("/WEB-INF/conf/links.json"))));
//            File webInfPath = new File(String.valueOf(this.getClass().getClassLoader().getResource("WEB-INF/conf/links.json")));

            List<StringLink> links = mapper.readValue(new File(String.valueOf(Paths.get(servletContext.getRealPath("/WEB-INF/conf/links.json")))), new TypeReference<List<StringLink>>(){});


//            user = mapper.readValue(new File("G:\\user.json"), User.class);
            System.out.println("");

            // Convert JSON string to Object
//            String jsonInString = "{\"age\":33,\"messages\":[\"msg 1\",\"msg 2\"],\"name\":\"mkyong\"}";
//            User user1 = mapper.readValue(jsonInString, User.class);
//            System.out.println(user1);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

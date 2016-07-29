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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by 3emluk on 29.07.16.
 */
@Service
public class LinkService {
    @Autowired
    ServletContext servletContext;
    private static Map<String, String> linksMap = new HashedMap();

    //TODO must be private
    public void initializeLinksMap() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {

            // Convert JSON string from file to Object

            List<StringLink> links = mapper.readValue(new File(String.valueOf(Paths.get(servletContext.getRealPath("/WEB-INF/conf/links.json")))), new TypeReference<List<StringLink>>() {
            });
            for (StringLink stringLink : links) {
                if (!linksMap.containsKey(stringLink.getName())) {
                    linksMap.put(stringLink.getName(), stringLink.getUrl());
                } else {
                    linksMap.put(stringLink.getName() + "_" + System.currentTimeMillis(), stringLink.getUrl());
                }
            }


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

    public static void find(String... args){

    }

}

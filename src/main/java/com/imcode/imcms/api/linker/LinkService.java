package com.imcode.imcms.api.linker;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.apache.commons.collections.map.HashedMap;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 3emluk for imCode from Ubranians on 29.07.16.
 * Provided service allow to save same links for future reuse.
 * There is possibility to parametrize links to make them more dynamic.
 * All links and params for them are stored at links.json file.
 * Repeating number of parameters is not allowed
 *
 * @author 3emluk
 */
@Service
public class LinkService {
    @Autowired
    ServletContext servletContext;
    private final static String URL_PARAMETER_PATTERN = "\\{\\d{1,3}\\}";
    private Map<String, String> linksMap = new HashedMap();

    private List<StringLink> links;

//    TODO Find a way to initialize LinkService
//    public LinkService() {
//        initializeLinksMap();
//    }

    /**
     * Getting all links from JSON file and saving it in RAM
     */
    private void initializeLinksMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Convert JSON string from file to Object
            links = mapper.readValue(new File(String.valueOf(Paths.get(servletContext.getRealPath("/WEB-INF/conf/links.json")))), new TypeReference<List<StringLink>>() {
            });
            for (StringLink stringLink : links) {
                if (!linksMap.containsKey(stringLink.getName())) {
                    linksMap.put(stringLink.getName(), stringLink.getUrl());
                } else {
                    linksMap.put(stringLink.getName() + "_" + System.currentTimeMillis(), stringLink.getUrl());
                }
            }

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting amount of parameters in link pattern
     *
     * @param urlPattern
     * @return
     */
    private int findArgsAmount(String urlPattern) {
        int count = 0;
        Pattern pattern = Pattern.compile(URL_PARAMETER_PATTERN);
        Matcher matcher = pattern.matcher(urlPattern);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Gets link by it's name with filled in params according to url pattern
     *
     * @param args First argument - name of link according to links.json
     *             All next arguments reflects order of mask "{}" at pattern
     * @return Completed link with provided arguments for pattern
     * @throws NameNotFoundException    if link with provided name wasn't found or different amount of arguments
     * @throws WrongNumberArgsException if link has different amount of arguments than described at lists.json
     */
    public String get(String... args) throws NameNotFoundException, WrongNumberArgsException {
        if (null == linksMap || linksMap.size() == 0) {
            initializeLinksMap();
        }

        String link = linksMap.get(args[0]);
        if (link == null || findArgsAmount(link) != args.length - 1) {
            link = null;
            for (Map.Entry<String, String> e : linksMap.entrySet()) {
                if (e.getKey().startsWith(args[0])) {
                    if (findArgsAmount(e.getValue()) != args.length - 1) {
                        continue;
                    } else {
                        link = e.getValue();
                    }
                    break;
                }
            }
        }

        if (link == null) {
            throw new NameNotFoundException();
        }
        if (findArgsAmount(link) != args.length - 1) {
            throw new WrongNumberArgsException("Arguments count doesn't match with pattern");
        }

        for (int i = 1; i < args.length; i++) {
            link = link.replace("{" + i + "}", args[i]);
        }
        return link;
    }

    /**
     * Gets link by it's name with filled in params according to url pattern and concatenates it with "forward:"
     *
     * @param args First argument - name of link according to links.json
     *             All next arguments reflects order of mask "{}" at pattern
     * @return Completed link with provided arguments for pattern
     * @throws NameNotFoundException    if link with provided name wasn't found or different amount of arguments
     * @throws WrongNumberArgsException if link has different amount of arguments than described at lists.json
     */
    public String forward(String... args) throws NameNotFoundException, WrongNumberArgsException {
        return "forward:" + this.get(args);
    }

    /**
     * Gets link by it's name with filled in params according to url pattern and concatenates it with "redirect:"
     *
     * @param args First argument - name of link according to links.json
     *             All next arguments reflects order of mask "{}" at pattern
     * @return Completed link with provided arguments for pattern
     * @throws NameNotFoundException    if link with provided name wasn't found or different amount of arguments
     * @throws WrongNumberArgsException if link has different amount of arguments than described at lists.json
     */
    public String redirect(String... args) throws NameNotFoundException, WrongNumberArgsException {
        return "redirect:" + this.get(args);
    }

    public List<StringLink> getJSON(){
        if (null == linksMap || linksMap.size() == 0) {
            initializeLinksMap();
        }
        return links;
    }
}

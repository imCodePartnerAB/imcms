package com.imcode.imcms.api.linker;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
 * edited by Serhii
 */
@Service
public class LinkService {
    private final static String URL_PARAMETER_PATTERN = "\\{\\d{1,3}\\}";
    private static final Log logger = LogFactory.getLog(LinkService.class);
    private static final String LINKS_JSON = "/WEB-INF/conf/links.json";
    private Map<String, String> linksMap = new HashMap<>();
    private List<StringLink> links;

    @Autowired
    public LinkService(ServletContext servletContext) {
        String realPathToJSON = servletContext.getRealPath(LINKS_JSON);
        try {
            initializeLinksMap(realPathToJSON);
        } catch (JsonGenerationException | JsonMappingException e) {
            logger.error("Can't parse links.json file, " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error reading links.json, " + e.getMessage());
        }
    }

    /**
     * Getting all links from JSON file and saving it in RAM
     */
    public void initializeLinksMap(String realPathToJSON) throws IOException {
        // Convert JSON string from file to Object
        File linksJSON = new File(realPathToJSON);
        links = new ObjectMapper().readValue(linksJSON, new TypeReference<List<StringLink>>() {
        });
        for (StringLink stringLink : links) {
            if (!linksMap.containsKey(stringLink.getName())) {
                linksMap.put(stringLink.getName(), stringLink.getUrl());
            } else {
                linksMap.put(stringLink.getName() + "_" + System.currentTimeMillis(), stringLink.getUrl());
            }
        }
    }

    /**
     * Getting amount of parameters in link pattern
     *
     * @param urlPattern
     * @return amount of arguments in url
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
     */
    public String get(String... args) {
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
            logger.error("Link with name " + args[0] + " not found in links.json");
            return null;
        }
        if (findArgsAmount(link) != args.length - 1) {
            logger.error("Link with name " + args[0] + ", unexpected amount of arguments");
            return null;
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
     */
    public String forward(String... args) {
        return "forward:" + this.get(args);
    }

    /**
     * Gets link by it's name with filled in params according to url pattern and concatenates it with "redirect:"
     *
     * @param args First argument - name of link according to links.json
     *             All next arguments reflects order of mask "{}" at pattern
     * @return Completed link with provided arguments for pattern
     */
    public String redirect(String... args) {
        return "redirect:" + this.get(args);
    }

    /**
     * Method for redirecting JSON from file
     *
     * @return List of Links from links.json
     */
    public List<StringLink> getJSON() {
        return links;
    }
}

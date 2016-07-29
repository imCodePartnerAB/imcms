package com.imcode.imcms.servlet.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by 3emluk on 29.07.16.
 */

@RestController
public class LinkController {

    @Autowired
    ServletContext servletContext;

    @RequestMapping(value = "/links", method = RequestMethod.GET)
    @ResponseBody
    public String getLinksConfigPath(){
        String fileString = "";
        try {
            fileString = new String(Files.readAllBytes(Paths.get(servletContext.getRealPath("/WEB-INF/conf/links.json"))), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileString;
    }

}

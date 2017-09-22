package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.linker.LinkService;
import com.imcode.imcms.api.linker.StringLink;
import com.imcode.imcms.imagearchive.service.Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 3emluk for imCode from Ubranians on 29.07.16.
 * Controller is used to redirect links information in JSON format to client
 *
 * @author 3emluk
 */

@RestController
public class LinkController {

    @Autowired
    private LinkService linkService;

    /**
     * Send to client links information in JSON
     *
     * @return JSON with links information provided at links.json
     */
    @RequestMapping(value = "/links", method = RequestMethod.GET)
    @ResponseBody
    public List<StringLink> getLinksConfigPath() {
        return linkService.getJSON();
    }

}

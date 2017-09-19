package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.dto.MenuElementDTO;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private DocumentMapper documentMapper;

    public MenuController() {
        documentMapper = Imcms.getServices().getDocumentMapper();
    }

    @RequestMapping(params = {"docId", "menuId"}, method = RequestMethod.GET)
    public List<MenuElementDTO> getMenuItems(@RequestParam("menuId") int menuNo,
                                             @RequestParam("docId") int documentId) {
        return documentMapper
                .<TextDocumentDomainObject>getWorkingDocument(documentId)
                .getMenu(menuNo)
                .getMenuItemsAsTree()
                .stream()
                .map(MenuElementDTO::of)
                .collect(Collectors.toList());
    }

}


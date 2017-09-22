package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.mapping.dto.MenuElementDTO;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.mappers.Mappable;
import imcode.server.Imcms;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final DocumentMapper documentMapper;
    private final Mappable<TreeMenuItemDomainObject, MenuElementDTO> mapper;

    @Autowired
    public MenuController(DocumentMapper documentMapper, Mappable<TreeMenuItemDomainObject, MenuElementDTO> mapper) {
        this.documentMapper = documentMapper;
        this.mapper = mapper;
    }

    @RequestMapping(params = {"docId", "menuId"}, method = RequestMethod.GET)
    public List<MenuElementDTO> getMenuItems(@RequestParam("menuId") int menuNo,
                                             @RequestParam("docId") int documentId) {
        return documentMapper
                .<TextDocumentDomainObject>getWorkingDocument(documentId)
                .getMenu(menuNo)
                .getMenuItemsAsTree()
                .stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

}


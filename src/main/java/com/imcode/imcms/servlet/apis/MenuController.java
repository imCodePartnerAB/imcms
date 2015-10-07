package com.imcode.imcms.servlet.apis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.container.TextDocMenuContainer;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentReference;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 23.12.2014.
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    private ImcmsServices imcmsServices;

    public MenuController() {
        imcmsServices = Imcms.getServices();
    }


    @RequestMapping("/{documentId}-{menuId}")
    protected Object doGet(@PathVariable("documentId") Integer documentId,
                           @PathVariable("menuId") Integer menuId) throws ServletException, IOException {
        TextDocumentDomainObject document = imcmsServices.getDocumentMapper().getWorkingDocument(documentId);
        MenuDomainObject menu = document.getMenu(menuId);

        return Stream.of(menu.getMenuItems()).map(it -> {
            ObjectMapper m = new ObjectMapper();

            Map<String, Object> props = m.convertValue(it, new TypeReference<Map<String, Object>>() {
            });

            props.put("status", String.valueOf(it.getDocument().getLifeCyclePhase()).toUpperCase().substring(0, 1));

            return props;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{meta}-{no}", method = RequestMethod.POST)
    protected Object doPost(@PathVariable("meta") Integer documentId,
                            @PathVariable("no") Integer menuId,
                            @RequestParam("referencedDocumentId") Integer referencedDocumentId) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();

        try {
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            TextDocumentDomainObject document = documentMapper.getWorkingDocument(documentId);
            MenuDomainObject menu = document.getMenu(menuId);
            DocumentReference docIdentity = documentMapper.getDocumentReference(documentMapper.getWorkingDocument(referencedDocumentId));
            MenuItemDomainObject menuItem = new MenuItemDomainObject(docIdentity);

            menu.addMenuItem(menuItem);
            documentMapper.saveTextDocMenu(TextDocMenuContainer.of(document.getVersionRef(), menuId, menu), Imcms.getUser());

            result.put("result", true);
        } catch (Exception e) {
            e.printStackTrace();

            result.put("result", false);
        }
        return result;
    }


    @RequestMapping(value = "/{documentId}-{menuId}", method = RequestMethod.PUT)
    protected Object doPut(@PathVariable("documentId") Integer documentId,
                           @PathVariable("menuId") Integer menuId,
                           @RequestBody MultiValueMap<String, String> data) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
//            Map<String, Object> parameters = new ObjectMapper()
//                    .readValue(
//                            RequestUtils.parse(request.getInputStream()).get("data"),
//                            new TypeReference<Map<String, Object>>() {
//                            }
//                    );

            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            TextDocumentDomainObject document = documentMapper.getWorkingDocument(documentId);
            MenuDomainObject menu = document.getMenu(menuId);
            ArrayList<LinkedHashMap<String, Object>> menuItemsData = new ObjectMapper().readValue(data.getFirst("data"), new TypeReference<ArrayList<LinkedHashMap<String, Object>>>() {
            });

            menu.removeAllMenuItems();

            for (LinkedHashMap<String, Object> entry : menuItemsData) {
                String treeSortIndex = entry.get("tree-sort-index").toString();
                Integer menuItemReferencedDocumentId = Integer.parseInt(entry.get("referenced-document").toString());
                DocumentReference docIdentity = documentMapper.getDocumentReference(documentMapper.getWorkingDocument(menuItemReferencedDocumentId));
                MenuItemDomainObject menuItem = new MenuItemDomainObject(docIdentity);
                menuItem.setTreeSortIndex(treeSortIndex);
                menu.addMenuItem(menuItem);
            }

            documentMapper.saveTextDocMenu(TextDocMenuContainer.of(document.getVersionRef(), menuId, menu), Imcms.getUser());
            result.put("result", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", false);
        }

        return result;
    }


    @RequestMapping(value = "/{documentId}-{menuId}", method = RequestMethod.DELETE)
    protected Object doDelete(@PathVariable("documentId") Integer documentId,
                              @PathVariable("menuId") Integer menuId,
                              @RequestBody MultiValueMap<String, String> data) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();

        try {
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            TextDocumentDomainObject document = documentMapper.getWorkingDocument(documentId);
            MenuDomainObject menu = document.getMenu(menuId);
            menu.removeMenuItemByDocumentId(Integer.parseInt(data.getFirst("referencedDocumentId")));
            documentMapper.saveTextDocMenu(TextDocMenuContainer.of(document.getVersionRef(), menuId, menu), Imcms.getUser());

            result.put("result", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", false);
        }
        return result;
    }


}

package com.imcode.imcms.servlet.apis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.dto.MenuElementDTO;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private DocumentMapper documentMapper;

    public MenuController() {
        documentMapper = Imcms.getServices().getDocumentMapper();
    }

    /**
     * Provide API access to retrieve menu items
     *
     * @param documentId {@link TextDocumentDomainObject} id that menu has been specified with
     * @param menuId     {@link MenuDomainObject} id
     * @return List of menu items entities
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/{documentId}-{menuId}")
    protected Object getMenuItemsList(@PathVariable("documentId") Integer documentId,
                                      @PathVariable("menuId") Integer menuId) throws ServletException, IOException {

        TextDocumentDomainObject document = documentMapper.getWorkingDocument(documentId);

        return Stream.of(document.getMenu(menuId).getMenuItems())
                .map(it -> {

                    Map<String, Object> props = new ObjectMapper()
                            .convertValue(it, new TypeReference<Map<String, Object>>() {
                            });

                    String status = String.valueOf(it.getDocument().getLifeCyclePhase());
                    props.put("status", status.toUpperCase().substring(0, 1));

                    return props;
                })
                .collect(Collectors.toList());
    }

    /**
     * Save menu to database.
     * User is working with menu items, modifying it, adding new one, and all this actions are being during runtime,
     * when he click save, all current menu items saved in database at once.
     * There are no additional operations for remove and add special items, just save all changes at once.
     *
     * @param documentId {@link TextDocumentDomainObject} id that menu has been specified with
     * @param menuId     {@link MenuDomainObject} id
     * @param data       menu items
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/{documentId}-{menuId}", method = RequestMethod.PUT)
    protected Object saveMenu(@PathVariable("documentId") Integer documentId,
                              @PathVariable("menuId") Integer menuId,
                              @RequestBody MultiValueMap<String, String> data) throws ServletException, IOException {

        Map<String, Object> result = new HashMap<>();
        try {
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


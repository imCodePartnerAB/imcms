package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.container.TextDocMenuContainer;
import com.imcode.imcms.util.JSONUtils;
import com.imcode.imcms.util.RequestUtils;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Shadowgun on 23.12.2014.
 */
public class MenuApiServlet extends HttpServlet {

    private ImcmsServices imcmsServices;

    public MenuApiServlet() {
        imcmsServices = Imcms.getServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            String toFind = request.getParameter("term");
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            List<DocumentDomainObject> documents = documentMapper.findDocumentsByHeadline(toFind);
            for (DocumentDomainObject document : documents) {
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("id", document.getId());
                objectMap.put("name", document.getId());
                objectMap.put("value", document.getHeadline());
                objectMap.put("label", document.getHeadline());
                objectMap.put("language", document.getLanguage().getName());
                objectMap.put("alias", document.getAlias());
                result.add(objectMap);
            }
        } catch (Exception e) {

        }
        JSONUtils.defaultJSONAnswer(response, result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String currentDocumentId = request.getParameter("meta");
            String documentId = request.getParameter("id");
            Integer menuId = Integer.parseInt(request.getParameter("no"));
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            TextDocumentDomainObject document = documentMapper.getWorkingDocument(Integer.parseInt(currentDocumentId));
            MenuDomainObject menu = document.getMenu(menuId);
            DocumentReference docIdentity = documentMapper.getDocumentReference(documentMapper.getWorkingDocument(Integer.parseInt(documentId)));
            MenuItemDomainObject menuItem = new MenuItemDomainObject(docIdentity);
            menu.addMenuItem(menuItem);
            documentMapper.saveTextDocMenu(TextDocMenuContainer.of(document.getVersionRef(), menuId, menu), Imcms.getUser());

            result.put("result", true);

        } catch (Exception e) {
            e.printStackTrace();

            result.put("result", false);
        }
        JSONUtils.defaultJSONAnswer(response, result);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> parameters = new ObjectMapper()
                    .readValue(
                            RequestUtils.parse(request.getInputStream()).get("data"),
                            new TypeReference<Map<String, Object>>() {
                            }
                    );

            Integer currentDocumentId = Integer.parseInt(parameters.get("meta").toString());
            Integer menuId = Integer.parseInt(parameters.get("no").toString());
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            TextDocumentDomainObject document = documentMapper.getWorkingDocument(currentDocumentId);
            MenuDomainObject menu = document.getMenu(menuId);
            List<LinkedHashMap<String, String>> menuItemsData = (ArrayList<LinkedHashMap<String, String>>) parameters.get("items");
            for (LinkedHashMap<String, String> entry : menuItemsData) {
                String treeSortIndex = entry.get("tree-sort-index");
                Integer menuItemReferencedDocumentId = Integer.parseInt(entry.get("referenced-document"));
                menu.getMenuItemByDocId(menuItemReferencedDocumentId).setTreeSortIndex(treeSortIndex);
            }
            documentMapper.saveTextDocMenu(TextDocMenuContainer.of(document.getVersionRef(), menuId, menu), Imcms.getUser());
            result.put("result", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", false);
        }
        JSONUtils.defaultJSONAnswer(response, result);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, String> parameters = RequestUtils.parse(request.getInputStream());
            Integer currentDocumentId = Integer.parseInt(parameters.get("meta"));
            Integer menuItemId = Integer.parseInt(parameters.get("referenced-document"));
            Integer menuId = Integer.parseInt(parameters.get("no"));
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            TextDocumentDomainObject document = documentMapper.getWorkingDocument(currentDocumentId);
            MenuDomainObject menu = document.getMenu(menuId);
            menu.removeMenuItemByDocumentId(menuItemId);
            documentMapper.saveTextDocMenu(TextDocMenuContainer.of(document.getVersionRef(), menuId, menu), Imcms.getUser());


            result.put("result", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", false);
        }
        JSONUtils.defaultJSONAnswer(response, result);
    }


}

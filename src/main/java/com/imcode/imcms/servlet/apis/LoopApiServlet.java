package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.TextDocumentContentSaver;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocLoopContainer;
import com.imcode.imcms.util.JSONUtils;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Shadowgun on 11.03.2015.
 */
public class LoopApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer metaId = Integer.parseInt(req.getParameter("meta"));
            Integer loopId = Integer.parseInt(req.getParameter("loopId"));
            TextDocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(metaId);
            DocRef docRef = DocRef.of(document.getVersionRef(), document.getLanguage().getCode());
            TextDocumentContentLoader textDocumentContentLoader = Imcms.getServices().getManagedBean(TextDocumentContentLoader.class);
            Loop loop = textDocumentContentLoader.getLoop(
                    document.getVersionRef(),
                    loopId);
            List<Map<String, Object>> entriesList = new ArrayList<>();
            loop.getEntries().forEach((no, bool) -> {
                Map<String, Object> entryData = new HashMap<>();
                TextDomainObject textDomainObject = textDocumentContentLoader
                        .getFirstLoopEntryText(docRef, LoopEntryRef.of(loopId, no));
                entryData.put("no", no);
                String strippedText = textDomainObject != null ?
                        textDomainObject.getText().replaceAll("<[^>]*>", "") : "content is not defined";
                entryData.put("text", strippedText.length() <= 150
                                ? strippedText : String.format("%s…", strippedText.substring(0, 150))
                );
                entriesList.add(entryData);
            });
            result.put("data", entriesList);
            result.put("result", true);
        } catch (Exception e) {
            result.put("message", e);
            result.put("result", false);
        }
        JSONUtils.defaultJSONAnswer(resp, result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> params = new ObjectMapper().readValue(req.getParameter("data"), new TypeReference<Map<String, Object>>() {

            });
            Integer metaId = Integer.parseInt(params.get("meta").toString());
            Integer loopId = Integer.parseInt(params.get("loopId").toString());
            List<Map<String, Object>> entries = (ArrayList<Map<String, Object>>) params.get("entries");
            TextDocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(metaId);
            TextDocumentContentSaver textDocumentContentSaver = Imcms.getServices().getManagedBean(TextDocumentContentSaver.class);
            Map<Integer, Boolean> loopMap = entries.stream().collect(Collectors.mapping((item) -> Integer.parseInt(item.get("no").toString()),
                    Collectors.toMap((key) -> key, (value) -> true)));

            textDocumentContentSaver.saveLoop(new TextDocLoopContainer(document.getVersionRef(), loopId, Loop.of(loopMap)));
            Imcms.getServices().getDocumentMapper().invalidateDocument(metaId);
            result.put("result", true);
        } catch (Exception e) {
            result.put("message", e);
            result.put("result", false);
        }
        JSONUtils.defaultJSONAnswer(resp, result);
    }
}

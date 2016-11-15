package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.TextDocumentContentSaver;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocLoopContainer;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/loop")
public class LoopController {

    @RequestMapping
    protected Object getLoop(@RequestParam("meta") Integer metaId,
                             @RequestParam("loopId") Integer loopId,
                             HttpServletRequest request) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            TextDocumentDomainObject document = Imcms.getServices().getDocumentMapper().getVersionedDocument(metaId, request);
            DocRef docRef = DocRef.of(document.getVersionRef(), document.getLanguage().getCode());
            TextDocumentContentLoader textDocumentContentLoader = Imcms.getServices().getManagedBean(TextDocumentContentLoader.class);
            TextDocumentContentSaver textDocumentContentSaver = Imcms.getServices().getManagedBean(TextDocumentContentSaver.class);
            List<Map<String, Object>> entriesList = new ArrayList<>();
            Loop loop = textDocumentContentLoader.getLoop(document.getVersionRef(), loopId);

            if (loop == null) {
                textDocumentContentSaver.updateContent(document, Imcms.getUser());
                loop = textDocumentContentLoader.getLoop(document.getVersionRef(), loopId);
            }

            loop.getEntries().forEach((no, bool) -> {
                Map<String, Object> entryData = new HashMap<>();
                TextDomainObject textDomainObject = textDocumentContentLoader
                        .getFirstLoopEntryText(docRef, LoopEntryRef.of(loopId, no));
                entryData.put("no", no);
                String strippedText = textDomainObject != null
                        ? textDomainObject.getText().replaceAll("<[^>]*>", "")
                        : "content is not defined";
                entryData.put("text", strippedText.length() <= 150
                        ? strippedText
                        : String.format("%s…", strippedText.substring(0, 150))
                );
                entriesList.add(entryData);
            });

            result.put("data", entriesList);
            result.put("result", true);
        } catch (Exception e) {
            result.put("message", e);
            result.put("result", false);
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST)
    protected Object doPost(@RequestParam("indexes[]") List<Integer> indexes,
                            @RequestParam("loopId") Integer loopId,
                            @RequestParam("meta") Integer metaId) throws ServletException, IOException {

        Map<String, Object> result = new HashMap<>();
        try {
            TextDocumentDomainObject document = Imcms.getServices().getDocumentMapper().getWorkingDocument(metaId);
            TextDocumentContentSaver textDocumentContentSaver = Imcms.getServices().getManagedBean(TextDocumentContentSaver.class);
            Map<Integer, Boolean> loopMap = indexes.stream()
                    .collect(Collectors.toMap(loopNo -> loopNo, loopNo -> true));

            textDocumentContentSaver.saveLoop(new TextDocLoopContainer(document.getVersionRef(), loopId, Loop.of(loopMap)));
            Imcms.getServices().getDocumentMapper().invalidateDocument(metaId);
            result.put("result", true);
        } catch (Exception e) {
            result.put("message", e);
            result.put("result", false);
        }

        return result;
    }
}

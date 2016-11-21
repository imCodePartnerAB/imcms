package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.TextDocumentContentSaver;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocLoopContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@RestController
@RequestMapping("/loop")
public class LoopController {

    @RequestMapping
    protected Object getLoop(@RequestParam("meta") Integer metaId,
                             @RequestParam("loopId") Integer loopId,
                             HttpServletRequest request) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            TextDocumentDomainObject document = Imcms.getServices().getDocumentMapper()
                    .getVersionedDocument(metaId, request);

            TextDocumentContentLoader textDocumentContentLoader = Imcms.getServices()
                    .getManagedBean(TextDocumentContentLoader.class);

            List<Map<String, Object>> entriesList = new ArrayList<>();
            VersionRef versionRef = document.getVersionRef();
            Loop loop = textDocumentContentLoader.getLoop(versionRef, loopId);

            if (loop == null) {
                Imcms.getServices().getManagedBean(TextDocumentContentSaver.class)
                        .updateContent(document, Imcms.getUser());
                loop = textDocumentContentLoader.getLoop(versionRef, loopId);
            }

            DocRef docRef = DocRef.of(versionRef, document.getLanguage().getCode());

            loop.getEntries().forEach((no, isEnabled) -> {
                Map<String, Object> entryData = new HashMap<>();

                entryData.put("no", no);
                entryData.put("isEnabled", isEnabled);

                TextDomainObject textDomainObject = textDocumentContentLoader
                        .getFirstLoopEntryText(docRef, LoopEntryRef.of(loopId, no));

                String strippedText = (textDomainObject != null)
                        ? textDomainObject.getText().replaceAll("<[^>]*>", "")
                        : "content is not defined";

                entryData.put("text", (strippedText.length() <= 150)
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
    protected Object doPost(@RequestParam(value = "indexes[]") List<Integer> indexes,
                            @RequestParam(value = "isEnabledFlags[]") List<Boolean> isEnabledFlags,
                            @RequestParam("loopId") Integer loopId,
                            @RequestParam("meta") Integer metaId) throws ServletException, IOException {

        Map<String, Object> result = new HashMap<>();

        if (indexes == null || isEnabledFlags == null) {
            indexes = new ArrayList<>();
            isEnabledFlags = new ArrayList<>();
        }

        if (indexes.size() != isEnabledFlags.size()) {
            result.put("message", "Different sizes of collections");
            result.put("result", false);
            return result;
        }
        try {
            ImcmsServices imcmsServices = Imcms.getServices();
            DocumentMapper documentMapper = imcmsServices.getDocumentMapper();
            TextDocumentDomainObject document = documentMapper.getWorkingDocument(metaId);

            Map<Integer, Boolean> entries = new ListOrderedMap<>();

            for (int i = 0; i < indexes.size(); i++) {
                entries.put(indexes.get(i), isEnabledFlags.get(i));
            }

            Loop loop = Loop.of(entries);
            Predicate<TextDocumentDomainObject.LoopItemRef> loopItemRefPredicate = entry ->
                    (loopId.equals(entry.getLoopNo()) && !loop.findEntryIndexByNo(entry.getEntryNo()).isPresent());

            document.getLoopImages().keySet().stream()
                    .filter(loopItemRefPredicate)
                    .forEach(document::deleteImage);

            document.getLoopTexts().keySet().stream()
                    .filter(loopItemRefPredicate)
                    .forEach(entry -> document.setText(entry, new TextDomainObject()));

            document.setLoop(loopId, loop);
            documentMapper.saveDocument(document, Imcms.getUser());

            TextDocLoopContainer container = new TextDocLoopContainer(document.getVersionRef(), loopId, loop);
            imcmsServices.getManagedBean(TextDocumentContentSaver.class).saveLoop(container);
            documentMapper.invalidateDocument(metaId);

            result.put("result", true);

        } catch (Exception e) {
            result.put("message", e);
            result.put("result", false);
        }

        return result;
    }
}

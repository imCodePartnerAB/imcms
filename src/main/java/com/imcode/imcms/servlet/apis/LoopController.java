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
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loop")
public class LoopController {

	@RequestMapping
	protected Object getLoop(@RequestParam("meta") Integer metaId,
							 @RequestParam("loopId") Integer loopId) throws ServletException, IOException {
		Map<String, Object> result = new HashMap<>();
		try {
			TextDocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(metaId);
			DocRef docRef = DocRef.of(document.getVersionRef(), document.getLanguage().getCode());
			TextDocumentContentLoader textDocumentContentLoader = Imcms.getServices().getManagedBean(TextDocumentContentLoader.class);
			TextDocumentContentSaver textDocumentContentSaver = Imcms.getServices().getManagedBean(TextDocumentContentSaver.class);
			List<Map<String, Object>> entriesList = new ArrayList<>();
			Loop loop = textDocumentContentLoader.getLoop(
					document.getVersionRef(),
					loopId);

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
	protected Object doPost(@RequestParam("entries") String params,
							@RequestParam("loopId") Integer loopId,
							@RequestParam("meta") Integer metaId) throws ServletException, IOException {
		// I can't make this method take a List or an array of Map<String, Object>, also wrapper class not work too.
		// So I wrote genEntries() parser.
		Map<String, Object> result = new HashMap<>();
		try {
			List<Map<String, String>> entries = genEntries(params);
			TextDocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(metaId);
			TextDocumentContentSaver textDocumentContentSaver = Imcms.getServices().getManagedBean(TextDocumentContentSaver.class);
			Map<Integer, Boolean> loopMap = entries.stream().collect(Collectors.mapping((item) -> Integer.parseInt(item.get("no")),
					Collectors.toMap((key) -> key, (value) -> true)));

			textDocumentContentSaver.saveLoop(new TextDocLoopContainer(document.getVersionRef(), loopId, Loop.of(loopMap)));
			Imcms.getServices().getDocumentMapper().invalidateDocument(metaId);
			result.put("result", true);
		} catch (Exception e) {
			result.put("message", e);
			result.put("result", false);
		}

		return result;
	}

	// params is smth like [{"no":1,"text":"content is not defined"},{"no":2,"text":""},{"no":3,"text":""},{"no":4,"text":""}]
	protected List<Map<String, String>> genEntries(String params) {
		List<Map<String, String>> result = new LinkedList<>();
		if (isValid(params)) {
			LinkedList<String> s = new LinkedList<>(Arrays.asList(params.split("\"")));
			// one loop looks like "[{"no":1,"text":"content is not defined"}]", so after split by " we have (6n + 1) elements
			s.removeFirst();
			if (numberIsGood(s)) {
				ListIterator<String> iter = s.listIterator();
				while (iter.hasNext()) {
					Map<String, String> loop = new HashMap<>();
					// working with "no":1,"
					String no = iter.next();
					if (!"no".equals(no)) {
						throw new NullPointerException("Something goes wrong! The 'no' attribute not found!");
					}
					String noVal = iter.next();
					// ":1," - we need remove first and last symbols to get the number
					String noValue = noVal.substring(1, noVal.length() - 1);
					loop.put(no, noValue);

					// "text":"content is not defined"
					String text = iter.next();
					if (!"text".equals(text)) {
						throw new NullPointerException("Something goes wrong! The 'text' attribute not found!");
					}
					if (!":".equals(iter.next())) {
						throw new NullPointerException("Something goes wrong! The ':' attribute not found!");
					}
					String textValue = iter.next();
					iter.next();
					loop.put(text, textValue);

					result.add(loop);
				}
			}
		}
		return result;
	}

	protected boolean isValid(String params) {
		// check if String params is an array, else smth wrong
		return params.startsWith("[") && params.endsWith("]");
	}

	protected boolean numberIsGood(LinkedList<String> str) {
		return (str.size() % 6) == 0;
	}
}

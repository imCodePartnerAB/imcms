package com.imcode.imcms.document.text;

import imcode.util.PropertyManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
@Component
public class TextContentFilter {
    private final Whitelist htmlTagsWhitelist = Whitelist.none();
    private final Set<String> allowedTags = new HashSet<>();

    @PostConstruct
    public void init() {
        final String classPath = this.getClass()
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        final File imcmsRoot = new File(classPath).getParentFile().getParentFile();
        PropertyManager.setRoot(imcmsRoot);

        final String[] whiteListTags = PropertyManager.getServerProperty("text.editor.html.tags.whitelist")
                .split(";");
        htmlTagsWhitelist.addTags(whiteListTags);
        allowedTags.addAll(Arrays.asList(whiteListTags));
    }

    public TextContentFilter addHtmlTagsToWhiteList(String[] newWhiteListTags) {
        htmlTagsWhitelist.addTags(newWhiteListTags);
        allowedTags.addAll(Arrays.asList(newWhiteListTags));
        return this;
    }

    public String cleanText(String cleanMe) {
        return Jsoup.clean(cleanMe, htmlTagsWhitelist);
    }

    public AllowedTagsCheckingResult checkBadTags(String checkMe) {
        final Set<String> badTags = Jsoup.parse(checkMe)
                .body()
                .childNodes()
                .stream()
                .map(Node::nodeName)
                .filter(tag -> !tag.contains("#") && !allowedTags.contains(tag)) // simple text becomes with "#" mark
                .collect(Collectors.toSet());

        boolean success = badTags.isEmpty();

        return new AllowedTagsCheckingResult(success, badTags);
    }
}

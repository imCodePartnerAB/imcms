package com.imcode.imcms.db;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Schema {

    public static Schema fromFile(File file) {
        try {
            return formXml(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Schema fromUrl(URL url) {
        return fromFile(new File(url.getFile()));
    }

    public static Schema formXml(String xml) {
        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            XPath xpath = XPathFactory.newInstance().newXPath();
            String versionStr = xpath.compile("/schema/@version").evaluate(document);
            String initVersionStr = xpath.compile("/schema/init/@version").evaluate(document);

            NodeList initScriptsNL = (NodeList) xpath.compile("/schema/init/script").evaluate(document, XPathConstants.NODESET);
            NodeList diffsNL = (NodeList) xpath.compile("/schema/diffs/diff").evaluate(document, XPathConstants.NODESET);

            List<String> initScripts = IntStream.range(0, initScriptsNL.getLength())
                    .mapToObj(i -> initScriptsNL.item(i).getTextContent().trim())
                    .collect(Collectors.toList());

            Set<Diff> diffs = IntStream.range(0, diffsNL.getLength())
                    .mapToObj(diffsNL::item)
                    .map(node -> {
                        try {
                            String from = xpath.compile("@from").evaluate(node);
                            String to = xpath.compile("@to").evaluate(node);
                            NodeList scriptsNL = (NodeList) xpath.compile("script").evaluate(node, XPathConstants.NODESET);

                            List<String> scripts = IntStream.range(0, scriptsNL.getLength())
                                    .mapToObj(i -> scriptsNL.item(i).getTextContent().trim())
                                    .collect(Collectors.toList());

                            return new Diff(Version.parse(from), Version.parse(to), scripts);
                        } catch (XPathException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());

            Version version = Version.parse(versionStr);
            Version initVersion = Version.parse(initVersionStr);

            return new Schema(version, new Init(initVersion, initScripts), diffs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final Version version;
    private final Init init;
    private final Set<Diff> diffs;
    private final String scriptsDir;

    public Schema(Version version, Init init, Set<Diff> diffs) {
        this(version, init, diffs, "");
    }

    public Schema(Version version, Init init, Set<Diff> diffs, String scriptsDir) {
        Validate.isTrue(diffs.size() == diffs.stream().map(Diff::getFrom).distinct().count(),
                "diffs from version value must be distinct: %s", diffs);

        diffs.forEach(diff -> {
            LinkedList<Diff> chain = diffsChainFrom(diffs, diff.getFrom());
            Diff lastDiff = chain.getLast();

            Validate.isTrue(lastDiff.getTo().equals(version),
                    "Diffs' chain %s must end (diff.to value) with version %s.", chain, lastDiff);
        });

        this.version = version;
        this.init = init;
        this.diffs = diffs;
        this.scriptsDir = scriptsDir;
    }

    public List<Diff> diffsChainFrom(Version version) {
        return diffsChainFrom(diffs, version);
    }

    private static LinkedList<Diff> diffsChainFrom(Set<Diff> diffs, Version from) {
        LinkedList<Diff> chain = new LinkedList<>();
        Version current = from;

        while (current != null) {
            Diff diff = findDiffByFrom(diffs, current).orElse(null);

            if (diff == null) {
                current = null;
            } else {
                chain.add(diff);
                current = diff.getTo();
            }
        }

        return chain;
    }

    private static Optional<Diff> findDiffByFrom(Set<Diff> diffs, Version from) {
        return diffs.stream().filter(d -> d.getFrom().equals(from)).findAny();
    }


    public Version getVersion() {
        return version;
    }

    public Init getInit() {
        return init;
    }

    public Set<Diff> getDiffs() {
        return diffs;
    }

    public String getScriptsDir() {
        return scriptsDir;
    }

    public Schema setScriptsDir(String newScriptsDir) {
        return new Schema(version, init, diffs, newScriptsDir.trim());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("version", version)
                .append("init", init)
                .append("diffs", diffs)
                .append("scriptsDir", scriptsDir)
                .toString();
    }
}

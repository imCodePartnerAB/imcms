package com.imcode.imcms.db;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Schema {

    private final Version version;
    private final Init init;
    private final Set<Diff> diffs;
    private final URI scriptsDir;

    private Schema(Version version, Init init, Set<Diff> diffs) throws URISyntaxException {
        this(version, init, diffs, new URI(""));
    }

    private Schema(Version version, Init init, Set<Diff> diffs, URI scriptsDir) {
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

    public static Schema fromInputStream(InputStream in) {
        return fromXml(new Scanner(in, StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next());
    }

    private static Schema fromXml(String xml) {
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

    List<Diff> diffsChainFrom(Version version) {
        return diffsChainFrom(diffs, version);
    }

    public Version getVersion() {
        return version;
    }

    public Init getInit() {
        return init;
    }

    URI getScriptsDir() {
        return scriptsDir;
    }

    public Schema setScriptsDir(URI newScriptsDir) {
        return new Schema(version, init, diffs, newScriptsDir);
    }

    @Override
    public String toString() {
	    return MoreObjects.toStringHelper(this)
			    .add("version", version)
			    .add("init", init)
			    .add("diffs", diffs)
			    .add("scriptsDir", scriptsDir)
			    .toString();
    }
}

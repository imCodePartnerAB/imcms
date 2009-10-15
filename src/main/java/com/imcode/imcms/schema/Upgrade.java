package com.imcode.imcms.schema;

import org.xml.sax.InputSource;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.io.StringReader;

/**
 *
 */
public class Upgrade {


    /**
     * Collections of diffs for given vendor.
     *
     * @param xml upgrade configuration.
     * @param vendor database vendor.
     * @return collection of diffs.
     */
    public static Collection<Diff> getDiffs(final String xml, final Vendor vendor) {
        Collection<String> versions = getVersions(xml);

        return CollectionUtils.collect(versions, new Transformer() {

            public Diff transform(Object o) {
                String versionStr = (String)o;
                Version version = Version.parse(versionStr);
                Collection<String> filenames = getDiffFilenames(xml, versionStr, vendor.name());

                return new Diff(version, filenames);
            }
        });
    }


    /**
     * Returns awailable schema upgrade versions.
     *
     * @param xml upgrade configuration.
     * @return awailable schema upgrade versions.
     */
    public static Collection<String> getVersions(String xml) {
        String expression = "/schema-upgrade/diff/@version";

        return getValues(getNodes(xml, expression));
    }


    /**
     * Returns collection of diff filenames for given version and vendor.
     *
     * @param xml upgrade configuration.
     *
     * @param versionStr
     * @param vendorName
     * @return collection of diff filenames.
     */
    public static Collection<String> getDiffFilenames(String xml, String versionStr, String vendorName) {
        String expression = createDiffFilenamesExpression(versionStr, vendorName);

        return getValues(getNodes(xml, expression));
    }


    /**
     * Creates and returns XPath expression for retrieving diff scripts locations.
     *
     * @param versionStr version string.
     * @param vendorName vendor name.
     * @return XPath expression for retrieving diff scripts locations.
     */
    public static String createDiffFilenamesExpression(String versionStr, String vendorName) {
        String expressionTemplate = "/schema-upgrade/diff[@version=%s]/vendor[@name='%s']/script/@location";

        return String.format(expressionTemplate, versionStr, vendorName);
    }


    /**
     * Maps nodes collection to their values collection.
     *
     * @param nodes nodes collection.
     * @return nodes values collection.
     */
    public static Collection<String> getValues(Collection<Node> nodes) {

        return CollectionUtils.collect(nodes, new Transformer() {
            public String transform(Object o) {
                return ((Node)o).getNodeValue();
            }
        });
    }


    /**
     * Evaluates an XPath expression over given xml and return nodes collection.
     *
     * @param xml xml string.
     * @param expression xpath expression.
     * @return collection of nodes.
     */
    public static Collection<Node> getNodes(String xml, String expression) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        InputSource inputSource = new InputSource(new StringReader(xml));
        NodeList nodeset;

        try {
            nodeset = (NodeList)xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }


        int count = nodeset.getLength();
        List<Node> nodes = new ArrayList<Node>(count);

        for (int i = 0; i < count; i++) {
            nodes.add(nodeset.item(i));
        }

        return nodes;
    }
}
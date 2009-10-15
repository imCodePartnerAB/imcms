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


    /** XPath expression for selecting diff versions. */
    public static final String XPATH_SELECT_DIFF_VERSIONS =
            "/schema-upgrade/diff/@version";

    /** XPath expression template for selecting diff locations. */
    public static final String XPATH_TEMPLATE__SELECT_DIFF_LOCATIONS =
            "/schema-upgrade/diff[@version = %s]/vendor[@name = '%s']/script/@location";

    
    /**
     * Returns collections of diffs for given vendor which versions are higher than base version
     *
     * @param xml upgrade configuration.
     * @param vendor database vendor.
     * @return collection of diffs which versions are higher than base version.
     */
    @SuppressWarnings("unchecked")
    public static Collection<Diff> getDiffs(final String xml, final Vendor vendor) {
        Collection<Double> versions = getVersions(xml);

        return CollectionUtils.collect(versions, new Transformer() {

            public Diff transform(Object o) {
                double version = (Double)o;
                Collection<String> locations = getDiffLocations(xml, version, vendor);

                return new Diff(version, locations);
            }
        });
    }


    /**
     * Returns collection of diffs versions.
     *
     * @param xml upgrade configuration.
     */
    @SuppressWarnings("unchecked")
    public static Collection<Double> getVersions(String xml) {
        String expression = String.format(XPATH_SELECT_DIFF_VERSIONS);
        Collection<String> values =  getValues(getNodes(xml, expression));

        return CollectionUtils.collect(values, new Transformer() {
            public Double transform(Object o) {
                return Double.parseDouble((String)o);
            }
        });
    }


    /**
     * Returns collection of diff locations for given version and vendor.
     *
     * @param xml upgrade configuration.
     *
     * @param version diff version
     * @param vendor database vendor.
     * @return collection of diff filenames.
     */
    public static Collection<String> getDiffLocations(String xml, double version, Vendor vendor) {
        String expression = String.format(XPATH_TEMPLATE__SELECT_DIFF_LOCATIONS, version, vendor);

        return getValues(getNodes(xml, expression));
    }


    /**
     * Maps nodes collection to their values collection.
     *
     * @param nodes nodes collection.
     * @return nodes values collection.
     */
    @SuppressWarnings("unchecked")
    private static Collection<String> getValues(Collection<Node> nodes) {
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
    private static Collection<Node> getNodes(String xml, String expression) {
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
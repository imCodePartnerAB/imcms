package com.imcode.imcms.domain.component.cgi;

import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.util.SimpleURLCanonicalizer;

public class IgnoreSchemeURIComparator implements URIComparator {

    private final String HTTPS = "https";
    private final String HTTP = "http";

    public boolean compare(String uri1, String uri2) {
        if (uri1 == null) {
            return uri2 == null;
        } else if (uri2 == null) {
            return uri1 == null;
        } else {
            String uri1Canon = SimpleURLCanonicalizer.canonicalize(uri1).replace(HTTPS, HTTP);
            String uri2Canon = SimpleURLCanonicalizer.canonicalize(uri2).replace(HTTPS, HTTP);
            return uri1Canon.equalsIgnoreCase(uri2Canon);
        }
    }

}

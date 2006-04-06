package imcode.util;

import java.util.Map;

public class Parser {

    /**
     * Parses a string.
     *
     * @param doc         The string.
     * @param tagsAndData An array of tags and data for each tag. Every even index is a tag, and every odd index is data for the tag before.
     * @return The parsed String.
     */
    public static String parseDoc(String doc, String[] tagsAndData) {
        if (doc == null) {
            return doc;
        }
        StringBuffer sb = new StringBuffer(doc);
        parseDoc(sb, tagsAndData);
        return sb.toString();
    }

    /**
     * Parses a StringBuffer.
     *
     * @param sb          The StringBuffer.
     * @param tagsAndData An array of tags and data for each tag. Every even index is a tag, and every odd index is data for the tag before.
     */
    private static void parseDoc(StringBuffer sb, String[] tagsAndData) {
        if (sb == null) {
            return ;
        }
        int length;
        for (int i = 0; i < tagsAndData.length; i += 2) {
            length = tagsAndData[i].length();
            if (length > 0) {
                String replacement = (null == tagsAndData[i + 1]) ? "" : tagsAndData[i + 1];
                for (int start = 0; (start = sb.toString().indexOf(tagsAndData[i], start)) != -1; start += replacement.length()) {
                    sb.replace(start, start + length, replacement);
                }
            }
        }
    }

}

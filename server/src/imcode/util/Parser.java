package imcode.util;

import java.util.Map;

public class Parser {

    /**
     * Parses a string.
     *
     * @param doc  The string.
     * @param tags The tags to replace.
     * @param data The data for each tag.
     */
    public static String parseDoc(String doc, String[] tags, String[] data) {
        if (doc == null) {
            return doc;
        }

        final StringBuffer sb = new StringBuffer(doc);
        int length;
        for (int i = 0; i < tags.length; i++) {
            length = tags[i].length();
            if (length > 0) {
                for (int start = 0; (start = sb.toString().indexOf(tags[i], start)) != -1; start += data[i].length()) {
                    sb.replace(start, start + length, (data[i] == null ? "" : data[i]));
                }
            }
        }
        return sb.toString();
    }

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
     * @return The StringBuffer sb.
     */
    public static StringBuffer parseDoc(StringBuffer sb, String[] tagsAndData) {
        if (sb == null) {
            return null;
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
        return sb;
    }

    /**
     * A nice little parsefunction which searches a StringBuffer for "taglikes" (tags), and replaces them with Strings from a Map.
     *
     * @param str            The StringBuffer to be modified.
     * @param tagdelim       A char that all tags start with, and end with. I.e. '#' for #tag#.
     * @param nontag         A String of chars that may not exist in a tag. Usually one uses whitespace. (" \t\r\n")
     * @param data           A Map that supplies the data for the tags. data.get(tag) is called for every "taglike" found.
     * @param removetaglikes A boolean that specifies whether tags that has a null value from the Map should be removed or not.
     * @param recurse        An int that specifies the number of levels to parse the data for tags. A value of 0 will disable.
     * @return The StringBuffer given in str, modified.
     */
    public static StringBuffer parseTags(StringBuffer str, char tagdelim, String nontag, Map data, boolean removetaglikes, int recurse) {
        if (str == null || data == null) {
            return null;
        }
        if (nontag == null) {
            nontag = "";
        }
        int length = str.length();
        boolean intag = false;
        int tagindex = 0;

        // A FSA (Finite State Automaton) to parse the StringBuffer for tags.
        // Iterates over the chars in the StringBuffer and sets different states
        // depending on what char is found.
        for (int i = 0; i < length; ++i) {
            char c = str.charAt(i);
            // If a potential beginning of a tag is found.
            if (c == tagdelim) {
                if (!intag) {
                    // We aren't inside a tag. That means this is the start of a tag.
                    intag = true;
                    // Remember the start of the tag.
                    tagindex = i;
                } else {
                    // We were inside a tag and found a tagdelimiter.
                    // The tag ends here! Now we probably want to replace it with something.
                    intag = false;
                    // Get data for tag from the Map. We peel off the tagdelimiting chars.
                    Object replacement = data.get(str.substring(tagindex + 1, i));
                    if (replacement == null) {
                        // The tag wasn't found in the Map, check if we should remove the tag, or ignore it.
                        if (removetaglikes) {
                            // Removes the tag
                            replacement = "";
                        } else {
                            // Ignores the tag (continues to the next).
                            continue;
                        }	// if-else
                    } else if (recurse > 0) {
                        // We have some levels of recursion left, so we search the data for tags to be replaced too.
                        replacement = parseTags(new StringBuffer(replacement.toString()), tagdelim, nontag, data, removetaglikes, recurse - 1).toString();
                    }	// if-else-if
                    // Replace the tag with the data.
                    String replace = replacement.toString();
                    str.replace(tagindex, i + 1, replace);
                    // And make sure to jump to the end of the data put in.
                    i = tagindex + replace.length() - 1;	// (-1 since the loop does ++i)
                    // The length of the StringBuffer probably just changed. Update.
                    length = str.length();
                }	// if-else
            } else {
                // This wasn't a tagdelimiter. Now i'll check if it is one of the "tagstoppers" (non-tag-chars).
                if (nontag.indexOf(c) != -1) {
                    // The tag ends here. (It wasn't a tag.)
                    intag = false;
                }	// if
            }	// if-else
        }	// for
        return str;
    }
}

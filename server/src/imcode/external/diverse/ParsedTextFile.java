package imcode.external.diverse;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

import imcode.util.Parser;

public class ParsedTextFile {

    private String htmlStr ;

    public ParsedTextFile( File theFile, List tags, List data ) throws IOException {
        htmlStr = IOUtils.toString( new BufferedReader( new InputStreamReader( new FileInputStream( theFile ) ) ) );
        htmlStr = Parser.parseDoc( htmlStr, (String[])tags.toArray( new String[tags.size()] ), (String[])data.toArray( new String[data.size()] )) ;
    }

    public String toString() {
        return htmlStr;
    }

}



/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-04
 * Time: 18:09:39
 */
package imcode.server.document;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;

import java.io.Reader;

public class WhitespaceLowerCaseAnalyzer extends Analyzer {

    public TokenStream tokenStream( String string, Reader reader ) {
        return new LowerCaseFilter( new WhitespaceTokenizer( reader ) );
    }

}
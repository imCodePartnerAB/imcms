package imcode.server.document.index;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;

public class AnalyzerImpl extends Analyzer {


    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer tokenizer;
        if (DocumentIndex.FIELD__KEYWORD.equals(fieldName)) {
            tokenizer = new NullTokenizer(reader);
        } else {
            tokenizer = new LetterOrDigitTokenizer(reader);
        }

        return new TokenStreamComponents(tokenizer, new LowerCaseFilter(Version.LUCENE_40, tokenizer));
    }

    private static class NullTokenizer extends CharTokenizer {

        private NullTokenizer(Reader reader) {
            super(Version.LUCENE_40, reader);
        }

        protected boolean isTokenChar(int c) {
            return true;
        }
    }

    private static class LetterOrDigitTokenizer extends CharTokenizer {

        private LetterOrDigitTokenizer(Reader reader) {
            super(Version.LUCENE_40, reader);
        }

        protected boolean isTokenChar(int c) {
            return Character.isLetterOrDigit(c) || '_' == c;
        }
    }
}

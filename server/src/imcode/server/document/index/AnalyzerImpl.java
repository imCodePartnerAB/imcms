package imcode.server.document.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.CharTokenizer;

public class AnalyzerImpl extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer;
        if (DocumentIndex.FIELD__SECTION.equals(fieldName)
                || DocumentIndex.FIELD__KEYWORD.equals(fieldName)) {
            tokenizer = new NullTokenizer();
        } else {
            tokenizer = new LetterOrDigitTokenizer();
        }

        return new TokenStreamComponents(tokenizer, new LowerCaseFilter(tokenizer));
    }

    private static class NullTokenizer extends CharTokenizer {

        protected boolean isTokenChar(int c) {
            return true;
        }
    }

    private static class LetterOrDigitTokenizer extends CharTokenizer {

        protected boolean isTokenChar(int c) {
            return Character.isLetterOrDigit(c) || '_' == c;
        }

    }
}

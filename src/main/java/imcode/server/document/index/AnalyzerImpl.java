package imcode.server.document.index;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

class AnalyzerImpl extends Analyzer {

    public TokenStream tokenStream( String fieldName, Reader reader ) {
        Tokenizer tokenizer;
        if ( DocumentIndex.FIELD__SECTION.equals( fieldName )
             || DocumentIndex.FIELD__KEYWORD.equals( fieldName ) ) {
            tokenizer = new NullTokenizer( reader );
        } else {
            tokenizer = new LetterOrDigitTokenizer( reader );
        }
        return new LowerCaseFilter( tokenizer );
    }

    private static class NullTokenizer extends CharTokenizer {

        private NullTokenizer( Reader reader ) {
            super( reader );
        }

        protected boolean isTokenChar( char c ) {
            return true;
        }
    }

    private static class LetterOrDigitTokenizer extends CharTokenizer {

        private LetterOrDigitTokenizer( Reader reader ) {
            super( reader );
        }

        protected boolean isTokenChar( char c ) {
            return Character.isLetterOrDigit( c ) || '_' == c;
        }
    }
}

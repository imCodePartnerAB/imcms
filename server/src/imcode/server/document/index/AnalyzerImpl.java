package imcode.server.document.index;

import org.apache.lucene.analysis.*;

import java.io.Reader;

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

package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

import java.io.IOException;

public abstract class DocumentIndex {

    public static final String FIELD__DOC_TYPE_ID = "doc_type_id";
    public static final String FIELD__IMAGE_LINK_URL = "image_link_url";
    public static final String FIELD__TEXT = "text";
    static final String FIELD__KEYWORD = "keyword";
    static final String FIELD__SECTION = "section";
    static final String FIELD__ACTIVATED_DATETIME = "activated_datetime";
    static final String FIELD__ARCHIVED_DATETIME = "archived_datetime";
    static final String FIELD__CATEGORY_ID = "category_id";
    static final String FIELD__CREATED_DATETIME = "created_datetime";
    static final String FIELD__META_HEADLINE = "meta_headline";
    static final String FIELD__META_ID = "meta_id";
    static final String FIELD__META_TEXT = "meta_text";
    static final String FIELD__MODIFIED_DATETIME = "modified_datetime";
    static final String FIELD__PARENT_ID = "parent_id";
    static final String FIELD__PARENT_MENU_ID = "parent_menu_id";
    static final String FIELD__PUBLICATION_END_DATETIME = "publication_end_datetime";
    static final String FIELD__PUBLICATION_START_DATETIME = "publication_start_datetime";
    static final String FIELD__STATUS = "status";

    public abstract void indexDocument( DocumentDomainObject document );

    public abstract DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IOException;

    public Query parseLucene( String queryString ) throws ParseException {
        return MultiFieldQueryParser.parse( queryString,
                                            new String[]{
                                                FIELD__META_ID,
                                                FIELD__META_HEADLINE,
                                                FIELD__META_TEXT,
                                                FIELD__TEXT,
                                                FIELD__KEYWORD
                                            },
                                            new AnalyzerImpl() );
    };

}
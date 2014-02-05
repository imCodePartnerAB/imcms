package imcode.server.document.index;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SolrQueryCustomizer {

    private transient UserDomainObject user;
    private transient Set<DocumentLanguage> languages = Collections.emptySet();

    public SolrQueryCustomizer setUser(UserDomainObject user) {
        this.user = user;
        return this;
    }

    public SolrQueryCustomizer setLanguage(DocumentLanguage language) {
        languages = Collections.singleton(language);
        return this;
    }

    public SolrQueryCustomizer setLanguages(Collection<DocumentLanguage> languages) {
        languages = new HashSet<DocumentLanguage>(languages);
        return this;
    }


    /**
     * @param solrQuery
     * @return customized query.
     * todo: fully implement UserDomainObject.canSearchFor with filter queries
     */
    public SolrQuery customize(SolrQuery solrQuery) {
        if (!languages.isEmpty()) {
            solrQuery.addFilterQuery(String.format("%s:(%s)", DocumentIndex.FIELD__LANGUAGE_CODE, StringUtils.join(languages, " ")));
        }

        if (user != null && !user.isSuperAdmin()) {
            solrQuery.addFilterQuery(String.format("%s:%s", DocumentIndex.FIELD__SEARCH_ENABLED, true));
            solrQuery.addFilterQuery(String.format("%s:(%s)", DocumentIndex.FIELD__ROLE_ID, StringUtils.join(user.getRoleIds(), " ")));
        }

        return solrQuery;
    }
}

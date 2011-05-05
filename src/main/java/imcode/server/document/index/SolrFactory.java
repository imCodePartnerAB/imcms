package imcode.server.document.index;

import imcode.server.Imcms;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.core.SolrCore;

public abstract class SolrFactory {

    private static final String DEFAULT_SOLR_SERVER_FACTORY = "imcode.server.document.index.EmbeddedSolrServerFactory";
    
    private static SolrFactory ssFactory;
    
    public static SolrFactory getInstance() {
        if (ssFactory == null) {
            String solrServerFactoryClass = Imcms.getServices().getConfig().getSolrServerFactoryClass();
            if (StringUtils.isBlank(solrServerFactoryClass)) {
                solrServerFactoryClass = DEFAULT_SOLR_SERVER_FACTORY;
            }

            try {
                ssFactory = (SolrFactory)Class.forName(solrServerFactoryClass).newInstance();
            } catch (Exception e) {
                throw new IndexException(e);
            }
        }

        return ssFactory;
    }

    public abstract SolrServer createServer();
}

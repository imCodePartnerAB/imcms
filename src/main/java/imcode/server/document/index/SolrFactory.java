package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.core.SolrCore;

import java.lang.reflect.Constructor;

public abstract class SolrFactory {

    private static final String DEFAULT_SOLR_SERVER_FACTORY = "imcode.server.document.index.EmbeddedSolrServerFactory";
    
    private static SolrFactory ssFactory;

    protected Config config;

    public static SolrFactory getInstance(Config config) {
        if (ssFactory == null) {
            String solrServerFactoryClass = config.getSolrServerFactoryClass();
            if (StringUtils.isBlank(solrServerFactoryClass)) {
                solrServerFactoryClass = DEFAULT_SOLR_SERVER_FACTORY;
            }

            try {
                Constructor c = Class.forName(solrServerFactoryClass).getConstructor(Config.class);
                ssFactory = (SolrFactory)c.newInstance(config);
            } catch (Exception e) {
                throw new IndexException(e);
            }
        }

        return ssFactory;
    }

    SolrFactory(Config config) {
        this.config = config;
    }

    public abstract SolrServer createServer();
    public abstract void destroy();
}

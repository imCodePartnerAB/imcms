package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.Imcms;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.core.SolrCore;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpSolrFactory extends SolrFactory {

    public HttpSolrFactory(Config config) {
        super(config);
    }

    @Override
    public SolrServer createServer() {
        try {
            URL url = new URL(new URL(config.getSolrHttpServerURL()), config.getSolrCoreName());
            return new CommonsHttpSolrServer(url);
        } catch (MalformedURLException e) {
            throw new IndexException(e);
        }
    }

    @Override
    public void destroy() { }
}

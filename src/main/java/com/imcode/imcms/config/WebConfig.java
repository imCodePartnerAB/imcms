package com.imcode.imcms.config;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Properties;

@Configuration
@EnableWebMvc
@ComponentScan({
        "com.imcode.imcms.servlet.apis",
        "com.imcode.imcms.controller",
        "imcode.util",
        "imcode.server"
})
public class WebConfig {

    private static final String DEFAULT_SOLR_HOME = "WEB-INF/solr";

    @Bean
    public File realPath(ServletContext servletContext) {
        return new File(servletContext.getRealPath("/"));
    }

    @Bean
    public Properties imcmsProperties(StandardEnvironment env, File realPath) {
        final Properties imcmsProperties = (Properties) env.getPropertySources().get("imcms.properties").getSource();
        final String solrHome = new File(realPath.getAbsolutePath(), DEFAULT_SOLR_HOME).getAbsolutePath();
        imcmsProperties.setProperty("SolrHome", solrHome);
        return imcmsProperties;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver(Properties imcmsProperties) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(Long.parseLong(imcmsProperties.getProperty("ImageArchiveMaxImageUploadSize")));
        return multipartResolver;
    }

    @Bean
    public ViewResolver templateViewResolver() {
        return instantiateJspViewResolver("/WEB-INF/templates/text/");
    }

    @Bean
    public ViewResolver internalViewResolver() {
        return instantiateJspViewResolver("/WEB-INF/jsp/imcms/views/");
    }

    private ViewResolver instantiateJspViewResolver(String prefix) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(prefix);
        viewResolver.setSuffix(".jsp");
        viewResolver.setExposedContextBeanNames("loopService", "imageService", "menuService");
        return viewResolver;
    }

    @Bean
    public Imcms imcms(ServletContext servletContext,
                       ImcmsServices imcmsServices,
                       Properties imcmsProperties) {

        return new Imcms(servletContext, imcmsServices, imcmsProperties);
    }
}

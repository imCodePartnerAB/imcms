package com.imcode.imcms.config;

import com.imcode.imcms.domain.service.ImageService;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import javax.servlet.ServletContext;
import java.util.Properties;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@Import(ApplicationConfig.class)
@ComponentScan({
        "com.imcode.imcms.controller",
        "imcode.util",
        "imcode.server",
        "com.imcode.imcms.aspects"
})
class WebConfig {

    @Bean
    public CommonsMultipartResolver multipartResolver(Properties imcmsProperties) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(Long.parseLong(imcmsProperties.getProperty("ImageArchiveMaxImageUploadSize")));
        return multipartResolver;
    }

    @Bean
    public ViewResolver templateViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/templates/text/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(1);
        viewResolver.setExposedContextBeanNames("loopService", "imageService", "menuService", "textService");
        return viewResolver;
    }

    @Bean
    public ViewResolver internalViewResolver() {
        final ResourceBundleViewResolver viewResolver = new ResourceBundleViewResolver();
        viewResolver.setBasename("views");
        viewResolver.setOrder(0);
        return viewResolver;
    }

    @Bean
    public Imcms imcms(ServletContext servletContext,
                       ImcmsServices imcmsServices,
                       ImageService imageService,
                       Properties imcmsProperties) {

        return new Imcms(servletContext, imcmsServices, imcmsProperties, imageService);
    }
}

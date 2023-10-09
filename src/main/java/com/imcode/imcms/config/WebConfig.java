package com.imcode.imcms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan({
        "com.imcode.imcms.controller",
        "com.imcode.imcms.aspects"
})
class WebConfig implements WebMvcConfigurer {

    @Bean
    public CommonsMultipartResolver multipartResolver(Properties imcmsProperties) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(Long.parseLong(imcmsProperties.getProperty("ImageArchiveMaxImageUploadSize")));
        multipartResolver.setSupportedMethods(HttpMethod.POST.name(), HttpMethod.PUT.name());
        return multipartResolver;
    }

    @Bean
    public ViewResolver templateViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/templates/text/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(1);
	    viewResolver.setExposedContextBeanNames("loopService", "imageService", "menuService", "textService", "documentMetadataService", "templateCSSService");
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
                       Properties imcmsProperties) {

        return new Imcms(servletContext, imcmsServices, imcmsProperties);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()); // new module, NOT JSR310Module
        return mapper;
    }

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/webapp/images/", "/webapp/images/generated", "")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic())
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new PageableHandlerMethodArgumentResolver());
	}
}

package com.imcode.imcms.config;

import com.imcode.imcms.imagearchive.Config;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.beans.PropertyEditor;
import java.io.File;
import java.util.Map;

/**
 * Created by zemluk on 13.10.16.
 */

@Configuration
public class ApplicationConfig {

    @Autowired
    private Environment env;// = new StandardServletEnvironment();

    @Autowired
    @Bean
    public Config imageArchiveConfig(Environment env2) {
        BidiMap languages = new DualHashBidiMap();
        languages.put("eng", "en");
        languages.put("swe", "sv");

        Config config = new Config();

        config.setStoragePath(new File(env2.getProperty("ImageArchiveStoragePath")));
        config.setTmpPath(new File(env2.getProperty("ImageArchiveTempPath")));
        config.setImageMagickPath(new File(env2.getProperty("ImageMagickPath")));
        config.setImagesPath(new File(env2.getProperty("ImageArchiveImagesPath")));
        config.setLibrariesPath(new File(env2.getProperty("ImageArchiveLibrariesPath")));
        config.setOldLibraryPaths(new File[]{new File(env2.getProperty("ImageArchiveOldLibraryPaths"))});
        config.setUsersLibraryFolder(env2.getProperty("ImageArchiveUsersLibraryFolder"));
        config.setMaxImageUploadSize(Long.parseLong(env2.getProperty("ImageArchiveMaxImageUploadSize")));
        config.setMaxZipUploadSize(Long.parseLong(env2.getProperty("ImageArchiveMaxZipUploadSize")));
        config.setLanguages(languages);
        return config;
    }

    @Bean
    public CustomEditorConfigurer customEditorConfigurer() {
        CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
        Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new ManagedMap<>();
        customEditors.put(java.io.File.class, com.imcode.imcms.imagearchive.util.FileEditor.class);
        customEditors.put(java.io.File[].class, com.imcode.imcms.imagearchive.util.FileArrayEditor.class);
        customEditorConfigurer.setCustomEditors(customEditors);
        return customEditorConfigurer;
    }

    @Bean
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        //TODO: Move that values to properties
        source.setBasenames("/WEB-INF/locale/imcms", "/WEB-INF/locale/image_archive");
        source.setFallbackToSystemLocale(false);
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

}
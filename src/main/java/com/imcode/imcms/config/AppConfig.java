package com.imcode.imcms.config;

import com.imcode.imcms.imagearchive.Config;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import java.beans.PropertyEditor;
import java.io.File;
import java.util.Map;

/**
 * Created by zemluk on 13.10.16.
 */

@Configuration
@PropertySources({@PropertySource(value = "/WEB-INF/conf/server.properties", ignoreResourceNotFound = true)})
public class AppConfig {

    @Autowired
    private Environment env;


    @Value("${JdbcUrl}")
    private String jdbcUrl;

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
    public Config imageArchiveConfig() {
        BidiMap languages = new DualHashBidiMap();
        languages.put("eng", "en");
        languages.put("swe", "sv");

        Config config = new Config();
//        config.setStoragePath(new File(env.getProperty("ImageArchiveStoragePath")));
//        config.setTmpPath(new File(env.getProperty("ImageArchiveTempPath")));
//        config.setImageMagickPath(new File(env.getProperty("ImageMagickPath")));
//        config.setImagesPath(new File(env.getProperty("ImageArchiveImagesPath")));
//        config.setLibrariesPath(new File(env.getProperty("ImageArchiveLibrariesPath")));
//        config.setOldLibraryPaths(new File[]{new File(env.getProperty("ImageArchiveOldLibraryPaths"))});
//        config.setUsersLibraryFolder(env.getProperty("ImageArchiveUsersLibraryFolder"));
//        config.setMaxImageUploadSize(Long.parseLong(env.getProperty("ImageArchiveMaxImageUploadSize")));
//        config.setMaxZipUploadSize(Long.parseLong(env.getProperty("ImageArchiveMaxZipUploadSize")));


        config.setStoragePath(new File(""));
        config.setTmpPath(new File(""));
        config.setImageMagickPath(new File(""));
        config.setImagesPath(new File(""));
        config.setLibrariesPath(new File(""));
        config.setOldLibraryPaths(new File[]{new File("")});
        config.setUsersLibraryFolder("");
        config.setMaxImageUploadSize(20);
        config.setMaxZipUploadSize(20);

        config.setLanguages(languages);
        return config;
    }

}
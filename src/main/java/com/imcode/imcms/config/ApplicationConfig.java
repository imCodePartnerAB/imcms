package com.imcode.imcms.config;

import com.imcode.imcms.imagearchive.Config;
import com.imcode.imcms.imagearchive.util.FileArrayEditor;
import com.imcode.imcms.imagearchive.util.FileEditor;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import java.beans.PropertyEditor;
import java.io.File;
import java.util.Map;

@Configuration
public class ApplicationConfig {

    @Autowired
    @Bean
    public Config imageArchiveConfig(Environment environment) {
        BidiMap languages = new DualHashBidiMap();
        languages.put("eng", "en");
        languages.put("swe", "sv");

        Config config = new Config();

        config.setStoragePath(new File(environment.getProperty("ImageArchiveStoragePath")));
        config.setTmpPath(new File(environment.getProperty("ImageArchiveTempPath")));
        config.setImageMagickPath(new File(environment.getProperty("ImageMagickPath")));
        config.setImagesPath(new File(environment.getProperty("ImageArchiveImagesPath")));
        config.setLibrariesPath(new File(environment.getProperty("ImageArchiveLibrariesPath")));
        config.setOldLibraryPaths(new File[]{new File(environment.getProperty("ImageArchiveOldLibraryPaths"))});
        config.setUsersLibraryFolder(environment.getProperty("ImageArchiveUsersLibraryFolder"));
        config.setMaxImageUploadSize(Long.parseLong(environment.getProperty("ImageArchiveMaxImageUploadSize")));
        config.setMaxZipUploadSize(Long.parseLong(environment.getProperty("ImageArchiveMaxZipUploadSize")));
        config.setLanguages(languages);
        return config;
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
}

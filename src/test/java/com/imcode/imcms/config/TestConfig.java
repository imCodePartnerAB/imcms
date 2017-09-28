package com.imcode.imcms.config;

import com.imcode.imcms.db.DB;
import com.imcode.imcms.db.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.InputStream;

@Configuration
@Import({MainConfig.class})
@ComponentScan({
        "com.imcode.imcms.util.datainitializer"
})
public class TestConfig {

    private final DataSource dataSource;

    @Autowired
    public TestConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void createImcmsDatabaseStructure() {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema.xml");
        final Schema schema = Schema.fromInputStream(inputStream);
        final DB db = new DB(dataSource);
        db.prepare(schema.setScriptsDir("src/main/webapp/WEB-INF/sql"));
    }

}

package com.imcode.imcms.db;

import com.imcode.imcms.test.DataSourceConfig;
import com.imcode.imcms.util.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataSourceConfig.class})
public class DBTest {

    @Inject @Named("dataSource")
    DataSource dataSource;

    @Inject @Named("dataSourceWithoutDbName")
    DataSource dataSourceWithoutDbName;

    @Inject
    String databaseName;

    void recreateDb() {
        Value.with(new JdbcTemplate(dataSourceWithoutDbName), template -> {
            template.execute(String.format("DROP DATABASE IF EXISTS %1$s", databaseName));
            template.execute(String.format("CREATE DATABASE %1$s", databaseName));
        });
    }

    @Test
    public void testIsNew() {
        recreateDb();

        DB db = new DB(dataSource);

        assertTrue(db.isNew());
        assertTrue(db.tables().isEmpty());
    }

    @Test(expected = Exception.class)
    public void testWhenNewThenGetVersionShouldThrowException() {
        recreateDb();

        DB db = new DB(dataSource);

        assertTrue(db.isNew());
        db.getVersion();
    }

    @Test(expected = Exception.class)
    public void testWhenNewThenUpdateVersionShouldThrowException() {
        recreateDb();

        DB db = new DB(dataSource);

        assertTrue(db.isNew());
        db.updateVersion(Version.of(1, 0));
    }

    @Test
    public void testPrepare() {
        recreateDb();

        DB db = new DB(dataSource);

        File scriptsDir = Paths.get("src/main/web/WEB-INF/sql").toFile();
        File schemaFile = Paths.get("src/main/resources/schema.xml").toFile();

        Schema schema = Schema.fromFile(schemaFile).setScriptsDir(scriptsDir.getAbsolutePath());
        db.prepare(schema);

        assertEquals(schema.getVersion(), db.getVersion());
    }
}
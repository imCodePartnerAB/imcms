package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.commands.CompositeDatabaseCommand;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.regex.Pattern;
import java.util.Locale;

import imcode.server.Imcms;

public class ImcmsDatabaseCreator {

    private final LocalizedMessageProvider localizedMessageProvider;
    private final Reader initScriptReader;

    private final static Logger LOG = Logger.getLogger(ImcmsDatabaseCreator.class);
    
    public ImcmsDatabaseCreator(Reader initScriptReader, LocalizedMessageProvider localizedMessageProvider) {
        this.initScriptReader = initScriptReader;
        this.localizedMessageProvider = localizedMessageProvider;
    }

    public void createDatabase(Database database, final org.apache.ddlutils.model.Database wantedDdl) {
        database.execute(new CompositeDatabaseCommand(
                new DatabaseCommand[] {
                        new DdlUtilsPlatformCommand() {
                            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                                CreationParameters params = new CreationParameters();
                                params.addParameter(null, "ENGINE", "InnoDB");
                                params.addParameter(null, "CHARACTER SET", "UTF8");
                                String sql = platform.getCreateTablesSql(wantedDdl, params, false, false);
                                LOG.trace(sql);
                                platform.evaluateBatch(sql, false);
                                return null ;
                            }
                        },
                        new DdlUtilsPlatformCommand() {
                            protected Object executePlatform(DatabaseConnection databaseConnection,
                                                             Platform platform) {
                                String sql;
                                try {
                                    sql = IOUtils.toString(initScriptReader) ;
                                } catch ( IOException e ) {
                                    throw new RuntimeException(e);
                                }
                                sql = massageSql(platform, sql);
                                LOG.trace(sql);
                                platform.evaluateBatch(sql, false) ;
                                return null ;
                            }
                        },
                }
        ));
    }

    private String massageSql(Platform platform, String sql) {
        String platformName = platform.getName().toLowerCase();
        sql = Pattern.compile(
                "^-- " + platformName + " ", Pattern.MULTILINE).matcher(sql).replaceAll("");
        sql = Pattern.compile("^-- \\w+ (.*?)\n", Pattern.MULTILINE).matcher(sql).replaceAll("");
        String language = Locale.getDefault().getISO3Language() ;
        if (StringUtils.isBlank(language) || !localizedMessageProvider.supportsLanguage(language)) {
            language = Imcms.getDefaultLanguage() ;
        }

        sql = sql.replaceAll("@language@", language);
        sql = sql.replaceAll("@headline@", localizedMessageProvider.get("start_document/headline").toLocalizedString(language)) ;
        sql = sql.replaceAll("@text1@", localizedMessageProvider.get("start_document/text1").toLocalizedString(language)) ;
        sql = sql.replaceAll("@text2@", localizedMessageProvider.get("start_document/text2").toLocalizedString(language)) ;
        return sql;
    }

}

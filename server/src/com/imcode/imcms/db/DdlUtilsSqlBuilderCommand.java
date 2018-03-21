package com.imcode.imcms.db;

import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.platform.SqlBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class DdlUtilsSqlBuilderCommand extends DdlUtilsPlatformCommand {

    protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform
    ) {
        SqlBuilder sqlBuilder = platform.getSqlBuilder();
        Writer oldWriter = sqlBuilder.getWriter();
        StringWriter newWriter = new StringWriter();
        sqlBuilder.setWriter(newWriter);
        Object result;
        try {
            result = executeSqlBuilder(databaseConnection, sqlBuilder);
        } catch (IOException e) {
            throw new DatabaseException(null, e);
        }
        sqlBuilder.setWriter(oldWriter);
        String sql = newWriter.toString();
        if (StringUtils.isNotBlank(sql)) {
            platform.evaluateBatch(databaseConnection.getConnection(), sql, false);
        }
        return result;
    }

    protected abstract Object executeSqlBuilder(DatabaseConnection databaseConnection,
                                                SqlBuilder sqlBuilder) throws IOException;
}

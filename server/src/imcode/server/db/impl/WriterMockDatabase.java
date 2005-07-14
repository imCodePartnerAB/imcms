package imcode.server.db.impl;

import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.io.Writer;

public class WriterMockDatabase extends MockDatabase {
    private PrintWriter writer;

    public WriterMockDatabase(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    Object getResultForSqlCall(String sql, Object[] params) {
        writer.println(sql+ " ("+ StringUtils.join(params, ", ")+")");
        writer.flush();
        return super.getResultForSqlCall(sql, params);
    }

}

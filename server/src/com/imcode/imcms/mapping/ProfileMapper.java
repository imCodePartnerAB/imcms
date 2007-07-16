package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import imcode.server.document.Profile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ProfileMapper extends Mapper<Profile>{

    public ProfileMapper(Database database) {
        super(database);
    }

    protected String getTableName() {
        return "profiles";
    }

    protected String getIdColumnName() {
        return "profile_id";
    }

    protected String getDefaultOrderBy() {
        return "name";
    }

    protected List<String> getDataColumnNames() {
        return Arrays.asList("name", "document_name");
    }

    protected Profile convertRow(ResultSet rs) throws SQLException {
        return new SimpleProfile(rs.getString("profile_id"), rs.getString("name"), rs.getString("document_name"));
    }

    protected Object[][] getDataValues(Profile profile) {
        return new Object[][] {
                { "name", profile.getName() },
                { "document_name", profile.getDocumentName() },
        };
    }

    public static class SimpleProfile implements Profile {

        private final String id;
        private final String name;
        private final String documentName;

        public SimpleProfile(String id, String name, String documentName) {
            this.id = id;
            this.name = name;
            this.documentName = documentName;
        }

        public String getName() {
            return name;
        }

        public String getDocumentName() {
            return documentName;
        }

        public Object getId() {
            return id;
        }
    }

}

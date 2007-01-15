package com.imcode.imcms.db.refactoring.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.util.List;
import java.util.Arrays;

public class DdlUtilsForeignKey extends AbstractForeignKey{

    private final String localTableName;
    private final org.apache.ddlutils.model.ForeignKey foreignKey;

    public DdlUtilsForeignKey(String localTableName, org.apache.ddlutils.model.ForeignKey foreignKey) {
        this.localTableName = localTableName;
        this.foreignKey = foreignKey;
    }

    public String getLocalTableName() {
        return localTableName;
    }

    public String getName() {
        return foreignKey.getName();
    }

    public List<Reference> getReferences() {
        return (List<Reference>) CollectionUtils.collect(Arrays.asList(foreignKey.getReferences()), new Transformer() {
            public Object transform(Object input) {
                return new DdlUtilsReference((org.apache.ddlutils.model.Reference) input);
            }
        });
    }

    public String getForeignTableName() {
        return foreignKey.getForeignTableName();
    }
}

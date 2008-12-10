package com.imcode.imcms.db.refactoring.model;

public class SimpleColumn implements Column {

    private Required required;
    private String defaultValue;
    private boolean autoIncremented;
    private Type type;
    private int size;
    private String name;

    public SimpleColumn(String name, Type type, int size, Required required) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.required =required;
    }

    public boolean isNullable() {
        return Required.NULL == required;
    }

    public boolean hasDefault() {
        return null != defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isAutoIncremented() {
        return autoIncremented;
    }

    public boolean isPrimaryKey() {
        return Required.PRIMARY_KEY.equals(required);
    }

    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequired(Required required) {
        this.required = required;
    }

    public enum Required {
        NULL,
        NOT_NULL,
        PRIMARY_KEY
    }

}

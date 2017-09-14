package com.imcode.imcms.mapping.jpa;

import org.hibernate.annotations.Subselect;

import javax.persistence.*;
import java.util.Objects;

/**
 *
 */
@Entity
@Subselect("select * from sys_types")
@Table(name = "sys_types")
@SecondaryTable(name = "sys_data", pkJoinColumns = @PrimaryKeyJoinColumn(name = "type_id"))
public class SystemProperty {

    @Id
    @Column(name = "type_id")
    private Integer id;

    private String name;

    @Column(table = "sys_data")
    private String value;

    public SystemProperty() {
    }

    public SystemProperty(Integer id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, value);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SystemProperty && equals((SystemProperty) obj));
    }

    private boolean equals(SystemProperty that) {
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

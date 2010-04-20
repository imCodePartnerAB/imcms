package com.imcode.imcms.api;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name="sys_types")
@SecondaryTable(name="sys_data", pkJoinColumns=@PrimaryKeyJoinColumn(name="type_id"))
public class SystemProperty {

    @Id
    @Column(name="type_id")
    private Integer id;

    private String name;

    @Column(table = "sys_data")
    private String value;

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

package com.imcode.imcms.mapping.jpa;

import javax.persistence.*;

@Entity
@Table(name = "ip_accesses")
public class IPAccess implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ip_access_id")
    private Integer id;

    @Column(name = "ip_start", columnDefinition = "decimal")
    private String start;

    @Column(name = "ip_end", columnDefinition = "decimal")
    private String end;

    @Column(name = "user_id")
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public IPAccess clone() throws CloneNotSupportedException {
        return (IPAccess) super.clone();
    }
}
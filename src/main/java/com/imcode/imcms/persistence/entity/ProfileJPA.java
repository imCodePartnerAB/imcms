package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles")
public class ProfileJPA extends Profile {

    @Id
    @Column(name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "document_name")
    private String documentName;

    public ProfileJPA(Profile from) {
        super(from);
    }
}

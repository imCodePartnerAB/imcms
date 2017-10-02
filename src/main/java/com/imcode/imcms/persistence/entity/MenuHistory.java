package com.imcode.imcms.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "com.imcode.imcms.persistence.entity.MenuHistory")
@Table(name = "imcms_menu_history")
@NoArgsConstructor
@Getter
@Setter
public class MenuHistory extends MenuBase {

    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    @Column(name = "user_id")
    private Integer userId;

}

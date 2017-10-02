package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "com.imcode.imcms.persistence.entity.Menu")
@Table(name = "imcms_menu")
@NoArgsConstructor
public class Menu extends MenuBase {
}

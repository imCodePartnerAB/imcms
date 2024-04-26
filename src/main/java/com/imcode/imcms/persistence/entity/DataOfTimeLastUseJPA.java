package com.imcode.imcms.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "imcms_last_time_use")
@Data
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NoArgsConstructor
@AllArgsConstructor
public class DataOfTimeLastUseJPA implements Serializable {

    private static final long serialVersionUID = -8580159104614047612L;

    @Id
    @Column(nullable = false, updatable = false)
    private Integer id;

    @Column(name = "time_last_reindex")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timeLastReindex;

    @Column(name = "time_last_image_files_reindex")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeLastImageFilesReindex;

    @Column(name = "time_last_remove_public_cache")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timeLastRemovePublicCache;

    @Column(name = "time_last_remove_static_cache")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timeLastRemoveStaticCache;

    @Column(name = "time_last_remove_other_cache")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timeLastRemoveOtherCache;


    @Column(name = "time_last_build_cache")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timeLastBuildCache;
}

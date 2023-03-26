package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.DocumentWasteBasket;
import com.imcode.imcms.model.UserData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "document_waste_basket")
@Data
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DocumentWasteBasketJPA extends DocumentWasteBasket {

    @Id
    @Column(name = "meta_id")
    private Integer metaId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "meta_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Meta meta;

    @Column(name = "added_datetime", nullable = false)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDatetime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User addedBy;

    @Override
    public void setAddedBy(UserData addedBy) {
        this.addedBy = new User(addedBy);
    }

    public DocumentWasteBasketJPA() {}

    public DocumentWasteBasketJPA(DocumentWasteBasket from) {
        super(from);
    }
}

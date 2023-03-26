package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;

/**
 * Document status state, mixed from {@link PublicationStatus} and
 * document's archived, published and publication end dates.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.02.18.
 */
public enum DocumentStatus {
    PUBLISHED,          // PublicationStatus == APPROVED and published date-time is <= than now
    PUBLISHED_WAITING,  // PublicationStatus == APPROVED and published date-time is > than now
    IN_PROCESS,         // PublicationStatus is NEW
    DISAPPROVED,        // PublicationStatus is DISAPPROVED
    ARCHIVED,           // archived date-time is <= than now
    PASSED,             // publicationEnd date-time is <= than now
    WASTE_BASKET        // document in the waste basket
}

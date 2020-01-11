package com.imcode.imcms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class DataAvailableDocumentInfo implements Serializable {

    private static final long serialVersionUID = -84850004568510098L;

    private List<Integer> docIds;
    private Integer countAvailableDocs;

}

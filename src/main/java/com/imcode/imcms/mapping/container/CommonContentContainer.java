package com.imcode.imcms.mapping.container;

import com.imcode.imcms.mapping.CommonContentVO;

import java.util.Objects;

public class CommonContentContainer {

    public static CommonContentContainer of(DocVersionRef docVersionRef, CommonContentVO commonContentVO) {
        return new CommonContentContainer(docVersionRef, commonContentVO);
    }

    private final DocVersionRef docVersionRef;

    private final CommonContentVO commonContentVO;

    public CommonContentContainer(DocVersionRef docVersionRef, CommonContentVO commonContentVO) {
        Objects.requireNonNull(docVersionRef);
        Objects.requireNonNull(commonContentVO);

        this.docVersionRef = docVersionRef;
        this.commonContentVO = commonContentVO;
    }

    public DocVersionRef getDocVersionRef() {
        return docVersionRef;
    }

    public CommonContentVO getCommonContent() {
        return commonContentVO;
    }

    public int getDocId() {
        return docVersionRef.getDocId();
    }

    public int getDocVersionNo() {
        return docVersionRef.getDocVersionNo();
    }
}

package com.imcode.imcms.mapping.dao;

import javax.persistence.TypedQuery;
import java.util.List;

public class QueryUtil {

    public static <T> T getFirstOrNull(TypedQuery<T> query) {
        List<T> result = query.getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}

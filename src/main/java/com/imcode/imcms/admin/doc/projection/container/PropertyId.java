package com.imcode.imcms.admin.doc.projection.container;

import com.vaadin.ui.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum PropertyId {

    INDEX("docs_projection.container_property.index", Integer.class),
    META_ID("docs_projection.container_property.meta_id", Component.class),
    PHASE("docs_projection.container_property.phase", String.class),
    TYPE("docs_projection.container_property.type", String.class),
    LANGUAGE("docs_projection.container_property.language", Component.class),
    ALIAS("docs_projection.container_property.alias", String.class),
    HEADLINE("docs_projection.container_property.headline", String.class),

    CREATED_DT("docs_projection.container_property.created_dt", String.class),
    MODIFIED_DT("docs_projection.container_property.modified_dt", String.class),

    PUBLICATION_START_DT("docs_projection.container_property.publication_start_dt", String.class),
    ARCHIVING_DT("docs_projection.container_property.archiving_dt", String.class),
    PUBLICATION_END_DT("docs_projection.container_property.publication_end_dt", String.class),

    PARENTS("docs_projection.container_property.parents", Component.class),
    CHILDREN("docs_projection.container_property.children", Component.class);

    private static final Collection<PropertyId> valuesCollection;

    static {
        valuesCollection = Collections.unmodifiableCollection(Arrays.asList(values()));
    }

    public static Collection<PropertyId> valuesCollection() {
        return valuesCollection;
    }

    private final String id;
    private final Class<?> type;

    PropertyId(String id, Class<?> type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String toString() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }
}

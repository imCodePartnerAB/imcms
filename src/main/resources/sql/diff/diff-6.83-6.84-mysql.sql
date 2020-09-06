SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 84;

alter table imcms_doc_versions drop foreign key fk__imcms_doc_versions__meta;

alter table imcms_doc_versions
    add constraint fk__imcms_doc_versions__meta
        foreign key (doc_id) references meta (meta_id)
            on update cascade on delete cascade;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;


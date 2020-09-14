SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 86;

alter table imcms_doc_versions drop foreign key fk__imcms_doc_versions__meta;

alter table imcms_doc_versions
    add constraint fk__imcms_doc_versions__meta
        foreign key (doc_id) references meta (meta_id)
            on update cascade on delete cascade;

alter table imcms_menu_item drop foreign key imcms_menu_item_fk_id_imcms_menu_item;

alter table imcms_menu_item
    add constraint imcms_menu_item_fk_id_imcms_menu_item
        foreign key (parent_item_id) references imcms_menu_item (id)
            on update cascade on delete cascade;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;


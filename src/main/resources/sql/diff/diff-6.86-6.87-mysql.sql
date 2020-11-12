SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 87;


alter table imcms_menu_item
  drop foreign key imcms_menu_item_fk_id_imcms_menu_item;

drop index imcms_menu_item_fk_id_imcms_menu_item
on imcms_menu_item;

alter table imcms_menu_item
  drop column parent_item_id;

alter table imcms_menu_item
  modify sort_order varchar(25) default '1' not null;

delete
from imcms_menu_item
where menu_id is null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
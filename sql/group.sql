create table contents (
  content_id int auto_increment primary key,
  content_no int not null,
  meta_id int not null,
  base_index int not null,

  unique index ux__meta_id__content_no (meta_id, content_no)
);

create table content_loops (
  loop_id int auto_increment primary key,
  content_id int not null,
  loop_index int not null,
  order_index int not null,

  unique index ux__content_id__loop_index (content_id, loop_index),
  unique index ux__content_id__order_index (content_id, order_index),
  unique index ux__content_id__loop_index__order_index (content_id, loop_index, order_index),
  foreign key fk__content_loops__contents (content_id) references contents (content_id)
);

/*
﻿create table groups (
  group_id int auto_increment primary key,

  group_no int not null,
  meta_id int not null,
  base_index int not null
);

create table group_entries (
  entry_id int auto_increment primary key,

  group_id int not null,
  entry_index int not null,
  entry_order_index int not null
);



create table group_entry_items (
  entry_item_id int auto_increment primary key,

  item_id int not null,
  item_type int not null,
  item_no int not null
);
*/
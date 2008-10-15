create table groups (
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
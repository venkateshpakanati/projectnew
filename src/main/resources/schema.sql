DROP TABLE IF EXISTS institution;
create table institution
(
   inst_id integer not null,
   inst_name varchar(255) not null,
   primary key(inst_id)
);
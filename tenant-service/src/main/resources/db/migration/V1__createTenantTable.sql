CREATE TABLE mm_tenant
(
    id   uuid         not null primary key,
    slug varchar(30) not null,
    name varchar(255) not null
);
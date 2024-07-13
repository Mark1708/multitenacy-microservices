CREATE SCHEMA IF NOT EXISTS tenant_1;
CREATE SCHEMA IF NOT EXISTS tenant_2;

CREATE TABLE tenant_1.mm_organization
(
    id   uuid         not null primary key,
    name varchar(255) not null
);

INSERT INTO tenant_1.mm_organization (id, name) VALUES (
    '1a8daff5-f575-442a-b822-e2fd780fe012', 'Название придумаю позже'
);

CREATE TABLE tenant_2.mm_organization
(
    id   uuid         not null primary key,
    name varchar(255) not null
);

INSERT INTO tenant_2.mm_organization (id, name) VALUES (
    'edf225a5-b727-4df8-95ea-0aada5f86a32', 'Позже еще не наступило'
);

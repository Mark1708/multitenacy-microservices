create table mm_device
(
    id        uuid         not null primary key,
    tenant_id varchar(255) not null,
    unit_id   uuid,
    imei      varchar(255) not null
);

INSERT INTO mm_device (id, tenant_id, unit_id, imei) VALUES (
    'bd187e29-e40a-4f0c-83bd-72269b7dbd27', 'tenant_1', '36ad58d8-a3f7-4f75-bcc1-5f25fb491124', '123456'
);

INSERT INTO mm_device (id, tenant_id, unit_id, imei) VALUES (
    '7d902ab6-fa91-439a-92e8-00b4df1dc501', 'tenant_2', '5ef1ccb2-94f2-474a-8760-8b86117c7256', '654321'
);

create table mm_employee
(
    id              uuid         not null primary key,
    organization_id uuid         not null,
    first_name      varchar(255) not null,
    second_name     varchar(255) not null,
    middle_name     varchar(255)
);


INSERT INTO mm_employee (id, organization_id, first_name, second_name, middle_name) VALUES (
    '36ad58d8-a3f7-4f75-bcc1-5f25fb491124', '1a8daff5-f575-442a-b822-e2fd780fe012', 'Иван', 'Иванов', 'Иванович'
);
INSERT INTO mm_employee (id, organization_id, first_name, second_name, middle_name) VALUES (
    '5ef1ccb2-94f2-474a-8760-8b86117c7256', 'edf225a5-b727-4df8-95ea-0aada5f86a32', 'Александр', 'Шишкин', 'Семенович'
);

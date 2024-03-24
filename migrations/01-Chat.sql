--liquibase formatted sql

--changeset ViciousXerra:create 'Chat' table
create table if not exists Chat
(
    id              bigint not null,

    primary key (id)
)


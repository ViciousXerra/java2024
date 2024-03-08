--liquibase formatted sql

--changeset ViciousXerra:create 'Chat' table
create table if not exists Chat
(
    id              bigint not null,
    username        text not null,
    created_at      timestamp with time zone not null,

    primary key (id),
    unique (id),
    unique (username)
)


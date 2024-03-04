--liquibase formatted sql

--changeset ViciousXerra:create 'Chat' table
create table IF NOT EXISTS Chat
(
    id              bigint generated always as identity,
    username        text not null,
    created_at      timestamp with time zone not null,

    primary key (id),
    unique (username)
)


--liquibase formatted sql

--changeset ViciousXerra:create 'Link' table
create table if not exists Link
(
    id              bigint generated always as identity,
    url             text not null,
    updated_at      timestamp with time zone not null,

    primary key (id),
    unique (url)
)

--liquibase formatted sql

--changeset ViciousXerra:create 'Link' table
create table IF NOT EXISTS Link
(
    id              bigint generated always as identity,
    url             text not null,
    updated_at      timestamp with time zone not null,

    primary key (id),
    unique (url)
)

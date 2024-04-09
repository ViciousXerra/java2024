--liquibase formatted sql

--changeset ViciousXerra:create 'Link' table
create table if not exists Link
(
    id              bigint generated always as identity,
    url             text not null,
    updated_at      timestamp with time zone not null default now(),
    checked_at      timestamp with time zone not null default now(),

    primary key (id),
    unique (url)
)

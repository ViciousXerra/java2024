--liquibase formatted sql

--changeset ViciousXerra:create 'ChatIdLinkId' table
create table if not exists ChatIdLinkId
(
    id              bigint generated always as identity,
    chat_id         bigint not null references Chat (id) on delete cascade,
    link_id         bigint not null references Link (id) on delete cascade
)

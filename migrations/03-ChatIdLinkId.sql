--liquibase formatted sql

--changeset ViciousXerra:create 'ChatIdLinkId' table
create table IF NOT EXISTS ChatIdLinkId
(
    id              bigint generated always as identity,
    chat_id         bigint not null references Chat (id),
    link_id         bigint not null references Link (id)
)

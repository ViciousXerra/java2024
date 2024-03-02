create table ChatIdLinkId
(
    id              bigint generated always as identity,
    chat_id         bigint not null references Chat (id),
    link_id         bigint not null references Link (id)
);

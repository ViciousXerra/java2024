create table Chat
(
    id              bigint generated always as identity,
    username        text not null,
    created_at      timestamp with time zone not null,

    primary key (id),
    unique (username)
);


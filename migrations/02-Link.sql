create table Link
(
    id              bigint generated always as identity,
    url             text not null,
    updated_at      timestamp with time zone not null,

    primary key (id),
    unique (url)
);

create table public.prices
(
    id         bigserial
        constraint prices_pkey primary key,
    product_id bigint not null unique,
    value      bigint not null

)
create table public.products
(
    id             bigserial
        constraint products_pkey primary key,
    name           varchar(255) not null,
    barcode        bigint       not null,
    count_in_stock bigint       not null
);
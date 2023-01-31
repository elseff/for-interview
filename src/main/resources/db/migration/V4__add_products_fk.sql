alter table public.prices
    add constraint products_fk
        foreign key (product_id) references public.products;
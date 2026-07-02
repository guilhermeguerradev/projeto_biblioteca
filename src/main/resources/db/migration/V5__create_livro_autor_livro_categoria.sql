create table livro_autor(
    id_livro bigint not null,
    id_autor bigint not null,

    constraint fk_livro_autor foreign key (id_livro)
        references livro(id)
        on delete cascade,

    constraint fk_autor_livro foreign key (id_autor)
        references autor(id)
        on delete cascade,

    primary key (id_livro,id_autor)
);

create table livro_categoria(
    id_categoria bigint not null,
    id_livro bigint not null,

    constraint fk_categoria_livro
        foreign key (id_categoria)
        references categoria(id)
        on delete cascade,

    constraint fk_livro_categoria
        foreign key (id_livro)
        references livro(id)
        on delete cascade ,

    primary key (id_livro,id_categoria)
);
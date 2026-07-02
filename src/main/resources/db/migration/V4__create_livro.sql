create table livro(
    id bigserial primary key,
    id_editora bigint not null,
    titulo varchar(255) not null,
    isbn varchar(255) not null unique,
    ano_publicacao int not null,
    edicao varchar(255) not null,
    idioma varchar(255) not null,
    numero_paginas int ,
    sinopse text not null,

    constraint fk_editora_livro foreign key (id_editora) references editora(id)
);
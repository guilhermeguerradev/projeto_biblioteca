CREATE TABLE usuario (
    id bigserial primary key ,
    nome varchar(255) not null,
    email varchar(255) not null unique,
    senha varchar(255) not null,
    telefone varchar(30),
    ativo boolean not null default true,
    data_cadastro timestamp not null default current_timestamp
);

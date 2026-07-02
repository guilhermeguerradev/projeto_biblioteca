create table categoria(
    id bigserial primary key,
    nome varchar(255) not null,
    descricao varchar(255) not null
);

create table editora(
    id bigserial primary key,
    nome varchar(255) not null,
    cidade varchar(255) not null,
    estado varchar(255) not null,
    pais varchar(255) not null
);

create table autor(
    id bigserial primary key,
    nome varchar(255) not null,
    nacionalidade varchar(255) not null
);
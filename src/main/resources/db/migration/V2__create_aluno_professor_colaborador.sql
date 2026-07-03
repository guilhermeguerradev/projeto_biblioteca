CREATE SEQUENCE seq_matricula_aluno START WITH 1 INCREMENT BY 1;

create table aluno (
    id bigint primary key,
    matricula varchar(255) not null unique,
    curso varchar(255),
    periodo smallint not null,

    constraint fk_aluno_usuario foreign key (id)
                   references usuario (id) on delete cascade

);

create table professor(
    id bigint primary key,
    siape varchar(255) not null unique,
    departamento varchar(255),
    titulacao varchar(255),

    constraint fk_professor_usuario foreign key (id)
                      references usuario (id) on delete cascade

);

create table colaborador(
    id bigint primary key,

    cargo varchar(30) not null check (
        cargo in ( 'BIBLIOTECARIO', 'AUXILIAR', 'ADMINISTRADOR')),

    setor varchar(30) not null check (
        setor in ('ATENDIMENTO', 'ACERVO', 'ADMINISTRATIVO')),

    data_admissao date not null,

    constraint fk_colaborador_usuario foreign key (id)
        references usuario (id) on delete cascade
);
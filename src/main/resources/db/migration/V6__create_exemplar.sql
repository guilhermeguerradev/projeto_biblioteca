create table exemplar(
    id serial primary key,
    livro_id int not null,
    codigo_patrimonio varchar(255) unique not null,
    status varchar(15) not null check (
        status IN (
               'DISPONIVEL',
               'EMPRESTADO',
               'RESERVADO',
               'EM_MANUTENCAO',
               'DANIFICADO',
               'EXTRAVIADO',
               'DESCARTADO'
        )
    ),

    localizacao varchar(255),
    data_aquisicao date,
    observacao text,

    constraint fk_examplar_livro foreign key (livro_id)
        references livro(id)
);
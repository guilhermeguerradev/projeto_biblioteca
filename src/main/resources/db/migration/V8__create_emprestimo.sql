create table emprestimo(
    id serial primary key,
    id_usuario int not null,
    id_colaborador int not null,
    id_exemplar int not null,

    data_emprestimo timestamp not null default current_timestamp,

    data_prevista timestamp not null default (
            current_timestamp + interval '7 days'
        ),

    data_devolucao timestamp,

    quantidade_renovacoes int not null default 0,

    status varchar(20) not null check (
        status IN (
               'ATIVO',
               'DEVOLVIDO',
               'ATRASADO',
               'CANCELADO'
        )),


    constraint fk_emprestimo_usuario foreign key (id_usuario)
                       references usuario(id),

    constraint fk_emprestimo_colaborador foreign key (id_colaborador)
                       references colaborador(id),

    constraint fk_emprestimo_exemplar foreign key (id_exemplar)
                       references exemplar(id)

);


create table emprestimo(
    id bigserial primary key,
    id_usuario bigint not null,
    id_colaborador bigint not null,
    id_exemplar bigint not null,

    data_emprestimo timestamp not null ,
    data_prevista timestamp not null ,
    data_devolucao timestamp,

    quantidade_renovacoes int not null default 0,

    status varchar(20) not null check (
        status IN (
               'ATIVO',
               'DEVOLVIDO',
               'ATRASADO',
               'CANCELADO',
               'FINALIZADO'
        )),


    constraint fk_emprestimo_usuario foreign key (id_usuario)
                       references usuario(id),

    constraint fk_emprestimo_colaborador foreign key (id_colaborador)
                       references colaborador(id),

    constraint fk_emprestimo_exemplar foreign key (id_exemplar)
                       references exemplar(id)

);


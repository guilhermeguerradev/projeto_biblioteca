create table multa(
    id bigserial primary key,
    id_emprestimo bigint not null unique
        references emprestimo(id)
                  on delete cascade,

    valor decimal(10,2) not null,

    motivo varchar(255) not null,

    status varchar(20) not null check (
        status in (
            'PENDENTE','PAGA','CANCELADA'
            )
        ),

    data_pagamento timestamp
);
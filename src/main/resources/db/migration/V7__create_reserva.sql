create table reserva(
    id serial primary key ,

    id_usuario int not null,

    id_livro int not null,

    data_reserva timestamp not null default current_timestamp,

    status varchar(15) not null check (
        status in (
        'ATIVA','DISPONIVEL','FINALIZADA','CANCELADA','EXPIRADA'
    )),

    constraint fk_usuario_reserva foreign key (id_usuario)
                    references usuario(id),

    constraint fk_livro_reserva foreign key (id_livro)
                    references livro(id),

    -- Um usuário não pode criar duas reservas para o mesmo livro.
    CONSTRAINT uk_reserva_usuario_livro
        UNIQUE (id_usuario, id_livro)
);
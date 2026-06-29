create table usuario_roles (
    usuario_id int not null,
    role varchar(50) not null check ( role in (
        'ROLE_ALUNO' , 'ROLE_PROFESSOR', 'ROLE_COLABORADOR', 'ROLE_ADMIN'
        ) ),

    primary key (usuario_id, role),

    constraint fk_roles_usuario foreign key (usuario_id) references usuario(id)
                           on delete cascade
);
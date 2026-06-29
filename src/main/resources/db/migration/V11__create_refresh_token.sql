create table refresh_token(
    id serial primary key,
    usuario_id int not null references usuario(id) on delete cascade,
    token text not null unique,
    expira_em timestamp not null,
    revogado boolean not null default false
);
create table Usuario(
	id_usuario serial primary key,
	nome varchar(50),
	data_nas date
);

create table Email(
	id_email serial primary key,
	email varchar(70),
	usuario_id int references Usuario(id_usuario)
);

create table Telefone(
	id_telefone serial primary key,
	telefone varchar(11),
	usuario_id int references Usuario(id_usuario)
);
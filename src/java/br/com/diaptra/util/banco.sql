create table Usuario(
	id_usuario serial primary key,
	nome varchar(50) not null,
	data_nas date not null,
	email varchar (70) not null,
	telefone varchar (11) not null,
	senha varchar (30) not null
);

create table Disciplina(
	id_disciplina serial primary key,
	nomeDisciplina varchar(50) not null
); 

create table Avaliacao(	
	id_avaliacao serial primary key,
	valorAvaliacao numeric(4,2) not null,
	nomeAvaliacao varchar (30) not null,
	descricaoAvaliacao varchar (250),
	notaObtidaAvaliaca numeric(4,2),
	disciplinaID int references Disciplina(id_disciplina),
	agendaID int references Agenda(id_agenda)
);

create table Agenda(
	id_agenda serial primary key,
	tituloAgendamento varchar (50) not null,
	descricaoAgenda varchar(100),
	dataAgenda date not null,
	horarioAgenda time
);

create Table Endereco(
	id_endereco serial primary key,
	rua varchar (50) not null,
        numero int not null,
	bairro varchar(50) not null,
	cidade varchar (30) not null,
	estado varchar(2) not null,
	complemento varchar (50),
	usuarioID int references Usuario(id_usuario)
);

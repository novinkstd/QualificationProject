
-- Таблица с темами теста
create table themes (
    id             integer     auto_increment primary key,
    themeName      varchar(30) not null,
    constraint uk__themes_themeName unique(themeName)
);

-- Таблица с вопросами
create table questions (
    id             	  integer     auto_increment primary key,
    questionName      varchar(30) 	not null,
    text			  varchar(100) 	not null,
    answer			  varchar(30) 	not null,
    constraint uk_questions_questionName unique(questionName)
);

--- Таблица соответсвий вопросов с темой
create table questionsList (
    id				integer   	auto_increment primary key,
    themeID      	integer 	not null,
    questionID		integer 	not null,
    constraint uk_questionsList_qustion_theme unique(questionID, themeID),
    constraint fk_questionsList_theme foreign key (themeID) references themes (id),
    constraint fk_questionsList_question foreign key (questionID) references questions (id)
);

--- Таблица пользователей
create table users (
    id             	  	integer     	auto_increment primary key,
    login      			varchar(20) 	not null,
    password		  	varchar(20) 	not null,
    isAdminRole			boolean 		not null,
    constraint uk_users_login unique(login)
);
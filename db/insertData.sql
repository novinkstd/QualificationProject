INSERT INTO users (login, password, isAdminRole)
VALUES ('admin', 'admin', true),
	   ('user1', '1234', false),
	   ('user2', 'qwerty', false)
INSERT	INTO THEMES (themeName)
VALUES	('pastSimple'),
		('presentSimple'),
		('Irregular Verbs')
INSERT INTO QUESTIONS (questionName, text, answer)
VALUES	('eat - ...', 'укажите 2-ю форму глагола eat', 'ate'),
		('know - ...', 'укажите 2-ю форму глагола know', 'knew'),
		('make - ...', 'укажите 3-ю форму глагола make', 'made'),
		('УП в Past Simple', 'Дополните предложение: He .... home late last night.', 'came' ),
		('ВП в Past Simple', 'Дополните предложение: ... you go out last night?', 'Did'),
		('УП в Present Simple', 'Дополните предложение: I (study/studies) Spanish with my sister.', 'study')
INSERT	INTO QUESTIONSLIST (themeId, QUESTIONID)
VALUES	(1, 1),
		(1, 2),
		(1, 4),
		(1, 5),
		(2, 6),
		(3, 1),
		(3, 2),
		(3, 3)
# --- !Ups




DROP TABLE IF EXISTS users;

CREATE TABLE users (
	id 					int           	NOT NULL AUTO_INCREMENT,
	email				varchar(255)	NOT NULL,
	password			varchar(255)    NOT NULL,
	firstName			varchar(127)	NOT NULL,
	lastName			varchar(127)	NOT NULL,
	

	PRIMARY KEY(id),
	UNIQUE (email)
);

INSERT INTO users VALUES (1, "chordtrane@gmail.com", "admin", "Admin", "Cyan");

DROP TABLE IF EXISTS songs;

CREATE TABLE songs (
	id 					int           	NOT NULL AUTO_INCREMENT,
	rawText				TEXT			,
	title				varchar(127)	NOT NULL,
	composer			varchar(127)	,
	dateCreated			varchar(127)	,
	timeSig				int             NOT NULL,
	userId				int             NOT NULL,
	currentKey			varchar(127)	NOT NULL,
	destinationKey		varchar(127)	NOT NULL,
	transposeOn			boolean 		NOT NULL,
	romanNumeral		boolean			NOT NULL,

	PRIMARY KEY(id)
);

INSERT INTO songs VALUES (1, "|C|", "Title", "Composer", "Date", 4, 1, "C", "C", 0, 0);
-- 
-- 

DROP TABLE IF EXISTS playbackSettings;

CREATE TABLE playbackSettings (
	songId				int 			NOT NULL,
	bpm 				int 			NOT NULL,
	repeats				int 			NOT NULL,
	pianoMinRange		int 			NOT NULL,
	pianoMaxRange		int 			NOT NULL,
	pianoStayInTessitura double 		NOT NULL,
	pianoConnectivity	double			NOT NULL,
	bassMinRange		int 			NOT NULL,
	bassMaxRange		int 			NOT NULL,
	bassStayInTessitura	double 			NOT NULL,
	bassConnectivity	double			NOT NULL,
	
	PRIMARY KEY(songId)
);

INSERT INTO playbackSettings VALUES (1, 120, 4, 48, 72, 1.0, 5.0, 24, 55, 1.0, 5.0);

-- 
# --- !Downs
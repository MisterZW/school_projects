DROP TABLE IF EXISTS AGENT CASCADE;

CREATE TABLE AGENT (
	username		VARCHAR(40),
	password		VARCHAR(40),

	CONSTRAINT AGENT_PK PRIMARY KEY(username)
);
USE jli_test;

DROP TABLE IF EXISTS testGetAssociationsPlayed;
CREATE TABLE testGetAssociationsPlayed (id INTEGER UNIQUE NOT NULL, si VARCHAR(255) UNIQUE NOT NULL, id2 INTEGER);
INSERT INTO testGetAssociationsPlayed VALUES(1,"http://TestTopicImpl/testGetAssociationsPlayed/1",NULL);
INSERT INTO testGetAssociationsPlayed VALUES(2,"http://TestTopicImpl/testGetAssociationsPlayed/2",3);
INSERT INTO testGetAssociationsPlayed VALUES(3,"http://TestTopicImpl/testGetAssociationsPlayed/3",NULL);


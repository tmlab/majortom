CREATE FUNCTION ILIKE(val VARCHAR(2048), to_test VARCHAR(2048)) 
RETURNS BOOLEAN
RETURN val LIKE to_test OR UCASE(val) LIKE to_test OR LCASE(val) LIKE to_test;;

CREATE FUNCTION IS_SUBSET(array1 BIGINT ARRAY, array2 BIGINT ARRAY)
RETURNS BOOLEAN
BEGIN ATOMIC
    FOR SELECT * FROM UNNEST(array1) DO
        IF c1 NOT IN ( UNNEST (array2)) THEN
            RETURN FALSE;
        END IF;
    END FOR;
    RETURN TRUE;
END;;

DROP TABLE IF EXISTS locators;;
CREATE MEMORY TABLE locators (
    id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1) PRIMARY KEY,
    reference character varying(1024) NOT NULL
);;

DROP TABLE IF EXISTS topicmaps;;
CREATE MEMORY TABLE topicmaps(
	id bigint GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 8) PRIMARY KEY,
    id_parent bigint,
    id_topicmap bigint,
    id_reifier bigint,
    id_base_locator bigint NOT NULL,
  CONSTRAINT fk_baselocator FOREIGN KEY (id_base_locator)
      REFERENCES locators (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS topics;;
CREATE MEMORY TABLE topics (
	id bigint GENERATED ALWAYS AS IDENTITY(START WITH 2 INCREMENT BY 8) PRIMARY KEY,
    id_parent bigint,
    id_topicmap bigint,
  CONSTRAINT fk_topic_parent FOREIGN KEY (id_parent)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;


ALTER TABLE topicmaps ADD
  CONSTRAINT fk_topicmaps_reifier FOREIGN KEY (id_reifier)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE;;



DROP TABLE IF EXISTS scopes;;
CREATE MEMORY TABLE scopes (
    id bigint GENERATED ALWAYS AS IDENTITY(START WITH 3 INCREMENT BY 8) PRIMARY KEY,
	id_topicmap bigint,
  CONSTRAINT fk_scopes_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
       ON DELETE CASCADE ON UPDATE CASCADE
);;


DROP TABLE IF EXISTS associations;;
CREATE MEMORY TABLE associations (
	id bigint GENERATED ALWAYS AS IDENTITY(START WITH 4 INCREMENT BY 8) PRIMARY KEY,
    id_parent bigint,
    id_topicmap bigint,
    id_reifier bigint,
    id_scope bigint DEFAULT 0 NOT NULL,
    id_type bigint NOT NULL
);;

ALTER TABLE associations ADD 
  CONSTRAINT fk_associations_parent FOREIGN KEY (id_parent)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE;;
ALTER TABLE associations ADD 
  CONSTRAINT fk_associations_type FOREIGN KEY (id_type)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE;;
ALTER TABLE associations ADD 
  CONSTRAINT fk_associations_scope FOREIGN KEY (id_scope)
      REFERENCES scopes (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE;;
ALTER TABLE associations ADD 
  CONSTRAINT fk_associations_reifier FOREIGN KEY (id_reifier)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE SET NULL ON UPDATE CASCADE;;
ALTER TABLE associations ADD 
  CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE;;


DROP TABLE IF EXISTS revisions;;
CREATE MEMORY TABLE revisions
(
  id bigint GENERATED ALWAYS AS IDENTITY(START WITH 1) PRIMARY KEY,
  time timestamp NOT NULL,
  id_topicmap bigint NOT NULL,
  type character varying(128) NOT NULL,
  CONSTRAINT fk_revisions_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS changesets;;
CREATE MEMORY TABLE changesets
(
  id bigint GENERATED ALWAYS AS IDENTITY(START WITH 1) PRIMARY KEY,
  id_revision bigint,
  id_notifier bigint NOT NULL,
  type character varying(128) NOT NULL,
  newvalue character varying(1024),
  oldvalue character varying(1024),
  time timestamp,
  CONSTRAINT fk_changesets_revision FOREIGN KEY (id_revision)
      REFERENCES revisions (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS history;;
CREATE MEMORY TABLE history
(
  id bigint NOT NULL,
  id_topicmap bigint NOT NULL,
  id_revision bigint NOT NULL,
  id_parent bigint NOT NULL,
  names varchar(1024),
  occurrences varchar(1024),
  variants varchar(1024),
  associations varchar(1024),
  id_scope bigint,
  id_reification bigint,
  id_player bigint,
  types varchar(1024),
  supertypes varchar(1024),
  value varchar(1024),
  type varchar(1),
  themes varchar(1024),
  itemidentifiers varchar(1024) NOT NULL,
  subjectidentifiers varchar(1024),
  subjectlocators varchar(1024),
  datatype varchar(1024),
  roles varchar(1024),
  bestlabel varchar(256),
  CONSTRAINT fk_history_revision FOREIGN KEY (id_revision)
      REFERENCES revisions (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_history_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS metadata;;
CREATE MEMORY TABLE metadata
(
  id_revision bigint NOT NULL,
  key varchar(1024) NOT NULL,
  value varchar(1024),
  CONSTRAINT fk_md_revision FOREIGN KEY (id_revision)
      REFERENCES revisions (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS names;;
CREATE MEMORY TABLE names (
	id bigint GENERATED ALWAYS AS IDENTITY(START WITH 5 INCREMENT BY 8) PRIMARY KEY,
    id_parent bigint,
    id_topicmap bigint,
    literal varchar(1024),
    id_reifier bigint,
    id_scope bigint NOT NULL,
    id_type bigint NOT NULL,
    value varchar(1024) NOT NULL,
  CONSTRAINT fk_names_type FOREIGN KEY (id_type)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_names_scope FOREIGN KEY (id_scope)
      REFERENCES scopes (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_names_reifier FOREIGN KEY (id_reifier)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_names_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;


DROP TABLE IF EXISTS occurrences;;
CREATE MEMORY TABLE occurrences (
	id bigint GENERATED ALWAYS AS IDENTITY(START WITH 6 INCREMENT BY 8) PRIMARY KEY,
    id_parent bigint,
    id_topicmap bigint,
    id_reifier bigint,
    id_scope bigint NOT NULL,
    id_type bigint NOT NULL,
    value varchar(1024) NOT NULL,
    id_datatype bigint NOT NULL,
  CONSTRAINT fk_occurrences_type FOREIGN KEY (id_type)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_occurrences_scope FOREIGN KEY (id_scope)
      REFERENCES scopes (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_occurrences_reifier FOREIGN KEY (id_reifier)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_occurrences_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;


DROP TABLE IF EXISTS rel_instance_of;;
CREATE MEMORY TABLE rel_instance_of (
    id_instance bigint NOT NULL,
    id_type bigint NOT NULL,
  CONSTRAINT fk_instance FOREIGN KEY (id_instance)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_topic_type FOREIGN KEY (id_type)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS rel_item_identifiers;;
CREATE MEMORY TABLE rel_item_identifiers (
    id_construct bigint NOT NULL,
    id_locator bigint NOT NULL,
  CONSTRAINT fk_ii_locator FOREIGN KEY (id_locator)
      REFERENCES locators (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS rel_kind_of;;
CREATE MEMORY TABLE rel_kind_of (
    id_subtype bigint,
    id_supertype bigint,
  CONSTRAINT fk_subtype FOREIGN KEY (id_subtype)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_supertype FOREIGN KEY (id_supertype)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS rel_subject_identifiers;;
CREATE MEMORY TABLE rel_subject_identifiers (
    id_topic bigint NOT NULL,
    id_locator bigint NOT NULL,
  CONSTRAINT fk_si_locator FOREIGN KEY (id_locator)
      REFERENCES locators (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_si_topic FOREIGN KEY (id_topic)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS rel_subject_locators;;
CREATE MEMORY TABLE rel_subject_locators (
    id_topic bigint,
    id_locator bigint NOT NULL,
  CONSTRAINT fk_sl_locator FOREIGN KEY (id_locator)
      REFERENCES locators (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_sl_topic FOREIGN KEY (id_topic)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS rel_themes;;
CREATE MEMORY TABLE rel_themes (
    id_scope bigint,
    id_theme bigint NOT NULL,
  CONSTRAINT fk_themes_scope FOREIGN KEY (id_scope)
      REFERENCES scopes (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_theme FOREIGN KEY (id_theme)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;

DROP TABLE IF EXISTS roles;;
CREATE MEMORY TABLE roles (
	id bigint GENERATED ALWAYS AS IDENTITY(START WITH 7 INCREMENT BY 8) PRIMARY KEY,
    id_parent bigint,
    id_topicmap bigint,
    id_type bigint NOT NULL,
    id_reifier bigint,
    id_player bigint NOT NULL,
 CONSTRAINT fk_roles_player FOREIGN KEY (id_player)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_roles_parent FOREIGN KEY (id_parent)
      REFERENCES associations (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_roles_type FOREIGN KEY (id_type)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_roles_reifier FOREIGN KEY (id_reifier)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_roles_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;


DROP TABLE IF EXISTS tags;;
CREATE MEMORY TABLE tags
(
  tag varchar(1024) NOT NULL,
  time timestamp NOT NULL
);;

DROP TABLE IF EXISTS variants;;
CREATE MEMORY TABLE variants (
	id bigint GENERATED ALWAYS AS IDENTITY(START WITH 8 INCREMENT BY 8) PRIMARY KEY,
    id_parent bigint,
    id_topicmap bigint,
    id_reifier bigint,
    id_scope bigint NOT NULL,
    value varchar(1024) NOT NULL,	
    id_datatype bigint NOT NULL,
  CONSTRAINT fk_variants_parent FOREIGN KEY (id_parent)
      REFERENCES names (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_variants_scope FOREIGN KEY (id_scope)
      REFERENCES scopes (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_variants_reifier FOREIGN KEY (id_reifier)
      REFERENCES topics (id) MATCH SIMPLE
      ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_variants_topicmap FOREIGN KEY (id_topicmap)
      REFERENCES topicmaps (id) MATCH SIMPLE
      ON DELETE CASCADE ON UPDATE CASCADE
);;



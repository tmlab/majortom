--
-- PostgreSQL database dump
--

-- Started on 2011-01-07 09:44:05

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 411 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 19 (class 1255 OID 15115772)
-- Dependencies: 6 411
-- Name: best_label(bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION best_label("topicMapId" bigint, "topicId" bigint) RETURNS character varying
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	label character varying;
	ids bigint[];		
	typeId bigint;
	numberOfThemes bigint;
	scopedNames bigint[];
BEGIN

	/*check if topic has more than one name*/
	FOR rec IN SELECT id FROM names WHERE id_parent = $2 LOOP
		ids := ids || rec.id;
	END LOOP;	
	/*topic has only one name*/
	IF array_upper(ids,1) = 1 THEN	
		SELECT value FROM names WHERE id IN ( SELECT unnest(ids)) INTO rec;
		RETURN rec.value;
	/* topic has more than one name */
	ELSEIF array_upper(ids,1) > 1 THEN		
		/*get if default name type*/
		SELECT t.id FROM topics AS t, locators AS l, rel_subject_identifiers AS r WHERE l.reference = 'http://psi.topicmaps.org/iso13250/model/topic-name' AND r.id_locator = l.id AND r.id_topic = t.id AND t.id_topicmap = $1 INTO rec;
		/*TMDM default name type exists */
		IF rec IS NOT NULL THEN
			typeId = rec.id;
			/* get names of the default name type */
			SELECT ARRAY(SELECT id FROM names WHERE id_type = typeId AND id_parent = $2 INTERSECT SELECT unnest(ids) AS id ) AS a INTO rec;
			/* only one default default name */			
			IF array_upper(rec.a,1) = 1 THEN
				/* get name values */
				SELECT value FROM names WHERE id IN ( SELECT unnest(rec.a)) INTO rec;
				RETURN rec.value;
			/*more than one default name */
			ELSEIF array_upper(rec.a,1) > 1 THEN
				ids := rec.a;			
			END IF;				
		END IF;
		/* get empty scope*/
		SELECT ARRAY ( SELECT id FROM names WHERE id_scope NOT IN ( SELECT DISTINCT id_scope FROM rel_themes ) AND id_parent = $2 INTERSECT SELECT unnest(ids) AS id ) AS a  INTO rec;		
		/* there is only one name with unconstrained scope*/			
		IF array_upper(rec.a,1) = 1 THEN
			/* get name values */
			SELECT value FROM names WHERE id IN ( SELECT unnest(rec.a)) INTO rec;
			RETURN rec.value;
		/* there are more than one name with unconstrained scope*/
		ELSEIF array_upper(rec.a,1) > 1 THEN
			/* get name values */
			SELECT value, length(value) AS l FROM names WHERE id IN ( SELECT unnest(rec.a)) ORDER BY l, value OFFSET 0 LIMIT 1 INTO rec2;
			RETURN rec2.value;
		END IF;
		numberOfThemes := -1;
		/* No name item with unconstrained scope -> order name items by number of themes */		
		FOR rec IN SELECT n.id_scope, COUNT(id_theme) AS c FROM names AS n, rel_themes AS r WHERE r.id_scope = n.id_scope AND id_parent = $2 GROUP BY n.id_scope ORDER BY c LOOP
			/*Get scoped names*/
			SELECT ARRAY(SELECT id FROM names WHERE id_scope = rec.id_scope AND id_parent = $2 ) AS a INTO rec2;
			IF array_upper(rec2.a,1) > 0 THEN
				/* set number of themes */
				IF numberOfThemes < '0' THEN
					numberOfThemes := rec.c;
				END IF;					
				/* more themes than expected */
				IF numberOfThemes = rec.c THEN					
					/* set scoped names */
					scopedNames := scopedNames || rec2.a;
				END IF;				
			END IF;		
		/*END LOOP of scoped names*/
		END LOOP;
		/* there is only one scope name*/			
		IF array_upper(scopedNames,1) = 1 THEN
			/* get name values */
			SELECT value FROM names WHERE id IN ( SELECT unnest(scopedNames)) INTO rec2;
			RETURN rec2.value;
		/* there are more than one scope name*/
		ELSEIF array_upper(scopedNames,1) > 1 THEN
			/* get name values */
			SELECT value, length(value) AS l  FROM names WHERE id IN ( SELECT unnest(scopedNames)) ORDER BY l, value OFFSET 0 LIMIT 1 INTO rec2;
			RETURN rec2.value;
		END IF;
	/* topic has no names */
	ELSE
		/*check subject-identifier*/
		SELECT reference FROM locators, rel_subject_identifiers WHERE id_topic = $2 AND id_locator = id ORDER BY reference OFFSET 0 LIMIT 1 INTO rec;
		IF rec IS NOT NULL THEN
			return rec.reference;
		END IF;
		/*check subject-locators*/
		SELECT reference FROM locators, rel_subject_locators WHERE id_topic = $2 AND id_locator = id ORDER BY reference OFFSET 0 LIMIT 1 INTO rec;
		IF rec IS NOT NULL THEN
			return rec.reference;
		END IF;
		/*check item-identifiers*/
		SELECT reference FROM locators, rel_item_identifiers WHERE id_construct = $2 AND id_locator = id ORDER BY reference OFFSET 0 LIMIT 1 INTO rec;
		IF rec IS NOT NULL THEN
			return rec.reference;
		END IF;		
		/*return the id*/		
		RETURN $2;
	END IF;
END;$_$;


ALTER FUNCTION public.best_label("topicMapId" bigint, "topicId" bigint) OWNER TO postgres;

--
-- TOC entry 20 (class 1255 OID 15115773)
-- Dependencies: 6 411
-- Name: best_label(bigint, bigint, bigint, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION best_label("topicMapId" bigint, "topicId" bigint, "themeId" bigint, strict boolean) RETURNS character varying
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	label character varying;
	ids bigint[];		
	typeId bigint;
	atLeastOneName boolean;
	numberOfThemes bigint;
	scopedNames bigint[];
BEGIN
	atLeastOneName := false;
	/*check if topic has more than one name*/
	FOR rec IN SELECT id FROM names WHERE id_parent = $2 LOOP
		ids := ids || rec.id;
	END LOOP;	
	IF  array_upper(ids,1) > 1 THEN				
		numberOfThemes := -1;		
		/* get scopes with the given theme */
		FOR rec IN SELECT n.id_scope, COUNT(id_theme) AS c FROM names AS n, rel_themes AS r WHERE r.id_scope = n.id_scope AND id_parent = $2 AND $3 IN ( SELECT id_theme FROM rel_themes WHERE id_scope = r.id_scope) GROUP BY n.id_scope ORDER BY c LOOP
			/*Get scoped names*/
			SELECT ARRAY(SELECT id FROM names WHERE id_scope = rec.id_scope AND id_parent = $2 ) AS a INTO rec2;
			IF array_upper(rec2.a,1) > 0 THEN
				/* set number of themes */
				IF numberOfThemes < '0' THEN
					numberOfThemes := rec.c;
				END IF;					
				/* more themes than expected */
				IF numberOfThemes = rec.c THEN					
					/* set scoped names */
					scopedNames := scopedNames || rec2.a;
					atLeastOneName := true;
				END IF;				
			END IF;	
		/*END LOOP of scoped names*/
		END LOOP;
		IF array_upper(scopedNames,1) = 1 THEN
			/* get name values */
			SELECT value FROM names WHERE id IN ( SELECT unnest(scopedNames)) INTO rec2;
			RETURN rec2.value;
		/* there are more than one scope name*/
		ELSEIF array_upper(scopedNames,1) > 1 THEN
			/* get name values */
			SELECT value, length(value) AS l  FROM names WHERE id IN ( SELECT unnest(scopedNames)) ORDER BY l, value OFFSET 0 LIMIT 1 INTO rec2;
			RETURN rec2.value;
		END IF;
		/* check scoped names */
		IF array_upper(scopedNames,1) = 1 THEN
			/* get name values */
			SELECT value FROM names WHERE id IN ( SELECT unnest(scopedNames)) INTO rec2;
			RETURN rec2.value;
		/* there are more than one scope name*/
		ELSEIF array_upper(scopedNames,1) > 1 THEN
			/* get name values */
			SELECT value, length(value) AS l  FROM names WHERE id IN ( SELECT unnest(rec2.a)) ORDER BY l, value OFFSET 0 LIMIT 1 INTO rec2;
			RETURN rec2.value;
		END IF;
		
		/* no name with the given theme in strict mode */
		IF $4 AND NOT atLeastOneName THEN
			RETURN NULL;
		END IF;
	ELSEIF $4 AND NOT atLeastOneName THEN
			RETURN NULL;
	END IF;
	RETURN best_label($1,$2);
END;$_$;


ALTER FUNCTION public.best_label("topicMapId" bigint, "topicId" bigint, "themeId" bigint, strict boolean) OWNER TO postgres;

--
-- TOC entry 21 (class 1255 OID 15115774)
-- Dependencies: 6 411
-- Name: cast_as_timestamp(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION cast_as_timestamp(value character varying) RETURNS timestamp with time zone
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
BEGIN
	BEGIN
		SELECT CAST ( $1 AS timestamp ) AS a INTO rec;
		IF FOUND THEN
			RETURN rec.a;
		END IF;
	EXCEPTION WHEN INVALID_DATETIME_FORMAT THEN
		RETURN NULL;
	END;
END;$_$;


ALTER FUNCTION public.cast_as_timestamp(value character varying) OWNER TO postgres;

--
-- TOC entry 23 (class 1255 OID 15115775)
-- Dependencies: 6 411
-- Name: detect_duplicate_associations(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION detect_duplicate_associations() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
	aRec RECORD;
	rec RECORD;
	rRec RECORD;
BEGIN	
	SELECT DISTINCT * INTO aRec FROM associations WHERE id = new.id_parent;
	IF FOUND THEN
		SELECT DISTINCT  a.id, a.id_reifier INTO rec FROM roles  AS r, associations AS a WHERE 0 IN ( SELECT COUNT (r) FROM ( SELECT id_type , id_player FROM roles WHERE id_parent = r.id_parent EXCEPT SELECT id_type, id_player FROM roles WHERE id_parent = aRec.id ) AS r ) AND r.id_parent <> aRec.id AND r.id_parent = a.id AND a.id_type = aRec.id_type AND a.id_scope = aRec.id_scope;
		IF FOUND THEN
			RAISE LOG 'Duplicate found with id %',rec.id;
			/* check if the duplicate association has a reifier */		
			IF rec.id_reifier IS NOT NULL THEN
				/* check if new entry has not a reifier */
				IF aRec.id_reifier IS NULL THEN
					RAISE LOG 'Move reifier';
					UPDATE associations SET id_reifier = rec.id_reifier WHERE id = aRec.id;
				/* both constructs are reified */
				ELSE
					/* merge reifier */
					RAISE LOG 'Merge reifier';
					PERFORM merge_topics(aRec.id_reifier, rec.id_reifier);				
				END IF;
			END IF;
			/* move role reifiers if necessary */
			RAISE LOG 'Move reifiers of roles';
			PERFORM move_role_reifiers(aRec.id,rec.id);		
			/* move item-identifiers */
			RAISE LOG 'Move item-identifier';
			UPDATE rel_item_identifiers SET id_construct = aRec.id WHERE id_construct = rec.id;
			/* delete duplicate association */	
			RAISE LOG 'Remove association';	
			DELETE FROM associations WHERE id = rec.id;	
		ELSE
			RAISE LOG 'No duplicate found for id %', new.id_parent;
		END IF;
	ELSE
		RAISE EXCEPTION 'Association with id % not found!', new.id_parent;
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.detect_duplicate_associations() OWNER TO postgres;

--
-- TOC entry 24 (class 1255 OID 15115776)
-- Dependencies: 411 6
-- Name: detect_duplicate_names(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION detect_duplicate_names() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
	dup RECORD;
BEGIN
	/* Select duplicate names */
	SELECT INTO dup  * FROM names WHERE id IN (SELECT id FROM names WHERE value = new.value AND id_parent = new.id_parent AND id_type = new.id_type AND id_scope = new.id_scope AND id <> new.id);
	/* is there a duplicate name? */	
	IF FOUND THEN	
		/* check if the duplicate name has a reifier */		
		IF dup.id_reifier IS NOT NULL THEN
			/* check if new entry has not a reifier */
			IF new.id_reifier IS NULL THEN
				new.id_reifier := dup.id_reifier;
			/* both constructs are reified */
			ELSE
				/* merge reifier */
				PERFORM merge_topics(new.id_reifier, dup.id_reifier);				
			END IF;
		END IF;
		/* move variants */
		UPDATE variants SET id_parent = new.id WHERE id_parent = dup.id;
		/* move item-identifiers */
		UPDATE rel_item_identifiers SET id_construct = new.id WHERE id_construct = dup.id;
		/* delete duplicate */
		DELETE FROM names WHERE id = dup.id;		
	END IF;
	/*return modified name*/	
	RETURN new;
END$$;


ALTER FUNCTION public.detect_duplicate_names() OWNER TO postgres;

--
-- TOC entry 25 (class 1255 OID 15115777)
-- Dependencies: 411 6
-- Name: detect_duplicate_occurrences(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION detect_duplicate_occurrences() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
	dup RECORD;
BEGIN
	/* Select duplicate occurrence */
	SELECT INTO dup  * FROM occurrences WHERE id IN (SELECT id FROM occurrences WHERE value = new.value AND id_parent = new.id_parent AND id_type = new.id_type AND id_scope = new.id_scope AND id_datatype = new.id_datatype AND id <> new.id);
	/* is there a duplicate occurrence? */	
	IF FOUND THEN	
		/* check if the duplicate occurrence has a reifier */		
		IF dup.id_reifier IS NOT NULL THEN
			/* check if new entry has not a reifier */
			IF new.id_reifier IS NULL THEN
				new.id_reifier := dup.id_reifier;
			/* both constructs are reified */
			ELSE
				/* merge reifier */
				PERFORM merge_topics(new.id_reifier, dup.id_reifier);				
			END IF;
		END IF;
		/* move item-identifiers */
		UPDATE rel_item_identifiers SET id_construct = new.id WHERE id_construct = dup.id;
		/* delete duplicate */
		DELETE FROM occurrences WHERE id = dup.id;		
	END IF;
	/*return modified occurrence*/	
	RETURN new;
END$$;


ALTER FUNCTION public.detect_duplicate_occurrences() OWNER TO postgres;

--
-- TOC entry 26 (class 1255 OID 15115778)
-- Dependencies: 411 6
-- Name: detect_duplicate_roles(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION detect_duplicate_roles() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
	dup RECORD;
BEGIN
	/* Select duplicate roles */
	SELECT * INTO dup FROM roles WHERE id_parent = new.id_parent AND id <> new.id AND id_type = new.id_type AND id_player = new.id_player;
	/* is there a duplicate role? */	
	IF FOUND THEN	
		/* check if the duplicate role has a reifier */		
		IF dup.id_reifier IS NOT NULL THEN
			/* check if new entry has not a reifier */
			IF new.id_reifier IS NULL THEN
				new.id_reifier := dup.id_reifier;
			/* both constructs are reified */
			ELSE
				/* merge reifier */
				PERFORM merge_topics(new.id_reifier, dup.id_reifier);				
			END IF;
		END IF;
		/* move item-identifiers */
		UPDATE rel_item_identifiers SET id_construct = new.id WHERE id_construct = dup.id;
		/* delete duplicate */
		DELETE FROM roles WHERE id = dup.id;		
	END IF;
	/*return modified role*/	
	RETURN new;
END$$;


ALTER FUNCTION public.detect_duplicate_roles() OWNER TO postgres;

--
-- TOC entry 27 (class 1255 OID 15115779)
-- Dependencies: 411 6
-- Name: detect_duplicate_variants(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION detect_duplicate_variants() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
	dup RECORD;
BEGIN
	/* Select duplicate variant */
	SELECT INTO dup  * FROM variants WHERE id IN (SELECT id FROM variants WHERE value = new.value AND id_parent = new.id_parent AND id_scope = new.id_scope AND id_datatype = new.id_datatype AND id <> new.id);
	/* is there a duplicate name? */	
	IF FOUND THEN	
		/* check if the duplicate variant has a reifier */		
		IF dup.id_reifier IS NOT NULL THEN
			/* check if new entry has not a reifier */
			IF new.id_reifier IS NULL THEN
				new.id_reifier := dup.id_reifier;
			/* both constructs are reified */
			ELSE
				/* merge reifier */
				PERFORM merge_topics(new.id_reifier, dup.id_reifier);				
			END IF;
		END IF;
		/* move item-identifiers */
		UPDATE rel_item_identifiers SET id_construct = new.id WHERE id_construct = dup.id;
		/* delete duplicate */
		DELETE FROM variants WHERE id = dup.id;		
	END IF;
	/*return modified variant*/	
	RETURN new;
END$$;


ALTER FUNCTION public.detect_duplicate_variants() OWNER TO postgres;

--
-- TOC entry 28 (class 1255 OID 15115780)
-- Dependencies: 411 6
-- Name: history_ako(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_ako() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_subtype,'SUPERTYPE_ADDED', new.id_supertype, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_subtype,'SUPERTYPE_REMOVED', NULL,old.id_supertype);
		RETURN old;
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_ako() OWNER TO postgres;

--
-- TOC entry 22 (class 1255 OID 15115781)
-- Dependencies: 411 6
-- Name: history_associations(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_associations() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_parent,'ASSOCIATION_ADDED', new.id, NULL);				
		RETURN new;
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_parent,'ASSOCIATION_REMOVED', NULL,old.id);
		RETURN old;
	ELSE	
		IF old.id_scope <> new.id_scope THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'SCOPE_MODIFIED', new.id_scope,old.id_scope);
		ELSEIF old.id_type <> new.id_type THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'TYPE_MODIFIED', new.id_type,old.id_type);		
		ELSEIF old.id_reifier <> new.id_reifier THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'REIFIER_MODIFIED', new.id_reifier,old.id_reifier);		
		END IF;				
	RETURN new;
	END IF;
END;$$;


ALTER FUNCTION public.history_associations() OWNER TO postgres;

--
-- TOC entry 29 (class 1255 OID 15115782)
-- Dependencies: 6 411
-- Name: history_isa(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_isa() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN		
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_instance,'TYPE_ADDED', new.id_type, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_instance,'TYPE_REMOVED', NULL,old.id_type);
		RETURN old;
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_isa() OWNER TO postgres;

--
-- TOC entry 30 (class 1255 OID 15115783)
-- Dependencies: 411 6
-- Name: history_item_identifiers(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_item_identifiers() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN		
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_construct,'ITEM_IDENTIFIER_ADDED', new.id_locator, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_construct,'ITEM_IDENTIFIER_REMOVED', NULL,old.id_locator);
		RETURN old;
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_item_identifiers() OWNER TO postgres;

--
-- TOC entry 31 (class 1255 OID 15115784)
-- Dependencies: 6 411
-- Name: history_names(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_names() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN		
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_parent,'NAME_ADDED', new.id, NULL);				
		RETURN new;
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_parent,'CONSTRUCT_REMOVED', NULL,old.id);
		RETURN old;
	ELSE	
		IF old.id_scope <> new.id_scope THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'SCOPE_MODIFIED', new.id_scope,old.id_scope);
		ELSEIF old.value <> new.value THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'VALUE_MODIFIED', new.value,old.value);
		ELSEIF old.id_type <> new.id_type THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'TYPE_MODIFIED', new.id_type,old.id_type);		
		ELSEIF old.id_reifier <> new.id_reifier THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'REIFIER_MODIFIED', new.id_reifier,old.id_reifier);		
		END IF;				
	RETURN new;
	END IF;
END;$$;


ALTER FUNCTION public.history_names() OWNER TO postgres;

--
-- TOC entry 32 (class 1255 OID 15115785)
-- Dependencies: 6 411
-- Name: history_occurrences(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_occurrences() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_parent,'OCCURRENCE_ADDED', new.id, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_parent,'CONSTRUCT_REMOVED', NULL,old.id);
		RETURN old;
	ELSE	
		IF old.id_scope <> new.id_scope THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'SCOPE_MODIFIED', new.id_scope,old.id_scope);
		ELSEIF old.value <> new.value THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'VALUE_MODIFIED', new.value,old.value);
		ELSEIF old.id_type <> new.id_type THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'TYPE_MODIFIED', new.id_type,old.id_type);		
		ELSEIF old.id_reifier <> new.id_reifier THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'REIFIER_MODIFIED', new.id_reifier,old.id_reifier);		
		ELSEIF old.id_datatype <> new.id_datatype THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'DATATYPE_MODIFIED', new.id_datatype,old.id_datatype);		
		END IF;		
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_occurrences() OWNER TO postgres;

--
-- TOC entry 33 (class 1255 OID 15115786)
-- Dependencies: 411 6
-- Name: history_roles(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_roles() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_parent,'ROLE_ADDED', new.id, NULL);				
		RETURN new;
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_parent,'ROLE_REMOVED', NULL,old.id);
		RETURN old;
	ELSE	
		IF old.id_player <> new.id_player THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'PLAYER_MODIFIED', new.id_player,old.id_player);
		ELSEIF old.id_type <> new.id_type THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'TYPE_MODIFIED', new.id_type,old.id_type);		
		ELSEIF old.id_reifier <> new.id_reifier THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'REIFIER_MODIFIED', new.id_reifier,old.id_reifier);		
		END IF;				
	RETURN new;
	END IF;
END;$$;


ALTER FUNCTION public.history_roles() OWNER TO postgres;

--
-- TOC entry 34 (class 1255 OID 15115787)
-- Dependencies: 411 6
-- Name: history_subject_identifiers(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_subject_identifiers() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_topic,'SUBJECT_IDENTIFIER_ADDED', new.id_locator, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_topic,'SUBJECT_IDENTIFIER_REMOVED', NULL,old.id_locator);
		RETURN old;
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_subject_identifiers() OWNER TO postgres;

--
-- TOC entry 35 (class 1255 OID 15115788)
-- Dependencies: 411 6
-- Name: history_subject_locators(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_subject_locators() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_topic,'SUBJECT_LOCATOR_ADDED', new.id_locator, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_topic,'SUBJECT_LOCATOR_REMOVED', NULL,old.id_locator);
		RETURN old;
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_subject_locators() OWNER TO postgres;

--
-- TOC entry 36 (class 1255 OID 15115789)
-- Dependencies: 6 411
-- Name: history_topics(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_topics() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN	
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_parent,'TOPIC_ADDED', new.id, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_parent,'TOPIC_REMOVED', NULL,old.id);
		RETURN old;
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_topics() OWNER TO postgres;

--
-- TOC entry 37 (class 1255 OID 15115790)
-- Dependencies: 6 411
-- Name: history_variants(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION history_variants() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
BEGIN
	SELECT id INTO rec FROM revisions WHERE time = now();
	IF NOT FOUND THEN
		FOR rec IN INSERT INTO revisions(time) VALUES (now()) RETURNING id LOOP
		END LOOP;
	END IF;
	IF tg_op = 'INSERT' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id_parent,'VARIANT_ADDED', new.id, NULL);
	ELSEIF tg_op = 'DELETE' THEN
		INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), old.id_parent,'VARIANT_REMOVED', NULL,old.id);
		RETURN old;
	ELSE	
		IF old.id_scope <> new.id_scope THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'SCOPE_MODIFIED', new.id_scope,old.id_scope);
		ELSEIF old.value <> new.value THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'VALUE_MODIFIED', new.value,old.value);
		ELSEIF old.id_reifier <> new.id_reifier THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'REIFIER_MODIFIED', new.id_reifier,old.id_reifier);		
		ELSEIF old.id_datatype <> new.id_datatype THEN
			INSERT INTO changesets(id_revision, time, id_notifier, type,newvalue,oldvalue) VALUES (rec.id,now(), new.id,'DATATYPE_MODIFIED', new.id_datatype,old.id_datatype);		
		END IF;		
	END IF;
	RETURN new;
END;$$;


ALTER FUNCTION public.history_variants() OWNER TO postgres;

--
-- TOC entry 38 (class 1255 OID 15115791)
-- Dependencies: 6
-- Name: merge_topics(bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION merge_topics("topicId" bigint, "otherId" bigint) RETURNS void
    LANGUAGE sql
    AS $_$/* replace as parent */
UPDATE constructs SET id_parent = $1 WHERE id_parent = $2;
/* replace as type */
UPDATE typeables SET id_type = $1 WHERE id_type = $2;
/* replace as player */
UPDATE roles SET id_player = $1 WHERE id_player = $2;
/* replace as supertype */
UPDATE rel_kind_of SET id_supertype = $1 WHERE id_supertype = $2;
/* replace as subtype */
UPDATE rel_kind_of SET id_subtype = $1 WHERE id_subtype = $2;
/* replace as type */
UPDATE rel_instance_of SET id_type = $1 WHERE id_type = $2;
/* replace as instance */
UPDATE rel_instance_of SET id_instance = $1 WHERE id_instance = $2;
/* replace as theme */
UPDATE rel_themes SET id_theme = $1 WHERE id_theme = $2;
/* replace for subject-identifier */
UPDATE rel_subject_identifiers SET id_topic = $1 WHERE id_topic = $2;
/* replace for subject-locator */
UPDATE rel_subject_locators SET id_topic = $1 WHERE id_topic = $2;
/* replace for item-identifier */
UPDATE rel_item_identifiers SET id_construct = $1 WHERE id_construct = $2;
/* replace as reifier */
UPDATE reifiables SET id_reifier = $1 WHERE id_reifier = $2;
/* remove topic */
DELETE FROM topics WHERE id = $2;$_$;


ALTER FUNCTION public.merge_topics("topicId" bigint, "otherId" bigint) OWNER TO postgres;

--
-- TOC entry 39 (class 1255 OID 15115792)
-- Dependencies: 6 411
-- Name: move_role_reifiers(bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION move_role_reifiers(target bigint, source bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$DECLARE 
	rec RECORD;
	rRec RECORD;
BEGIN
	FOR rec IN SELECT id_type, id_player, id_reifier FROM roles WHERE id_parent = target LOOP
		SELECT INTO rRec id_reifier FROM roles WHERE id_parent = source AND id_type = rec.id_type AND id_player = rec.id_player;
			IF FOUND THEN
				/* check if the duplicate association has a reifier */		
				IF rRec.id_reifier IS NOT NULL THEN
					/* check if new entry has not a reifier */
					IF rec.id_reifier IS NULL THEN
						rec.id_reifier := rRec.id_reifier;
					/* both constructs are reified */
					ELSE
						/* merge reifier */
						PERFORM merge_topics(rec.id_reifier, rRec.id_reifier);				
					END IF;
				END IF;		
		END IF;
	END LOOP;
END;$$;


ALTER FUNCTION public.move_role_reifiers(target bigint, source bigint) OWNER TO postgres;

--
-- TOC entry 59 (class 1255 OID 16456854)
-- Dependencies: 411 6
-- Name: randomTopicMap(character varying, bigint, bigint, bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION "randomTopicMap"(baselocator character varying, "numberOfTypes" bigint, "numberOfInstances" bigint, "numberOfNames" bigint, "numberOfOccurrences" bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	iTypes bigint;	
	iInstances bigint;	
	iNames bigint;	
	iOccurrences bigint;	
	idType bigint;
	idScope bigint;
	idTopic bigint;
	idLocator bigint;
	idTopicMap bigint;
	identity character varying;
	valueString  character varying;
BEGIN	
	/*
	 * create locator
	 */
	INSERT INTO locators (reference) SELECT $1 WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = $1);
	/* select topic map */
	SELECT INTO rec t.id FROM topicmaps AS t , locators AS l WHERE id_base_locator = l.id AND l.reference = $1;
	IF  rec.id IS NULL THEN
		/* create topic map if not exists */		 
		FOR rec IN INSERT INTO topicmaps (id_base_locator) SELECT id FROM locators WHERE reference = $1 AND NOT EXISTS ( SELECT tm.id FROM topicmaps AS tm, locators AS l WHERE l.reference LIKE $1 AND l.id = tm.id_base_locator) RETURNING * LOOP
			idTopicMap := rec.id;
		END LOOP;
	ELSE
		idTopicMap := rec.id;
	END IF;

	/* create a scope */
	FOR rec IN INSERT INTO scopes(id_topicmap) VALUES (idTopicMap) RETURNING id LOOP
		idScope := rec.id;
	END LOOP;
	/* create xsd:string */
	INSERT INTO locators (reference) SELECT $1 WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = 'http://www.w3.org/2001/XMLSchema#string');
	/* iterate to create topic types */
	FOR iTypes IN 1..$2 LOOP
		identity := $1 || CAST ( '/type/' AS character varying) || iTypes;
		/* create topic */
		INSERT INTO locators (reference) SELECT identity WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = identity);
		FOR rec IN INSERT INTO topics(id_topicmap) VALUES (idTopicMap) RETURNING id LOOP
			idType := rec.id;
		END LOOP;		
		/* add subject-identifier */
		INSERT INTO rel_subject_identifiers (id_topic, id_locator) SELECT idType, id FROM locators WHERE reference = identity;
		/* create instances */
		FOR iInstances IN 1..$3 LOOP	
			identity := $1 || CAST ( '/type/' AS character varying) || iTypes || CAST ( '/' AS character varying) || iInstances;
			/* create topic */
			INSERT INTO locators (reference) SELECT identity WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = identity);
			FOR rec IN INSERT INTO topics(id_topicmap) VALUES (idTopicMap) RETURNING id LOOP
				idTopic := rec.id;
			END LOOP;
			/* add subject-identifier */
			INSERT INTO rel_subject_identifiers (id_topic, id_locator) SELECT idTopic, id FROM locators WHERE reference = identity;
			/* add type */
			INSERT INTO rel_instance_of (id_type, id_instance ) VALUES (idType, idTopic);	 		
			/* create names */
			FOR iNames IN 1..$4 LOOP
				valueString := iNames || CAST ( '. Name of ' AS character varying) || identity;
				INSERT INTO names(id_topicmap, id_parent, id_type, value, id_scope ) SELECT idTopicMap, idTopic, id, valueString, idScope FROM topics WHERE id_topicmap = idTopicMap ORDER BY random() OFFSET 0 LIMIT 1;
			END LOOP;			
			/* create occurrences */
			FOR iOccurrences IN 1..$5 LOOP
				valueString := iOccurrences || CAST ( '. Occurrence of ' AS character varying) || identity;
				INSERT INTO occurrences(id_topicmap, id_parent, id_type, value, id_scope, id_datatype ) SELECT idTopicMap, idTopic, topics.id, valueString, idScope, locators.id FROM topics, locators WHERE id_topicmap = idTopicMap AND reference = 'http://www.w3.org/2001/XMLSchema#string' ORDER BY random() OFFSET 0 LIMIT 1;
			END LOOP;
		END LOOP;
	END LOOP;
	RETURN idTopicMap;
END$_$;


ALTER FUNCTION public."randomTopicMap"(baselocator character varying, "numberOfTypes" bigint, "numberOfInstances" bigint, "numberOfNames" bigint, "numberOfOccurrences" bigint) OWNER TO postgres;

--
-- TOC entry 53 (class 1255 OID 16190219)
-- Dependencies: 6 411
-- Name: remove_duplicate_associations(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_associations("idTopicMap" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE	
	association RECORD;
	other RECORD;
	role RECORD;
	otherRole RECORD;
	deleted bigint[];
BEGIN	
	FOR association IN SELECT id, id_type, id_scope, id_reifier FROM associations WHERE id_topicmap = $1 LOOP
		IF ARRAY[association.id] <@ deleted  THEN
		  /*Nothing to do here*/
		ELSE
			FOR other IN SELECT id, id_type, id_scope, id_reifier FROM associations AS a WHERE id_parent = $1  AND id != association.id AND id_type = association.id_type AND id_scope = association.id_scope AND 0 IN ( SELECT COUNT (r) FROM ( SELECT id_type , id_player FROM roles WHERE id_parent = a.id_parent EXCEPT SELECT id_type, id_player FROM roles WHERE id_parent = association.id ) AS r ) LOOP
				/* check roles */
				FOR role IN SELECT id, id_type, id_player, id_reifier FROM roles WHERE id_parent = association.id LOOP
					FOR otherRole IN SELECT id, id_reifier FROM roles WHERE id_parent = other.id AND id_type = role.id_type AND id_player = role.id_player LOOP
						/* Move item-identifiers */
						UPDATE rel_item_identifiers SET id_construct = role.id WHERE id_construct = otherRole.id;
						/* check reification */
						IF role.id_reifier IS NULL AND otherRole.id_reifier IS NOT NULL THEN
							UPDATE roles SET id_reifier = otherRole.id_reifier WHERE id = role.id;
						ELSEIF role.id_reifier IS NOT NULL AND otherRole.id_reifier IS NOT NULL THEN
							PERFORM merge_topics(role.id_reifier, otherRole.id_reifier);
							UPDATE roles SET id_reifier = NULL WHERE id = otherRole.id;
						END IF;
					END LOOP;
				END LOOP;
				/* Move item-identifiers */
				UPDATE rel_item_identifiers SET id_construct = association.id WHERE id_construct = other.id;
				/* check reification */
				IF association.id_reifier IS NULL AND other.id_reifier IS NOT NULL THEN
					UPDATE associations SET id_reifier = other.id_reifier WHERE id = association.id;
				ELSEIF association.id_reifier IS NOT NULL AND other.id_reifier IS NOT NULL THEN
					PERFORM merge_topics(association.id_reifier, other.id_reifier);
					UPDATE associations SET id_reifier = NULL WHERE id = other.id;
				END IF;
				/* remove name */
				DELETE FROM associations WHERE id = other.id;
				deleted := deleted || other.id;
			END LOOP;
		END IF;
		PERFORM remove_duplicate_roles(association.id);
	END LOOP;
END$_$;


ALTER FUNCTION public.remove_duplicate_associations("idTopicMap" bigint) OWNER TO postgres;

--
-- TOC entry 50 (class 1255 OID 15115800)
-- Dependencies: 6 411
-- Name: remove_duplicate_names(bigint[], boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_names("typeIds" bigint[], "matchAll" boolean) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	rec3 RECORD;
	result bigint[];
	typeId bigint;
	first boolean;
BEGIN
	first := TRUE;
	FOR rec2 IN SELECT unnest($1) AS id LOOP
		SELECT transitive_supertypes(rec2.id) AS a INTO rec;
		IF FOUND THEN 
			/* is first iteration */
			IF first THEN					
				result := rec.a;
				first := FALSE;
			ELSE				
				/* should match all */
				IF $2 THEN
					/* is second but result is empty */
					IF rec.a IS NULL THEN
						RETURN NULL;
					END IF;
					/* result is not empty */					
					SELECT ARRAY ( SELECT unnest(rec.a) AS id INTERSECT SELECT unnest( result ) AS id ) AS a INTO rec3;
					IF FOUND THEN
						result := rec3.a; 
					ELSE
						RAISE EXCEPTION 'unexpected case!';
					END IF;
				ELSE
					result := result || rec.a;
				END IF;
			END IF;
		ELSEIF $2 THEN
			/* no condition satisfies match all */
			RETURN NULL;
		END IF;				
	END LOOP;
	RETURN result;
END;$_$;


ALTER FUNCTION public.remove_duplicate_names("typeIds" bigint[], "matchAll" boolean) OWNER TO postgres;

--
-- TOC entry 56 (class 1255 OID 16190208)
-- Dependencies: 411 6
-- Name: remove_duplicate_names(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_names("idTopic" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE	
	name RECORD;
	other RECORD;
BEGIN	
	FOR name IN SELECT id, id_type, value, id_scope, id_reifier FROM names WHERE id_parent = $1 LOOP
		FOR other IN SELECT id, id_type, value, id_scope, id_reifier FROM names WHERE id_parent = $1  AND id != name.id AND value = name.value AND id_type = name.id_type AND id_scope = name.id_scope AND name.id IN ( SELECT id FROM names WHERE id_parent = $1 ) LOOP
			/* Move variants */	
			UPDATE variants SET id_parent = name.id WHERE id_parent = other.id;
			/* Move item-identifiers */
			UPDATE rel_item_identifiers SET id_construct = name.id WHERE id_construct = other.id;
			/* check reification */
			IF name.id_reifier IS NULL AND other.id_reifier IS NOT NULL THEN
				UPDATE names SET id_reifier = other.id_reifier WHERE id = name.id;
			ELSEIF name.id_reifier IS NOT NULL AND other.id_reifier IS NOT NULL THEN
				PERFORM merge_topics(name.id_reifier, other.id_reifier);
				UPDATE names SET id_reifier = NULL WHERE id = other.id;
			END IF;
			/* remove name */
			DELETE FROM names WHERE id = other.id;
		END LOOP;	
		PERFORM remove_duplicate_variants(name.id);
	END LOOP;
END$_$;


ALTER FUNCTION public.remove_duplicate_names("idTopic" bigint) OWNER TO postgres;

--
-- TOC entry 51 (class 1255 OID 16190218)
-- Dependencies: 411 6
-- Name: remove_duplicate_occurrences(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_occurrences("idTopic" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	occurrence RECORD;
	other RECORD;
	deleted bigint[];
BEGIN	
	FOR occurrence IN SELECT id, id_type, value, id_datatype, id_scope, id_reifier FROM occurrences WHERE id_parent = $1 LOOP
		IF ARRAY[occurrence.id] <@ deleted  THEN
		  /*Nothing to do here*/
		ELSE
			FOR other IN SELECT id, id_type, value, id_datatype, id_scope, id_reifier FROM occurrences WHERE id_parent = $1  AND id != occurrence.id AND value = occurrence.value AND id_datatype = occurrence.id_datatype AND id_type = occurrence.id_type AND id_scope = occurrence.id_scope LOOP
				/* Move item-identifiers */
				UPDATE rel_item_identifiers SET id_construct = occurrence.id WHERE id_construct = other.id;
				/* check reification */
				IF occurrence.id_reifier IS NULL AND other.id_reifier IS NOT NULL THEN
					UPDATE occurrences SET id_reifier = other.id_reifier WHERE id = occurrence.id;
				ELSEIF occurrence.id_reifier IS NOT NULL AND other.id_reifier IS NOT NULL THEN
					PERFORM merge_topics(occurrence.id_reifier, other.id_reifier);
					UPDATE occurrences SET id_reifier = NULL WHERE id = other.id;	
				END IF;
				/* remove name */
				DELETE FROM occurrences WHERE id = other.id;
				deleted := deleted || other.id;
			END LOOP;
		END IF;
	END LOOP;
END$_$;


ALTER FUNCTION public.remove_duplicate_occurrences("idTopic" bigint) OWNER TO postgres;

--
-- TOC entry 54 (class 1255 OID 16456221)
-- Dependencies: 6 411
-- Name: remove_duplicate_played_associations(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_played_associations("idTopic" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE	
	association RECORD;
	other RECORD;
	role RECORD;
	otherRole RECORD;
	deleted bigint[];
BEGIN	
	FOR association IN SELECT id, id_type, id_scope, id_reifier, id_parent FROM associations WHERE id IN ( SELECT DISTINCT id_parent FROM roles WHERE id_player = $1 ) LOOP
		IF ARRAY[association.id] <@ deleted  THEN
		  /*Nothing to do here*/
		ELSE
			FOR other IN SELECT id, id_type, id_scope, id_reifier FROM associations AS a WHERE id_parent = association.id_parent  AND id != association.id AND id_type = association.id_type AND id_scope = association.id_scope AND 0 IN ( SELECT COUNT (r) FROM ( SELECT id_type , id_player FROM roles WHERE id_parent = a.id_parent EXCEPT SELECT id_type, id_player FROM roles WHERE id_parent = association.id ) AS r ) LOOP
				/* check roles */
				FOR role IN SELECT id, id_type, id_player, id_reifier FROM roles WHERE id_parent = association.id LOOP
					FOR otherRole IN SELECT id, id_reifier FROM roles WHERE id_parent = other.id AND id_type = role.id_type AND id_player = role.id_player LOOP
						/* Move item-identifiers */
						UPDATE rel_item_identifiers SET id_construct = role.id WHERE id_construct = otherRole.id;
						/* check reification */
						IF role.id_reifier IS NULL AND otherRole.id_reifier IS NOT NULL THEN
							UPDATE roles SET id_reifier = otherRole.id_reifier WHERE id = role.id;
						ELSEIF role.id_reifier IS NOT NULL AND otherRole.id_reifier IS NOT NULL THEN
							PERFORM merge_topics(role.id_reifier, otherRole.id_reifier);
							UPDATE roles SET id_reifier = NULL WHERE id = otherRole.id;
						END IF;
					END LOOP;
				END LOOP;
				/* Move item-identifiers */
				UPDATE rel_item_identifiers SET id_construct = association.id WHERE id_construct = other.id;
				/* check reification */
				IF association.id_reifier IS NULL AND other.id_reifier IS NOT NULL THEN
					UPDATE associations SET id_reifier = other.id_reifier WHERE id = association.id;
				ELSEIF association.id_reifier IS NOT NULL AND other.id_reifier IS NOT NULL THEN
					PERFORM merge_topics(association.id_reifier, other.id_reifier);
					UPDATE associations SET id_reifier = NULL WHERE id = other.id;
				END IF;
				/* remove name */
				DELETE FROM associations WHERE id = other.id;
				deleted := deleted || other.id;
			END LOOP;
		END IF;
		PERFORM remove_duplicate_roles(association.id);
	END LOOP;
END$_$;


ALTER FUNCTION public.remove_duplicate_played_associations("idTopic" bigint) OWNER TO postgres;

--
-- TOC entry 52 (class 1255 OID 16190221)
-- Dependencies: 411 6
-- Name: remove_duplicate_roles(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_roles("idAssociation" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE	
	role RECORD;
	other RECORD;
	deleted bigint[];
BEGIN	
	FOR role IN SELECT id, id_type, id_player, id_reifier FROM roles WHERE id_parent = $1 LOOP
		IF ARRAY[role.id] <@ deleted  THEN
		  /*Nothing to do here*/
		ELSE
			FOR other IN SELECT id, id_type, id_player, id_reifier FROM roles WHERE id_parent = $1  AND id != role.id AND id_type = role.id_type AND id_player = role.id_player LOOP
				/* Move item-identifiers */
				UPDATE rel_item_identifiers SET id_construct = role.id WHERE id_construct = other.id;
				/* check reification */
				IF role.id_reifier IS NULL AND other.id_reifier IS NOT NULL THEN
					UPDATE roles SET id_reifier = other.id_reifier WHERE id = role.id;
				ELSEIF role.id_reifier IS NOT NULL AND other.id_reifier IS NOT NULL THEN
					PERFORM merge_topics(role.id_reifier, other.id_reifier);
					UPDATE roles SET id_reifier = NULL WHERE id = other.id;
				END IF;
				/* remove role */
				DELETE FROM roles WHERE id = other.id;
				deleted := deleted || other.id;
			END LOOP;
		END IF;
	END LOOP;
END$_$;


ALTER FUNCTION public.remove_duplicate_roles("idAssociation" bigint) OWNER TO postgres;

--
-- TOC entry 58 (class 1255 OID 16456220)
-- Dependencies: 411 6
-- Name: remove_duplicate_topiccontent(bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_topiccontent("idTopicMap" bigint, "idTopic" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	topic RECORD;
BEGIN
	/* remove duplicate names */
	PERFORM remove_duplicate_names($2);				
	/* remove duplicate occurrences */
	PERFORM remove_duplicate_occurrences($2);	
	/* remove duplicate associations */
	PERFORM remove_duplicate_played_associations($2);
END$_$;


ALTER FUNCTION public.remove_duplicate_topiccontent("idTopicMap" bigint, "idTopic" bigint) OWNER TO postgres;

--
-- TOC entry 57 (class 1255 OID 16190217)
-- Dependencies: 6 411
-- Name: remove_duplicate_variants(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicate_variants("idName" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE	
	variant RECORD;
	other RECORD;
	deleted bigint[];
BEGIN	
	FOR variant IN SELECT id, id_datatype, value, id_scope, id_reifier FROM variants WHERE id_parent = $1 LOOP
		IF ARRAY[variant.id] <@ deleted  THEN
		  /*Nothing to do here*/
		ELSE
			FOR other IN SELECT id, id_datatype, value, id_scope, id_reifier FROM variants WHERE id_parent = $1  AND id != variant.id AND value = variant.value AND id_datatype = variant.id_datatype AND id_scope = variant.id_scope LOOP
				/* Move item-identifiers */
				UPDATE rel_item_identifiers SET id_construct = variant.id WHERE id_construct = other.id;
				/* check reification */
				IF variant.id_reifier IS NULL AND other.id_reifier IS NOT NULL THEN
					UPDATE variants SET id_reifier = other.id_reifier WHERE id = variant.id;
				ELSEIF variant.id_reifier IS NOT NULL AND other.id_reifier IS NOT NULL THEN
					PERFORM merge_topics(variant.id_reifier, other.id_reifier);
					UPDATE variants SET id_reifier = NULL WHERE id = other.id;	
				END IF;
				/* remove variant */
				DELETE FROM variants WHERE id = other.id;
				deleted := deleted || other.id;
			END LOOP;
		END IF;
	END LOOP;
END$_$;


ALTER FUNCTION public.remove_duplicate_variants("idName" bigint) OWNER TO postgres;

--
-- TOC entry 55 (class 1255 OID 16190222)
-- Dependencies: 6 411
-- Name: remove_duplicates(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION remove_duplicates("idTopicMap" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	topic RECORD;
BEGIN
	FOR topic IN SELECT id FROM topics WHERE id_topicmap = $1 ORDER BY id LOOP
		/* remove duplicate names */
		PERFORM remove_duplicate_names(topic.id);				
		/* remove duplicate occurrences */
		PERFORM remove_duplicate_occurrences(topic.id);
		RAISE LOG 'CHECK TOPIC with ID %', topic.id;
	END LOOP;
	/* remove duplicate associations */
	PERFORM remove_duplicate_associations($1);
END$_$;


ALTER FUNCTION public.remove_duplicates("idTopicMap" bigint) OWNER TO postgres;

--
-- TOC entry 40 (class 1255 OID 15115793)
-- Dependencies: 411 6
-- Name: scope_by_themes(bigint[], boolean, boolean, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION scope_by_themes("themeIds" bigint[], "matchAll" boolean, "exactMatch" boolean, "tmId" bigint) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	result bigint[];
BEGIN
	/* empty scope? */
	IF $1 IS NULL OR array_upper($1,1) IS NULL THEN
		SELECT ARRAY ( SELECT id FROM scopes WHERE id NOT IN ( SELECT DISTINCT id_scope FROM rel_themes ) AND id_topicmap = $4 ) AS a INTO rec2;
		IF FOUND THEN 
			result := rec2.a;
		END IF;
	/* not-empty scope but matching all */
	ELSEIF $2 THEN
		SELECT ARRAY ( SELECT DISTINCT id_scope FROM rel_themes AS r WHERE ( $3 AND ARRAY ( SELECT id_theme FROM rel_themes WHERE id_scope = r.id_scope ORDER BY id_theme ASC ) = $1 ) OR ( NOT $3 AND ARRAY ( SELECT id_theme FROM rel_themes WHERE id_scope = r.id_scope ORDER BY id_theme ASC ) @> $1 )) AS a INTO rec2;
		IF FOUND THEN
			result := result || rec2.a;
		END IF;
	/* non-empty scope but not matching all*/	
	ELSE
		/* loop over all themes */
		FOR rec IN SELECT unnest($1) AS id LOOP
			/* SELECT all scopes with the theme */
			SELECT ARRAY ( SELECT DISTINCT id_scope FROM rel_themes WHERE id_theme = rec.id ) AS a INTO rec2;
			IF FOUND THEN 
				result := result || rec2.a;
			END IF;
		END LOOP;		
	END IF;			
	RETURN result;
END;$_$;


ALTER FUNCTION public.scope_by_themes("themeIds" bigint[], "matchAll" boolean, "exactMatch" boolean, "tmId" bigint) OWNER TO postgres;

--
-- TOC entry 41 (class 1255 OID 15115794)
-- Dependencies: 411 6
-- Name: topics_by_type_transitive(bigint[], boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION topics_by_type_transitive("typeIds" bigint[], "matchAll" boolean) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE
	result bigint[];
	rec RECORD;
	rec2 RECORD;
	rec3 RECORD;
	first boolean;
BEGIN
	first := TRUE;
	FOR rec IN SELECT unnest($1) AS id LOOP
		FOR rec2 IN SELECT ARRAY(SELECT id_instance FROM rel_instance_of WHERE id_type IN (SELECT unnest(types_and_subtypes(rec.id)))) AS a LOOP
			IF first THEN
				result := rec2.a;
				first := FALSE;
			ELSEIF $2 THEN
				IF rec2.a IS NULL THEN
					RETURN NULL;
				END IF;
				SELECT ARRAY( SELECT unnest(result) AS id INTERSECT SELECT unnest(rec2.a) AS id ) AS a INTO rec3;
				result := rec3.a;
			ELSE
				result := result || rec2.a;
			END IF;
		END LOOP;
	END LOOP;
	SELECT ARRAY ( SELECT DISTINCT unnest(result)) AS a INTO rec;
	result := rec.a;
	RETURN result;
END;$_$;


ALTER FUNCTION public.topics_by_type_transitive("typeIds" bigint[], "matchAll" boolean) OWNER TO postgres;

--
-- TOC entry 42 (class 1255 OID 15115795)
-- Dependencies: 411 6
-- Name: transitive_subtypes(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transitive_subtypes("typeId" bigint) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	result bigint[];	
	knownTypes bigint[];
BEGIN		
	FOR rec IN SELECT id_subtype FROM rel_kind_of WHERE id_supertype = $1 LOOP		
		knownTypes := knownTypes || rec.id_subtype;
		result := result || rec.id_subtype;			
		FOR rec2 IN SELECT DISTINCT unnest(transitive_subtypes(rec.id_subtype,knownTypes)) AS a LOOP				
			IF NOT ( result @> ARRAY[rec2.a] ) THEN
				result := result || rec2.a;
			END IF;
		END LOOP;					
	END LOOP;	
	RETURN result;
END;$_$;


ALTER FUNCTION public.transitive_subtypes("typeId" bigint) OWNER TO postgres;

--
-- TOC entry 43 (class 1255 OID 15115796)
-- Dependencies: 411 6
-- Name: transitive_subtypes(bigint, bigint[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transitive_subtypes("topicId" bigint, "knownTypes" bigint[]) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	result bigint[];
	temp bigint[];
BEGIN	
	result := result || $1;
	temp := $2 || $1;
	FOR rec IN SELECT id_subtype FROM rel_kind_of WHERE id_supertype = $1 LOOP
		IF NOT ( temp @> ARRAY[rec.id_subtype] )  THEN			
			FOR rec2 IN SELECT DISTINCT unnest(transitive_subtypes(rec.id_subtype, temp)) AS a LOOP	
				IF NOT ( result @> ARRAY[rec2.a] ) THEN
					result := result || rec2.a;
					temp := temp || rec2.a;
				END IF;
			END LOOP;			
		END IF;
	END LOOP;	
	RETURN result;
END;$_$;


ALTER FUNCTION public.transitive_subtypes("topicId" bigint, "knownTypes" bigint[]) OWNER TO postgres;

--
-- TOC entry 44 (class 1255 OID 15115797)
-- Dependencies: 411 6
-- Name: transitive_subtypes(bigint[], boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transitive_subtypes("typeIds" bigint[], "matchAll" boolean) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	rec3 RECORD;
	result bigint[];
	typeId bigint;
	first boolean;
BEGIN
	first := TRUE;
	FOR rec2 IN SELECT unnest($1) AS id LOOP
		SELECT transitive_subtypes(rec2.id) AS a INTO rec;
		IF FOUND THEN 
			/* is first iteration */
			IF first THEN					
				result := rec.a;
				first := FALSE;
			ELSE				
				/* should match all */
				IF $2 THEN
					/* is second but result is empty */
					IF rec.a IS NULL THEN
						RETURN NULL;
					END IF;
					/* result is not empty */					
					SELECT ARRAY ( SELECT unnest(rec.a) AS id INTERSECT SELECT unnest( result ) AS id ) AS a INTO rec3;
					IF FOUND THEN
						result := rec3.a; 
					ELSE
						RAISE EXCEPTION 'unexpected case!';
					END IF;
				ELSE
					result := result || rec.a;
				END IF;
			END IF;
		ELSEIF $2 THEN
			/* no condition satisfies match all */
			RETURN NULL;
		END IF;				
	END LOOP;
	RETURN result;
END;$_$;


ALTER FUNCTION public.transitive_subtypes("typeIds" bigint[], "matchAll" boolean) OWNER TO postgres;

--
-- TOC entry 45 (class 1255 OID 15115798)
-- Dependencies: 411 6
-- Name: transitive_supertypes(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transitive_supertypes("typeId" bigint) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	result bigint[];
	knownTypes bigint[];
BEGIN	
	FOR rec IN SELECT id_supertype FROM rel_kind_of WHERE id_subtype = $1 LOOP	
		result := result || rec.id_supertype;
		knownTypes := knownTypes || rec.id_supertype;
		FOR rec2 IN SELECT DISTINCT unnest(transitive_supertypes(rec.id_supertype, knownTypes)) AS a LOOP			
			IF NOT ( result @> ARRAY[rec2.a] ) THEN
				result := result || rec2.a;
				knownTypes := knownTypes || rec2.a;
			END IF;
		END LOOP;					
	END LOOP;	
	RETURN result;
END;$_$;


ALTER FUNCTION public.transitive_supertypes("typeId" bigint) OWNER TO postgres;

--
-- TOC entry 46 (class 1255 OID 15115799)
-- Dependencies: 411 6
-- Name: transitive_supertypes(bigint, bigint[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transitive_supertypes("typeId" bigint, "knownTypes" bigint[]) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	result bigint[];
	temp bigint[];
BEGIN	
	result := result || $1;
	temp := $2 || $1;
	FOR rec IN SELECT id_supertype FROM rel_kind_of WHERE id_subtype = $1 LOOP
		IF NOT ( temp @> ARRAY[rec.id_supertype] )  THEN			
			FOR rec2 IN SELECT unnest(transitive_supertypes(rec.id_supertype, temp)) AS a LOOP			
				IF NOT ( result @> ARRAY[rec2.a] ) THEN
					result := result || rec2.a;
					temp := temp || rec2.a;
				END IF;
			END LOOP;			
		END IF;
	END LOOP;	
	RETURN result;
END;$_$;


ALTER FUNCTION public.transitive_supertypes("typeId" bigint, "knownTypes" bigint[]) OWNER TO postgres;

--
-- TOC entry 47 (class 1255 OID 15115801)
-- Dependencies: 6 411
-- Name: transitive_types(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transitive_types("topicId" bigint) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	rec RECORD;
	rec2 RECORD;
	result bigint[];
BEGIN
	FOR rec IN SELECT id_type FROM rel_instance_of WHERE id_instance = $1 LOOP
		result := result || rec.id_type;
		FOR rec2 IN SELECT unnest(transitive_supertypes(rec.id_type)) AS a LOOP
			IF NOT ( result @> ARRAY[rec2.a] ) THEN
				result := result || rec2.a;
			END IF;
		END LOOP;
	END LOOP;
	RETURN result;
END;$_$;


ALTER FUNCTION public.transitive_types("topicId" bigint) OWNER TO postgres;

--
-- TOC entry 48 (class 1255 OID 15115802)
-- Dependencies: 411 6
-- Name: types_and_subtypes(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION types_and_subtypes("typeId" bigint) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE
	result bigint[];
	rec RECORD;
BEGIN
	result := result || $1;
	SELECT transitive_subtypes($1) AS a INTO rec;
	IF FOUND THEN
		result := result || rec.a;
	END IF;
	RETURN result;
END;$_$;


ALTER FUNCTION public.types_and_subtypes("typeId" bigint) OWNER TO postgres;

--
-- TOC entry 49 (class 1255 OID 15115803)
-- Dependencies: 6 411
-- Name: types_and_subtypes(bigint[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION types_and_subtypes("typeIds" bigint[]) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $_$DECLARE 
	result bigint[];
	rec RECORD;
	rec2 RECORD;
BEGIN
	FOR rec IN SELECT unnest($1) AS id LOOP
		SELECT types_and_subtypes(rec.id) AS a INTO rec2;
		IF FOUND THEN
			result := result || rec2.a;
		END IF;
	END LOOP;
	return result;
END;

	$_$;


ALTER FUNCTION public.types_and_subtypes("typeIds" bigint[]) OWNER TO postgres;

SET search_path = pg_catalog;

--
-- TOC entry 1849 (class 2605 OID 15115804)
-- Dependencies: 21 21
-- Name: CAST (character varying AS timestamp with time zone); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (character varying AS timestamp with time zone) WITH FUNCTION public.cast_as_timestamp(character varying) AS ASSIGNMENT;


SET search_path = public, pg_catalog;

--
-- TOC entry 1598 (class 1259 OID 15115805)
-- Dependencies: 6
-- Name: seq_construct_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_construct_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.seq_construct_id OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = true;

--
-- TOC entry 1599 (class 1259 OID 15115807)
-- Dependencies: 1908 6
-- Name: constructs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE constructs (
    id bigint DEFAULT nextval('seq_construct_id'::regclass) NOT NULL,
    id_parent bigint,
    id_topicmap bigint
);


ALTER TABLE public.constructs OWNER TO postgres;

--
-- TOC entry 1600 (class 1259 OID 15115811)
-- Dependencies: 1909 1599 6
-- Name: reifiables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE reifiables (
    id_reifier bigint
)
INHERITS (constructs);


ALTER TABLE public.reifiables OWNER TO postgres;

--
-- TOC entry 1601 (class 1259 OID 15115815)
-- Dependencies: 1910 1911 6 1600
-- Name: scopeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopeables (
    id_scope bigint DEFAULT 0 NOT NULL
)
INHERITS (reifiables);


ALTER TABLE public.scopeables OWNER TO postgres;

--
-- TOC entry 1602 (class 1259 OID 15115820)
-- Dependencies: 1912 1599 6
-- Name: typeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE typeables (
    id_type bigint NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.typeables OWNER TO postgres;

--
-- TOC entry 1603 (class 1259 OID 15115824)
-- Dependencies: 1913 1914 6 1601 1602
-- Name: associations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE associations (
    id_type bigint
)
INHERITS (scopeables, typeables);


ALTER TABLE public.associations OWNER TO postgres;

--
-- TOC entry 1604 (class 1259 OID 15115829)
-- Dependencies: 6
-- Name: seq_changeset_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_changeset_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.seq_changeset_id OWNER TO postgres;

--
-- TOC entry 1605 (class 1259 OID 15115831)
-- Dependencies: 1915 6
-- Name: changesets; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE changesets (
    id bigint DEFAULT nextval('seq_changeset_id'::regclass) NOT NULL,
    id_revision bigint,
    id_notifier bigint NOT NULL,
    type character varying(128) NOT NULL,
    newvalue character varying(1024),
    oldvalue character varying(1024),
    "time" timestamp with time zone
);


ALTER TABLE public.changesets OWNER TO postgres;

--
-- TOC entry 1606 (class 1259 OID 15115838)
-- Dependencies: 1916 6 1599
-- Name: literals; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE literals (
    value character varying NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.literals OWNER TO postgres;

--
-- TOC entry 1607 (class 1259 OID 15115845)
-- Dependencies: 1917 1918 1601 6 1606
-- Name: datatypeawares; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE datatypeawares (
    value character varying,
    id_datatype bigint NOT NULL
)
INHERITS (scopeables, literals);


ALTER TABLE public.datatypeawares OWNER TO postgres;

--
-- TOC entry 1608 (class 1259 OID 15115853)
-- Dependencies: 6
-- Name: history; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE history (
    id bigint NOT NULL,
    id_topicmap bigint NOT NULL,
    id_revision bigint NOT NULL,
    id_parent bigint NOT NULL,
    names bigint[],
    occurrences bigint[],
    variants bigint[],
    associations bigint[],
    id_scope bigint,
    id_reification bigint,
    id_player bigint,
    types bigint[],
    supertypes bigint[],
    value character varying,
    type character varying(1),
    themes bigint[],
    itemidentifiers character varying[] NOT NULL,
    subjectidentifiers character varying[],
    subjectlocators character varying[],
    datatype character varying,
    roles bigint[],
    bestlabel character varying(256)
);


ALTER TABLE public.history OWNER TO postgres;

--
-- TOC entry 1609 (class 1259 OID 15115859)
-- Dependencies: 6
-- Name: seq_locator_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_locator_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.seq_locator_id OWNER TO postgres;

--
-- TOC entry 1610 (class 1259 OID 15115861)
-- Dependencies: 1919 6
-- Name: locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE locators (
    id bigint DEFAULT nextval('seq_locator_id'::regclass) NOT NULL,
    reference character varying(1024) NOT NULL
);


ALTER TABLE public.locators OWNER TO postgres;

--
-- TOC entry 1611 (class 1259 OID 15115868)
-- Dependencies: 6
-- Name: metadata; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE metadata (
    id_revision bigint NOT NULL,
    key character varying NOT NULL,
    value character varying
);


ALTER TABLE public.metadata OWNER TO postgres;

--
-- TOC entry 1612 (class 1259 OID 15115874)
-- Dependencies: 1920 1921 1602 1601 1606 6
-- Name: names; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE names (
    id_type bigint,
    value character varying
)
INHERITS (scopeables, typeables, literals);


ALTER TABLE public.names OWNER TO postgres;

--
-- TOC entry 1613 (class 1259 OID 15115882)
-- Dependencies: 1922 1923 1607 6 1602
-- Name: occurrences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE occurrences (
    id_type bigint
)
INHERITS (datatypeawares, typeables);


ALTER TABLE public.occurrences OWNER TO postgres;

--
-- TOC entry 1614 (class 1259 OID 15115890)
-- Dependencies: 6
-- Name: rel_instance_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_instance_of (
    id_instance bigint NOT NULL,
    id_type bigint NOT NULL
);


ALTER TABLE public.rel_instance_of OWNER TO postgres;

--
-- TOC entry 1615 (class 1259 OID 15115893)
-- Dependencies: 6
-- Name: rel_item_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_item_identifiers (
    id_construct bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_item_identifiers OWNER TO postgres;

--
-- TOC entry 1616 (class 1259 OID 15115896)
-- Dependencies: 6
-- Name: rel_kind_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_kind_of (
    id_subtype bigint,
    id_supertype bigint
);


ALTER TABLE public.rel_kind_of OWNER TO postgres;

--
-- TOC entry 1617 (class 1259 OID 15115899)
-- Dependencies: 6
-- Name: rel_subject_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_identifiers (
    id_topic bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_identifiers OWNER TO postgres;

--
-- TOC entry 1618 (class 1259 OID 15115902)
-- Dependencies: 6
-- Name: rel_subject_locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_locators (
    id_topic bigint,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_locators OWNER TO postgres;

--
-- TOC entry 1619 (class 1259 OID 15115905)
-- Dependencies: 6
-- Name: rel_themes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_themes (
    id_scope bigint,
    id_theme bigint NOT NULL
);


ALTER TABLE public.rel_themes OWNER TO postgres;

--
-- TOC entry 1620 (class 1259 OID 15115908)
-- Dependencies: 6
-- Name: seq_revision_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_revision_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.seq_revision_id OWNER TO postgres;

--
-- TOC entry 1621 (class 1259 OID 15115910)
-- Dependencies: 1924 6
-- Name: revisions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE revisions (
    id bigint DEFAULT nextval('seq_revision_id'::regclass) NOT NULL,
    "time" timestamp with time zone NOT NULL,
    id_topicmap bigint NOT NULL,
    type character varying(128) NOT NULL
);


ALTER TABLE public.revisions OWNER TO postgres;

--
-- TOC entry 1622 (class 1259 OID 15115914)
-- Dependencies: 1925 6 1600 1602
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE roles (
    id_type bigint,
    id_player bigint NOT NULL
)
INHERITS (reifiables, typeables);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 1623 (class 1259 OID 15115918)
-- Dependencies: 6
-- Name: seq_scope_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_scope_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.seq_scope_id OWNER TO postgres;

--
-- TOC entry 1624 (class 1259 OID 15115920)
-- Dependencies: 1926 6
-- Name: scopes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopes (
    id bigint DEFAULT nextval('seq_scope_id'::regclass) NOT NULL,
    id_topicmap bigint
);


ALTER TABLE public.scopes OWNER TO postgres;

--
-- TOC entry 1625 (class 1259 OID 15115924)
-- Dependencies: 6
-- Name: seq_topicmap_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_topicmap_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.seq_topicmap_id OWNER TO postgres;

--
-- TOC entry 1626 (class 1259 OID 15115926)
-- Dependencies: 6
-- Name: tags; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tags (
    tag character varying NOT NULL,
    "time" timestamp with time zone NOT NULL
);


ALTER TABLE public.tags OWNER TO postgres;

--
-- TOC entry 1627 (class 1259 OID 15115932)
-- Dependencies: 1927 1600 6
-- Name: topicmaps; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topicmaps (
    id bigint DEFAULT nextval('seq_topicmap_id'::regclass),
    id_reifier bigint,
    id_base_locator bigint NOT NULL
)
INHERITS (reifiables);


ALTER TABLE public.topicmaps OWNER TO postgres;

--
-- TOC entry 1628 (class 1259 OID 15115936)
-- Dependencies: 1928 6 1599
-- Name: topics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topics (
)
INHERITS (constructs);


ALTER TABLE public.topics OWNER TO postgres;

--
-- TOC entry 1629 (class 1259 OID 15115940)
-- Dependencies: 1929 1930 6 1607
-- Name: variants; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE variants (
)
INHERITS (datatypeawares);


ALTER TABLE public.variants OWNER TO postgres;

--
-- TOC entry 1938 (class 2606 OID 15115949)
-- Dependencies: 1603 1603
-- Name: pk_associations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT pk_associations PRIMARY KEY (id);


--
-- TOC entry 1940 (class 2606 OID 15115951)
-- Dependencies: 1605 1605
-- Name: pk_changeset; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT pk_changeset PRIMARY KEY (id);


--
-- TOC entry 1932 (class 2606 OID 15115953)
-- Dependencies: 1599 1599
-- Name: pk_constructs; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT pk_constructs PRIMARY KEY (id);


--
-- TOC entry 1942 (class 2606 OID 15115955)
-- Dependencies: 1607 1607
-- Name: pk_datatypeawares; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT pk_datatypeawares PRIMARY KEY (id);


--
-- TOC entry 1944 (class 2606 OID 15115957)
-- Dependencies: 1608 1608 1608 1608
-- Name: pk_history; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY history
    ADD CONSTRAINT pk_history PRIMARY KEY (id, id_revision, id_topicmap);


--
-- TOC entry 1946 (class 2606 OID 15115959)
-- Dependencies: 1610 1610
-- Name: pk_locators; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT pk_locators PRIMARY KEY (id);


--
-- TOC entry 1950 (class 2606 OID 15115961)
-- Dependencies: 1611 1611 1611
-- Name: pk_metadata; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT pk_metadata PRIMARY KEY (id_revision, key);


--
-- TOC entry 1952 (class 2606 OID 15115963)
-- Dependencies: 1612 1612
-- Name: pk_names; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY names
    ADD CONSTRAINT pk_names PRIMARY KEY (id);


--
-- TOC entry 1954 (class 2606 OID 15115965)
-- Dependencies: 1613 1613
-- Name: pk_occurrences; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT pk_occurrences PRIMARY KEY (id);


--
-- TOC entry 1934 (class 2606 OID 15115967)
-- Dependencies: 1600 1600
-- Name: pk_reifiables; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT pk_reifiables PRIMARY KEY (id);


--
-- TOC entry 1956 (class 2606 OID 15115969)
-- Dependencies: 1621 1621
-- Name: pk_revisions; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT pk_revisions PRIMARY KEY (id);


--
-- TOC entry 1958 (class 2606 OID 15115971)
-- Dependencies: 1622 1622
-- Name: pk_roles; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);


--
-- TOC entry 1960 (class 2606 OID 15115973)
-- Dependencies: 1624 1624
-- Name: pk_scope; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT pk_scope PRIMARY KEY (id);


--
-- TOC entry 1936 (class 2606 OID 15115975)
-- Dependencies: 1601 1601
-- Name: pk_scopes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT pk_scopes PRIMARY KEY (id);


--
-- TOC entry 1962 (class 2606 OID 15115977)
-- Dependencies: 1626 1626
-- Name: pk_tags; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tags
    ADD CONSTRAINT pk_tags PRIMARY KEY (tag);


--
-- TOC entry 1964 (class 2606 OID 15115979)
-- Dependencies: 1627 1627
-- Name: pk_topicmap; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT pk_topicmap PRIMARY KEY (id);


--
-- TOC entry 1966 (class 2606 OID 15115981)
-- Dependencies: 1628 1628
-- Name: pk_topics; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT pk_topics PRIMARY KEY (id);


--
-- TOC entry 1968 (class 2606 OID 15115983)
-- Dependencies: 1629 1629
-- Name: pk_variants; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT pk_variants PRIMARY KEY (id);


--
-- TOC entry 1948 (class 2606 OID 15115985)
-- Dependencies: 1610 1610
-- Name: unique_reference; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT unique_reference UNIQUE (reference);


--
-- TOC entry 1995 (class 2606 OID 15115990)
-- Dependencies: 1945 1627 1610
-- Name: fk_baselocator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_baselocator FOREIGN KEY (id_base_locator) REFERENCES locators(id);


--
-- TOC entry 1975 (class 2606 OID 15115995)
-- Dependencies: 1607 1610 1945
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id) ON DELETE RESTRICT;


--
-- TOC entry 1981 (class 2606 OID 15116000)
-- Dependencies: 1965 1614 1628
-- Name: fk_instance; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_instance FOREIGN KEY (id_instance) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1983 (class 2606 OID 15116005)
-- Dependencies: 1945 1615 1610
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1986 (class 2606 OID 15116010)
-- Dependencies: 1945 1610 1617
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1988 (class 2606 OID 15116015)
-- Dependencies: 1618 1945 1610
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1973 (class 2606 OID 15116020)
-- Dependencies: 1603 1963 1627
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1979 (class 2606 OID 15116025)
-- Dependencies: 1628 1965 1612
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1993 (class 2606 OID 15116030)
-- Dependencies: 1937 1622 1603
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES associations(id) ON DELETE CASCADE;


--
-- TOC entry 1997 (class 2606 OID 15116035)
-- Dependencies: 1628 1963 1627
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1998 (class 2606 OID 15116040)
-- Dependencies: 1629 1951 1612
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES names(id) ON DELETE CASCADE;


--
-- TOC entry 1980 (class 2606 OID 15116045)
-- Dependencies: 1628 1965 1613
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1996 (class 2606 OID 15116050)
-- Dependencies: 1627 1965 1628
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1970 (class 2606 OID 15116055)
-- Dependencies: 1628 1965 1600
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id) ON DELETE SET NULL;


--
-- TOC entry 1976 (class 2606 OID 15116060)
-- Dependencies: 1621 1608 1955
-- Name: fk_reivision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_reivision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1974 (class 2606 OID 15116065)
-- Dependencies: 1955 1621 1605
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1978 (class 2606 OID 15116070)
-- Dependencies: 1611 1621 1955
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1990 (class 2606 OID 15116075)
-- Dependencies: 1619 1624 1959
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1971 (class 2606 OID 15116080)
-- Dependencies: 1601 1624 1959
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1984 (class 2606 OID 15116085)
-- Dependencies: 1628 1965 1616
-- Name: fk_subtype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_subtype FOREIGN KEY (id_subtype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1985 (class 2606 OID 15116090)
-- Dependencies: 1965 1616 1628
-- Name: fk_supertype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_supertype FOREIGN KEY (id_supertype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1991 (class 2606 OID 15116095)
-- Dependencies: 1965 1628 1619
-- Name: fk_theme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_theme FOREIGN KEY (id_theme) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1987 (class 2606 OID 15116100)
-- Dependencies: 1965 1628 1617
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1989 (class 2606 OID 15116105)
-- Dependencies: 1965 1628 1618
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1969 (class 2606 OID 15116110)
-- Dependencies: 1627 1599 1963
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1994 (class 2606 OID 15116115)
-- Dependencies: 1624 1963 1627
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1977 (class 2606 OID 15116120)
-- Dependencies: 1608 1627 1963
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1992 (class 2606 OID 15116125)
-- Dependencies: 1963 1627 1621
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1982 (class 2606 OID 15116130)
-- Dependencies: 1965 1614 1628
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1972 (class 2606 OID 15116135)
-- Dependencies: 1628 1965 1602
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typeables
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2003 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2011-01-07 09:44:05

--
-- PostgreSQL database dump complete
--


--
-- PostgreSQL database dump
--

-- Started on 2010-09-08 08:58:34

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 401 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 49 (class 1255 OID 6732807)
-- Dependencies: 6 401
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
			SELECT value FROM names WHERE id IN ( SELECT unnest(rec.a)) ORDER BY value OFFSET 0 LIMIT 1 INTO rec2;
			RETURN rec2.value;
		END IF;
		/* No name item with unconstrained scope -> order name items by number of themes */
		FOR rec IN SELECT n.id_scope, COUNT(id_theme) AS c FROM names AS n, rel_themes AS r WHERE r.id_scope = n.id_scope AND id_parent = $2 GROUP BY n.id_scope ORDER BY c LOOP
			/*Get scoped names*/
			SELECT ARRAY(SELECT id FROM names WHERE id_scope = rec.id_scope AND id_parent = $2 INTERSECT SELECT unnest(ids) AS id ) AS a INTO rec2;
			/* there is only one scope name*/			
			IF array_upper(rec2.a,1) = 1 THEN
				/* get name values */
				SELECT value FROM names WHERE id IN ( SELECT unnest(rec2.a)) INTO rec2;
				RETURN rec2.value;
			/* there are more than one scope name*/
			ELSEIF array_upper(rec2.a,1) > 1 THEN
				/* get name values */
				SELECT value FROM names WHERE id IN ( SELECT unnest(rec2.a)) ORDER BY value OFFSET 0 LIMIT 1 INTO rec2;
				RETURN rec2.value;
			END IF;
		/*END LOOP of scoped names*/
		END LOOP;
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
-- TOC entry 19 (class 1255 OID 4801024)
-- Dependencies: 401 6
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
-- TOC entry 20 (class 1255 OID 4801025)
-- Dependencies: 401 6
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
-- TOC entry 21 (class 1255 OID 4801026)
-- Dependencies: 6 401
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
-- TOC entry 22 (class 1255 OID 4801027)
-- Dependencies: 6 401
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
-- TOC entry 23 (class 1255 OID 4801028)
-- Dependencies: 6 401
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
-- TOC entry 24 (class 1255 OID 4801029)
-- Dependencies: 401 6
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
-- TOC entry 25 (class 1255 OID 4801030)
-- Dependencies: 401 6
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
-- TOC entry 26 (class 1255 OID 4801031)
-- Dependencies: 401 6
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
-- TOC entry 27 (class 1255 OID 4801032)
-- Dependencies: 401 6
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
-- TOC entry 28 (class 1255 OID 4801033)
-- Dependencies: 401 6
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
-- TOC entry 29 (class 1255 OID 4801034)
-- Dependencies: 401 6
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
-- TOC entry 30 (class 1255 OID 4801035)
-- Dependencies: 401 6
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
-- TOC entry 31 (class 1255 OID 4801036)
-- Dependencies: 6 401
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
-- TOC entry 32 (class 1255 OID 4801037)
-- Dependencies: 6 401
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
-- TOC entry 33 (class 1255 OID 4801038)
-- Dependencies: 6 401
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
-- TOC entry 34 (class 1255 OID 4801039)
-- Dependencies: 6 401
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
-- TOC entry 35 (class 1255 OID 4801040)
-- Dependencies: 401 6
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
-- TOC entry 37 (class 1255 OID 4801041)
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
-- TOC entry 38 (class 1255 OID 4801042)
-- Dependencies: 401 6
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
-- TOC entry 39 (class 1255 OID 4801043)
-- Dependencies: 6 401
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
-- TOC entry 40 (class 1255 OID 4801044)
-- Dependencies: 6 401
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
-- TOC entry 41 (class 1255 OID 4801045)
-- Dependencies: 6 401
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
-- TOC entry 42 (class 1255 OID 4801046)
-- Dependencies: 6 401
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
-- TOC entry 43 (class 1255 OID 4801047)
-- Dependencies: 6 401
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
-- TOC entry 44 (class 1255 OID 4801048)
-- Dependencies: 6 401
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
-- TOC entry 36 (class 1255 OID 4801049)
-- Dependencies: 6 401
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
-- TOC entry 45 (class 1255 OID 4801050)
-- Dependencies: 401 6
-- Name: transitive_supertypes(bigint[], boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION transitive_supertypes("typeIds" bigint[], "matchAll" boolean) RETURNS bigint[]
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


ALTER FUNCTION public.transitive_supertypes("typeIds" bigint[], "matchAll" boolean) OWNER TO postgres;

--
-- TOC entry 46 (class 1255 OID 4801051)
-- Dependencies: 6 401
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
-- TOC entry 47 (class 1255 OID 4801052)
-- Dependencies: 401 6
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
-- TOC entry 48 (class 1255 OID 4801053)
-- Dependencies: 6 401
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
-- TOC entry 1839 (class 2605 OID 4801054)
-- Dependencies: 19 19
-- Name: CAST (character varying AS timestamp with time zone); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (character varying AS timestamp with time zone) WITH FUNCTION public.cast_as_timestamp(character varying) AS ASSIGNMENT;


SET search_path = public, pg_catalog;

--
-- TOC entry 1588 (class 1259 OID 4801055)
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
-- TOC entry 1589 (class 1259 OID 4801057)
-- Dependencies: 1898 6
-- Name: constructs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE constructs (
    id bigint DEFAULT nextval('seq_construct_id'::regclass) NOT NULL,
    id_parent bigint,
    id_topicmap bigint
);


ALTER TABLE public.constructs OWNER TO postgres;

--
-- TOC entry 1590 (class 1259 OID 4801061)
-- Dependencies: 1899 6 1589
-- Name: reifiables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE reifiables (
    id_reifier bigint
)
INHERITS (constructs);


ALTER TABLE public.reifiables OWNER TO postgres;

--
-- TOC entry 1591 (class 1259 OID 4801065)
-- Dependencies: 1900 1901 6 1590
-- Name: scopeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopeables (
    id_scope bigint DEFAULT 0 NOT NULL
)
INHERITS (reifiables);


ALTER TABLE public.scopeables OWNER TO postgres;

--
-- TOC entry 1592 (class 1259 OID 4801070)
-- Dependencies: 1902 6 1589
-- Name: typeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE typeables (
    id_type bigint NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.typeables OWNER TO postgres;

--
-- TOC entry 1593 (class 1259 OID 4801074)
-- Dependencies: 1903 1904 1592 6 1591
-- Name: associations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE associations (
    id_type bigint
)
INHERITS (scopeables, typeables);


ALTER TABLE public.associations OWNER TO postgres;

--
-- TOC entry 1594 (class 1259 OID 4801079)
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
-- TOC entry 1595 (class 1259 OID 4801081)
-- Dependencies: 1905 6
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
-- TOC entry 1596 (class 1259 OID 4801088)
-- Dependencies: 1906 1589 6
-- Name: literals; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE literals (
    value character varying NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.literals OWNER TO postgres;

--
-- TOC entry 1597 (class 1259 OID 4801095)
-- Dependencies: 1907 1908 1596 6 1591
-- Name: datatypeawares; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE datatypeawares (
    value character varying,
    id_datatype bigint NOT NULL
)
INHERITS (scopeables, literals);


ALTER TABLE public.datatypeawares OWNER TO postgres;

--
-- TOC entry 1598 (class 1259 OID 4801103)
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
-- TOC entry 1599 (class 1259 OID 4801109)
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
-- TOC entry 1600 (class 1259 OID 4801111)
-- Dependencies: 1909 6
-- Name: locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE locators (
    id bigint DEFAULT nextval('seq_locator_id'::regclass) NOT NULL,
    reference character varying(1024) NOT NULL
);


ALTER TABLE public.locators OWNER TO postgres;

--
-- TOC entry 1601 (class 1259 OID 4801118)
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
-- TOC entry 1602 (class 1259 OID 4801124)
-- Dependencies: 1910 1911 1596 1591 6 1592
-- Name: names; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE names (
    id_type bigint,
    value character varying
)
INHERITS (scopeables, typeables, literals);


ALTER TABLE public.names OWNER TO postgres;

--
-- TOC entry 1603 (class 1259 OID 4801132)
-- Dependencies: 1912 1913 1592 1597 6
-- Name: occurrences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE occurrences (
    id_type bigint
)
INHERITS (datatypeawares, typeables);


ALTER TABLE public.occurrences OWNER TO postgres;

--
-- TOC entry 1604 (class 1259 OID 4801140)
-- Dependencies: 6
-- Name: rel_instance_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_instance_of (
    id_instance bigint NOT NULL,
    id_type bigint NOT NULL
);


ALTER TABLE public.rel_instance_of OWNER TO postgres;

--
-- TOC entry 1605 (class 1259 OID 4801143)
-- Dependencies: 6
-- Name: rel_item_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_item_identifiers (
    id_construct bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_item_identifiers OWNER TO postgres;

--
-- TOC entry 1606 (class 1259 OID 4801146)
-- Dependencies: 6
-- Name: rel_kind_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_kind_of (
    id_subtype bigint,
    id_supertype bigint
);


ALTER TABLE public.rel_kind_of OWNER TO postgres;

--
-- TOC entry 1607 (class 1259 OID 4801149)
-- Dependencies: 6
-- Name: rel_subject_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_identifiers (
    id_topic bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_identifiers OWNER TO postgres;

--
-- TOC entry 1608 (class 1259 OID 4801152)
-- Dependencies: 6
-- Name: rel_subject_locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_locators (
    id_topic bigint,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_locators OWNER TO postgres;

--
-- TOC entry 1609 (class 1259 OID 4801155)
-- Dependencies: 6
-- Name: rel_themes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_themes (
    id_scope bigint,
    id_theme bigint NOT NULL
);


ALTER TABLE public.rel_themes OWNER TO postgres;

--
-- TOC entry 1610 (class 1259 OID 4801158)
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
-- TOC entry 1611 (class 1259 OID 4801160)
-- Dependencies: 1914 6
-- Name: revisions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE revisions (
    id bigint DEFAULT nextval('seq_revision_id'::regclass) NOT NULL,
    "time" timestamp with time zone NOT NULL,
    id_topicmap bigint NOT NULL
);


ALTER TABLE public.revisions OWNER TO postgres;

--
-- TOC entry 1612 (class 1259 OID 4801164)
-- Dependencies: 1915 1592 1590 6
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE roles (
    id_type bigint,
    id_player bigint NOT NULL
)
INHERITS (reifiables, typeables);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 1613 (class 1259 OID 4801168)
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
-- TOC entry 1614 (class 1259 OID 4801170)
-- Dependencies: 1916 6
-- Name: scopes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopes (
    id bigint DEFAULT nextval('seq_scope_id'::regclass) NOT NULL,
    id_topicmap bigint
);


ALTER TABLE public.scopes OWNER TO postgres;

--
-- TOC entry 1615 (class 1259 OID 4801174)
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
-- TOC entry 1616 (class 1259 OID 4801176)
-- Dependencies: 6
-- Name: tags; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tags (
    tag character varying NOT NULL,
    "time" timestamp with time zone NOT NULL
);


ALTER TABLE public.tags OWNER TO postgres;

--
-- TOC entry 1617 (class 1259 OID 4801182)
-- Dependencies: 1917 1590 6
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
-- TOC entry 1618 (class 1259 OID 4801186)
-- Dependencies: 1918 1589 6
-- Name: topics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topics (
)
INHERITS (constructs);


ALTER TABLE public.topics OWNER TO postgres;

--
-- TOC entry 1619 (class 1259 OID 4801190)
-- Dependencies: 1919 1920 1597 6
-- Name: variants; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE variants (
)
INHERITS (datatypeawares);


ALTER TABLE public.variants OWNER TO postgres;

--
-- TOC entry 1928 (class 2606 OID 4801199)
-- Dependencies: 1593 1593
-- Name: pk_associations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT pk_associations PRIMARY KEY (id);


--
-- TOC entry 1930 (class 2606 OID 4801201)
-- Dependencies: 1595 1595
-- Name: pk_changeset; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT pk_changeset PRIMARY KEY (id);


--
-- TOC entry 1922 (class 2606 OID 4801203)
-- Dependencies: 1589 1589
-- Name: pk_constructs; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT pk_constructs PRIMARY KEY (id);


--
-- TOC entry 1932 (class 2606 OID 4801205)
-- Dependencies: 1597 1597
-- Name: pk_datatypeawares; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT pk_datatypeawares PRIMARY KEY (id);


--
-- TOC entry 1934 (class 2606 OID 4801207)
-- Dependencies: 1598 1598 1598 1598
-- Name: pk_history; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY history
    ADD CONSTRAINT pk_history PRIMARY KEY (id, id_revision, id_topicmap);


--
-- TOC entry 1936 (class 2606 OID 4801209)
-- Dependencies: 1600 1600
-- Name: pk_locators; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT pk_locators PRIMARY KEY (id);


--
-- TOC entry 1940 (class 2606 OID 4801211)
-- Dependencies: 1601 1601 1601
-- Name: pk_metadata; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT pk_metadata PRIMARY KEY (id_revision, key);


--
-- TOC entry 1942 (class 2606 OID 4801213)
-- Dependencies: 1602 1602
-- Name: pk_names; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY names
    ADD CONSTRAINT pk_names PRIMARY KEY (id);


--
-- TOC entry 1944 (class 2606 OID 4801215)
-- Dependencies: 1603 1603
-- Name: pk_occurrences; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT pk_occurrences PRIMARY KEY (id);


--
-- TOC entry 1924 (class 2606 OID 4801217)
-- Dependencies: 1590 1590
-- Name: pk_reifiables; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT pk_reifiables PRIMARY KEY (id);


--
-- TOC entry 1946 (class 2606 OID 4801219)
-- Dependencies: 1611 1611
-- Name: pk_revisions; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT pk_revisions PRIMARY KEY (id);


--
-- TOC entry 1948 (class 2606 OID 4801221)
-- Dependencies: 1612 1612
-- Name: pk_roles; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);


--
-- TOC entry 1950 (class 2606 OID 4801223)
-- Dependencies: 1614 1614
-- Name: pk_scope; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT pk_scope PRIMARY KEY (id);


--
-- TOC entry 1926 (class 2606 OID 4801225)
-- Dependencies: 1591 1591
-- Name: pk_scopes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT pk_scopes PRIMARY KEY (id);


--
-- TOC entry 1952 (class 2606 OID 4801227)
-- Dependencies: 1616 1616
-- Name: pk_tags; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tags
    ADD CONSTRAINT pk_tags PRIMARY KEY (tag);


--
-- TOC entry 1954 (class 2606 OID 4801229)
-- Dependencies: 1617 1617
-- Name: pk_topicmap; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT pk_topicmap PRIMARY KEY (id);


--
-- TOC entry 1956 (class 2606 OID 4801231)
-- Dependencies: 1618 1618
-- Name: pk_topics; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT pk_topics PRIMARY KEY (id);


--
-- TOC entry 1958 (class 2606 OID 4801233)
-- Dependencies: 1619 1619
-- Name: pk_variants; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT pk_variants PRIMARY KEY (id);


--
-- TOC entry 1938 (class 2606 OID 4801235)
-- Dependencies: 1600 1600
-- Name: unique_reference; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT unique_reference UNIQUE (reference);


--
-- TOC entry 1991 (class 2620 OID 4801236)
-- Dependencies: 20 1612
-- Name: trigger_detect_duplicate_associations; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_associations
    AFTER UPDATE ON roles
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_associations();


--
-- TOC entry 1989 (class 2620 OID 4801237)
-- Dependencies: 1602 21
-- Name: trigger_detect_duplicate_names_on_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_names_on_update
    BEFORE UPDATE ON names
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_names();


--
-- TOC entry 1990 (class 2620 OID 4801238)
-- Dependencies: 22 1603
-- Name: trigger_detect_duplicate_occurrences; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_occurrences
    BEFORE UPDATE ON occurrences
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_occurrences();


--
-- TOC entry 1992 (class 2620 OID 4801239)
-- Dependencies: 1612 23
-- Name: trigger_detect_duplicate_roles; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_roles
    BEFORE INSERT OR UPDATE ON roles
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_roles();

ALTER TABLE roles DISABLE TRIGGER trigger_detect_duplicate_roles;


--
-- TOC entry 1985 (class 2606 OID 4801240)
-- Dependencies: 1935 1600 1617
-- Name: fk_baselocator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_baselocator FOREIGN KEY (id_base_locator) REFERENCES locators(id);


--
-- TOC entry 1965 (class 2606 OID 4801245)
-- Dependencies: 1935 1597 1600
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id) ON DELETE RESTRICT;


--
-- TOC entry 1971 (class 2606 OID 4801250)
-- Dependencies: 1618 1604 1955
-- Name: fk_instance; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_instance FOREIGN KEY (id_instance) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1973 (class 2606 OID 4801255)
-- Dependencies: 1600 1605 1935
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1976 (class 2606 OID 4801260)
-- Dependencies: 1600 1607 1935
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1978 (class 2606 OID 4801265)
-- Dependencies: 1608 1600 1935
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1963 (class 2606 OID 4801270)
-- Dependencies: 1953 1593 1617
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1969 (class 2606 OID 4801275)
-- Dependencies: 1602 1955 1618
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1983 (class 2606 OID 4801280)
-- Dependencies: 1593 1612 1927
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES associations(id) ON DELETE CASCADE;


--
-- TOC entry 1987 (class 2606 OID 4801285)
-- Dependencies: 1618 1617 1953
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1988 (class 2606 OID 4801290)
-- Dependencies: 1619 1941 1602
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES names(id) ON DELETE CASCADE;


--
-- TOC entry 1970 (class 2606 OID 4801295)
-- Dependencies: 1603 1618 1955
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1986 (class 2606 OID 4801300)
-- Dependencies: 1618 1955 1617
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1960 (class 2606 OID 4801305)
-- Dependencies: 1618 1955 1590
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id) ON DELETE SET NULL;


--
-- TOC entry 1966 (class 2606 OID 4801310)
-- Dependencies: 1598 1945 1611
-- Name: fk_reivision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_reivision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1964 (class 2606 OID 4801315)
-- Dependencies: 1611 1945 1595
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1968 (class 2606 OID 4801320)
-- Dependencies: 1945 1601 1611
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1980 (class 2606 OID 4801325)
-- Dependencies: 1614 1609 1949
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1961 (class 2606 OID 4801330)
-- Dependencies: 1949 1591 1614
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1974 (class 2606 OID 4801335)
-- Dependencies: 1618 1606 1955
-- Name: fk_subtype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_subtype FOREIGN KEY (id_subtype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1975 (class 2606 OID 4801340)
-- Dependencies: 1955 1606 1618
-- Name: fk_supertype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_supertype FOREIGN KEY (id_supertype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1981 (class 2606 OID 4801345)
-- Dependencies: 1618 1955 1609
-- Name: fk_theme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_theme FOREIGN KEY (id_theme) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1977 (class 2606 OID 4801350)
-- Dependencies: 1618 1607 1955
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1979 (class 2606 OID 4801355)
-- Dependencies: 1608 1955 1618
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1959 (class 2606 OID 4801360)
-- Dependencies: 1617 1953 1589
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1984 (class 2606 OID 4801365)
-- Dependencies: 1953 1614 1617
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1967 (class 2606 OID 4801370)
-- Dependencies: 1598 1617 1953
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1982 (class 2606 OID 4801375)
-- Dependencies: 1611 1953 1617
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1972 (class 2606 OID 4801380)
-- Dependencies: 1955 1618 1604
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1962 (class 2606 OID 4801385)
-- Dependencies: 1592 1618 1955
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typeables
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1997 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2010-09-08 08:58:35

--
-- PostgreSQL database dump complete
--


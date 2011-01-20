--
-- PostgreSQL database dump
--

-- Started on 2011-01-07 11:31:03

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 426 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 56 (class 1255 OID 26528507)
-- Dependencies: 426 6
-- Name: array_cut(text[], bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION array_cut("array" text[], "from" bigint, "to" bigint) RETURNS text[]
    LANGUAGE plpgsql
    AS $_$DECLARE
	sub text[];
	iIndex bigint;
	iLength bigint;
BEGIN
	iLength := array_length($1,1);
	FOR iIndex IN $2..$3 LOOP
		IF iLength >= iIndex THEN
			sub := sub || $1[iIndex];
		END IF;
	END LOOP;
	RETURN sub;
END$_$;


ALTER FUNCTION public.array_cut("array" text[], "from" bigint, "to" bigint) OWNER TO postgres;

--
-- TOC entry 57 (class 1255 OID 26412395)
-- Dependencies: 6 426
-- Name: array_pos(text[], text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION array_pos("array" text[], token text) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	iPos integer;
BEGIN
	FOR iPos IN 1..array_length($1,1) LOOP
		IF $1[iPos] = $2 THEN
			RETURN iPos;
		END IF;
	END LOOP;
	RETURN NULL;
END$_$;


ALTER FUNCTION public.array_pos("array" text[], token text) OWNER TO postgres;

--
-- TOC entry 58 (class 1255 OID 26524870)
-- Dependencies: 6 426
-- Name: array_pos(text[], text, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION array_pos("array" text[], item text, start bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	iPos integer;
BEGIN
	FOR iPos IN $3..array_length($1,1) LOOP
		IF $1[iPos] = $2 THEN
			RETURN iPos;
		END IF;
	END LOOP;
	RETURN NULL;
END$_$;


ALTER FUNCTION public.array_pos("array" text[], item text, start bigint) OWNER TO postgres;

--
-- TOC entry 19 (class 1255 OID 26254253)
-- Dependencies: 426 6
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
-- TOC entry 20 (class 1255 OID 26254255)
-- Dependencies: 426 6
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
-- TOC entry 21 (class 1255 OID 26254256)
-- Dependencies: 6 426
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
-- TOC entry 69 (class 1255 OID 26638113)
-- Dependencies: 6 426
-- Name: create_a_kind_of_association(bigint, bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION create_a_kind_of_association("idTopicMap" bigint, "idSupertype" bigint, "idSubtype" bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE 
	idSubtypeRT bigint;
	idSupertypeRT bigint;
	idAssociationType bigint;
	idAssociation bigint;
	rec RECORD;
BEGIN
	/* get association and role types */
	idAssociationType := topic_by_subjectidentifier($1,'http://psi.topicmaps.org/iso13250/model/supertype-subtype');
	idSubtypeRT := topic_by_subjectidentifier($1,'http://psi.topicmaps.org/iso13250/model/subtype');
	idSupertypeRT := topic_by_subjectidentifier($1,'http://psi.topicmaps.org/iso13250/model/supertype');
	/* create association */
	FOR rec IN INSERT INTO associations(id_topicmap,id_type) VALUES ( $1 , idAssociationType) RETURNING id LOOP
		idAssociation := rec.id;
	END LOOP;
	/* create roles */
	INSERT INTO roles(id_topicmap, id_parent, id_type, id_player) VALUES ( $1,idAssociation, idSupertypeRT,$2);
	INSERT INTO roles(id_topicmap, id_parent, id_type, id_player) VALUES ( $1,idAssociation, idSubtypeRT,$3);
END;$_$;


ALTER FUNCTION public.create_a_kind_of_association("idTopicMap" bigint, "idSupertype" bigint, "idSubtype" bigint) OWNER TO postgres;

--
-- TOC entry 70 (class 1255 OID 26636048)
-- Dependencies: 6 426
-- Name: detect_datatype(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION detect_datatype(value text) RETURNS text
    LANGUAGE plpgsql
    AS $_$DECLARE
	datatype text;
BEGIN
	/* is anyURI */
	IF strpos($1,'<') != 0 THEN
		datatype := 'http://www.w3.org/2001/XMLSchema#anyURI';
	/*is a date or a datetime*/
	ELSEIF strpos($1,'-') != 0 THEN
		IF strpos($1,'T') != 0 THEN
			datatype := 'http://www.w3.org/2001/XMLSchema#dateTime';
		ELSE
			datatype := 'http://www.w3.org/2001/XMLSchema#date';
		END IF;
	/* is a decimal value */
	ELSEIF strpos($1,'.') != 0 THEN
		datatype := 'http://www.w3.org/2001/XMLSchema#decimal';
	/* is a integer value */
	ELSE
		datatype := 'http://www.w3.org/2001/XMLSchema#integer';
	END IF;
	RETURN datatype;
END$_$;


ALTER FUNCTION public.detect_datatype(value text) OWNER TO postgres;

--
-- TOC entry 23 (class 1255 OID 26254257)
-- Dependencies: 6 426
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
-- TOC entry 24 (class 1255 OID 26254258)
-- Dependencies: 426 6
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
-- TOC entry 25 (class 1255 OID 26254259)
-- Dependencies: 426 6
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
-- TOC entry 26 (class 1255 OID 26254260)
-- Dependencies: 426 6
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
-- TOC entry 27 (class 1255 OID 26254261)
-- Dependencies: 6 426
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
-- TOC entry 67 (class 1255 OID 26355545)
-- Dependencies: 426 6
-- Name: from_ctm(bigint, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION from_ctm("idTopicMap" bigint, ctm text) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	tokens text[];
	prefix text[][2];
	token text;
	identity text;
	iIndex integer;
	iNextIndex integer;
	iPos integer;
	subtokens text[];
	idReifier bigint;
BEGIN
	/* tokenize CTM string */
	tokens := tokenize($2);
	prefix := ARRAY[ARRAY['tm','http://psi.topicmaps.org/iso13250/model/']];
	iPos := 1;
	iIndex := array_pos(tokens,'%prefix');
	WHILE iIndex IS NOT NULL LOOP
		 IF tokens[iIndex] = '%prefix' THEN		 		 
			subtokens := array_cut(tokens, iIndex, iIndex + 2 );
			prefix = prefix || ARRAY[ARRAY[subtokens[2],substring(subtokens[3],2,length(subtokens[3])-2)]];			
		ELSEIF tokens[iIndex] = '%mergemap' THEN		 		 
			/* IGNORE */
		ELSEIF tokens[iIndex] = '%include' THEN		 		 
			/* IGNORE */
		END IF;
		iPos := iIndex +1;
		/* check for next token */
		iIndex := array_pos(tokens,'%prefix', iPos);
		IF iIndex IS NULL THEN
			iIndex := array_pos(tokens,'%include', iPos);
		END IF;
		IF iIndex IS NULL THEN
			iIndex := array_pos(tokens,'%mergemap', iPos);
		END IF;
	END LOOP;		
	iPos := iPos+2;	
	token := tokens[iPos];
	IF token = '~' THEN
		/* is subject-locator */
		IF tokens[iPos+1] = '=' THEN
			identity := get_identifier(prefix,  tokens[iPos+2]);
			iPos := iPos + 3;
			idReifier := topic_by_subjectlocator($1,identity);
		/* is item-identifier */
		ELSEIF tokens[iPos+1] = '^' THEN
			identity := get_identifier(prefix,  tokens[iPos+2]);
			iPos := iPos + 3;
			idReifier :=  topic_by_itemidentifier($1,identity);
		/* is subject-identifier */
		ELSE
			identity := get_identifier(prefix,  tokens[iPos+1]);
			iPos := iPos + 2;
			/* is CTM embed identity */
			IF  identity IS NULL THEN
				SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
				identity := rec.reference || tokens[iPos+1];
				/* create pseudo-topic */
				idReifier :=  topic_by_itemidentifier($1,identity);
				ELSE 
				idReifier :=  topic_by_subjectidentifier($1,identity);					
			END IF;	
		END IF;
		UPDATE reifiables SET id_reifier = idReifier WHERE id = $1;
	END IF;

	/* create all topics */	
	iIndex := array_pos(tokens,'.', iPos);
	WHILE iIndex IS NOT NULL LOOP
		subtokens := array_cut(tokens, iPos, iIndex );		
		PERFORM from_ctm_topic($1, subtokens, prefix);
		iPos := iIndex + 1;
		iIndex := array_pos(tokens,'.', iPos);
	END LOOP; 
	/* create all associations */
	iPos := array_pos(tokens,'(', iPos);
	IF iPos IS NOT NULL THEN
		iIndex := array_pos(tokens,')', iPos+1);
		WHILE iIndex IS NOT NULL AND iPos IS NOT NULL LOOP
			iNextIndex := array_pos(tokens,'(', iIndex);
			/* is last association */
			IF iNextIndex IS NULL THEN			
				subtokens := array_cut(tokens, iPos-1,  array_length(tokens,1));
			ELSE 
				subtokens := array_cut(tokens, iPos-1,  iNextIndex-2);
			END IF;		
			/* create association */
			PERFORM from_ctm_association($1, subtokens,prefix);
			/* was last association */
			IF iNextIndex IS NULL THEN		
				EXIT;
			END IF;
			iPos := iNextIndex;
			iIndex := array_pos(tokens,')', iPos+1);		
		END LOOP;
	END IF;
END$_$;


ALTER FUNCTION public.from_ctm("idTopicMap" bigint, ctm text) OWNER TO postgres;

--
-- TOC entry 60 (class 1255 OID 26634102)
-- Dependencies: 426 6
-- Name: from_ctm_association(bigint, text[], anyarray); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION from_ctm_association("idTopicMap" bigint, tokens text[], prefix anyarray) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	iIndex bigint;
	iPos bigint;
	token text;
	nextToken text;
	identity text;
	idType bigint;
	idReifier bigint;
	idScope bigint;
	idThemes bigint[];
	idAssociation bigint;
	subtokens text[];
BEGIN
	token := $2[1];	
	/* handle first identity of topic */
	/* is subject-locator */
	IF token = '=' THEN
		identity := get_identifier($3,  $2[2]);
		iIndex := 3;
		idType := topic_by_subjectlocator($1,identity);
	/* is item-identifier */
	ELSEIF token = '^' THEN
		identity := get_identifier($3,  $2[2]);
		iIndex := 3;
		idType := topic_by_itemidentifier($1,identity);
	/* is subject-identifier */
	ELSE
		identity := get_identifier($3,  token);
		iIndex := 2;
		/* is CTM embed identity */
		IF  identity IS NULL THEN
			SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
			identity := rec.reference || token;
			/* create pseudo-topic */
			idType := topic_by_itemidentifier($1,identity);
		ELSE 
			idType := topic_by_subjectidentifier($1,identity);					
		END IF;	
	END IF;
	iPos := array_pos($2,')');
	/* extract roles */
	subtokens := array_cut($2, iIndex+1,iPos-1);
	iIndex := iPos+1;
	/* check next tokens */
	WHILE iIndex <= array_length($2,1) LOOP
		token := $2[iIndex];			
		/* is scope */
		IF token = '@'OR token = ','  THEN
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($3,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||  topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($3,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||   topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($3,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;
					/* create pseudo-topic */
					idThemes := idThemes ||  topic_by_itemidentifier($1,identity);
				ELSE 
					idThemes := idThemes ||  topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
		/* is reifier */		
		ELSEIF token = '~' THEN
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($3,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($3,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($3,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;					
					/* create pseudo-topic */
					idReifier := topic_by_itemidentifier($1,identity);
				ELSE 
					idReifier := topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;	
		ELSE
			RAISE EXCEPTION 'Invalid token % in CTM association', token;
		END IF;		
	END LOOP; 
	/* create scope */
	idScope := get_or_create_scope($1,idThemes);
	FOR rec IN INSERT INTO associations(id_topicmap, id_type, id_scope, id_reifier ) VALUES ($1,idType,idScope,idReifier) RETURNING id LOOP
		idAssociation := rec.id;
	END LOOP;
	/* check role tokens */
	iPos := 1;
	iIndex := array_pos(subtokens,',', iPos);
	WHILE iIndex IS NOT NULL LOOP
		PERFORM from_ctm_role($1,idAssociation,array_cut(subtokens, iPos, iIndex-1), $3);
		iPos := iIndex + 1;
		iIndex := array_pos(subtokens,',', iPos);
	END LOOP;
	PERFORM from_ctm_role($1,idAssociation,array_cut(subtokens, iPos, array_length(subtokens,1)), $3);
END$_$;


ALTER FUNCTION public.from_ctm_association("idTopicMap" bigint, tokens text[], prefix anyarray) OWNER TO postgres;

--
-- TOC entry 71 (class 1255 OID 26592889)
-- Dependencies: 426 6
-- Name: from_ctm_name(bigint, bigint, text[], anyarray); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION from_ctm_name("idTopicMap" bigint, "idParent" bigint, tokens text[], prefix anyarray) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	token text;
	nextToken text;
	identity text;
	iIndex integer;
	idReifier bigint;
	idTheme bigint;
	idType bigint;
	idThemes bigint[];
	val text;
	idScope bigint;
	idName bigint;
	variantTokens text[];
BEGIN		
	/*is default name type if second token is value*/
	IF substring($3[2],1,1) = '"' THEN
		/* get value */
		val := $3[2];	
		idType := topic_by_subjectidentifier($1,'http://psi.topicmaps.org/iso13250/model/topic-name');	
		iIndex = 3;
	ELSE 		
		/* the name type */
		token := $3[2];
		/* is subject-locator */	
		IF token = '=' THEN
			identity := get_identifier($4,  $3[3]);
			/* get value */
			val := $3[5];	
			iIndex := 6;
			idType := topic_by_subjectlocator($1,identity);
		/* is item-identifier */
		ELSEIF nextToken = '^' THEN
			identity := get_identifier($4,  $3[3]);
			/* get value */
			val := $3[5];	
			iIndex := 6;
			idType := topic_by_itemidentifier($1,identity);
		/* is subject-identifier */
		ELSE
			identity := get_identifier(prefix,  token);
			/* get value */
			val := $3[4];	
			iIndex := 5;
			/* is CTM embed identity */
			IF  identity IS NULL THEN
				SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
				identity := rec.reference || token;
				/* create pseudo-topic */
				idType := topic_by_itemidentifier($1,identity);
			ELSE 
				idType := topic_by_subjectidentifier($1,identity);					
			END IF;	
		END IF;		
	END IF;

	/* clean value */
	IF length(val) >= 6 AND substring(val,1,3) = '"""' THEN
		val := substring(val, 4, length(val)-6);
	ELSE
		val := substring(val, 2, length(val)-2);
	END IF;

	/* check next tokens */
	WHILE iIndex <= array_length($3,1) LOOP
		token := $3[iIndex];			
		/* is scope */
		IF token = '@' OR token = ','  THEN
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||  topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||   topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($4,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;
					/* create pseudo-topic */
					idThemes := idThemes ||  topic_by_itemidentifier($1,identity);
				ELSE 
					idThemes := idThemes ||  topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
		/* is reifier */		
		ELSEIF token = '~' THEN
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($4,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;					
					/* create pseudo-topic */
					idReifier := topic_by_itemidentifier($1,identity);
				ELSE 
					idReifier := topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
		/* is variant */
		ELSEIF token = '(' THEN
			/* check if scope id is NULL */
			IF idScope IS NULL THEN				
				idScope := get_or_create_scope($1,idThemes);
			END IF;
			/* create name if not already created */
			IF  idName IS NULL THEN
				FOR rec IN INSERT INTO names(id_topicmap, id_parent, "value", id_type, id_scope, id_reifier) VALUES ($1,$2,val,idType,idScope, idReifier)  RETURNING id LOOP
					idName := rec.id;
				END LOOP;
			END IF;
			variantTokens := NULL;
			iIndex := iIndex + 1;
			WHILE iIndex <= array_length($3,1) LOOP
				token := $3[iIndex];
				IF token = ')' THEN					
					PERFORM from_ctm_variant($1,idName,variantTokens,$4);
				ELSE
					variantTokens := variantTokens || token;
				END IF;
				iIndex := iIndex +1;
			END LOOP;
		ELSE 
			RAISE EXCEPTION 'Invalid token % as part of CTM name', token;
		END IF;		
	END LOOP; 
	/* check if scope id is NULL */
	IF idScope IS NULL THEN				
		idScope := get_or_create_scope($1,idThemes);
	END IF;

	/* create name if not already created */
	IF  idName IS NULL THEN
		INSERT INTO names(id_topicmap, id_parent, "value", id_type, id_scope, id_reifier) VALUES ($1,$2,val,idType,idScope, idReifier);		
	END IF;	
END$_$;


ALTER FUNCTION public.from_ctm_name("idTopicMap" bigint, "idParent" bigint, tokens text[], prefix anyarray) OWNER TO postgres;

--
-- TOC entry 66 (class 1255 OID 26594747)
-- Dependencies: 426 6
-- Name: from_ctm_occurrence(bigint, bigint, text[], anyarray); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION from_ctm_occurrence("idTopicMap" bigint, "idTopic" bigint, tokens text[], prefix anyarray) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	token text;
	nextToken text;
	identity text;
	iIndex integer;
	idReifier bigint;
	idTheme bigint;
	idType bigint;
	idThemes bigint[];
	val text;
	datatype text;
	idScope bigint;
BEGIN	
	/* set default datatype */
	datatype := 'http://www.w3.org/2001/XMLSchema#string';	
	/* the occurrence type */
	token := $3[1];
	/* is subject-locator */
	IF token = '=' THEN
		identity := get_identifier($4,  $3[2]);
		/* get value */
		val := $3[4];
		iIndex := 5;
		idType := topic_by_subjectlocator($1,identity);
	/* is item-identifier */
	ELSEIF token = '^' THEN
		identity := get_identifier($4,  $3[2]);
		/* get value */
		val := $3[4];
		iIndex := 5;
		idType := topic_by_itemidentifier($1,identity);
	/* is subject-identifier */
	ELSE
		identity := get_identifier(prefix,  token);
		iIndex := 4;
		val := $3[3];
		/* is CTM embed identity */
		IF  identity IS NULL THEN
			SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
			identity := rec.reference || token;		
			/* create pseudo-topic */
			idType := topic_by_itemidentifier($1,identity);
		ELSE 
			idType := topic_by_subjectidentifier($1,identity);					
		END IF;	
	END IF;		
	
	/* clean value */
	IF length(val) >= 6 AND substring(val,1,3) = '"""' THEN
		val := substring(val, 4, length(val)-6);
	ELSEIF substring(val,1,1) = '"' THEN
		val := substring(val, 2, length(val)-2);
	ELSE
		datatype := detect_datatype(val);
	END IF;

	/* check next tokens */
	WHILE iIndex <= array_length($3,1) LOOP
		token := $3[iIndex];
		/* is datatype token */
		IF token = '^^' THEN
			datatype := get_identifier(prefix,  tokens[iIndex+1]);
			iIndex := iIndex + 2;
		/* is scope */
		ELSEIF token = '@' OR token = ',' THEN
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||  topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||   topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier(prefix,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;	
					/* create pseudo-topic */
					idThemes := idThemes ||  topic_by_itemidentifier($1,identity);
				ELSE 
					idThemes := idThemes ||  topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
		/* is reifier */		
		ELSEIF token = '~' THEN			
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier(prefix,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;	
					/* create pseudo-topic */
					idReifier := topic_by_itemidentifier($1,identity);
				ELSE 
					idReifier := topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
		ELSE 
			RAISE EXCEPTION 'Invalid token % as part of CTM occurrence', token;
		END IF;		
	END LOOP; 
	IF idScope IS NULL THEN
		idScope := get_or_create_scope($1,idThemes);
	END IF;
	/* create locator */
	INSERT INTO locators (reference) SELECT datatype WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = datatype);
	/* create occurrence */
	INSERT INTO occurrences(id_topicmap, id_parent, "value", id_type, id_datatype, id_scope, id_reifier) SELECT $1,$2,val,idType, id,idScope, idReifier FROM locators WHERE reference = datatype;
END$_$;


ALTER FUNCTION public.from_ctm_occurrence("idTopicMap" bigint, "idTopic" bigint, tokens text[], prefix anyarray) OWNER TO postgres;

--
-- TOC entry 59 (class 1255 OID 26634104)
-- Dependencies: 426 6
-- Name: from_ctm_role(bigint, bigint, text[], anyarray); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION from_ctm_role("idTopicMap" bigint, "idAssociation" bigint, tokens text[], prefix anyarray) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	idType bigint;
	idPlayer bigint;	
	idReifier bigint;	
	token text;
	identity text;
	iIndex bigint;
	rec RECORD;
BEGIN	
	/* get role-type */
	token := $3[1];
	/* is subject-locator */
	IF token = '=' THEN
		identity := get_identifier($4,  $3[2]);
		iIndex := 4;
		idType := topic_by_subjectlocator($1,identity);
	/* is item-identifier */
	ELSEIF token = '^' THEN
		identity := get_identifier($4,  $3[2]);
		iIndex := 4;
		idType := topic_by_itemidentifier($1,identity);
	/* is subject-identifier */
	ELSE
		identity := get_identifier($4,  token);
		iIndex := 3;
		/* is CTM embed identity */
		IF  identity IS NULL THEN
			SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
			identity := rec.reference || token;
			/* create pseudo-topic */
			idType := topic_by_itemidentifier($1,identity);
		ELSE 
			idType := topic_by_subjectidentifier($1,identity);					
		END IF;	
	END IF;	

	/* get role-player */
	token := $3[iIndex];
	/* is subject-locator */
	IF token = '=' THEN
		identity := get_identifier($4,  $3[iIndex+1]);
		iIndex := iIndex + 2;
		idPlayer := topic_by_subjectlocator($1,identity);
	/* is item-identifier */
	ELSEIF token = '^' THEN
		identity := get_identifier($4,  $3[iIndex+1]);
		iIndex := iIndex + 2;
		idPlayer := topic_by_itemidentifier($1,identity);
	/* is subject-identifier */
	ELSE
		identity := get_identifier($4,  token);
		iIndex := iIndex + 1;
		/* is CTM embed identity */
		IF  identity IS NULL THEN
			SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
			identity := rec.reference || token;
			/* create pseudo-topic */
			idPlayer := topic_by_itemidentifier($1,identity);
		ELSE 
			idPlayer := topic_by_subjectidentifier($1,identity);					
		END IF;	
	END IF;	

	/* reifier */
	IF array_length($3,1) > iIndex AND $3[iIndex] = '~' THEN
		token := $3[iIndex];
		/* is subject-locator */
		IF token = '=' THEN
			identity := get_identifier($4,  $3[iIndex+1]);			
			idReifier := topic_by_subjectlocator($1,identity);
		/* is item-identifier */
		ELSEIF token = '^' THEN
			identity := get_identifier($4,  $3[iIndex+1]);			
			idReifier := topic_by_itemidentifier($1,identity);
		/* is subject-identifier */
		ELSE
			identity := get_identifier($4,  token);			
			/* is CTM embed identity */
			IF  identity IS NULL THEN
				SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
				identity := rec.reference || token;
				/* create pseudo-topic */
				idReifier := topic_by_itemidentifier($1,identity);
			ELSE 
				idReifier := topic_by_subjectidentifier($1,identity);					
			END IF;	
		END IF;	
	END IF;
	/* create role */
	INSERT INTO roles(id_topicmap, id_parent, id_type, id_player, id_reifier) VALUES ($1,$2,idType,idPlayer,idReifier);
END$_$;


ALTER FUNCTION public.from_ctm_role("idTopicMap" bigint, "idAssociation" bigint, tokens text[], prefix anyarray) OWNER TO postgres;

--
-- TOC entry 72 (class 1255 OID 26596476)
-- Dependencies: 426 6
-- Name: from_ctm_topic(bigint, text[], anyarray); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION from_ctm_topic("idTopicMap" bigint, tokens text[], prefix anyarray) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	iIndex bigint;
	iPos bigint;
	subtokens text[];
	token text;
	identity text;
	idTopic bigint;
	idType bigint;
BEGIN
	token := $2[1];	
	/* handle first identity of topic */
	/* is subject-locator */
	IF token = '=' THEN
		identity := get_identifier($3,  $2[2]);
		iIndex := 3;
		idTopic := topic_by_subjectlocator($1,identity);
	/* is item-identifier */
	ELSEIF token = '^' THEN
		identity := get_identifier($3,  $2[2]);
		iIndex := 3;
		idTopic := topic_by_itemidentifier($1,identity);
	/* is subject-identifier */
	ELSE
		identity := get_identifier(prefix,  token);		
		iIndex := 2;
		/* is CTM embed identity */
		IF  identity IS NULL THEN
			SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
			identity := rec.reference || token;
			/* create pseudo-topic */
			idTopic := topic_by_itemidentifier($1,identity);
		ELSE 
			idTopic := topic_by_subjectidentifier($1,identity);					
		END IF;	
	END IF;	
		
	iPos := array_pos($2, ';', iIndex);
	IF iPos IS NULL THEN
		iPos := array_pos($2, '.', iIndex);	
	END IF;
	WHILE iPos IS NOT NULL LOOP
		/* get sub tokens */
		subtokens := array_cut($2, iIndex, iPos-1);
		/* is subject-identifier */
		IF array_length(subtokens,1) = 1 THEN 
			identity := get_identifier($3,  subtokens[1]);
			/* create locator */
			INSERT INTO locators (reference) SELECT identity WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = identity);
			/* try to read topic */
			SELECT INTO rec t.id FROM topics AS t, locators AS l, rel_subject_identifiers AS r WHERE l.reference = identity AND r.id_topic = t.id AND r.id_locator = l.id AND t.id_topicmap = $1;
			/* topic exists */
			IF FOUND THEN
				PERFORM merge_topics(idTopic, rec.id);
			/* add subject-identifier */
			ELSE
				INSERT INTO rel_subject_identifiers(id_topic, id_locator) SELECT idTopic, id FROM locators WHERE reference = identity;
			END IF;
		/* is subject-locator */
		ELSEIF subtokens[1] = '=' THEN
			identity := get_identifier($3,  subtokens[2]);
			/* create locator */
			INSERT INTO locators (reference) SELECT identity WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = identity);
			/* try to read topic */
			SELECT INTO rec t.id FROM topics AS t, locators AS l, rel_subject_locators AS r WHERE l.reference = identity AND r.id_topic = t.id AND r.id_locator = l.id AND t.id_topicmap = $1;
			/* topic exists */
			IF FOUND THEN
				PERFORM merge_topics(idTopic, rec.id);
			/* add subject-locator */
			ELSE
				INSERT INTO rel_subject_locators(id_topic, id_locator) SELECT idTopic, id FROM locators WHERE reference = identity;
			END IF;
		/* is item-identifier */
		ELSEIF subtokens[1] = '^' THEN
			identity := get_identifier($3,  subtokens[2]);
			/* create locator */
			INSERT INTO locators (reference) SELECT identity WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = identity);
			/* try to read topic */
			SELECT INTO rec c.id FROM constructs AS c, locators AS l, rel_item_identifiers AS r WHERE l.reference = identity AND r.id_construct = c.id AND r.id_locator = l.id AND c.id_topicmap = $1;
			/* topic exists */
			IF FOUND THEN
				PERFORM merge_topics(idTopic, rec.id);
			/* add item-identifier */
			ELSE
				INSERT INTO rel_item_identifiers(id_construct, id_locator) SELECT idTopic, id FROM locators WHERE reference = identity;
			END IF;
		/* is instance-of */
		ELSEIF subtokens[1] = 'isa' THEN			
			/* is subject-locator */
			IF subtokens[2] = '=' THEN
				identity := get_identifier($3,  subtokens[3]);
				idType :=     topic_by_subjectlocator(identity);
			/* is item-identifier */
			ELSEIF subtokens[2] = '^' THEN
				identity := get_identifier($3,  subtokens[3]);
				idType :=   topic_by_itemidentifier(identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($3,  subtokens[2]);
				/* is CTM embed identity */
				IF  identity IS NULL THEN					
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || subtokens[2];
					/* create pseudo-topic */
					idType := topic_by_itemidentifier($1,identity);
				ELSE 
					idType := topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
			/* create isa relation */
			INSERT INTO rel_instance_of(id_type, id_instance) VALUES ( idType, idTopic);
		/* is kind-of */
		ELSEIF subtokens[1] = 'ako' THEN			
			/* is subject-locator */
			IF subtokens[2] = '=' THEN
				identity := get_identifier($3,  subtokens[3]);
				idType :=     topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF subtokens[2] = '^' THEN
				identity := get_identifier($3,  subtokens[3]);
				idType :=   topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($3,  subtokens[2]);
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || subtokens[2];
					/* create pseudo-topic */
					idType := topic_by_itemidentifier($1,identity);
				ELSE 
					idType := topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
			/* create ako relation */
			INSERT INTO rel_kind_of(id_supertype, id_subtype) VALUES ( idType, idTopic);
			PERFORM create_a_kind_of_association($1,idType, idTopic);
		/* is name */
		ELSEIF subtokens[1] = '-' THEN	
			PERFORM from_ctm_name($1, idTopic, subtokens, $3);		
		/* is occurrence */
		ELSE
			PERFORM from_ctm_occurrence($1, idTopic, subtokens, $3);		
		END IF;	
		/* next index */			
		iIndex := iPos+1;
		iPos := array_pos(tokens, ';', iIndex);	
		IF iPos IS NULL THEN
			iPos := array_pos(tokens, '.', iIndex);	
		END IF;
	END LOOP;
END$_$;


ALTER FUNCTION public.from_ctm_topic("idTopicMap" bigint, tokens text[], prefix anyarray) OWNER TO postgres;

--
-- TOC entry 68 (class 1255 OID 26590096)
-- Dependencies: 426 6
-- Name: from_ctm_variant(bigint, bigint, text[], anyarray); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION from_ctm_variant("idTopicMap" bigint, "idParent" bigint, tokens text[], prefixes anyarray) RETURNS void
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	token text;
	nextToken text;
	identity text;
	iIndex integer;
	idReifier bigint;
	idTheme bigint;
	idThemes bigint[];
	val text;
	datatype text;
	idScope bigint;
BEGIN
	/* get value */
	val := $3[1];
	/* set default datatype */
	datatype := 'http://www.w3.org/2001/XMLSchema#string';		
	/* clean value */
	IF length(val) >= 6 AND substring(val,1,3) = '"""' THEN
		val := substring(val, 4, length(val)-6);
	ELSEIF substring(val,1,1) = '"' THEN
		val := substring(val, 2, length(val)-2);
	ELSE
		datatype := detect_datatype(val);
	END IF;
	iIndex := 2;
	/* parse token array */
	WHILE iIndex <= array_length($3,1) LOOP
		token := $3[iIndex];
		/* is datatype token */
		IF token = '^^' THEN
			datatype := get_identifier(prefix,  tokens[iIndex+1]);
			iIndex := iIndex + 2;
		/* is scope */
		ELSEIF token = '@' OR token = ',' THEN
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||  topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idThemes := idThemes ||   topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($4,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;
					/* create pseudo-topic */
					idThemes := idThemes ||  topic_by_itemidentifier($1,identity);
				ELSE 
					idThemes := idThemes ||  topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
		/* is reifier */		
		ELSEIF token = '~' THEN			
			nextToken := tokens[iIndex+1];
			/* is subject-locator */
			IF nextToken = '=' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_subjectlocator($1,identity);
			/* is item-identifier */
			ELSEIF nextToken = '^' THEN
				identity := get_identifier($4,  tokens[iIndex+2]);
				iIndex := iIndex + 3;
				idReifier := topic_by_itemidentifier($1,identity);
			/* is subject-identifier */
			ELSE
				identity := get_identifier($4,  nextToken);
				iIndex := iIndex + 2;
				/* is CTM embed identity */
				IF  identity IS NULL THEN
					SELECT INTO rec reference FROM locators AS l, topicmaps AS tm WHERE tm.id = $1 AND tm.id_base_locator = l.id;
					identity := rec.reference || nextToken;
					/* create pseudo-topic */
					idReifier := topic_by_itemidentifier($1,identity);
				ELSE 
					idReifier := topic_by_subjectidentifier($1,identity);					
				END IF;	
			END IF;
		END IF;		
	END LOOP; 
	IF idScope IS NULL THEN
		idScope := get_or_create_scope($1,idThemes);
	END IF;
	/* create locator */
	INSERT INTO locators (reference) SELECT datatype WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = datatype);
	/* create variant */
	INSERT INTO variants(id_topicmap, id_parent, "value", id_datatype, id_scope, id_reifier) SELECT $1,$2,val,id,idScope, idReifier FROM locators WHERE reference = datatype;
END$_$;


ALTER FUNCTION public.from_ctm_variant("idTopicMap" bigint, "idParent" bigint, tokens text[], prefixes anyarray) OWNER TO postgres;

--
-- TOC entry 53 (class 1255 OID 26395314)
-- Dependencies: 6 426
-- Name: get_identifier(anyarray, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION get_identifier(prefixes anyarray, identifier text) RETURNS text
    LANGUAGE plpgsql
    AS $_$DECLARE 
	iPos integer;
	token text;
BEGIN
	/* is absolute IRI */
	IF substring($2,1,1) = '<' THEN
		RETURN substring($2,2,length($2)-2);
	/* is relative IRI */
	ELSE 
		/* look for colon indicates prefix usage */
		iPos := strpos ( $2, ':' );		
		IF iPos > 0 THEN
			/* create absolute IRI */
			token := item_by_value($1,substring($2,1,iPos-1));			
			token := token || substring($2,iPos+1);
			RETURN token;
		END IF;			
	END IF;		
	/* is CTM internal identity */
	RETURN NULL;
END$_$;


ALTER FUNCTION public.get_identifier(prefixes anyarray, identifier text) OWNER TO postgres;

--
-- TOC entry 65 (class 1255 OID 26633494)
-- Dependencies: 6 426
-- Name: get_or_create_scope(bigint, bigint[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION get_or_create_scope("idTopicMap" bigint, "idThemes" bigint[]) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	idScope bigint;
	idTheme bigint;
BEGIN
	/* is non-empty scope */
	IF array_length($2,1) > 0 THEN
		SELECT INTO rec id FROM scopes WHERE id_topicmap = $1 AND 0 IN ( SELECT COUNT(id_theme) FROM ( SELECT id_theme FROM rel_themes AS r WHERE r.id_scope = id EXCEPT SELECT unnest($2) AS id_theme ) AS e ) AND id IN ( SELECT id_scope FROM rel_themes );
		IF FOUND THEN
			idScope = rec.id;
		/* create new scope for variant */
		ELSE
			FOR rec IN INSERT INTO scopes(id_topicmap) VALUES ( $1 ) RETURNING id LOOP
				idScope = rec.id;
			END LOOP;
			FOR idTheme IN SELECT unnest($2) LOOP
				INSERT INTO rel_themes(id_scope, id_theme) VALUES ( idScope, idTheme);
			END LOOP;
		END IF;
	/* is empty scope */
	ELSE
		SELECT INTO rec id FROM scopes WHERE id NOT IN ( SELECT DISTINCT id_scope FROM rel_themes );
		IF FOUND THEN
			idScope := rec.id;
		/* create new empty scope */
		ELSE
			FOR rec IN INSERT INTO scopes(id_topicmap) VALUES ($1) RETURNING id LOOP
				idScope = rec.id;
			END LOOP;
		END IF;
	END IF;
	RETURN idScope;
END$_$;


ALTER FUNCTION public.get_or_create_scope("idTopicMap" bigint, "idThemes" bigint[]) OWNER TO postgres;

--
-- TOC entry 28 (class 1255 OID 26254262)
-- Dependencies: 426 6
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
-- TOC entry 22 (class 1255 OID 26254263)
-- Dependencies: 426 6
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
-- TOC entry 29 (class 1255 OID 26254264)
-- Dependencies: 6 426
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
-- TOC entry 30 (class 1255 OID 26254265)
-- Dependencies: 426 6
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
-- TOC entry 31 (class 1255 OID 26254266)
-- Dependencies: 426 6
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
-- TOC entry 32 (class 1255 OID 26254267)
-- Dependencies: 6 426
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
-- TOC entry 33 (class 1255 OID 26254268)
-- Dependencies: 426 6
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
-- TOC entry 34 (class 1255 OID 26254269)
-- Dependencies: 6 426
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
-- TOC entry 35 (class 1255 OID 26254270)
-- Dependencies: 6 426
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
-- TOC entry 36 (class 1255 OID 26254271)
-- Dependencies: 6 426
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
-- TOC entry 37 (class 1255 OID 26254272)
-- Dependencies: 426 6
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
-- TOC entry 51 (class 1255 OID 26257185)
-- Dependencies: 6 426
-- Name: import_topicmap(character varying, anyarray); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION import_topicmap(baselocator character varying, data anyarray) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	idTopicMap bigint;
	idTopic bigint;
	idOtherTopic bigint;
	iTopics bigint;
	iSubjectIdentifier bigint;
	iSubjectLocator bigint;
	iItemIdentifier bigint;
	topicData character varying[];
	subjectIdentifiers character varying[];
	subjectIdentifier character varying;
BEGIN
	/* create locator */
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
	/* iterate over topics */
	FOR iTopics IN 1..array_length($2,1) LOOP
		idTopic := NULL;
		topicData := $2[iTopics];
		/* add subject-identifier */
		subjectIdentifiers := topicData[1];		
		FOR iSubjectIdentifier IN 1..array_length(subjectIdentifiers,1) LOOP
			subjectIdentifier := subjectIdentifiers[iSubjectIdentifier];
			/* create locator */
			INSERT INTO locators (reference) SELECT subjectIdentifier WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = subjectIdentifier);
			/* check if topic with subject-identifier exists */
			FOR rec IN SELECT t.id FROM rel_subject_identifiers AS r, topics AS t, locators AS l WHERE l.reference = subjectIdentifier AND r.id_locator = l.id AND r.id_topic = t.id AND t.id_topicmap = idTopicMap LOOP
				/* subject-identifier never seen before */
				IF idTopic IS NULL THEN
					idTopic := rec.id;
				/* merge topics */
				ELSE 
					PERFORM merge_topic(rec.id, idTopic);
					idTopic := rec.id;
				END IF;
			END LOOP; 
		END LOOP;
	END LOOP;
END$_$;


ALTER FUNCTION public.import_topicmap(baselocator character varying, data anyarray) OWNER TO postgres;

--
-- TOC entry 52 (class 1255 OID 26365494)
-- Dependencies: 6 426
-- Name: item_by_value(anyarray, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION item_by_value(map anyarray, item text) RETURNS text
    LANGUAGE plpgsql
    AS $_$DECLARE 
	iIndex integer;	
BEGIN
	FOR iIndex IN 1..array_length($1,1) LOOP				
		IF $1[iIndex][1] = $2 THEN
			RETURN $1[iIndex][2];
		END IF;
	END LOOP;
	RETURN NULL;
END$_$;


ALTER FUNCTION public.item_by_value(map anyarray, item text) OWNER TO postgres;

--
-- TOC entry 38 (class 1255 OID 26254273)
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
-- TOC entry 39 (class 1255 OID 26254274)
-- Dependencies: 426 6
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
-- TOC entry 40 (class 1255 OID 26254275)
-- Dependencies: 6 426
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
-- TOC entry 61 (class 1255 OID 26828855)
-- Dependencies: 426 6
-- Name: to_ctm(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION to_ctm(id_topicmap bigint) RETURNS text
    LANGUAGE plpgsql
    AS $_$DECLARE 
	ctm text;
	rec RECORD;
BEGIN
	ctm := '%encoding "utf-8"\n';	
	ctm := ctm || '%version 1.0\n';
	
	/* reifier */
	FOR rec IN SELECT id_reifier FROM topicmaps WHERE id = $1 LOOP
		IF rec.id_reifier IS NOT NULL THEN
			ctm := ctm || ' ~ ';
			ctm := ctm || to_ctm_identity(rec.id_reifier);
			ctm := ctm || '\r\n';
		END IF;
	END LOOP;

	/* topics */
	FOR rec IN SELECT id FROM topics WHERE id_topicmap = $1 LOOP
		ctm := ctm || to_ctm_topic(rec.id);
	END LOOP;

	/* association */
	FOR rec IN SELECT id FROM associations WHERE id_topicmap = $1 LOOP
		ctm := ctm || to_ctm_association(rec.id);
	END LOOP;

	RETURN ctm;
END$_$;


ALTER FUNCTION public.to_ctm(id_topicmap bigint) OWNER TO postgres;

--
-- TOC entry 74 (class 1255 OID 26828852)
-- Dependencies: 6 426
-- Name: to_ctm_association(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION to_ctm_association("idAssociation" bigint) RETURNS text
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	rec2 RECORD;
	ctm text;
	first boolean;
BEGIN	
	ctm := '';
	FOR rec IN SELECT id_type, id_scope, id_reifier FROM associations WHERE id = $1 LOOP			
		ctm := to_ctm_identity(rec.id_type);
		ctm := ctm || ' ( \n\t';
		first := true;
	RETURN ctm;
		/* roles */
		FOR rec2 IN SELECT id_type, id_player, id_reifier FROM roles WHERE id_parent = $1 LOOP
			IF NOT first THEN
				ctm := ctm || ' ,\n\t';
			END IF;
			ctm := ctm || to_ctm_identity(rec2.id_type);
			ctm := ctm || ' : ';
			ctm := ctm || to_ctm_identity(rec2.id_player);
			IF rec2.id_reifier IS NOT NULL THEN
				ctm := ctm || ' ~ ';
				ctm := ctm || to_ctm_identity(rec2.id_reifier);
			END IF;			
			first := false;
		END LOOP;
		first := true;
		/* scope */
		FOR rec2 IN SELECT id_theme FROM rel_themes WHERE id_scope = rec.id_scope LOOP
			IF first THEN
				ctm := ctm || '@ ';
			ELSE
				ctm := ctm || ', ';
			END IF;	
			ctm := ctm || to_ctm_identity(rec2.id_theme);
			first := false;
		END LOOP;
		/* reifier */
		IF rec.id_reifier IS NOT NULL THEN
			ctm := ctm || ' ~ ';
			ctm := ctm || to_ctm_identity(rec.id_reifier); 
		END IF;
		ctm := ctm || ') \n';
	END LOOP;
	RETURN ctm;
END$_$;


ALTER FUNCTION public.to_ctm_association("idAssociation" bigint) OWNER TO postgres;

--
-- TOC entry 62 (class 1255 OID 26828851)
-- Dependencies: 6 426
-- Name: to_ctm_identity(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION to_ctm_identity("idTopic" bigint) RETURNS text
    LANGUAGE plpgsql
    AS $_$DECLARE 
	ctm text;
	rec RECORD;
BEGIN
	ctm := '';
	/* check subject-identifier */
	FOR rec IN Select reference FROM rel_subject_identifiers, locators WHERE id_topic = $1 AND id = id_locator LOOP	
		ctm := ctm || '<';
		ctm := ctm || rec.reference;
		ctm := ctm || '>';
		RETURN ctm;
	END LOOP;

	/* check subject-locator */
	FOR rec IN Select reference FROM rel_subject_locators, locators WHERE id_topic = $1 AND id = id_locator LOOP	
		ctm := ctm || '= <';
		ctm := ctm || rec.reference;
		ctm := ctm || '>';
		RETURN ctm;
	END LOOP;

	/* check item-identifier */
	FOR rec IN Select reference FROM rel_item_identifiers, locators WHERE id_construct = $1 AND id = id_locator LOOP	
		ctm := ctm || '^ <';
		ctm := ctm || rec.reference;
		ctm := ctm || '>';
		RETURN ctm;
	END LOOP;
	
	/* generate by id */
	FOR rec In SELECT reference FROM locators AS l, topics AS t, topicmaps AS tm WHERE t.id = $1 AND t.id_parent = tm.id AND l.id = tm.id_base_locator LOOP
		ctm := ctm || '^ <';
		ctm := ctm || rec.reverence;
		ctm := ctm || '/';
		ctm := ctm || $1;
		ctm := ctm || '>';		
		RETURN ctm;
	END LOOP;

	RETURN NULL;
END$_$;


ALTER FUNCTION public.to_ctm_identity("idTopic" bigint) OWNER TO postgres;

--
-- TOC entry 73 (class 1255 OID 26828850)
-- Dependencies: 426 6
-- Name: to_ctm_topic(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION to_ctm_topic("topicId" bigint) RETURNS text
    LANGUAGE plpgsql
    AS $_$DECLARE
	ctm text;
	rec RECORD;
	first boolean;
	second boolean;
	firstTheme boolean;
	rec2 RECORD;
	rec3 RECORD;
BEGIN
	/* intialize */	
	ctm := '';
	first := true;
	second := true;
	/* subject-identifier */
	FOR rec IN Select reference FROM rel_subject_identifiers, locators WHERE id_topic = $1 AND id = id_locator LOOP	
		IF NOT second THEN
			ctm := ctm || ';\n\t';
		END IF;
		ctm := ctm || '<';
		ctm := ctm || rec.reference;
		ctm := ctm || '> ';
		IF first THEN
			first := false;
		ELSE
			second := false;
		END IF;
	END LOOP;
	
	/* subject-locator */
	FOR rec IN Select reference FROM rel_subject_locators, locators WHERE id_topic = $1 AND id = id_locator LOOP	
		IF NOT second THEN
			ctm := ctm || ';\n\t';
		END IF;
		ctm := ctm || '= <';
		ctm := ctm || rec.reference;
		ctm := ctm || '> ';
		IF first THEN
			first := false;
		ELSE
			second := false;
		END IF;
	END LOOP;
	
	/* item-identifier */
	FOR rec IN Select reference FROM rel_item_identifiers, locators WHERE id_construct = $1 AND id = id_locator LOOP	
		IF NOT second THEN
			ctm := ctm || ';\n\t';
		END IF;
		ctm := ctm || '^ <';
		ctm := ctm || rec.reference;
		ctm := ctm || '> ';
		IF first THEN
			first := false;
		ELSE
			second := false;
		END IF;
	END LOOP;
	
	/* types */
	FOR rec IN Select id_type FROM rel_instance_of WHERE id_instance = $1 LOOP	
		IF NOT second THEN
			ctm := ctm || ';\n\t';
		END IF;
		ctm := ctm || 'ISA ';		
		ctm := ctm || to_ctm_identity(rec.id_type);
		IF first THEN
			first := false;
		ELSE
			second := false;
		END IF;
		ctm := ctm || ' ';
	END LOOP;

	/* supertypes */
	FOR rec IN Select id_supertype FROM rel_kind_of WHERE id_subtype = $1 LOOP	
		IF NOT second THEN
			ctm := ctm || ';\n\t';
		END IF;
		ctm := ctm || 'AKO ';		
		ctm := ctm || to_ctm_identity(rec.id_supertype);
		IF first THEN
			first := false;
		ELSE
			second := false;
		END IF;
		ctm := ctm || ' ';
	END LOOP;

	/* occurrences */
	FOR rec IN Select id_type, value, reference, id_scope, id_reifier FROM occurrences, locators AS l WHERE id_parent = $1 AND id_datatype = l.id LOOP	
		IF NOT second THEN
			ctm := ctm || ';\n\t';
		END IF;	
		ctm := ctm || to_ctm_identity(rec.id_type); 		/* type */
		ctm := ctm || ' """';					
		ctm := ctm || rec.value;				/* value */
		ctm := ctm || '"""^^<';
		ctm := ctm || rec.reference;				/* datatype */
		ctm := ctm || '> ';
		firstTheme := true;
		/* scope */
		FOR rec2 IN SELECT id_theme FROM rel_themes WHERE id_scope = rec.id_scope LOOP
			IF firstTheme THEN
				ctm := ctm || ' @';
			ELSE
				ctm := ctm || ' , ';
			END IF;	
			ctm := ctm || to_ctm_identity(rec2.id_theme);
			firstTheme := false;
		END LOOP;
		/* reifier */
		IF rec.id_reifier IS NOT NULL THEN
			ctm := ctm || ' ~ ';
			ctm := ctm || to_ctm_identity(rec.id_reifier); 
		END IF;
		IF first THEN
			first := false;
		ELSE
			second := false;
		END IF;
		ctm := ctm || ' ';
	END LOOP;

	/* names */
	FOR rec IN Select id, id_type, value, id_scope, id_reifier FROM names WHERE id_parent = $1 LOOP	
		IF NOT second THEN
			ctm := ctm || ';\n\t';
		END IF;	
		ctm := ctm || '- ';	
		ctm := ctm || to_ctm_identity(rec.id_type); 		/* type */
		ctm := ctm || ' """';					
		ctm := ctm || rec.value;				/* value */
		ctm := ctm || '"""';
		firstTheme := true;
		/* scope */
		FOR rec2 IN SELECT id_theme FROM rel_themes WHERE id_scope = rec.id_scope LOOP
			IF firstTheme THEN
				ctm := ctm || ' @ ';
			ELSE
				ctm := ctm || ' , ';
			END IF;	
			ctm := ctm || to_ctm_identity(rec2.id_theme);
			firstTheme := false;
		END LOOP;
		/* reifier */
		IF rec.id_reifier IS NOT NULL THEN
			ctm := ctm || ' ~ ';
			ctm := ctm || to_ctm_identity(rec.id_reifier); 
		END IF;
		/* variants */
		FOR rec2 IN SELECT value, reference, id_scope, id_reifier FROM variants, locators AS l WHERE id_parent = rec.id AND id_datatype = l.id LOOP
			ctm := ctm || ' ( ';	
			ctm := ctm || '"""';					
			ctm := ctm || rec2.value;				/* value */
			ctm := ctm || '"""^^<';
			ctm := ctm || rec2.reference;				/* datatype */
			ctm := ctm || '> ';
			firstTheme := true;
			/* scope */
			FOR rec3 IN SELECT id_theme FROM rel_themes WHERE id_scope = rec2.id_scope LOOP
				IF firstTheme THEN
					ctm := ctm || ' @ ';
				ELSE
					ctm := ctm || ' , ';
				END IF;	
				ctm := ctm || to_ctm_identity(rec3.id_theme);
				firstTheme := false;
			END LOOP;
			/* reifier */
			IF rec2.id_reifier IS NOT NULL THEN
				ctm := ctm || ' ~ ';
				ctm := ctm || to_ctm_identity(rec2.id_reifier); 
			END IF;
			ctm := ctm || ' ) ';	
		END LOOP;
		IF first THEN
			first := false;
		ELSE
			second := false;
		END IF;
		ctm := ctm || ' ';
	END LOOP;

	ctm := ctm || '.\n';
	RETURN ctm;
END;$_$;


ALTER FUNCTION public.to_ctm_topic("topicId" bigint) OWNER TO postgres;

--
-- TOC entry 54 (class 1255 OID 26309022)
-- Dependencies: 6 426
-- Name: tokenize(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION tokenize(ctm text) RETURNS text[]
    LANGUAGE plpgsql
    AS $_$DECLARE
	tokens text[];
	token text;
	splitToken text;
	iToken bigint;
	iIndex integer;
	protectionToken text;
	lastToken text;
	lastLastToken text;
	multiLineComment boolean;
	singleLineComment boolean;
BEGIN
	multiLineComment := false;
	singleLineComment := false;
	protectionToken := NULL;
	token := '';
	FOR iToken IN 1..length($1) LOOP
		/* get next character */
		splitToken := substring($1,iToken,1);		
		/* check if protection is set */
		IF protectionToken IS NOT NULL THEN
			/* is end of IRI protection */
			IF protectionToken = '<' AND splitToken = '>' THEN
				protectionToken := NULL;
			/* is end of tripple quote string */
			ELSEIF protectionToken = '"""' AND splitToken = '"' AND lastToken = '"' AND lastLastToken = '"' AND length(token) > 3 THEN
				protectionToken := NULL;
			/* is end of string */
			ELSEIF protectionToken = '"' AND splitToken = '"' THEN
				iIndex := iToken + 1;
				/* is """ */
				IF substring($1,iIndex,1) = '"' THEN
					protectionToken := '"""';
				ELSE
					protectionToken := NULL;
				END IF;
			END IF;
			token := token || splitToken;
		/* is comment hash */
		ELSEIF splitToken = '#' THEN
			IF singleLineComment THEN
				CONTINUE;
			ELSEIF multiLineComment THEN
				multiLineComment := false;
			ELSE 
				IF length(token) > 0 THEN
					tokens := tokens || token;
					token := '';
				END IF;
				IF length ($1) > iToken +1 AND substring($1, iIndex+1,1) = '(' THEN
					multiLineComment := true;
				ELSE
					singleLineComment := true;
				END IF;
				CONTINUE;
			END IF;						
		/* is new-line */
		ELSEIF splitToken = '\n' OR splitToken = '\r' THEN
			IF singleLineComment THEN
				singleLineComment := false;
				CONTINUE;
			END IF;
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
		/* is comment ? */
		ELSEIF multiLineComment OR singleLineComment THEN
			CONTINUE;
		/* is IRI start token */
		ELSEIF splitToken = '<' THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			protectionToken := splitToken;
			token := token || splitToken;
		/* is string protection token */
		ELSEIF splitToken = '"' THEN		
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			iIndex := iToken + 2;
			/* is tripple quote */
			IF length($1) >= iIndex AND substring($1,iToken+1,1) = '"' AND substring($1,iIndex,1) = '"' THEN
				protectionToken := '"""';
			ELSE
				protectionToken := splitToken;
			END IF;
			token := token || splitToken;		
		/* is whitespace token */
		ELSEIF splitToken = ' 'OR splitToken = '\t' THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;	
		/* is scope token */
		ELSEIF splitToken = '@' THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is reifier token */
		ELSEIF splitToken = '~' THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is subject locator token */
		ELSEIF splitToken = '=' THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is item identifier token */
		ELSEIF splitToken = '^' THEN
			/* is datatyped */
			IF substring($1,iToken+1,1) = '^' THEN
				IF length(token) > 0 THEN
					tokens := tokens || token;
					token := '';
				END IF;
				splitToken := '^^';
				tokens := tokens || splitToken;
			ELSEIF lastToken != '^' THEN
				IF length(token) > 0 THEN
					tokens := tokens || token;
					token := '';
				END IF;
				tokens := tokens || splitToken;
			END IF;
			lastLastToken := lastToken;
			lastToken := '^';
			CONTINUE;
		/* is openeing bracket token */
		ELSEIF splitToken = '('  THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is closing bracket token */
		ELSEIF splitToken = ')'  THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is topic tail token */
		ELSEIF splitToken = ';'  THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is topic end token or decimal value */
		ELSEIF splitToken = '.' AND (substring($1,iToken+1,1) = ' '  OR substring($1,iToken+1,1) = '\n' )THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is colon token */
		ELSEIF splitToken = ':' AND lastToken = '>'  THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is comma token */
		ELSEIF splitToken = ',' THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* is minus token */
		ELSEIF splitToken = '-' AND ( substring($1,iToken+1,1) = '<' OR substring($1,iToken+1,1) = ' ' )  THEN
			IF length(token) > 0 THEN
				tokens := tokens || token;
				token := '';
			END IF;
			tokens := tokens || splitToken;
		/* add token */		
		ELSE
			token := token || splitToken;
		END IF;
		lastLastToken := lastToken;
		lastToken := splitToken;
	END LOOP;
	/* add last token */
	IF length(token) > 0 THEN
		tokens := tokens || token;
	END IF;
	RETURN tokens;
END$_$;


ALTER FUNCTION public.tokenize(ctm text) OWNER TO postgres;

--
-- TOC entry 2023 (class 0 OID 0)
-- Dependencies: 54
-- Name: FUNCTION tokenize(ctm text); Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON FUNCTION tokenize(ctm text) IS 'CTM Tokenizer Method';


--
-- TOC entry 63 (class 1255 OID 26385428)
-- Dependencies: 426 6
-- Name: topic_by_itemidentifier(bigint, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION topic_by_itemidentifier("topicMapId" bigint, ref text) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
BEGIN
	/* create locator */
	INSERT INTO locators (reference) SELECT $2 WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = $2);
	/* try to read topic */
	SELECT INTO rec t.id FROM topics AS t, locators AS l, rel_item_identifiers AS r WHERE l.reference = $2 AND r.id_construct = t.id AND r.id_locator = l.id AND t.id_topicmap = $1;
	/* topic exists */
	IF FOUND THEN
		RETURN rec.id;
	/* create new topic and add item-identifier */
	ELSE
		FOR rec IN INSERT INTO topics(id_topicmap, id_parent) VALUES ($1,$1) RETURNING id LOOP
			INSERT INTO rel_item_identifiers(id_construct, id_locator) SELECT rec.id, id FROM locators WHERE reference = $2;
			RETURN rec.id;
		END LOOP;
	END IF;
	RETURN NULL;
END$_$;


ALTER FUNCTION public.topic_by_itemidentifier("topicMapId" bigint, ref text) OWNER TO postgres;

--
-- TOC entry 55 (class 1255 OID 26383400)
-- Dependencies: 6 426
-- Name: topic_by_subjectidentifier(bigint, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION topic_by_subjectidentifier("topicMapId" bigint, ref text) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
	identifier text;
BEGIN
	/* create locator */
	INSERT INTO locators (reference) SELECT $2 WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = $2);
	/* try to read topic */
	SELECT INTO rec t.id FROM topics AS t, locators AS l, rel_subject_identifiers AS r WHERE l.reference = $2 AND r.id_topic = t.id AND r.id_locator = l.id AND t.id_topicmap = $1;
	/* topic exists */
	IF FOUND THEN
		RETURN rec.id;
	/* create new topic and add subject-identifier */
	ELSE
		FOR rec IN INSERT INTO topics(id_topicmap,id_parent) VALUES ($1,$1) RETURNING id LOOP
			INSERT INTO rel_subject_identifiers(id_topic, id_locator) SELECT rec.id, id FROM locators WHERE reference = $2;
			RETURN rec.id;
		END LOOP;
	END IF;
	RETURN NULL;
END$_$;


ALTER FUNCTION public.topic_by_subjectidentifier("topicMapId" bigint, ref text) OWNER TO postgres;

--
-- TOC entry 64 (class 1255 OID 26384321)
-- Dependencies: 6 426
-- Name: topic_by_subjectlocator(bigint, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION topic_by_subjectlocator("topicMapId" bigint, ref text) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$DECLARE
	rec RECORD;
BEGIN
	/* create locator */
	INSERT INTO locators (reference) SELECT $2 WHERE NOT EXISTS (SELECT id FROM locators WHERE reference = $2);
	/* try to read topic */
	SELECT INTO rec t.id FROM topics AS t, locators AS l, rel_subject_locators AS r WHERE l.reference = $2 AND r.id_topic = t.id AND r.id_locator = l.id AND t.id_topicmap = $1;
	/* topic exists */
	IF FOUND THEN
		RETURN rec.id;
	/* create new topic and add subject-locator */
	ELSE
		FOR rec IN INSERT INTO topics(id_topicmap,id_parent) VALUES ($1,$1) RETURNING id LOOP
			INSERT INTO rel_subject_locators(id_topic, id_locator) SELECT rec.id, id FROM locators WHERE reference = $2;
			RETURN rec.id;
		END LOOP;
	END IF;
	RETURN NULL;
END$_$;


ALTER FUNCTION public.topic_by_subjectlocator("topicMapId" bigint, ref text) OWNER TO postgres;

--
-- TOC entry 41 (class 1255 OID 26254276)
-- Dependencies: 426 6
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
-- TOC entry 42 (class 1255 OID 26254277)
-- Dependencies: 6 426
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
-- TOC entry 43 (class 1255 OID 26254278)
-- Dependencies: 6 426
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
-- TOC entry 44 (class 1255 OID 26254279)
-- Dependencies: 6 426
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
-- TOC entry 45 (class 1255 OID 26254280)
-- Dependencies: 6 426
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
-- TOC entry 46 (class 1255 OID 26254281)
-- Dependencies: 6 426
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
-- TOC entry 47 (class 1255 OID 26254282)
-- Dependencies: 426 6
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
-- TOC entry 48 (class 1255 OID 26254283)
-- Dependencies: 6 426
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
-- TOC entry 49 (class 1255 OID 26254284)
-- Dependencies: 6 426
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
-- TOC entry 50 (class 1255 OID 26254285)
-- Dependencies: 426 6
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
-- TOC entry 1864 (class 2605 OID 26254286)
-- Dependencies: 21 21
-- Name: CAST (character varying AS timestamp with time zone); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (character varying AS timestamp with time zone) WITH FUNCTION public.cast_as_timestamp(character varying) AS ASSIGNMENT;


SET search_path = public, pg_catalog;

--
-- TOC entry 1613 (class 1259 OID 26254287)
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
-- TOC entry 1614 (class 1259 OID 26254289)
-- Dependencies: 1923 6
-- Name: constructs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE constructs (
    id bigint DEFAULT nextval('seq_construct_id'::regclass) NOT NULL,
    id_parent bigint,
    id_topicmap bigint
);


ALTER TABLE public.constructs OWNER TO postgres;

--
-- TOC entry 1615 (class 1259 OID 26254294)
-- Dependencies: 1924 1614 6
-- Name: reifiables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE reifiables (
    id_reifier bigint
)
INHERITS (constructs);


ALTER TABLE public.reifiables OWNER TO postgres;

--
-- TOC entry 1616 (class 1259 OID 26254298)
-- Dependencies: 1925 1926 1615 6
-- Name: scopeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopeables (
    id_scope bigint DEFAULT 0 NOT NULL
)
INHERITS (reifiables);


ALTER TABLE public.scopeables OWNER TO postgres;

--
-- TOC entry 1617 (class 1259 OID 26254303)
-- Dependencies: 1927 6 1614
-- Name: typeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE typeables (
    id_type bigint NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.typeables OWNER TO postgres;

--
-- TOC entry 1618 (class 1259 OID 26254307)
-- Dependencies: 1928 1929 1617 1616 6
-- Name: associations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE associations (
    id_type bigint
)
INHERITS (scopeables, typeables);


ALTER TABLE public.associations OWNER TO postgres;

--
-- TOC entry 1619 (class 1259 OID 26254312)
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
-- TOC entry 1620 (class 1259 OID 26254314)
-- Dependencies: 1930 6
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
-- TOC entry 1621 (class 1259 OID 26254321)
-- Dependencies: 1931 6 1614
-- Name: literals; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE literals (
    value character varying NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.literals OWNER TO postgres;

--
-- TOC entry 1622 (class 1259 OID 26254329)
-- Dependencies: 1932 1933 1621 1616 6
-- Name: datatypeawares; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE datatypeawares (
    value character varying,
    id_datatype bigint NOT NULL
)
INHERITS (scopeables, literals);


ALTER TABLE public.datatypeawares OWNER TO postgres;

--
-- TOC entry 1623 (class 1259 OID 26254337)
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
-- TOC entry 1624 (class 1259 OID 26254348)
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
-- TOC entry 1625 (class 1259 OID 26254350)
-- Dependencies: 1934 6
-- Name: locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE locators (
    id bigint DEFAULT nextval('seq_locator_id'::regclass) NOT NULL,
    reference character varying(1024) NOT NULL
);


ALTER TABLE public.locators OWNER TO postgres;

--
-- TOC entry 1626 (class 1259 OID 26254357)
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
-- TOC entry 1627 (class 1259 OID 26254364)
-- Dependencies: 1935 1936 1616 1621 1617 6
-- Name: names; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE names (
    id_type bigint,
    value character varying
)
INHERITS (scopeables, typeables, literals);


ALTER TABLE public.names OWNER TO postgres;

--
-- TOC entry 1628 (class 1259 OID 26254372)
-- Dependencies: 1937 1938 6 1622 1617
-- Name: occurrences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE occurrences (
    id_type bigint
)
INHERITS (datatypeawares, typeables);


ALTER TABLE public.occurrences OWNER TO postgres;

--
-- TOC entry 1629 (class 1259 OID 26254380)
-- Dependencies: 6
-- Name: rel_instance_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_instance_of (
    id_instance bigint NOT NULL,
    id_type bigint NOT NULL
);


ALTER TABLE public.rel_instance_of OWNER TO postgres;

--
-- TOC entry 1630 (class 1259 OID 26254383)
-- Dependencies: 6
-- Name: rel_item_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_item_identifiers (
    id_construct bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_item_identifiers OWNER TO postgres;

--
-- TOC entry 1631 (class 1259 OID 26254386)
-- Dependencies: 6
-- Name: rel_kind_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_kind_of (
    id_subtype bigint,
    id_supertype bigint
);


ALTER TABLE public.rel_kind_of OWNER TO postgres;

--
-- TOC entry 1632 (class 1259 OID 26254389)
-- Dependencies: 6
-- Name: rel_subject_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_identifiers (
    id_topic bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_identifiers OWNER TO postgres;

--
-- TOC entry 1633 (class 1259 OID 26254392)
-- Dependencies: 6
-- Name: rel_subject_locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_locators (
    id_topic bigint,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_locators OWNER TO postgres;

--
-- TOC entry 1634 (class 1259 OID 26254395)
-- Dependencies: 6
-- Name: rel_themes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_themes (
    id_scope bigint,
    id_theme bigint NOT NULL
);


ALTER TABLE public.rel_themes OWNER TO postgres;

--
-- TOC entry 1635 (class 1259 OID 26254398)
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
-- TOC entry 1636 (class 1259 OID 26254400)
-- Dependencies: 1939 6
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
-- TOC entry 1637 (class 1259 OID 26254404)
-- Dependencies: 1940 1615 1617 6
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE roles (
    id_type bigint,
    id_player bigint NOT NULL
)
INHERITS (reifiables, typeables);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 1638 (class 1259 OID 26254408)
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
-- TOC entry 1639 (class 1259 OID 26254410)
-- Dependencies: 1941 6
-- Name: scopes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopes (
    id bigint DEFAULT nextval('seq_scope_id'::regclass) NOT NULL,
    id_topicmap bigint
);


ALTER TABLE public.scopes OWNER TO postgres;

--
-- TOC entry 1640 (class 1259 OID 26254415)
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
-- TOC entry 1641 (class 1259 OID 26254417)
-- Dependencies: 6
-- Name: tags; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tags (
    tag character varying NOT NULL,
    "time" timestamp with time zone NOT NULL
);


ALTER TABLE public.tags OWNER TO postgres;

--
-- TOC entry 1642 (class 1259 OID 26254423)
-- Dependencies: 1942 1615 6
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
-- TOC entry 1643 (class 1259 OID 26254427)
-- Dependencies: 1943 1614 6
-- Name: topics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topics (
)
INHERITS (constructs);


ALTER TABLE public.topics OWNER TO postgres;

--
-- TOC entry 1644 (class 1259 OID 26254431)
-- Dependencies: 1944 1945 1622 6
-- Name: variants; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE variants (
)
INHERITS (datatypeawares);


ALTER TABLE public.variants OWNER TO postgres;

--
-- TOC entry 1953 (class 2606 OID 26254441)
-- Dependencies: 1618 1618
-- Name: pk_associations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT pk_associations PRIMARY KEY (id);


--
-- TOC entry 1955 (class 2606 OID 26254443)
-- Dependencies: 1620 1620
-- Name: pk_changeset; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT pk_changeset PRIMARY KEY (id);


--
-- TOC entry 1947 (class 2606 OID 26254445)
-- Dependencies: 1614 1614
-- Name: pk_constructs; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT pk_constructs PRIMARY KEY (id);


--
-- TOC entry 1957 (class 2606 OID 26254448)
-- Dependencies: 1622 1622
-- Name: pk_datatypeawares; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT pk_datatypeawares PRIMARY KEY (id);


--
-- TOC entry 1959 (class 2606 OID 26254450)
-- Dependencies: 1623 1623 1623 1623
-- Name: pk_history; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY history
    ADD CONSTRAINT pk_history PRIMARY KEY (id, id_revision, id_topicmap);


--
-- TOC entry 1961 (class 2606 OID 26254452)
-- Dependencies: 1625 1625
-- Name: pk_locators; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT pk_locators PRIMARY KEY (id);


--
-- TOC entry 1965 (class 2606 OID 26254454)
-- Dependencies: 1626 1626 1626
-- Name: pk_metadata; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT pk_metadata PRIMARY KEY (id_revision, key);


--
-- TOC entry 1967 (class 2606 OID 26254457)
-- Dependencies: 1627 1627
-- Name: pk_names; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY names
    ADD CONSTRAINT pk_names PRIMARY KEY (id);


--
-- TOC entry 1969 (class 2606 OID 26254459)
-- Dependencies: 1628 1628
-- Name: pk_occurrences; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT pk_occurrences PRIMARY KEY (id);


--
-- TOC entry 1949 (class 2606 OID 26254462)
-- Dependencies: 1615 1615
-- Name: pk_reifiables; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT pk_reifiables PRIMARY KEY (id);


--
-- TOC entry 1971 (class 2606 OID 26254466)
-- Dependencies: 1636 1636
-- Name: pk_revisions; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT pk_revisions PRIMARY KEY (id);


--
-- TOC entry 1973 (class 2606 OID 26254469)
-- Dependencies: 1637 1637
-- Name: pk_roles; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);


--
-- TOC entry 1975 (class 2606 OID 26254471)
-- Dependencies: 1639 1639
-- Name: pk_scope; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT pk_scope PRIMARY KEY (id);


--
-- TOC entry 1951 (class 2606 OID 26254473)
-- Dependencies: 1616 1616
-- Name: pk_scopes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT pk_scopes PRIMARY KEY (id);


--
-- TOC entry 1977 (class 2606 OID 26254477)
-- Dependencies: 1641 1641
-- Name: pk_tags; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tags
    ADD CONSTRAINT pk_tags PRIMARY KEY (tag);


--
-- TOC entry 1979 (class 2606 OID 26254482)
-- Dependencies: 1642 1642
-- Name: pk_topicmap; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT pk_topicmap PRIMARY KEY (id);


--
-- TOC entry 1981 (class 2606 OID 26254484)
-- Dependencies: 1643 1643
-- Name: pk_topics; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT pk_topics PRIMARY KEY (id);


--
-- TOC entry 1983 (class 2606 OID 26254486)
-- Dependencies: 1644 1644
-- Name: pk_variants; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT pk_variants PRIMARY KEY (id);


--
-- TOC entry 1963 (class 2606 OID 26254489)
-- Dependencies: 1625 1625
-- Name: unique_reference; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT unique_reference UNIQUE (reference);


--
-- TOC entry 2016 (class 2620 OID 26254490)
-- Dependencies: 1637 23
-- Name: trigger_detect_duplicate_associations; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_associations
    AFTER UPDATE ON roles
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_associations();


--
-- TOC entry 2014 (class 2620 OID 26254491)
-- Dependencies: 24 1627
-- Name: trigger_detect_duplicate_names_on_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_names_on_update
    BEFORE UPDATE ON names
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_names();


--
-- TOC entry 2015 (class 2620 OID 26254492)
-- Dependencies: 25 1628
-- Name: trigger_detect_duplicate_occurrences; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_occurrences
    BEFORE UPDATE ON occurrences
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_occurrences();


--
-- TOC entry 2017 (class 2620 OID 26254493)
-- Dependencies: 26 1637
-- Name: trigger_detect_duplicate_roles; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_detect_duplicate_roles
    BEFORE INSERT OR UPDATE ON roles
    FOR EACH ROW
    EXECUTE PROCEDURE detect_duplicate_roles();

ALTER TABLE roles DISABLE TRIGGER trigger_detect_duplicate_roles;


--
-- TOC entry 2010 (class 2606 OID 26254494)
-- Dependencies: 1960 1625 1642
-- Name: fk_baselocator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_baselocator FOREIGN KEY (id_base_locator) REFERENCES locators(id);


--
-- TOC entry 1990 (class 2606 OID 26254499)
-- Dependencies: 1622 1625 1960
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id) ON DELETE RESTRICT;


--
-- TOC entry 1996 (class 2606 OID 26254504)
-- Dependencies: 1629 1643 1980
-- Name: fk_instance; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_instance FOREIGN KEY (id_instance) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1998 (class 2606 OID 26254509)
-- Dependencies: 1630 1625 1960
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 2001 (class 2606 OID 26254514)
-- Dependencies: 1632 1625 1960
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 2003 (class 2606 OID 26254519)
-- Dependencies: 1633 1625 1960
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1988 (class 2606 OID 26254524)
-- Dependencies: 1618 1642 1978
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1994 (class 2606 OID 26254529)
-- Dependencies: 1980 1627 1643
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2008 (class 2606 OID 26254534)
-- Dependencies: 1952 1637 1618
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES associations(id) ON DELETE CASCADE;


--
-- TOC entry 2012 (class 2606 OID 26254539)
-- Dependencies: 1642 1643 1978
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 2013 (class 2606 OID 26254544)
-- Dependencies: 1644 1627 1966
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES names(id) ON DELETE CASCADE;


--
-- TOC entry 1995 (class 2606 OID 26254549)
-- Dependencies: 1980 1643 1628
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2011 (class 2606 OID 26254554)
-- Dependencies: 1643 1980 1642
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1985 (class 2606 OID 26254559)
-- Dependencies: 1615 1643 1980
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id) ON DELETE SET NULL;


--
-- TOC entry 1991 (class 2606 OID 26254564)
-- Dependencies: 1623 1970 1636
-- Name: fk_reivision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_reivision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1989 (class 2606 OID 26254570)
-- Dependencies: 1970 1620 1636
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1993 (class 2606 OID 26254575)
-- Dependencies: 1626 1636 1970
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 2005 (class 2606 OID 26254580)
-- Dependencies: 1974 1639 1634
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1986 (class 2606 OID 26254585)
-- Dependencies: 1616 1639 1974
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1999 (class 2606 OID 26254590)
-- Dependencies: 1643 1980 1631
-- Name: fk_subtype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_subtype FOREIGN KEY (id_subtype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2000 (class 2606 OID 26254595)
-- Dependencies: 1643 1631 1980
-- Name: fk_supertype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_supertype FOREIGN KEY (id_supertype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2006 (class 2606 OID 26254600)
-- Dependencies: 1980 1643 1634
-- Name: fk_theme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_theme FOREIGN KEY (id_theme) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2002 (class 2606 OID 26254605)
-- Dependencies: 1980 1632 1643
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2004 (class 2606 OID 26254610)
-- Dependencies: 1980 1633 1643
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1984 (class 2606 OID 26254615)
-- Dependencies: 1978 1642 1614
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 2009 (class 2606 OID 26254620)
-- Dependencies: 1978 1639 1642
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1992 (class 2606 OID 26254625)
-- Dependencies: 1978 1623 1642
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 2007 (class 2606 OID 26254630)
-- Dependencies: 1642 1636 1978
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1997 (class 2606 OID 26254635)
-- Dependencies: 1643 1629 1980
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1987 (class 2606 OID 26254640)
-- Dependencies: 1643 1980 1617
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typeables
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 2022 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2011-01-07 11:31:03

--
-- PostgreSQL database dump complete
--


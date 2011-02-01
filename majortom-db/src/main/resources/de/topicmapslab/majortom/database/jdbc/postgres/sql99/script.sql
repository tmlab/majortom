--
-- PostgreSQL database dump
--

-- Started on 2010-08-12 15:27:30

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 370 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

--
-- TOC entry 1557 (class 1259 OID 3046880)
-- Dependencies: 6
-- Name: seq_construct_id; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_construct_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_with_oids = true;

--
-- TOC entry 1558 (class 1259 OID 3046882)
-- Dependencies: 1866 6
-- Name: constructs; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE constructs (
    id bigint DEFAULT nextval('seq_construct_id'::regclass) NOT NULL,
    id_parent bigint,
    id_topicmap bigint
);


--
-- TOC entry 1559 (class 1259 OID 3046886)
-- Dependencies: 1867 1558 6
-- Name: reifiables; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE reifiables (
    id_reifier bigint
)
INHERITS (constructs);


--
-- TOC entry 1560 (class 1259 OID 3046890)
-- Dependencies: 1868 1869 6 1559
-- Name: scopeables; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE scopeables (
    id_scope bigint DEFAULT 0 NOT NULL
)
INHERITS (reifiables);


--
-- TOC entry 1561 (class 1259 OID 3046895)
-- Dependencies: 1870 1558 6
-- Name: typeables; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE typeables (
    id_type bigint NOT NULL
)
INHERITS (constructs);


--
-- TOC entry 1562 (class 1259 OID 3046899)
-- Dependencies: 1871 1872 6 1561 1560
-- Name: associations; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE associations (
)
INHERITS (scopeables, typeables);


--
-- TOC entry 1563 (class 1259 OID 3046904)
-- Dependencies: 6
-- Name: seq_changeset_id; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_changeset_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1564 (class 1259 OID 3046906)
-- Dependencies: 1873 6
-- Name: changesets; Type: TABLE; Schema: public; Owner: -; Tablespace: 
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


--
-- TOC entry 1565 (class 1259 OID 3046913)
-- Dependencies: 1874 6 1558
-- Name: literals; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE literals (
    value character varying NOT NULL
)
INHERITS (constructs);


--
-- TOC entry 1566 (class 1259 OID 3046920)
-- Dependencies: 1875 1876 6 1565 1560
-- Name: datatypeawares; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE datatypeawares (
    id_datatype bigint NOT NULL
)
INHERITS (scopeables, literals);


--
-- TOC entry 1567 (class 1259 OID 3046928)
-- Dependencies: 6
-- Name: history; Type: TABLE; Schema: public; Owner: -; Tablespace: 
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
  	bestlabel character varying(256),
  	bestidentifier character varying(256)
);


--
-- TOC entry 1568 (class 1259 OID 3046934)
-- Dependencies: 6
-- Name: seq_locator_id; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_locator_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1569 (class 1259 OID 3046936)
-- Dependencies: 1877 6
-- Name: locators; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE locators (
    id bigint DEFAULT nextval('seq_locator_id'::regclass) NOT NULL,
    reference character varying(1024) NOT NULL
);


--
-- TOC entry 1570 (class 1259 OID 3046943)
-- Dependencies: 6
-- Name: metadata; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE metadata (
    id_revision bigint NOT NULL,
    key character varying NOT NULL,
    value character varying
);


--
-- TOC entry 1571 (class 1259 OID 3046949)
-- Dependencies: 1878 1879 1560 1561 6 1565
-- Name: names; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE names (
)
INHERITS (scopeables, typeables, literals);


--
-- TOC entry 1572 (class 1259 OID 3046957)
-- Dependencies: 1880 1881 1566 6 1561
-- Name: occurrences; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE occurrences (
)
INHERITS (datatypeawares, typeables);


--
-- TOC entry 1573 (class 1259 OID 3046965)
-- Dependencies: 6
-- Name: rel_instance_of; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rel_instance_of (
    id_instance bigint NOT NULL,
    id_type bigint NOT NULL
);


--
-- TOC entry 1574 (class 1259 OID 3046968)
-- Dependencies: 6
-- Name: rel_item_identifiers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rel_item_identifiers (
    id_construct bigint NOT NULL,
    id_locator bigint NOT NULL
);


--
-- TOC entry 1575 (class 1259 OID 3046971)
-- Dependencies: 6
-- Name: rel_kind_of; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rel_kind_of (
    id_subtype bigint,
    id_supertype bigint
);


--
-- TOC entry 1576 (class 1259 OID 3046974)
-- Dependencies: 6
-- Name: rel_subject_identifiers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rel_subject_identifiers (
    id_topic bigint NOT NULL,
    id_locator bigint NOT NULL
);


--
-- TOC entry 1577 (class 1259 OID 3046977)
-- Dependencies: 6
-- Name: rel_subject_locators; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rel_subject_locators (
    id_topic bigint,
    id_locator bigint NOT NULL
);


--
-- TOC entry 1578 (class 1259 OID 3046980)
-- Dependencies: 6
-- Name: rel_themes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rel_themes (
    id_scope bigint,
    id_theme bigint NOT NULL
);


--
-- TOC entry 1579 (class 1259 OID 3046983)
-- Dependencies: 6
-- Name: seq_revision_id; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_revision_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1580 (class 1259 OID 3046985)
-- Dependencies: 1882 6
-- Name: revisions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE revisions (
    id bigint DEFAULT nextval('seq_revision_id'::regclass) NOT NULL,
    "time" timestamp with time zone NOT NULL,
    id_topicmap bigint NOT NULL,
   	type character varying(128) NOT NULL
);


--
-- TOC entry 1581 (class 1259 OID 3046989)
-- Dependencies: 1883 1559 6 1561
-- Name: roles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE roles (
    id_player bigint NOT NULL
)
INHERITS (reifiables, typeables);


--
-- TOC entry 1582 (class 1259 OID 3046993)
-- Dependencies: 6
-- Name: seq_scope_id; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE seq_scope_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1583 (class 1259 OID 3046995)
-- Dependencies: 1884 6
-- Name: scopes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE scopes (
    id bigint DEFAULT nextval('seq_scope_id'::regclass) NOT NULL,
    id_topicmap bigint
);

--
-- TOC entry 1585 (class 1259 OID 3047001)
-- Dependencies: 6
-- Name: tags; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE tags (
    tag character varying NOT NULL,
    "time" timestamp with time zone NOT NULL
);


--
-- TOC entry 1586 (class 1259 OID 3047007)
-- Dependencies: 1885 1559 6
-- Name: topicmaps; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE topicmaps (
    id_base_locator bigint NOT NULL
)
INHERITS (reifiables);


--
-- TOC entry 1587 (class 1259 OID 3047011)
-- Dependencies: 1886 6 1558
-- Name: topics; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE topics (
)
INHERITS (constructs);


--
-- TOC entry 1588 (class 1259 OID 3047015)
-- Dependencies: 1887 1888 6 1566
-- Name: variants; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE variants (
)
INHERITS (datatypeawares);


--
-- TOC entry 1896 (class 2606 OID 3047024)
-- Dependencies: 1562 1562
-- Name: pk_associations; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT pk_associations PRIMARY KEY (id);


--
-- TOC entry 1898 (class 2606 OID 3047026)
-- Dependencies: 1564 1564
-- Name: pk_changeset; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT pk_changeset PRIMARY KEY (id);


--
-- TOC entry 1890 (class 2606 OID 3047028)
-- Dependencies: 1558 1558
-- Name: pk_constructs; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT pk_constructs PRIMARY KEY (id);


--
-- TOC entry 1900 (class 2606 OID 3047030)
-- Dependencies: 1566 1566
-- Name: pk_datatypeawares; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT pk_datatypeawares PRIMARY KEY (id);


--
-- TOC entry 1902 (class 2606 OID 3047032)
-- Dependencies: 1567 1567 1567 1567
-- Name: pk_history; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY history
    ADD CONSTRAINT pk_history PRIMARY KEY (id, id_revision, id_topicmap);


--
-- TOC entry 1904 (class 2606 OID 3047034)
-- Dependencies: 1569 1569
-- Name: pk_locators; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT pk_locators PRIMARY KEY (id);


--
-- TOC entry 1908 (class 2606 OID 3047036)
-- Dependencies: 1570 1570 1570
-- Name: pk_metadata; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT pk_metadata PRIMARY KEY (id_revision, key);


--
-- TOC entry 1910 (class 2606 OID 3047038)
-- Dependencies: 1571 1571
-- Name: pk_names; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY names
    ADD CONSTRAINT pk_names PRIMARY KEY (id);


--
-- TOC entry 1912 (class 2606 OID 3047040)
-- Dependencies: 1572 1572
-- Name: pk_occurrences; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT pk_occurrences PRIMARY KEY (id);


--
-- TOC entry 1892 (class 2606 OID 3047042)
-- Dependencies: 1559 1559
-- Name: pk_reifiables; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT pk_reifiables PRIMARY KEY (id);


--
-- TOC entry 1914 (class 2606 OID 3047044)
-- Dependencies: 1580 1580
-- Name: pk_revisions; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT pk_revisions PRIMARY KEY (id);


--
-- TOC entry 1916 (class 2606 OID 3047046)
-- Dependencies: 1581 1581
-- Name: pk_roles; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);


--
-- TOC entry 1918 (class 2606 OID 3047048)
-- Dependencies: 1583 1583
-- Name: pk_scope; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT pk_scope PRIMARY KEY (id);


--
-- TOC entry 1894 (class 2606 OID 3047050)
-- Dependencies: 1560 1560
-- Name: pk_scopes; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT pk_scopes PRIMARY KEY (id);


--
-- TOC entry 1920 (class 2606 OID 3047052)
-- Dependencies: 1585 1585
-- Name: pk_tags; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY tags
    ADD CONSTRAINT pk_tags PRIMARY KEY (tag);


--
-- TOC entry 1922 (class 2606 OID 3047054)
-- Dependencies: 1586 1586
-- Name: pk_topicmap; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT pk_topicmap PRIMARY KEY (id);


--
-- TOC entry 1924 (class 2606 OID 3047056)
-- Dependencies: 1587 1587
-- Name: pk_topics; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT pk_topics PRIMARY KEY (id);


--
-- TOC entry 1926 (class 2606 OID 3047058)
-- Dependencies: 1588 1588
-- Name: pk_variants; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT pk_variants PRIMARY KEY (id);


--
-- TOC entry 1906 (class 2606 OID 3047060)
-- Dependencies: 1569 1569
-- Name: unique_reference; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT unique_reference UNIQUE (reference);


--
-- TOC entry 1953 (class 2606 OID 3047061)
-- Dependencies: 1903 1569 1586
-- Name: fk_baselocator; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_baselocator FOREIGN KEY (id_base_locator) REFERENCES locators(id);


--
-- TOC entry 1933 (class 2606 OID 3047066)
-- Dependencies: 1903 1569 1566
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id) ON DELETE RESTRICT;


--
-- TOC entry 1939 (class 2606 OID 3047071)
-- Dependencies: 1573 1587 1923
-- Name: fk_instance; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_instance FOREIGN KEY (id_instance) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1941 (class 2606 OID 3047076)
-- Dependencies: 1574 1569 1903
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1944 (class 2606 OID 3047081)
-- Dependencies: 1576 1569 1903
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1946 (class 2606 OID 3047086)
-- Dependencies: 1577 1569 1903
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1931 (class 2606 OID 3047091)
-- Dependencies: 1562 1586 1921
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1937 (class 2606 OID 3047096)
-- Dependencies: 1571 1587 1923
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1938 (class 2606 OID 3047101)
-- Dependencies: 1572 1587 1923
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1951 (class 2606 OID 3047106)
-- Dependencies: 1581 1562 1895
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES associations(id) ON DELETE CASCADE;


--
-- TOC entry 1955 (class 2606 OID 3047111)
-- Dependencies: 1587 1586 1921
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1956 (class 2606 OID 3047116)
-- Dependencies: 1571 1909 1588
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES names(id) ON DELETE CASCADE;


--
-- TOC entry 1954 (class 2606 OID 3047121)
-- Dependencies: 1586 1923 1587
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1928 (class 2606 OID 3047126)
-- Dependencies: 1923 1587 1559
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id) ON DELETE SET NULL;


--
-- TOC entry 1934 (class 2606 OID 3047131)
-- Dependencies: 1913 1567 1580
-- Name: fk_reivision; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_reivision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1932 (class 2606 OID 3047136)
-- Dependencies: 1564 1580 1913
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY changesets
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1936 (class 2606 OID 3047141)
-- Dependencies: 1570 1580 1913
-- Name: fk_revision; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY metadata
    ADD CONSTRAINT fk_revision FOREIGN KEY (id_revision) REFERENCES revisions(id) ON DELETE CASCADE;


--
-- TOC entry 1948 (class 2606 OID 3047146)
-- Dependencies: 1583 1578 1917
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1929 (class 2606 OID 3047151)
-- Dependencies: 1560 1583 1917
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1942 (class 2606 OID 3047156)
-- Dependencies: 1587 1923 1575
-- Name: fk_subtype; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_subtype FOREIGN KEY (id_subtype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1943 (class 2606 OID 3047161)
-- Dependencies: 1923 1575 1587
-- Name: fk_supertype; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_supertype FOREIGN KEY (id_supertype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1949 (class 2606 OID 3047166)
-- Dependencies: 1587 1578 1923
-- Name: fk_theme; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_theme FOREIGN KEY (id_theme) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1945 (class 2606 OID 3047171)
-- Dependencies: 1576 1587 1923
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1947 (class 2606 OID 3047176)
-- Dependencies: 1923 1587 1577
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1927 (class 2606 OID 3047181)
-- Dependencies: 1921 1558 1586
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1952 (class 2606 OID 3047186)
-- Dependencies: 1586 1921 1583
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1935 (class 2606 OID 3047191)
-- Dependencies: 1921 1567 1586
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY history
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1950 (class 2606 OID 3047196)
-- Dependencies: 1921 1580 1586
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY revisions
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1940 (class 2606 OID 3047201)
-- Dependencies: 1923 1573 1587
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1930 (class 2606 OID 3047206)
-- Dependencies: 1561 1923 1587
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY typeables
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1961 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
--REVOKE ALL ON SCHEMA public FROM postgres;
--GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2010-08-12 15:27:31

--
-- PostgreSQL database dump complete
--


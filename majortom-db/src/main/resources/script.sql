--
-- PostgreSQL database dump
--

-- Started on 2010-07-13 10:29:10

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 354 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 1541 (class 1259 OID 24642)
-- Dependencies: 3
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
-- TOC entry 1547 (class 1259 OID 24672)
-- Dependencies: 1845 3
-- Name: constructs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE constructs (
    id bigint DEFAULT nextval('seq_construct_id'::regclass) NOT NULL,
    id_parent bigint,
    id_topicmap bigint
);


ALTER TABLE public.constructs OWNER TO postgres;

--
-- TOC entry 1548 (class 1259 OID 24683)
-- Dependencies: 1846 1547 3
-- Name: reifiables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE reifiables (
    id_reifier bigint
)
INHERITS (constructs);


ALTER TABLE public.reifiables OWNER TO postgres;

--
-- TOC entry 1551 (class 1259 OID 24716)
-- Dependencies: 1849 1850 1548 3
-- Name: scopeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopeables (
    id_scope bigint DEFAULT 0 NOT NULL
)
INHERITS (reifiables);


ALTER TABLE public.scopeables OWNER TO postgres;

--
-- TOC entry 1564 (class 1259 OID 25072)
-- Dependencies: 1862 1547 3
-- Name: typeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE typeables (
    id_type bigint NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.typeables OWNER TO postgres;

--
-- TOC entry 1556 (class 1259 OID 24894)
-- Dependencies: 1859 1860 3 1564 1551
-- Name: associations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE associations (
    id_type bigint
)
INHERITS (scopeables, typeables);


ALTER TABLE public.associations OWNER TO postgres;

--
-- TOC entry 1565 (class 1259 OID 25081)
-- Dependencies: 1863 3 1547
-- Name: literals; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE literals (
    value character varying NOT NULL
)
INHERITS (constructs);


ALTER TABLE public.literals OWNER TO postgres;

--
-- TOC entry 1552 (class 1259 OID 24732)
-- Dependencies: 1851 1852 1551 3 1565
-- Name: datatypeawares; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE datatypeawares (
    id_datatype bigint NOT NULL,
    value character varying
)
INHERITS (scopeables, literals);


ALTER TABLE public.datatypeawares OWNER TO postgres;

--
-- TOC entry 1543 (class 1259 OID 24646)
-- Dependencies: 3
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
-- TOC entry 1545 (class 1259 OID 24650)
-- Dependencies: 1843 3
-- Name: locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE locators (
    id bigint DEFAULT nextval('seq_locator_id'::regclass) NOT NULL,
    reference character varying(1024) NOT NULL
);


ALTER TABLE public.locators OWNER TO postgres;

--
-- TOC entry 1554 (class 1259 OID 24780)
-- Dependencies: 1855 1856 1565 1551 1564 3
-- Name: names; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE names (
    value character varying,
    id_type bigint
)
INHERITS (scopeables, typeables, literals);


ALTER TABLE public.names OWNER TO postgres;

--
-- TOC entry 1555 (class 1259 OID 24855)
-- Dependencies: 1857 1858 1552 3 1564
-- Name: occurrences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE occurrences (
    id_type bigint
)
INHERITS (datatypeawares, typeables);


ALTER TABLE public.occurrences OWNER TO postgres;

--
-- TOC entry 1559 (class 1259 OID 24971)
-- Dependencies: 3
-- Name: rel_instance_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_instance_of (
    id_instance bigint NOT NULL,
    id_type bigint NOT NULL
);


ALTER TABLE public.rel_instance_of OWNER TO postgres;

--
-- TOC entry 1561 (class 1259 OID 25001)
-- Dependencies: 3
-- Name: rel_item_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_item_identifiers (
    id_construct bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_item_identifiers OWNER TO postgres;

--
-- TOC entry 1558 (class 1259 OID 24956)
-- Dependencies: 3
-- Name: rel_kind_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_kind_of (
    id_subtype bigint,
    id_supertype bigint
);


ALTER TABLE public.rel_kind_of OWNER TO postgres;

--
-- TOC entry 1562 (class 1259 OID 25016)
-- Dependencies: 3
-- Name: rel_subject_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_identifiers (
    id_topic bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_identifiers OWNER TO postgres;

--
-- TOC entry 1563 (class 1259 OID 25031)
-- Dependencies: 3
-- Name: rel_subject_locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_locators (
    id_topic bigint,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_locators OWNER TO postgres;

--
-- TOC entry 1560 (class 1259 OID 24986)
-- Dependencies: 3
-- Name: rel_themes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_themes (
    id_scope bigint,
    id_theme bigint NOT NULL
);


ALTER TABLE public.rel_themes OWNER TO postgres;

--
-- TOC entry 1557 (class 1259 OID 24925)
-- Dependencies: 1861 3 1564 1548
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE roles (
    id_type bigint,
    id_player bigint NOT NULL
)
INHERITS (reifiables, typeables);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 1544 (class 1259 OID 24648)
-- Dependencies: 3
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
-- TOC entry 1550 (class 1259 OID 24707)
-- Dependencies: 1848 3
-- Name: scopes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopes (
    id bigint DEFAULT nextval('seq_scope_id'::regclass) NOT NULL
);


ALTER TABLE public.scopes OWNER TO postgres;

--
-- TOC entry 1542 (class 1259 OID 24644)
-- Dependencies: 3
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
-- TOC entry 1546 (class 1259 OID 24661)
-- Dependencies: 1844 3
-- Name: topicmaps; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topicmaps (
    id bigint DEFAULT nextval('seq_topicmap_id'::regclass) NOT NULL,
    id_base_locator bigint NOT NULL,
    id_reifier bigint
)
INHERITS (reifiables);


ALTER TABLE public.topicmaps OWNER TO postgres;

--
-- TOC entry 1549 (class 1259 OID 24687)
-- Dependencies: 1847 1547 3
-- Name: topics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topics (
)
INHERITS (constructs);


ALTER TABLE public.topics OWNER TO postgres;

--
-- TOC entry 1553 (class 1259 OID 24751)
-- Dependencies: 1853 1854 3 1552
-- Name: variants; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE variants (
)
INHERITS (datatypeawares);


ALTER TABLE public.variants OWNER TO postgres;

--
-- TOC entry 1889 (class 2606 OID 24899)
-- Dependencies: 1556 1556
-- Name: pk_associations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT pk_associations PRIMARY KEY (id);


--
-- TOC entry 1871 (class 2606 OID 24677)
-- Dependencies: 1547 1547
-- Name: pk_constructs; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT pk_constructs PRIMARY KEY (id);


--
-- TOC entry 1881 (class 2606 OID 24737)
-- Dependencies: 1552 1552
-- Name: pk_datatypeawares; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT pk_datatypeawares PRIMARY KEY (id);


--
-- TOC entry 1865 (class 2606 OID 24658)
-- Dependencies: 1545 1545
-- Name: pk_locators; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT pk_locators PRIMARY KEY (id);


--
-- TOC entry 1885 (class 2606 OID 24788)
-- Dependencies: 1554 1554
-- Name: pk_names; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY names
    ADD CONSTRAINT pk_names PRIMARY KEY (id);


--
-- TOC entry 1887 (class 2606 OID 24863)
-- Dependencies: 1555 1555
-- Name: pk_occurrences; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT pk_occurrences PRIMARY KEY (id);


--
-- TOC entry 1873 (class 2606 OID 24698)
-- Dependencies: 1548 1548
-- Name: pk_reifiables; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT pk_reifiables PRIMARY KEY (id);


--
-- TOC entry 1891 (class 2606 OID 24930)
-- Dependencies: 1557 1557
-- Name: pk_roles; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);


--
-- TOC entry 1877 (class 2606 OID 24715)
-- Dependencies: 1550 1550
-- Name: pk_scope; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT pk_scope PRIMARY KEY (id);


--
-- TOC entry 1879 (class 2606 OID 24721)
-- Dependencies: 1551 1551
-- Name: pk_scopes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT pk_scopes PRIMARY KEY (id);


--
-- TOC entry 1869 (class 2606 OID 24666)
-- Dependencies: 1546 1546
-- Name: pk_topicmap; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT pk_topicmap PRIMARY KEY (id);


--
-- TOC entry 1875 (class 2606 OID 24696)
-- Dependencies: 1549 1549
-- Name: pk_topics; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT pk_topics PRIMARY KEY (id);


--
-- TOC entry 1883 (class 2606 OID 24759)
-- Dependencies: 1553 1553
-- Name: pk_variants; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT pk_variants PRIMARY KEY (id);


--
-- TOC entry 1867 (class 2606 OID 24660)
-- Dependencies: 1545 1545
-- Name: unique_reference; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT unique_reference UNIQUE (reference);


--
-- TOC entry 1892 (class 2606 OID 24667)
-- Dependencies: 1546 1545 1864
-- Name: fk_baselocator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_baselocator FOREIGN KEY (id_base_locator) REFERENCES locators(id);


--
-- TOC entry 1898 (class 2606 OID 25842)
-- Dependencies: 1545 1864 1552
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id) ON DELETE RESTRICT;


--
-- TOC entry 1906 (class 2606 OID 25852)
-- Dependencies: 1559 1874 1549
-- Name: fk_instance; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_instance FOREIGN KEY (id_instance) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1910 (class 2606 OID 25011)
-- Dependencies: 1864 1545 1561
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1911 (class 2606 OID 25026)
-- Dependencies: 1562 1545 1864
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1913 (class 2606 OID 25041)
-- Dependencies: 1545 1563 1864
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1902 (class 2606 OID 25792)
-- Dependencies: 1556 1546 1868
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1900 (class 2606 OID 25817)
-- Dependencies: 1874 1554 1549
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1901 (class 2606 OID 25837)
-- Dependencies: 1555 1549 1874
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1903 (class 2606 OID 25892)
-- Dependencies: 1556 1888 1557
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES associations(id) ON DELETE CASCADE;


--
-- TOC entry 1896 (class 2606 OID 25902)
-- Dependencies: 1546 1549 1868
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1899 (class 2606 OID 25912)
-- Dependencies: 1553 1884 1554
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES names(id) ON DELETE CASCADE;


--
-- TOC entry 1895 (class 2606 OID 24699)
-- Dependencies: 1874 1549 1548
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1893 (class 2606 OID 59239)
-- Dependencies: 1549 1874 1546
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1908 (class 2606 OID 25882)
-- Dependencies: 1550 1876 1560
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1897 (class 2606 OID 25897)
-- Dependencies: 1551 1876 1550
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id) ON DELETE CASCADE;


--
-- TOC entry 1904 (class 2606 OID 25862)
-- Dependencies: 1874 1558 1549
-- Name: fk_subtype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_subtype FOREIGN KEY (id_subtype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1905 (class 2606 OID 25867)
-- Dependencies: 1549 1558 1874
-- Name: fk_supertype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_supertype FOREIGN KEY (id_supertype) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1909 (class 2606 OID 25887)
-- Dependencies: 1874 1549 1560
-- Name: fk_theme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_theme FOREIGN KEY (id_theme) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1912 (class 2606 OID 25872)
-- Dependencies: 1549 1874 1562
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1914 (class 2606 OID 25877)
-- Dependencies: 1549 1874 1563
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1894 (class 2606 OID 25847)
-- Dependencies: 1546 1868 1547
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id) ON DELETE CASCADE;


--
-- TOC entry 1907 (class 2606 OID 25857)
-- Dependencies: 1549 1874 1559
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1915 (class 2606 OID 25907)
-- Dependencies: 1874 1564 1549
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY typeables
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id) ON DELETE CASCADE;


--
-- TOC entry 1920 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2010-07-13 10:29:10

--
-- PostgreSQL database dump complete
--


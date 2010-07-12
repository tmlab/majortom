--
-- PostgreSQL database dump
--

-- Started on 2010-07-08 09:42:48

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1937 (class 1262 OID 24641)
-- Name: majortom; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE majortom WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'German_Germany.1252' LC_CTYPE = 'German_Germany.1252';


ALTER DATABASE majortom OWNER TO postgres;

\connect majortom

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 349 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 1536 (class 1259 OID 24642)
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
-- TOC entry 1542 (class 1259 OID 24672)
-- Dependencies: 1838 3
-- Name: constructs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE constructs (
    id bigint DEFAULT nextval('seq_construct_id'::regclass) NOT NULL,
    id_parent bigint,
    id_topicmap bigint
);


ALTER TABLE public.constructs OWNER TO postgres;

--
-- TOC entry 1543 (class 1259 OID 24683)
-- Dependencies: 1839 1542 3
-- Name: reifiables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE reifiables (
    id_reifier bigint
)
INHERITS (constructs);


ALTER TABLE public.reifiables OWNER TO postgres;

--
-- TOC entry 1546 (class 1259 OID 24716)
-- Dependencies: 1842 3 1543
-- Name: scopeables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopeables (
    id_scope bigint
)
INHERITS (reifiables);


ALTER TABLE public.scopeables OWNER TO postgres;

--
-- TOC entry 1551 (class 1259 OID 24894)
-- Dependencies: 1847 1546 3
-- Name: associations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE associations (
    id_type bigint NOT NULL
)
INHERITS (scopeables);


ALTER TABLE public.associations OWNER TO postgres;

--
-- TOC entry 1547 (class 1259 OID 24732)
-- Dependencies: 1843 3 1546
-- Name: datatypeawares; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE datatypeawares (
    id_datatype bigint NOT NULL,
    value character varying NOT NULL
)
INHERITS (scopeables);


ALTER TABLE public.datatypeawares OWNER TO postgres;

--
-- TOC entry 1538 (class 1259 OID 24646)
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
-- TOC entry 1540 (class 1259 OID 24650)
-- Dependencies: 1836 3
-- Name: locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE locators (
    id bigint DEFAULT nextval('seq_locator_id'::regclass) NOT NULL,
    reference character varying(1024) NOT NULL
);


ALTER TABLE public.locators OWNER TO postgres;

--
-- TOC entry 1549 (class 1259 OID 24780)
-- Dependencies: 1845 1546 3
-- Name: names; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE names (
    value character varying NOT NULL,
    id_type bigint NOT NULL
)
INHERITS (scopeables);


ALTER TABLE public.names OWNER TO postgres;

--
-- TOC entry 1550 (class 1259 OID 24855)
-- Dependencies: 1846 3 1547
-- Name: occurrences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE occurrences (
    id_type bigint NOT NULL
)
INHERITS (datatypeawares);


ALTER TABLE public.occurrences OWNER TO postgres;

--
-- TOC entry 1554 (class 1259 OID 24971)
-- Dependencies: 3
-- Name: rel_instance_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_instance_of (
    id_instance bigint NOT NULL,
    id_type bigint NOT NULL
);


ALTER TABLE public.rel_instance_of OWNER TO postgres;

--
-- TOC entry 1556 (class 1259 OID 25001)
-- Dependencies: 3
-- Name: rel_item_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_item_identifiers (
    id_construct bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_item_identifiers OWNER TO postgres;

--
-- TOC entry 1553 (class 1259 OID 24956)
-- Dependencies: 3
-- Name: rel_kind_of; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_kind_of (
    id_subtype bigint,
    id_supertype bigint
);


ALTER TABLE public.rel_kind_of OWNER TO postgres;

SET default_with_oids = false;

--
-- TOC entry 1557 (class 1259 OID 25016)
-- Dependencies: 3
-- Name: rel_subject_identifiers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_identifiers (
    id_topic bigint NOT NULL,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_identifiers OWNER TO postgres;

--
-- TOC entry 1558 (class 1259 OID 25031)
-- Dependencies: 3
-- Name: rel_subject_locators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_subject_locators (
    id_topic bigint,
    id_locator bigint NOT NULL
);


ALTER TABLE public.rel_subject_locators OWNER TO postgres;

--
-- TOC entry 1555 (class 1259 OID 24986)
-- Dependencies: 3
-- Name: rel_themes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rel_themes (
    id_scope bigint,
    id_theme bigint NOT NULL
);


ALTER TABLE public.rel_themes OWNER TO postgres;

SET default_with_oids = true;

--
-- TOC entry 1552 (class 1259 OID 24925)
-- Dependencies: 1848 3 1543
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE roles (
    id_type bigint NOT NULL,
    id_player bigint NOT NULL
)
INHERITS (reifiables);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 1539 (class 1259 OID 24648)
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

SET default_with_oids = false;

--
-- TOC entry 1545 (class 1259 OID 24707)
-- Dependencies: 1841 3
-- Name: scopes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE scopes (
    id bigint DEFAULT nextval('seq_scope_id'::regclass) NOT NULL
);


ALTER TABLE public.scopes OWNER TO postgres;

--
-- TOC entry 1537 (class 1259 OID 24644)
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
-- TOC entry 1541 (class 1259 OID 24661)
-- Dependencies: 1837 3
-- Name: topicmaps; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topicmaps (
    id bigint DEFAULT nextval('seq_topicmap_id'::regclass) NOT NULL,
    id_base_locator bigint NOT NULL
);


ALTER TABLE public.topicmaps OWNER TO postgres;

SET default_with_oids = true;

--
-- TOC entry 1544 (class 1259 OID 24687)
-- Dependencies: 1840 1542 3
-- Name: topics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topics (
)
INHERITS (constructs);


ALTER TABLE public.topics OWNER TO postgres;

--
-- TOC entry 1548 (class 1259 OID 24751)
-- Dependencies: 1844 1547 3
-- Name: variants; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE variants (
)
INHERITS (datatypeawares);


ALTER TABLE public.variants OWNER TO postgres;

--
-- TOC entry 1874 (class 2606 OID 24899)
-- Dependencies: 1551 1551
-- Name: pk_associations; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT pk_associations PRIMARY KEY (id);


--
-- TOC entry 1856 (class 2606 OID 24677)
-- Dependencies: 1542 1542
-- Name: pk_constructs; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT pk_constructs PRIMARY KEY (id);


--
-- TOC entry 1866 (class 2606 OID 24737)
-- Dependencies: 1547 1547
-- Name: pk_datatypeawares; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT pk_datatypeawares PRIMARY KEY (id);


--
-- TOC entry 1850 (class 2606 OID 24658)
-- Dependencies: 1540 1540
-- Name: pk_locators; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT pk_locators PRIMARY KEY (id);


--
-- TOC entry 1870 (class 2606 OID 24788)
-- Dependencies: 1549 1549
-- Name: pk_names; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY names
    ADD CONSTRAINT pk_names PRIMARY KEY (id);


--
-- TOC entry 1872 (class 2606 OID 24863)
-- Dependencies: 1550 1550
-- Name: pk_occurrences; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT pk_occurrences PRIMARY KEY (id);


--
-- TOC entry 1858 (class 2606 OID 24698)
-- Dependencies: 1543 1543
-- Name: pk_reifiables; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT pk_reifiables PRIMARY KEY (id);


--
-- TOC entry 1876 (class 2606 OID 24930)
-- Dependencies: 1552 1552
-- Name: pk_roles; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);


--
-- TOC entry 1862 (class 2606 OID 24715)
-- Dependencies: 1545 1545
-- Name: pk_scope; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopes
    ADD CONSTRAINT pk_scope PRIMARY KEY (id);


--
-- TOC entry 1864 (class 2606 OID 24721)
-- Dependencies: 1546 1546
-- Name: pk_scopes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT pk_scopes PRIMARY KEY (id);


--
-- TOC entry 1854 (class 2606 OID 24666)
-- Dependencies: 1541 1541
-- Name: pk_topicmap; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT pk_topicmap PRIMARY KEY (id);


--
-- TOC entry 1860 (class 2606 OID 24696)
-- Dependencies: 1544 1544
-- Name: pk_topics; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT pk_topics PRIMARY KEY (id);


--
-- TOC entry 1868 (class 2606 OID 24759)
-- Dependencies: 1548 1548
-- Name: pk_variants; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT pk_variants PRIMARY KEY (id);


--
-- TOC entry 1880 (class 2606 OID 24975)
-- Dependencies: 1554 1554 1554
-- Name: unique_instance_of; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT unique_instance_of UNIQUE (id_instance, id_type);


--
-- TOC entry 1884 (class 2606 OID 25005)
-- Dependencies: 1556 1556 1556
-- Name: unique_item_identifiers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT unique_item_identifiers UNIQUE (id_construct, id_locator);


--
-- TOC entry 1878 (class 2606 OID 24960)
-- Dependencies: 1553 1553 1553
-- Name: unique_kind_of; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT unique_kind_of UNIQUE (id_subtype, id_supertype);


--
-- TOC entry 1852 (class 2606 OID 24660)
-- Dependencies: 1540 1540
-- Name: unique_reference; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY locators
    ADD CONSTRAINT unique_reference UNIQUE (reference);


--
-- TOC entry 1886 (class 2606 OID 25020)
-- Dependencies: 1557 1557 1557
-- Name: unique_subject_identifiers; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT unique_subject_identifiers UNIQUE (id_topic, id_locator);


--
-- TOC entry 1888 (class 2606 OID 25035)
-- Dependencies: 1558 1558 1558
-- Name: unique_subject_locators; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT unique_subject_locators UNIQUE (id_topic, id_locator);


--
-- TOC entry 1882 (class 2606 OID 24990)
-- Dependencies: 1555 1555 1555
-- Name: unique_themes; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT unique_themes UNIQUE (id_scope, id_theme);


--
-- TOC entry 1889 (class 2606 OID 24667)
-- Dependencies: 1540 1541 1849
-- Name: fk_baselocator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topicmaps
    ADD CONSTRAINT fk_baselocator FOREIGN KEY (id_base_locator) REFERENCES locators(id);


--
-- TOC entry 1929 (class 2606 OID 25006)
-- Dependencies: 1542 1855 1556
-- Name: fk_construct; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT fk_construct FOREIGN KEY (id_construct) REFERENCES constructs(id);


--
-- TOC entry 1895 (class 2606 OID 24743)
-- Dependencies: 1849 1540 1547
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id);


--
-- TOC entry 1897 (class 2606 OID 24760)
-- Dependencies: 1548 1849 1540
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id);


--
-- TOC entry 1910 (class 2606 OID 24879)
-- Dependencies: 1540 1550 1849
-- Name: fk_datatype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_datatype FOREIGN KEY (id_datatype) REFERENCES locators(id);


--
-- TOC entry 1926 (class 2606 OID 24981)
-- Dependencies: 1859 1544 1554
-- Name: fk_instance; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_instance FOREIGN KEY (id_instance) REFERENCES topics(id);


--
-- TOC entry 1930 (class 2606 OID 25011)
-- Dependencies: 1849 1540 1556
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_item_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1932 (class 2606 OID 25026)
-- Dependencies: 1540 1849 1557
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1934 (class 2606 OID 25041)
-- Dependencies: 1540 1849 1558
-- Name: fk_locator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_locator FOREIGN KEY (id_locator) REFERENCES locators(id);


--
-- TOC entry 1905 (class 2606 OID 24815)
-- Dependencies: 1859 1544 1549
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id);


--
-- TOC entry 1893 (class 2606 OID 24835)
-- Dependencies: 1541 1544 1853
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id);


--
-- TOC entry 1901 (class 2606 OID 24850)
-- Dependencies: 1869 1549 1548
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES names(id);


--
-- TOC entry 1909 (class 2606 OID 24874)
-- Dependencies: 1550 1544 1859
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topics(id);


--
-- TOC entry 1914 (class 2606 OID 24905)
-- Dependencies: 1853 1541 1551
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES topicmaps(id);


--
-- TOC entry 1919 (class 2606 OID 24936)
-- Dependencies: 1873 1551 1552
-- Name: fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_parent FOREIGN KEY (id_parent) REFERENCES associations(id);


--
-- TOC entry 1922 (class 2606 OID 24951)
-- Dependencies: 1552 1544 1859
-- Name: fk_player; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_player FOREIGN KEY (id_player) REFERENCES topics(id);


--
-- TOC entry 1891 (class 2606 OID 24699)
-- Dependencies: 1544 1859 1543
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reifiables
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1896 (class 2606 OID 24805)
-- Dependencies: 1544 1859 1547
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datatypeawares
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1904 (class 2606 OID 24810)
-- Dependencies: 1549 1544 1859
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1899 (class 2606 OID 24840)
-- Dependencies: 1544 1859 1548
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1908 (class 2606 OID 24869)
-- Dependencies: 1859 1550 1544
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1915 (class 2606 OID 24910)
-- Dependencies: 1859 1551 1544
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1920 (class 2606 OID 24941)
-- Dependencies: 1552 1544 1859
-- Name: fk_reifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_reifier FOREIGN KEY (id_reifier) REFERENCES topics(id);


--
-- TOC entry 1903 (class 2606 OID 24799)
-- Dependencies: 1861 1549 1545
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id);


--
-- TOC entry 1894 (class 2606 OID 24825)
-- Dependencies: 1545 1546 1861
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY scopeables
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id);


--
-- TOC entry 1900 (class 2606 OID 24845)
-- Dependencies: 1861 1548 1545
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id);


--
-- TOC entry 1911 (class 2606 OID 24884)
-- Dependencies: 1545 1550 1861
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id);


--
-- TOC entry 1916 (class 2606 OID 24915)
-- Dependencies: 1545 1861 1551
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id);


--
-- TOC entry 1927 (class 2606 OID 24991)
-- Dependencies: 1861 1545 1555
-- Name: fk_scope; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_scope FOREIGN KEY (id_scope) REFERENCES scopes(id);


--
-- TOC entry 1923 (class 2606 OID 24961)
-- Dependencies: 1553 1859 1544
-- Name: fk_subtype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_subtype FOREIGN KEY (id_subtype) REFERENCES topics(id);


--
-- TOC entry 1924 (class 2606 OID 24966)
-- Dependencies: 1553 1859 1544
-- Name: fk_supertype; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_kind_of
    ADD CONSTRAINT fk_supertype FOREIGN KEY (id_supertype) REFERENCES topics(id);


--
-- TOC entry 1928 (class 2606 OID 24996)
-- Dependencies: 1544 1859 1555
-- Name: fk_theme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_themes
    ADD CONSTRAINT fk_theme FOREIGN KEY (id_theme) REFERENCES topics(id);


--
-- TOC entry 1931 (class 2606 OID 25021)
-- Dependencies: 1544 1859 1557
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_identifiers
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id);


--
-- TOC entry 1933 (class 2606 OID 25036)
-- Dependencies: 1558 1859 1544
-- Name: fk_topic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_subject_locators
    ADD CONSTRAINT fk_topic FOREIGN KEY (id_topic) REFERENCES topics(id);


--
-- TOC entry 1890 (class 2606 OID 24678)
-- Dependencies: 1853 1542 1541
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY constructs
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id);


--
-- TOC entry 1898 (class 2606 OID 24775)
-- Dependencies: 1548 1541 1853
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY variants
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id);


--
-- TOC entry 1902 (class 2606 OID 24789)
-- Dependencies: 1541 1853 1549
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id);


--
-- TOC entry 1892 (class 2606 OID 24830)
-- Dependencies: 1853 1544 1541
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id);


--
-- TOC entry 1907 (class 2606 OID 24864)
-- Dependencies: 1853 1541 1550
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id);


--
-- TOC entry 1913 (class 2606 OID 24900)
-- Dependencies: 1541 1853 1551
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id);


--
-- TOC entry 1918 (class 2606 OID 24931)
-- Dependencies: 1541 1552 1853
-- Name: fk_topicmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_topicmap FOREIGN KEY (id_topicmap) REFERENCES topicmaps(id);


--
-- TOC entry 1906 (class 2606 OID 24820)
-- Dependencies: 1859 1549 1544
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY names
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id);


--
-- TOC entry 1912 (class 2606 OID 24889)
-- Dependencies: 1859 1550 1544
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY occurrences
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id);


--
-- TOC entry 1917 (class 2606 OID 24920)
-- Dependencies: 1544 1551 1859
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY associations
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id);


--
-- TOC entry 1921 (class 2606 OID 24946)
-- Dependencies: 1552 1544 1859
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id);


--
-- TOC entry 1925 (class 2606 OID 24976)
-- Dependencies: 1554 1544 1859
-- Name: fk_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rel_instance_of
    ADD CONSTRAINT fk_type FOREIGN KEY (id_type) REFERENCES topics(id);


--
-- TOC entry 1939 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2010-07-08 09:42:48

--
-- PostgreSQL database dump complete
--


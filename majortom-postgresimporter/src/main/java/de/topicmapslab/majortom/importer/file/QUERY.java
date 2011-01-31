/**
 * 
 */
package de.topicmapslab.majortom.importer.file;

/**
 * @author Sven Krosse
 * 
 */
public interface QUERY {

	public static final String TOPIC = "INSERT INTO topics(id_topicmap, id_parent, id) VALUES ({0},{1},{2});\r\n";
	public static final String NAME = "INSERT INTO names(id_topicmap, id_parent, id, id_type, value, id_scope, id_reifier) VALUES ({0},{1},{2},{3},E''{4}'',{5},{6});\r\n";
	public static final String OCCURRENCE = "INSERT INTO occurrences(id_topicmap, id_parent, id, id_type, value, id_datatype, id_scope, id_reifier) VALUES ({0},{1},{2},{3},E''{4}'',{5}, {6},{7});\r\n";
	public static final String VARIANT = "INSERT INTO variants(id_topicmap, id_parent, id, value, id_datatype, id_scope, id_reifier) VALUES ({0},{1},{2},E''{3}'',{4},{5},{6});\r\n";
	public static final String ASSOCIATION = "INSERT INTO associations(id_topicmap, id_parent, id, id_type, id_scope, id_reifier) VALUES ({0},{1},{2},{3},{4},{5});\r\n";
	public static final String ROLE = "INSERT INTO roles(id_topicmap, id_parent, id, id_type, id_player, id_reifier) VALUES ({0},{1},{2},{3},{4},{5});\r\n";

	public static final String SI = "INSERT INTO rel_subject_identifiers(id_topic, id_locator) VALUES ({0},{1});\r\n";
	public static final String SL = "INSERT INTO rel_subject_locators(id_topic, id_locator) VALUES ({0},{1});\r\n";
	public static final String II = "INSERT INTO rel_item_identifiers(id_construct, id_locator) VALUES ({0},{1});\r\n";

	public static final String ISA = "INSERT INTO rel_instance_of(id_type, id_instance) VALUES ({0},{1});\r\n";
	public static final String AKO = "INSERT INTO rel_kind_of(id_supertype, id_subtype) VALUES ({0},{1});\r\n";

	public static final String LOCATOR = "INSERT INTO locators (id, reference) VALUES ({0},E''{1}'');\r\n";

	public static final String SCOPE = "INSERT INTO scopes(id_topicmap, id) VALUES ({0},{1});\r\n";
	public static final String THEME = "INSERT INTO rel_themes(id_scope, id_theme) VALUES ({0},{1});\r\n";

	public static final String CURRVAL = "SELECT nextval('seq_construct_id'::regclass), nextval('seq_locator_id'::regclass), nextval('seq_scope_id'::regclass);";
	public static final String SETVAL = "SELECT setval(''seq_construct_id''::regclass, {0}), setval(''seq_locator_id''::regclass, {1}) , setval(''seq_scope_id''::regclass,{2});";

	public static final String MERGE 	= "UPDATE rel_kind_of SET id_subtype = {0} WHERE id_subtype = {1}; \r\n"
										+ "UPDATE rel_kind_of SET id_supertype = {2} WHERE id_supertype = {3}; \r\n"
										+ "UPDATE rel_instance_of SET id_instance = {4} WHERE id_instance = {5}; \r\n"
										+ "UPDATE rel_instance_of SET id_type = {6} WHERE id_type = {7}; \r\n"
										+ "UPDATE rel_themes SET id_theme = {8} WHERE id_theme = {9}; \r\n"
										+ "UPDATE typeables SET id_type = {10} WHERE id_type = {11}; \r\n"
										+ "UPDATE reifiables SET id_reifier = {12} WHERE id_reifier = {13}; \r\n"
										+ "UPDATE rel_item_identifiers SET id_construct = {14} WHERE id_construct = {15}; \r\n"
										+ "UPDATE rel_subject_identifiers SET id_topic = {16} WHERE id_topic = {17}; \r\n"
										+ "UPDATE rel_subject_locators SET id_topic = {18} WHERE id_topic = {19}; \r\n"
										+ "UPDATE roles SET id_player = {20} WHERE id_player = {21}; \r\n"
										+ "UPDATE constructs SET id_parent = {22} WHERE id_parent = {23};" 
										+ "DELETE FROM topics WHERE id = {24}; \r\n";

	/*
	 * SELECT QUERIES
	 */
	public static final String SELECT_SI = "SELECT id_topic, id_locator FROM rel_subject_identifiers WHERE id_topic IN ( SELECT id FROM topics WHERE id_topicmap = {0})";
	public static final String SELECT_II = "SELECT id_construct, id_locator FROM rel_item_identifiers WHERE id_construct IN ( SELECT id FROM constructs WHERE id_topicmap = {0})";
	public static final String SELECT_SL = "SELECT id_topic, id_locator FROM rel_subject_locators WHERE id_topic IN ( SELECT id FROM topics WHERE id_topicmap = {0})";
	public static final String SELECT_LOCATOR = "SELECT id, reference FROM locators";
	public static final String SELECT_NAME = "SELECT id_topicmap, id_parent, id, id_type, value, id_scope, id_reifier FROM names WHERE id_topicmap = {0}";
	public static final String SELECT_OCCURRENCE = "SELECT id_topicmap, id_parent, id, id_type, value, id_datatype, id_scope, id_reifier FROM occurrences WHERE id_topicmap = {0}";
	public static final String SELECT_VARIANT = "SELECT id_topicmap, id_parent, id, value, id_datatype, id_scope, id_reifier FROM variants WHERE id_topicmap = {0}";
	public static final String SELECT_ASSOCIATION = "SELECT id_topicmap,id, id_type, id_scope, id_reifier FROM associations WHERE id_topicmap = {0}";
	public static final String SELECT_ROLE = "SELECT id_topicmap, id_parent, id, id_type, id_player, id_reifier FROM roles WHERE id_topicmap = {0}";

	public static final String SELECT_ISA = "SELECT id_type, id_instance FROM rel_instance_of WHERE id_type IN ( SELECT id FROM topics WHERE id_topicmap = {0})";
	public static final String SELECT_AKO = "SELECT id_supertype, id_subtype FROM rel_kind_of WHERE id_supertype IN ( SELECT id FROM topics WHERE id_topicmap = {0})";
	public static final String SELECT_SCOPE = "SELECT id, ARRAY(SELECT id_theme FROM rel_themes WHERE id_scope = s.id) FROM scopes AS s WHERE id IN ( SELECT id_scope FROM scopeables WHERE id_topicmap = {0})";
}

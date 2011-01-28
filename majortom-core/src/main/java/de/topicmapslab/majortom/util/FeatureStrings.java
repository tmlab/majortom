/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.util;

import java.util.Set;

/**
 * Class containing all org.tmapi.features.
 * 
 * @author Sven Krosse
 * 
 */
public class FeatureStrings {

	// TMAPI specific feature strings
	
	public static final String AUTOMATIC_MERGING = "http://tmapi.org/features/automerge";
	
	public static final String TOPIC_MAPS_MODEL_FEATURES = "http://tmapi.org/features/model/";
	public static final String TOPIC_MAPS_MODEL_FEATURE_XTM_1_0_MODEL = "http://tmapi.org/features/model/xtm1.0/";
	public static final String TOPIC_MAPS_MODEL_FEATURE_XTM_1_1_MODEL = "http://tmapi.org/features/model/xtm1.1/";
	
	public static final String TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION = "http://tmapi.org/features/type-instance-associations";
	public static final String TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION = "http://tmapi.org/features/supertype-subtype-associations";
	
	public static final String MERGING_SUPPORT_FEATURES = "http://tmapi.org/features/merge/";
	public static final String MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME = "http://tmapi.org/features/merge/byTopicName/";
	
	public static final String LOCATOR_ADDRESS_NOTATION_FEATURES = "http://tmapi.org/features/notation/";
	public static final String LOCATOR_ADDRESS_NOTATION_FEATURE_URI_NOTATION = "http://tmapi.org/features/notation/URI/";
	
	public static final String READ_ONLY_SYSTEM = "http://tmapi.org/features/readOnly";

	// MaJorToM specific feature strings
	
	private static final String MAJORTOM_PREFIX = "de.topicmapslab.majortom";
	
	public static final String SUPPORT_HISTORY = MAJORTOM_PREFIX + ".topicmapstore.history";
	
	public static final String ENABLE_CACHING = MAJORTOM_PREFIX + ".topicmapstore.caching";
	
	public static final String SUPPORT_TRANSACTION = MAJORTOM_PREFIX + ".topicmapstore.transaction";

	public static final String CONSTRAINTS = MAJORTOM_PREFIX + ".constraints";
	
	public static final String DELETION_CONSTRAINTS = CONSTRAINTS + ".deletion";
	
	public static final String CONCURRENT_COLLECTIONS = MAJORTOM_PREFIX + ".collection.concurrent";
	
	public static final String DELETION_CONSTRAINTS_REIFICATION = DELETION_CONSTRAINTS + ".reification";
	
	public static final Set<String> FEATURES = HashUtil.getHashSet();

	static {
		FEATURES.add(AUTOMATIC_MERGING);
		FEATURES.add(TOPIC_MAPS_MODEL_FEATURES);
		FEATURES.add(TOPIC_MAPS_MODEL_FEATURE_XTM_1_0_MODEL);
		FEATURES.add(TOPIC_MAPS_MODEL_FEATURE_XTM_1_1_MODEL);
		FEATURES.add(TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION);
		FEATURES.add(TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION);
		FEATURES.add(MERGING_SUPPORT_FEATURES);
		FEATURES.add(MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME);
		FEATURES.add(LOCATOR_ADDRESS_NOTATION_FEATURES);
		FEATURES.add(LOCATOR_ADDRESS_NOTATION_FEATURE_URI_NOTATION);
		FEATURES.add(READ_ONLY_SYSTEM);
		
		FEATURES.add(SUPPORT_HISTORY);
		FEATURES.add(ENABLE_CACHING);
		FEATURES.add(SUPPORT_TRANSACTION);
		FEATURES.add(DELETION_CONSTRAINTS_REIFICATION);
		
		FEATURES.add(CONCURRENT_COLLECTIONS);
	}
}

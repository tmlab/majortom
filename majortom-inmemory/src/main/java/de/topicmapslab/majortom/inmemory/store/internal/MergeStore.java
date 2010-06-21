package de.topicmapslab.majortom.inmemory.store.internal;
///*******************************************************************************
// * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
// * 
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *   http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//package de.topicmap.engine.inMemory.store.internal;
//
//import java.util.Map;
//import java.util.Set;
//
//import de.topicmap.engine.inMemory.store.model.IDataStore;
//import de.topicmapslab.engine.model.core.ITopic;
//import de.topicmapslab.engine.model.exception.TopicMapStoreException;
//import de.topicmapslab.engine.util.HashUtil;
//
///**
// * Internal data store of vritual merge mappings.
// * 
// * @author Sven Krosse
// * 
// */
//public class MergeStore implements IDataStore {
//
//	private Map<ITopic, Set<ITopic>> merged;
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void close() {
//		if (merged != null) {
//			merged.clear();
//		}
//	}
//
//	/**
//	 * Returns all virtual merged topics of the given one.
//	 * 
//	 * @param t the topic
//	 * @return a set containing all topics virtual merged including the given
//	 *         one
//	 */
//	public Set<ITopic> getMerged(ITopic t) {
//		if (merged == null || !merged.containsKey(t)) {
//			Set<ITopic> set = HashUtil.getHashSet();
//			set.add(t);
//			return set;
//		}
//		return merged.get(t);
//	}
//
//	/**
//	 * Adding a virtual merge mapping pair to the internal store
//	 * 
//	 * @param t1 the topic 1
//	 * @param t2 the topic 2
//	 */
//	public void addPair(ITopic t1, ITopic t2) {
//		if (merged == null) {
//			merged = HashUtil.getHashMap();
//		}
//
//		Set<ITopic> set = HashUtil.getHashSet();
//		set.addAll(getMerged(t1));
//		set.add(t1);
//		set.addAll(getMerged(t2));
//		set.add(t2);
//
//		for (ITopic t : set) {
//			merged.put(t, set);
//		}
//	}
//
//	/**
//	 * Remove the given topic as internal instance of the store
//	 * 
//	 * @param t the topic
//	 */
//	public void removeTopic(ITopic t) {
//		if (merged == null || !merged.containsKey(t)) {
//			throw new TopicMapStoreException("Unknown virtual merging pair");
//		}
//
//		Set<ITopic> set = HashUtil.getHashSet();
//		set.addAll(getMerged(t));
//		set.remove(t);
//
//		for (ITopic oT : set) {
//			merged.put(oT, set);
//		}
//
//		merged.remove(t);
//	}
//
//}

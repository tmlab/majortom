package de.topicmapslab.majortom.redis.store.index;

import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.COLON;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.DATATYPE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.ITEM_IDENTIFIER;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.NAME;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.OCCURRENCE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.PARENT;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.STAR;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.VALUE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.VARIANT;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;

import redis.clients.jedis.Pipeline;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.majortom.redis.store.RedisStoreIdentity;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;
import de.topicmapslab.majortom.redis.util.RedisHandler;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.LiteralUtils;

public class RedisLiteralIndex extends IndexImpl<RedisTopicMapStore> implements ILiteralIndex {

	private static final String NULL_IS_AN_INVALID_VALUE_AND_OR_DATATYPE = "null is an invalid value and/or datatype";
	private static final String NULL_IS_AN_INVALID_VALUE = "null is an invalid value";
	private static final String INDEX_IS_CLOSED = "Index is closed!";
	private RedisHandler redis;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 */
	public RedisLiteralIndex(RedisTopicMapStore store) {
		super(store);
		redis = store.getRedis();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(String value) {
		Set<Name> set = HashUtil.getHashSet();
		set.addAll(getNamesInternal(value));
		return set;
	}

	/**
	 * Internal method to get all names by their value
	 * 
	 * @param value
	 *            the value
	 * @return the names
	 */
	public Collection<IName> getNamesInternal(String value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<IName> set = HashUtil.getHashSet();
		if (value == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		Set<String> keySet = redis.list(NAME + COLON + STAR);
		Pipeline p = redis.pipeline();
		for (String key : HashUtil.getHashSet(keySet)) {
			if (key.endsWith(VARIANT) || key.endsWith(ITEM_IDENTIFIER)) {
				keySet.remove(key);
				continue;
			}
			p.hget(key, VALUE);
		}
		String[] keys = keySet.toArray(new String[0]);
		List<Object> valueObjects = p.execute();
		int i = 0;
		for (Object valueObject : valueObjects) {
			String key = keys[i++];
			if (valueObject.getClass().isArray()) {
				String testValue = new String((byte[]) valueObject);
				if (value.equals(testValue)) {
					String parentId = redis.get(key, PARENT);
					ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId),
							getTopicMapStore().getTopicMap());
					set.add(getTopicMapStore().getConstructFactory().newName(new RedisStoreIdentity(key), parent));
				}
			}
		}
		return set;
	}

	/**
	 * Internal method to get all names
	 * 
	 * @return the names
	 */
	public Collection<IName> getNamesInternal() {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<IName> set = HashUtil.getHashSet();
		Set<String> keySet = redis.list(NAME + COLON + STAR);
		for (String key : keySet) {
			if (key.endsWith(VARIANT) || key.endsWith(ITEM_IDENTIFIER)) {
				continue;
			}
			String parentId = redis.get(key, PARENT);
			ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newName(new RedisStoreIdentity(key), parent));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(String value) {
		return getOccurrences(value, Namespaces.XSD.STRING);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Locator locator) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (locator == null) {
			throw new IllegalArgumentException("null is an invalid locator");
		}
		return getOccurrences(locator.getReference(), Namespaces.XSD.ANYURI);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(String value, Locator datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (datatype == null) {
			throw new IllegalArgumentException("null is an invalid datatype");
		}
		return getOccurrences(value, datatype.getReference());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(String value, String datatype) {
		Set<Occurrence> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(value, datatype));
		return set;
	}

	/**
	 * Internal method to return all occurrences by their value and datatype
	 * 
	 * @param value
	 *            the value
	 * @param datatype
	 *            the datatype
	 * @return the occurrences
	 */
	public Collection<IOccurrence> getOccurrencesInternal(String value, String datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (value == null || datatype == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		Set<String> keySet = redis.list(OCCURRENCE + COLON + STAR);
		Pipeline p = redis.pipeline();
		for (String key : HashUtil.getHashSet(keySet)) {
			if (key.endsWith(ITEM_IDENTIFIER)) {
				keySet.remove(key);
				continue;
			}
			p.hmget(key, VALUE, DATATYPE);
		}
		String[] keys = keySet.toArray(new String[0]);
		List<Object> valueObjects = p.execute();
		int i = 0;
		for (Object valueObject : valueObjects) {
			String key = keys[i++];
			if (valueObject instanceof ArrayList) {
				List<?> valueArrayList = (List<?>) valueObject;
				String testValue = new String((byte[]) valueArrayList.get(0));
				String testDatatype = new String((byte[]) valueArrayList.get(1));

				if (datatype.equals(testDatatype) && testValue.equals(value)) {
					String parentId = redis.get(key, PARENT);
					ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId),
							getTopicMapStore().getTopicMap());
					set.add(getTopicMapStore().getConstructFactory().newOccurrence(new RedisStoreIdentity(key), parent));
				}
			}
		}
		return set;
	}

	/**
	 * Internal method to return all occurrences by their datatype
	 * 
	 * @param datatype
	 *            the datatype
	 * @return the occurrences
	 */
	public Collection<IOccurrence> getOccurrencesInternalByDatatype(String datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		if (datatype == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		Set<String> keySet = redis.list(OCCURRENCE + COLON + STAR);
		Pipeline p = redis.pipeline();
		for (String key : HashUtil.getHashSet(keySet)) {
			if (key.endsWith(ITEM_IDENTIFIER)) {
				keySet.remove(key);
				continue;
			}
			p.hget(key, DATATYPE);
		}
		String[] keys = keySet.toArray(new String[0]);

		List<Object> datatypeObjects = p.execute();
		int i = 0;
		for (Object datatypeObject : datatypeObjects) {
			String key = keys[i++];
			if (datatypeObject.getClass().isArray()) {
				String testDatatype = new String((byte[]) datatypeObject);
				if (datatype.equals(testDatatype)) {
					String parentId = redis.get(key, PARENT);
					ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId),
							getTopicMapStore().getTopicMap());
					set.add(getTopicMapStore().getConstructFactory().newOccurrence(new RedisStoreIdentity(key), parent));
				}
			}
		}
		return set;
	}

	/**
	 * Internal method to return all variants by their datatype
	 * 
	 * @param datatype
	 *            the datatype
	 * @return the variants
	 */
	public Collection<IVariant> getVariantsInternalByDatatype(String datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (datatype == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		Set<IVariant> set = HashUtil.getHashSet();
		Set<String> keySet = redis.list(VARIANT + COLON + STAR);
		Pipeline p = redis.pipeline();
		for (String key : HashUtil.getHashSet(keySet)) {
			if (key.endsWith(ITEM_IDENTIFIER)) {
				keySet.remove(key);
				continue;
			}
			p.hget(key, DATATYPE);
		}
		String[] keys = keySet.toArray(new String[0]);

		List<Object> datatypeObjects = p.execute();
		int i = 0;
		for (Object datatypeObject : datatypeObjects) {
			String key = keys[i++];
			if (datatypeObject.getClass().isArray()) {
				String testDatatype = new String((byte[]) datatypeObject);
				if (datatype.equals(testDatatype)) {
					set.add((IVariant) getTopicMapStore().doReadConstruct(getTopicMapStore().getTopicMap(), key));
				}
			}
		}
		return set;
	}

	/**
	 * Internal method to return all occurrences
	 * 
	 * @return the occurrences
	 */
	public Collection<IOccurrence> getOccurrencesInternal() {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		Set<String> keySet = redis.list(OCCURRENCE + COLON + STAR);
		for (String key : keySet) {
			if (key.endsWith(ITEM_IDENTIFIER)) {
				continue;
			}
			String parentId = redis.get(key, PARENT);
			ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newOccurrence(new RedisStoreIdentity(key), parent));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value) {
		return getVariants(value, Namespaces.XSD.STRING);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Locator value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (value == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		return getVariants(value.getReference(), Namespaces.XSD.ANYURI);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value, Locator datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (datatype == null) {
			throw new IllegalArgumentException("null is an invalid datatype");
		}
		return getVariants(value, datatype.getReference());
	}

	/**
	 * Internal method to all variants by the value and datatype
	 * 
	 * @param value
	 *            the value
	 * @param datatype
	 *            the datatype
	 * @return the variants
	 */
	public Collection<Variant> getVariants(String value, String datatype) {
		Set<Variant> set = HashUtil.getHashSet();
		set.addAll(getVariantsInternal(value, datatype));
		return set;
	}

	/**
	 * Internal method to return all variants by their value and datatype
	 * 
	 * @param value
	 *            the value
	 * @param datatype
	 *            the datatype
	 * @return the variants
	 */
	public Collection<IVariant> getVariantsInternal(String value, String datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (value == null || datatype == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE_AND_OR_DATATYPE);
		}
		Set<IVariant> set = HashUtil.getHashSet();
		Set<String> keySet = redis.list(VARIANT + COLON + STAR);
		Pipeline p = redis.pipeline();
		for (String key : HashUtil.getHashSet(keySet)) {
			if (key.endsWith(ITEM_IDENTIFIER)) {
				keySet.remove(key);
				continue;
			}
			p.hmget(key, VALUE, DATATYPE);
		}
		String[] keys = keySet.toArray(new String[0]);
		List<Object> valueObjects = p.execute();
		int i = 0;
		for (Object valueObject : valueObjects) {
			String key = keys[i++];
			if (valueObject instanceof ArrayList) {
				List<?> valueArrayList = (List<?>) valueObject;
				String testValue = new String((byte[]) valueArrayList.get(0));
				String testDatatype = new String((byte[]) valueArrayList.get(1));

				if (datatype.equals(testDatatype) && testValue.equals(value)) {
					String parentNameId = redis.get(key, PARENT);
					String parentsTopicId = redis.get(parentNameId, PARENT);
					ITopic parentsTopic = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentsTopicId),
							getTopicMapStore().getTopicMap());
					IName parentName = getTopicMapStore().getConstructFactory().newName(new RedisStoreIdentity(parentNameId), parentsTopic);
					set.add(getTopicMapStore().getConstructFactory().newVariant(new RedisStoreIdentity(key), parentName));
				}
			}
		}
		return set;
	}

	/**
	 * Internal method to get all variants
	 * 
	 * @return the variants
	 */
	public Collection<IVariant> getVariantsInternal() {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<IVariant> set = HashUtil.getHashSet();
		Set<String> keySet = redis.list(VARIANT + COLON + STAR);
		for (String key : keySet) {
			if (key.endsWith(ITEM_IDENTIFIER)) {
				continue;
			}
			String parentNameId = redis.get(key, PARENT);
			String parentsTopicId = redis.get(parentNameId, PARENT);
			ITopic parentsTopic = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentsTopicId),
					getTopicMapStore().getTopicMap());
			IName parentName = getTopicMapStore().getConstructFactory().newName(new RedisStoreIdentity(parentNameId), parentsTopic);
			set.add(getTopicMapStore().getConstructFactory().newVariant(new RedisStoreIdentity(key), parentName));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getBooleans(boolean arg0) {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(String.valueOf(arg0), Namespaces.XSD.BOOLEAN));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String arg0) {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(arg0, Namespaces.XSD.STRING));
		set.addAll(getNamesInternal(arg0));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Locator datatypeLocator) {
		return getCharacteristicsByDatatype(datatypeLocator.getReference());
	}

	/**
	 * Internal method to get all characteristics by the datatype
	 * 
	 * @param datatype
	 *            the datatype
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getCharacteristicsByDatatype(String datatype) {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		if (Namespaces.XSD.STRING.equals(datatype)) {
			set.addAll(getNamesInternal());
		}
		set.addAll(getOccurrencesInternalByDatatype(datatype));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String value, Locator datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (datatype == null) {
			throw new IllegalArgumentException("null is an invalid datatype");
		}
		return getCharacteristics(value, datatype.getReference());
	}

	/**
	 * Internal method to get all characteristics by the value and datatype
	 * 
	 * @param value
	 *            the value
	 * @param datatype
	 *            the datatype
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(String value, String datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		if (Namespaces.XSD.STRING.equals(datatype)) {
			set.addAll(getNamesInternal(value));
		}
		set.addAll(getOccurrencesInternal(value, datatype));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regex) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (regex == null) {
			throw new IllegalArgumentException("null is an invalid regex");
		}
		return getCharacteristicsMatches(Pattern.compile(regex));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regex) {
		return getCharacteristicsMatches(regex, Namespaces.XSD.STRING);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regex, Locator datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (regex == null || datatype == null) {
			throw new IllegalArgumentException("null is an invalid regex and/or datatype");
		}
		return getCharacteristicsMatches(Pattern.compile(regex), datatype.getReference());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regex, Locator datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (datatype == null) {
			throw new IllegalArgumentException("null is an invalid datatype");
		}
		return getCharacteristicsMatches(regex, datatype.getReference());
	}

	/**
	 * Internal method to get all characteristics by a regexp and the datatype
	 * 
	 * @param regex
	 *            the regular expression
	 * @param datatype
	 *            the datatype
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regex, String datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		if (regex == null || datatype == null) {
			throw new IllegalArgumentException("null is an invalid regex and/or datatype");
		}

		Set<String> keySet;
		String[] keys;
		Pipeline p;
		List<Object> valueObjects;
		int i;
		if (Namespaces.XSD.STRING.equals(datatype)) {
			keySet = redis.list(NAME + COLON + STAR);
			p = redis.pipeline();

			for (String key : HashUtil.getHashSet(keySet)) {
				if (key.endsWith(ITEM_IDENTIFIER) || key.endsWith(VARIANT)) {
					keySet.remove(key);
					continue;
				}
				p.hget(key, VALUE);
			}
			keys = keySet.toArray(new String[0]);
			valueObjects = p.execute();
			i = 0;
			for (Object valueObject : valueObjects) {
				String key = keys[i++];
				if (valueObject.getClass().isArray()) {
					String testValue = new String((byte[]) valueObject);
					if (regex.matcher(testValue).matches()) {
						String parentId = redis.get(key, PARENT);
						ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId),
								getTopicMapStore().getTopicMap());
						set.add(getTopicMapStore().getConstructFactory().newName(new RedisStoreIdentity(key), parent));
					}
				}
			}
		}
		keySet = redis.list(OCCURRENCE + COLON + STAR);
		p = redis.pipeline();
		for (String key : HashUtil.getHashSet(keySet)) {
			if (key.endsWith(ITEM_IDENTIFIER)) {
				keySet.remove(key);
				continue;
			}
			p.hmget(key, VALUE, DATATYPE);
		}
		keys = keySet.toArray(new String[0]);
		valueObjects = p.execute();
		i = 0;
		for (Object valueObject : valueObjects) {
			String key = keys[i++];
			if (valueObject instanceof ArrayList) {
				List<?> valueArrayList = (List<?>) valueObject;
				String testValue = new String((byte[]) valueArrayList.get(0));
				String testDatatype = new String((byte[]) valueArrayList.get(1));
				if (datatype.equals(testDatatype) && regex.matcher(testValue).matches()) {
					String parentId = redis.get(key, PARENT);
					ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId),
							getTopicMapStore().getTopicMap());
					set.add(getTopicMapStore().getConstructFactory().newOccurrence(new RedisStoreIdentity(key), parent));
				}
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (value == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(value.toString(), Namespaces.XSD.WGS84_COORDINATE));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value, double diff) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (value == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		Collection<IOccurrence> coordinates = getOccurrencesInternalByDatatype(Namespaces.XSD.WGS84_COORDINATE);
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (IOccurrence c : coordinates) {
			try {
				if (c.coordinateValue().getDistance(value) <= diff) {
					set.add(c);
				}
			} catch (ParseException e) {
				// IGNORE
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IDatatypeAware> getDatatypeAwares(Locator datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (datatype == null) {
			throw new IllegalArgumentException("null is an invalid datatype");
		}
		return getDatatypeAwares(datatype.getReference());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IDatatypeAware> getDatatypeAwares(String datatype) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<IDatatypeAware> set = HashUtil.getHashSet();
		if (datatype == null) {
			throw new IllegalArgumentException("null is an invalid datatype");
		}
		set.addAll(getOccurrencesInternalByDatatype(datatype));
		set.addAll(getVariantsInternalByDatatype(datatype));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (value == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(DatatypeAwareUtils.toString(value, Namespaces.XSD.DATETIME), Namespaces.XSD.DATETIME));
		set.addAll(getOccurrencesInternal(DatatypeAwareUtils.toString(value, Namespaces.XSD.DATE), Namespaces.XSD.DATE));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value, Calendar deviance) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (value == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_VALUE);
		}
		double deviance_ = ((double) (deviance.get(Calendar.SECOND) + (deviance.get(Calendar.MINUTE) + (deviance.get(Calendar.HOUR) + (deviance
				.get(Calendar.DAY_OF_MONTH) + (deviance.get(Calendar.MONTH) + deviance.get(Calendar.YEAR) * 12) * 30) * 24) * 60) * 60)) * 1000;

		Collection<IOccurrence> dateTimes = HashUtil.getHashSet();
		dateTimes.addAll(getOccurrencesInternalByDatatype(Namespaces.XSD.DATETIME));
		dateTimes.addAll(getOccurrencesInternalByDatatype(Namespaces.XSD.DATE));
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (IOccurrence c : dateTimes) {
			try {
				if (LiteralUtils.inRange(c.dateTimeValue(), value, deviance_)) {
					set.add(c);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(Double.toString(value), Namespaces.XSD.DOUBLE));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value, double diff) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Collection<IOccurrence> doubles = getOccurrencesInternalByDatatype(Namespaces.XSD.DOUBLE);
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (IOccurrence c : doubles) {
			if (Math.abs(c.doubleValue() - value) <= diff) {
				set.add(c);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(Float.toString(value), Namespaces.XSD.FLOAT));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value, double diff) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Collection<IOccurrence> floats = getOccurrencesInternalByDatatype(Namespaces.XSD.FLOAT);
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (IOccurrence c : floats) {
			if (Math.abs(c.floatValue() - value) <= diff) {
				set.add(c);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(Integer.toString(value), Namespaces.XSD.INT));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value, double diff) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Collection<IOccurrence> ints = getOccurrencesInternalByDatatype(Namespaces.XSD.INT);
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (IOccurrence c : ints) {
			if (Math.abs(c.intValue() - value) <= diff) {
				set.add(c);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal(Long.toString(value), Namespaces.XSD.LONG));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value, double diff) {
		if (!isOpen()) {
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Collection<IOccurrence> longs = getOccurrencesInternalByDatatype(Namespaces.XSD.LONG);
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (IOccurrence c : longs) {
			if (Math.abs(c.longValue() - value) <= diff) {
				set.add(c);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames() {
		Set<Name> set = HashUtil.getHashSet();
		set.addAll(getNamesInternal());
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences() {
		Set<Occurrence> set = HashUtil.getHashSet();
		set.addAll(getOccurrencesInternal());
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getUris(URI uri) {
		return getCharacteristics(uri.toASCIIString(), Namespaces.XSD.ANYURI);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants() {
		Set<Variant> set = HashUtil.getHashSet();
		set.addAll(getVariantsInternal());
		return set;
	}

}

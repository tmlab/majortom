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
package de.topicmapslab.majortom.index.nonpaged;

import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Variant;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.index.core.BaseCachedLiteralIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * Base implementation of an cached literal index
 * 
 * @author Sven Krosse
 * 
 */
public abstract class CachedLiteralIndexImpl<X extends ITopicMapStore> extends BaseCachedLiteralIndexImpl<X> implements ILiteralIndex {

	/**
	 * @param store
	 */
	public CachedLiteralIndexImpl(X store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getBooleans(boolean value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetBooleans(value);
		}
		Collection<ICharacteristics> results = read(Boolean.class, value, null);
		if (results == null) {
			results = doGetBooleans(value);
			cache(Boolean.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetCharacteristics(value);
		}
		Collection<ICharacteristics> results = read(String.class, value, null);
		if (results == null) {
			results = doGetCharacteristics(value);
			cache(String.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( datatype == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetCharacteristics(datatype);
		}
		Collection<ICharacteristics> results = read(String.class, datatype, null);
		if (results == null) {
			results = doGetCharacteristics(datatype);
			cache(String.class, datatype, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String value, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( datatype == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetCharacteristics(value, datatype);
		}
		Collection<ICharacteristics> results = read(String.class, value, datatype);
		if (results == null) {
			results = doGetCharacteristics(value, datatype);
			cache(String.class, value, datatype, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		return getCharacteristicsMatches(Pattern.compile(regExp));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regExp, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( datatype == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		return getCharacteristicsMatches(Pattern.compile(regExp), datatype);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetCharacteristicsMatches(regExp);
		}
		Collection<ICharacteristics> results = read(Pattern.class, regExp, null);
		if (results == null) {
			results = doGetCharacteristicsMatches(regExp);
			cache(Pattern.class, regExp, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( datatype == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetCharacteristicsMatches(regExp, datatype);
		}
		Collection<ICharacteristics> results = read(Pattern.class, regExp, datatype);
		if (results == null) {
			results = doGetCharacteristicsMatches(regExp, datatype);
			cache(Pattern.class, regExp, datatype, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetCoordinates(value);
		}
		Collection<ICharacteristics> results = read(Wgs84Coordinate.class, value, null);
		if (results == null) {
			results = doGetCoordinates(value);
			cache(Wgs84Coordinate.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetCoordinates(value, deviance);
		}
		Collection<ICharacteristics> results = read(Wgs84Coordinate.class, value, deviance);
		if (results == null) {
			results = doGetCoordinates(value, deviance);
			cache(Wgs84Coordinate.class, value, deviance, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IDatatypeAware> getDatatypeAwares(Locator dataType) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( dataType == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetDatatypeAwares(dataType);
		}
		Collection<IDatatypeAware> results = read(IDatatypeAware.class, null, dataType);
		if (results == null) {
			results = doGetDatatypeAwares(dataType);
			cacheConstructs(IDatatypeAware.class, null, dataType, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetDateTime(value);
		}
		Collection<ICharacteristics> results = read(Calendar.class, value, null);
		if (results == null) {
			results = doGetDateTime(value);
			cache(Calendar.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value, Calendar deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( deviance == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetDateTime(value, deviance);
		}
		Collection<ICharacteristics> results = read(Calendar.class, value, deviance);
		if (results == null) {
			results = doGetDateTime(value, deviance);
			cache(Calendar.class, value, deviance, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetDoubles(value);
		}
		Collection<ICharacteristics> results = read(Double.class, value, null);
		if (results == null) {
			results = doGetDoubles(value);
			cache(Double.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetDoubles(value, deviance);
		}
		Collection<ICharacteristics> results = read(Double.class, value, deviance);
		if (results == null) {
			results = doGetDoubles(value, deviance);
			cache(Double.class, value, deviance, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetFloats(value);
		}
		Collection<ICharacteristics> results = read(Float.class, value, null);
		if (results == null) {
			results = doGetFloats(value);
			cache(Float.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetFloats(value, deviance);
		}
		Collection<ICharacteristics> results = read(Float.class, value, deviance);
		if (results == null) {
			results = doGetFloats(value, deviance);
			cache(Float.class, value, deviance, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetIntegers(value);
		}
		Collection<ICharacteristics> results = read(Integer.class, value, null);
		if (results == null) {
			results = doGetIntegers(value);
			cache(Integer.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetIntegers(value, deviance);
		}
		Collection<ICharacteristics> results = read(Integer.class, value, deviance);
		if (results == null) {
			results = doGetIntegers(value, deviance);
			cache(Integer.class, value, deviance, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetLongs(value);
		}
		Collection<ICharacteristics> results = read(Long.class, value, null);
		if (results == null) {
			results = doGetLongs(value);
			cache(Long.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetLongs(value, deviance);
		}
		Collection<ICharacteristics> results = read(Long.class, value, deviance);
		if (results == null) {
			results = doGetLongs(value, deviance);
			cache(Long.class, value, deviance, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetNames();
		}
		Collection<Name> results = readConstructs(Name.class);
		if (results == null) {
			results = doGetNames();
			cacheConstructs(Name.class, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(String literal) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( literal == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetNames(literal);
		}
		Collection<Name> results = readConstructs(Name.class, literal, null);
		if (results == null) {
			results = doGetNames(literal);
			cacheConstructs(Name.class, literal, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetOccurrences();
		}
		Collection<Occurrence> results = readConstructs(Occurrence.class);
		if (results == null) {
			results = doGetOccurrences();
			cacheConstructs(Occurrence.class, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(final String value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetOccurrences(value);
		}
		Collection<Occurrence> results = readConstructs(Occurrence.class, value, null);
		if (results == null) {
			results = doGetOccurrences(value);
			cacheConstructs(Occurrence.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(final String value, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( datatype == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetOccurrences(value, datatype);
		}
		Collection<Occurrence> results = readConstructs(Occurrence.class, value, datatype);
		if (results == null) {
			results = doGetOccurrences(value, datatype);
			cacheConstructs(Occurrence.class, value, datatype, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Locator value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetOccurrences(value);
		}
		Collection<Occurrence> results = readConstructs(Occurrence.class, value, null);
		if (results == null) {
			results = doGetOccurrences(value);
			cacheConstructs(Occurrence.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getUris(URI value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetUris(value);
		}
		Collection<ICharacteristics> results = read(URI.class, value, null);
		if (results == null) {
			results = doGetUris(value);
			cache(URI.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetVariants();
		}
		Collection<Variant> results = readConstructs(Variant.class);
		if (results == null) {
			results = doGetVariants();
			cacheConstructs(Variant.class, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Locator value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetVariants(value);
		}
		Collection<Variant> results = readConstructs(Variant.class, value, null);
		if (results == null) {
			results = doGetVariants(value);
			cacheConstructs(Variant.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetVariants(value);
		}
		Collection<Variant> results = readConstructs(Variant.class, value, null);
		if (results == null) {
			results = doGetVariants(value);
			cacheConstructs(Variant.class, value, null, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( value == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( datatype == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetVariants(value, datatype);
		}
		Collection<Variant> results = readConstructs(Variant.class, value, datatype);
		if (results == null) {
			results = doGetVariants(value, datatype);
			cacheConstructs(Variant.class, value, datatype, results);
		}
		return results;
	}

	/**
	 * Returns all characteristics with the given value.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristics(String value);

	/**
	 * Returns all characteristics with the given datatype.
	 * 
	 * @param datatype
	 *            the datatype
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristics(Locator datatype);

	/**
	 * Returns all characteristics with the given value and the given datatype.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * @param datatype
	 *            the datatype
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristics(String value, Locator datatype);

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @return the characteristics with matching values
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp);

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression and has the datatype.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @param datatype
	 *            the datatype
	 * 
	 * @return the characteristics with matching values
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, Locator datatype);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:anyURI.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the URI value
	 */

	protected abstract Collection<ICharacteristics> doGetUris(URI value);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:integer.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the characteristics with the integer value
	 */
	protected abstract Collection<ICharacteristics> doGetIntegers(int value);

	/**
	 * Returns all characteristics with the datatype xsd:integer and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetIntegers(int value, double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:long.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the characteristics with the long value
	 */
	protected abstract Collection<ICharacteristics> doGetLongs(long value);

	/**
	 * Returns all characteristics with the datatype xsd:long and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetLongs(long value, double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:float.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the characteristics with the float value
	 */
	protected abstract Collection<ICharacteristics> doGetFloats(float value);

	/**
	 * Returns all characteristics with the datatype xsd:float and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetFloats(float value, double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:double.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the double value
	 */
	protected abstract Collection<ICharacteristics> doGetDoubles(double value);

	/**
	 * Returns all characteristics with the datatype xsd:double and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetDoubles(double value, double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:dateTime.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the characteristics with the dateTime value
	 */
	protected abstract Collection<ICharacteristics> doGetDateTime(Calendar value);

	/**
	 * Returns all characteristics with the datatype xsd:time and with a time
	 * value which has a difference from the given value lower or equals than
	 * the given deviance. The given deviance calendar will be interpreted as
	 * maximum difference from the given value. Each time information handled as
	 * difference. For example if the difference should be lower than one day,
	 * one hour and 10 minutes the dateTime literal should be
	 * <code>0000-00-01T01:10:00</code>.
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @return the characteristics with the time value
	 */
	protected abstract Collection<ICharacteristics> doGetDateTime(Calendar value, Calendar deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:boolean.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the characteristics with the boolean value
	 */
	protected abstract Collection<ICharacteristics> doGetBooleans(boolean value);

	/**
	 * Returns all characteristics with the given value and the datatype tm:geo.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the characteristics with the geographic coordinates
	 */
	protected abstract Collection<ICharacteristics> doGetCoordinates(Wgs84Coordinate value);

	/**
	 * Returns all characteristics with the datatype xsd:integer and a
	 * geographical coordinate which has a lower or equal distance to the given
	 * coordinate like the given deviance.
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum distance
	 * @return the characteristics
	 */
	protected abstract Collection<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, double deviance);

	/**
	 * Returns all variants and occurrences with the given data-type.
	 * 
	 * @param dataType
	 *            the data type
	 * 
	 * @return a collection of all matching variants and occurrences within the
	 *         given range
	 */
	protected abstract Collection<IDatatypeAware> doGetDatatypeAwares(Locator dataType);

	/**
	 * Return all names contained by the current topic map.
	 * 
	 * @return all names of the topic map
	 */
	protected abstract Collection<Name> doGetNames();

	/**
	 * Return all names contained by the current topic map with a the given
	 * literal as value.
	 * 
	 * @param literal
	 *            the literal
	 * 
	 * @return all names of the topic map with the given literal as value
	 */
	protected abstract Collection<Name> doGetNames(final String literal);

	/**
	 * Return all occurrences contained by the current topic map.
	 * 
	 * @return all occurrences of the topic map
	 */
	protected abstract Collection<Occurrence> doGetOccurrences();

	/**
	 * Return all occurrences contained by the current topic map with a the
	 * given literal as value.
	 * 
	 * @param literal
	 *            the literal
	 * @return all occurrences of the topic map with the given literal as value
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(final String literal);

	/**
	 * Return all occurrences contained by the current topic map with a the
	 * given literal as value and datatype.
	 * 
	 * @param literal
	 *            the literal
	 * @param datatype
	 *            the datatype
	 * @return all occurrences of the topic map with the given literal as value
	 *         and datatype
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(final String literal, Locator datatype);

	/**
	 * Return all occurrences contained by the current topic map with the given
	 * value.
	 * 
	 * @param value
	 *            the value
	 * @return all occurrences of the topic map with the given value
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(Locator value);

	/**
	 * Return all variants contained by the current topic map.
	 * 
	 * @return all variants of the topic map
	 */
	protected abstract Collection<Variant> doGetVariants();

	/**
	 * Return all variants contained by the current topic map with a the given
	 * literal as value and datatype.
	 * 
	 * @param literal
	 *            the literal
	 * @param datatype
	 *            the datatype
	 * @return all variants of the topic map with the given literal as value and
	 *         datatype
	 */
	protected abstract Collection<Variant> doGetVariants(final String literal, Locator datatype);

	/**
	 * Return all variants contained by the current topic map with the given
	 * value.
	 * 
	 * @param value
	 *            the value
	 * @return all variants of the topic map with the given value
	 */
	protected abstract Collection<Variant> doGetVariants(Locator value);

	/**
	 * Return all variants contained by the current topic map with the given
	 * literal as value.
	 * 
	 * @param literal
	 *            the literal
	 * @return all variants of the topic map with the given literal as value
	 */
	protected abstract Collection<Variant> doGetVariants(final String literal);

}

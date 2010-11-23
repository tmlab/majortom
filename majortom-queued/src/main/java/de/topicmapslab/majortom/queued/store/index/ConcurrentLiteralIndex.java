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
package de.topicmapslab.majortom.queued.store.index;

import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.index.ILiteralIndex;

/**
 * @author Sven Krosse
 * 
 */
public class ConcurrentLiteralIndex extends ConcurentIndexImpl<ILiteralIndex> implements ILiteralIndex {

	/**
	 * @param parentIndex
	 * @param lock
	 */
	public ConcurrentLiteralIndex(ILiteralIndex parentIndex, Lock lock) {
		super(parentIndex, lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(String value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Locator value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(String value, Locator datatype) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(value, datatype);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Locator value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value, Locator datatype) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(value, datatype);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(String value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Locator datatype) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(datatype);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String value, Locator datatype) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(value, datatype);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristicsMatches(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regExp, Locator datatype) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristicsMatches(regExp, datatype);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristicsMatches(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristicsMatches(regExp, datatype);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getUris(URI value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getUris(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getIntegers(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value, double deviance) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getIntegers(value, deviance);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getLongs(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value, double deviance) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getLongs(value, deviance);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getFloats(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value, double deviance) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getFloats(value, deviance);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getDoubles(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value, double deviance) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getDoubles(value, deviance);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getDateTime(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value, Calendar deviance) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getDateTime(value, deviance);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getBooleans(boolean value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getBooleans(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCoordinates(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value, double deviance) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCoordinates(value, deviance);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IDatatypeAware> getDatatypeAwares(Locator dataType) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getDatatypeAwares(dataType);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants();
		} finally {
			lock.unlock();
		}
	}

}

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

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.index.IIdentityIndex;

/**
 * @author Sven Krosse
 * 
 */
public class ConcurrentIdentityIndex extends ConcurentIndexImpl<IIdentityIndex> implements IIdentityIndex {

	/**
	 * @param parentIndex
	 * @param lock
	 */
	public ConcurrentIdentityIndex(IIdentityIndex parentIndex, Lock lock) {
		super(parentIndex, lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getItemIdentifiers() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getItemIdentifiers();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectIdentifiers() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSubjectIdentifiers();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectLocators() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSubjectLocators();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructByItemIdentifier(String reference) throws MalformedIRIException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getConstructByItemIdentifier(reference);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructByItemIdentifier(Locator locator) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getConstructByItemIdentifier(locator);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(String regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getConstructsByItemIdentifier(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(Pattern regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getConstructsByItemIdentifier(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(String reference) throws MalformedIRIException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicBySubjectIdentifier(reference);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(Locator locator) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicBySubjectIdentifier(locator);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(String regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicsBySubjectIdentifier(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(Pattern regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicsBySubjectIdentifier(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(String reference) throws MalformedIRIException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicBySubjectLocator(reference);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(Locator locator) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicBySubjectLocator(locator);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(String regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicsBySubjectLocator(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(Pattern regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicsBySubjectLocator(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(String regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getConstructsByIdentifier(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(Pattern regExp) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getConstructsByIdentifier(regExp);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectIdentifier(String reference) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsSubjectIdentifier(reference);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectIdentifier(Locator locator) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsSubjectIdentifier(locator);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectLocator(String reference) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsSubjectLocator(reference);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectLocator(Locator locator) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsSubjectLocator(locator);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsItemIdentifier(String reference) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsItemIdentifier(reference);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsItemIdentifier(Locator locator) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsItemIdentifier(locator);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsIdentifier(String reference) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsIdentifier(reference);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsIdentifier(Locator locator) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().existsIdentifier(locator);
		} finally {
			lock.unlock();
		}
	}

}

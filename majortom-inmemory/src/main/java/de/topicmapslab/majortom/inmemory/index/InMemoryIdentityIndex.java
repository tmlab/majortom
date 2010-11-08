package de.topicmapslab.majortom.inmemory.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of the {@link IIdentityIndex}
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryIdentityIndex extends IndexImpl<InMemoryTopicMapStore> implements IIdentityIndex {

	/**
	 * constructor
	 * 
	 * @param store the in-memory store
	 */
	public InMemoryIdentityIndex(InMemoryTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsIdentifier(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference can not be null.");
		}
		return existsIdentifier(getTopicMapStore().getIdentityStore().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null!");
		}
		return getTopicMapStore().getIdentityStore().containsIdentifier((ILocator) locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsItemIdentifier(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference can not be null.");
		}
		return existsItemIdentifier(getTopicMapStore().getIdentityStore().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsItemIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null!");
		}
		return getTopicMapStore().getIdentityStore().containsItemIdentifier((ILocator) locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectIdentifier(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference can not be null.");
		}
		return existsSubjectIdentifier(getTopicMapStore().getIdentityStore().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null!");
		}
		return getTopicMapStore().getIdentityStore().containsSubjectIdentifier((ILocator) locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectLocator(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference can not be null.");
		}
		return existsSubjectLocator(getTopicMapStore().getIdentityStore().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectLocator(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null!");
		}
		return getTopicMapStore().getIdentityStore().containsSubjectLocator((ILocator) locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructByItemIdentifier(String reference) throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference can not be null.");
		}
		return getConstructByItemIdentifier(getTopicMapStore().getIdentityStore().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructByItemIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null!");
		}
		return getTopicMapStore().getIdentityStore().byItemIdentifier((ILocator) locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		return Collections.unmodifiableCollection(getConstructsByIdentifier(Pattern.compile(regExp)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		Set<Construct> set = HashUtil.getHashSet();
		set.addAll(getConstructsByItemIdentifier(regExp));
		set.addAll(getTopicsBySubjectIdentifier(regExp));
		set.addAll(getTopicsBySubjectLocator(regExp));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		return Collections.unmodifiableCollection(getConstructsByItemIdentifier(Pattern.compile(regExp)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		Set<Construct> set = HashUtil.getHashSet();
		for (ILocator locator : getTopicMapStore().getIdentityStore().getItemIdentifiers()) {
			if (regExp.matcher(locator.getReference()).matches()) {
				set.add(getTopicMapStore().getIdentityStore().byItemIdentifier(locator));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getItemIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Locator> set = HashUtil.getHashSet();
		set.addAll(getTopicMapStore().getIdentityStore().getItemIdentifiers());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Locator> set = HashUtil.getHashSet();
		set.addAll(getTopicMapStore().getIdentityStore().getSubjectIdentifiers());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectLocators() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Locator> set = HashUtil.getHashSet();
		set.addAll(getTopicMapStore().getIdentityStore().getSubjectLocators());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(String reference) throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null!");
		}
		return getTopicBySubjectIdentifier(getTopicMapStore().getIdentityStore().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null!");
		}
		return getTopicMapStore().getIdentityStore().bySubjectIdentifier((ILocator) locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(String reference) throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null!");
		}
		return getTopicBySubjectLocator(getTopicMapStore().getIdentityStore().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null!");
		}
		return getTopicMapStore().getIdentityStore().bySubjectLocator((ILocator) locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		return Collections.unmodifiableCollection(getTopicsBySubjectIdentifier(Pattern.compile(regExp)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (ILocator locator : getTopicMapStore().getIdentityStore().getSubjectIdentifiers()) {
			if (regExp.matcher(locator.getReference()).matches()) {
				set.add(getTopicMapStore().getIdentityStore().bySubjectIdentifier(locator));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		return Collections.unmodifiableCollection(getTopicsBySubjectLocator(Pattern.compile(regExp)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (ILocator locator : getTopicMapStore().getIdentityStore().getSubjectLocators()) {
			if (regExp.matcher(locator.getReference()).matches()) {
				set.add(getTopicMapStore().getIdentityStore().bySubjectLocator(locator));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

}

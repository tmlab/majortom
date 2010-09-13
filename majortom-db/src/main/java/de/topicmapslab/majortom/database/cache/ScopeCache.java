package de.topicmapslab.majortom.database.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

public class ScopeCache implements ITopicMapListener {
	/**
	 * storage map of scope-themes mapping
	 */
	private Map<IScope, Set<ITopic>> scopes;
	/**
	 * storage map of name-scope relation
	 */
	private Map<IName, IScope> nameScopes;
	/**
	 * storage map of occurrence-scope relation
	 */
	private Map<IOccurrence, IScope> occurrenceScopes;
	/**
	 * storage map of variant-scope relation
	 */
	private Map<IVariant, IScope> variantScopes;
	/**
	 * storage map of association-scope relation
	 */
	private Map<IAssociation, IScope> associationScopes;	

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		if (scopes != null) {
			scopes.clear();
		}
		if (nameScopes != null) {
			nameScopes.clear();
		}
		if (occurrenceScopes != null) {
			occurrenceScopes.clear();
		}
		if (variantScopes != null) {
			variantScopes.clear();
		}
		if (associationScopes != null) {
			associationScopes.clear();
		}
	}

	/**
	 * Returns the scope instance for the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @return the scope instance
	 */
	public IScope getScope(ITopic... themes) {
		return getScope(Arrays.asList(themes));
	}

	/**
	 * Returns the scope instance for the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @return the scope instance
	 */
	public IScope getScope(Collection<ITopic> themes) {
		if (scopes == null) {
			scopes = HashUtil.getHashMap();
		}
		for (Entry<IScope, Set<ITopic>> entry : scopes.entrySet()) {
			if (entry.getValue().size() == themes.size()
					&& entry.getValue().containsAll(themes)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Secure extraction of the scope of the given scoped construct from the
	 * given map
	 * 
	 * @param <T>
	 *            the generic type of scoped item
	 * @param map
	 *            the map
	 * @param scoped
	 *            the scoped item
	 * @return the scope or <code>null</code> if the given map is
	 *         <code>null</code> or does not contain the given key
	 */
	public <T extends IScopable> IScope getScope(Map<T, IScope> map, T scoped) {
		if (map == null || !map.containsKey(scoped)) {
			return null;
		}
		return map.get(scoped);
	}

	/**
	 * Returns the scope of the scoped construct
	 * 
	 * @param scoped
	 *            the scoped construct
	 * @return the scope and never <code>null</code>
	 */
	public IScope getScope(IScopable scoped) {
		if (scoped instanceof IAssociation) {
			return getScope(associationScopes, (IAssociation) scoped);
		} else if (scoped instanceof IOccurrence) {
			return getScope(occurrenceScopes, (IOccurrence) scoped);
		} else if (scoped instanceof IName) {
			return getScope(nameScopes, (IName) scoped);
		} else if (scoped instanceof IVariant) {
			return getScope(variantScopes, (IVariant) scoped);
		} else {
			throw new TopicMapStoreException("Type of scoped item is unknown '"
					+ scoped.getClass() + "'.");
		}
	}

	/**
	 * Cache the scope of the given construct to the internal store
	 * 
	 * @param scopable
	 *            the scoped item
	 * @param scope
	 *            the scope
	 */
	public void cacheScope(IScopable scopable, IScope scope) {
		if (scopable instanceof IAssociation) {
			cacheAssociationScope((IAssociation) scopable, scope);
		} else if (scopable instanceof IOccurrence) {
			cacheOccurrenceScope((IOccurrence) scopable, scope);
		} else if (scopable instanceof IName) {
			cacheNameScope((IName) scopable, scope);
		} else if (scopable instanceof IVariant) {
			cacheVariantScope((IVariant) scopable, scope);
		}
	}

	/**
	 * Cache the scope of the given association to the internal store
	 * 
	 * @param association
	 *            the association
	 * @param scope
	 *            the scope
	 */
	public void cacheAssociationScope(IAssociation association, IScope scope) {
		if (associationScopes == null) {
			associationScopes = HashUtil.getHashMap();
		}
		associationScopes.put(association, scope);
	}

	/**
	 * Cache the scope of the given name to the internal store
	 * 
	 * @param name
	 *            the name
	 * @param scope
	 *            the scope
	 */
	public void cacheNameScope(IName name, IScope scope) {
		if (nameScopes == null) {
			nameScopes = HashUtil.getHashMap();
		}
		nameScopes.put(name, scope);
	}

	/**
	 * Cache the scope of the given occurrence to the internal store
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @param scope
	 *            the scope
	 */
	public void cacheOccurrenceScope(IOccurrence occurrence, IScope scope) {
		if (occurrenceScopes == null) {
			occurrenceScopes = HashUtil.getHashMap();
		}
		occurrenceScopes.put(occurrence, scope);
	}

	/**
	 * Cache the scope of the given variant to the internal store
	 * 
	 * @param variant
	 *            the variant
	 * @param scope
	 *            the scope
	 */
	public void cacheVariantScope(IVariant variant, IScope scope) {
		if (variantScopes == null) {
			variantScopes = HashUtil.getHashMap();
		}
		variantScopes.put(variant, scope);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		/*
		 * name was removed
		 */
		if (event == TopicMapEventType.NAME_REMOVED && nameScopes != null) {
			nameScopes.remove(oldValue);
		}
		/*
		 * occurrence was removed
		 */
		else if (event == TopicMapEventType.OCCURRENCE_REMOVED
				&& occurrenceScopes != null) {
			occurrenceScopes.remove(oldValue);
		}
		/*
		 * variant was removed
		 */
		else if (event == TopicMapEventType.VARIANT_REMOVED
				&& variantScopes != null) {
			variantScopes.remove(oldValue);
		}
		/*
		 * association was removed
		 */
		else if (event == TopicMapEventType.ASSOCIATION_REMOVED
				&& associationScopes != null) {
			associationScopes.remove(oldValue);
		}
		/*
		 * topic was removed -> potential theme
		 */
		else if (event == TopicMapEventType.TOPIC_REMOVED) {
			if (scopes != null) {
				scopes.clear();
			}
			if (nameScopes != null) {
				nameScopes.clear();
			}
			if (occurrenceScopes != null) {
				occurrenceScopes.clear();
			}
			if (variantScopes != null) {
				variantScopes.clear();
			}
			if (associationScopes != null) {
				associationScopes.clear();
			}
		}
		/*
		 * scope was modified
		 */
		else if (event == TopicMapEventType.SCOPE_MODIFIED) {			
			cacheScope((IScopable) notifier, (IScope) newValue);		
			/*
			 * variant scopes are dependent from the parent name scope
			 */
			if ( ( notifier instanceof IName || notifier instanceof IVariant ) && variantScopes != null ){
				variantScopes.clear();
			}
		}
	}

}
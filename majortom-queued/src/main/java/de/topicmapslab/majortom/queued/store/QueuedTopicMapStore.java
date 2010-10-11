/**
 * 
 */
package de.topicmapslab.majortom.queued.store;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.queued.queue.TopicMapStoreQueue;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;

/**
 * A queued topic map store, which reads and writes any context to memory and
 * creating a task to persist information to database.
 * 
 * @author Sven Krosse
 * 
 */
public class QueuedTopicMapStore extends TopicMapStoreImpl {

	private InMemoryTopicMapStore inMemoryTopicMapStore;
	private JdbcTopicMapStore jdbcTopicMapStore;
	private TopicMapStoreQueue queue;

	/**
	 * constructor
	 */
	public QueuedTopicMapStore() {
	}

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 */
	public QueuedTopicMapStore(ITopicMapSystem topicMapSystem) {
		super(topicMapSystem);
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(Locator topicMapBaseLocator)
			throws TopicMapStoreException {
		/*
		 * initialize in memory layer
		 */
		inMemoryTopicMapStore = new InMemoryTopicMapStore();
		inMemoryTopicMapStore.initialize(topicMapBaseLocator);
		inMemoryTopicMapStore.setTopicMap(getTopicMap());
		inMemoryTopicMapStore.setTopicMapSystem(getTopicMapSystem());
		/*
		 * initialize in database layer
		 */
		jdbcTopicMapStore = new JdbcTopicMapStore();
		jdbcTopicMapStore.initialize(topicMapBaseLocator);
		jdbcTopicMapStore.setTopicMap(getTopicMap());
		jdbcTopicMapStore.setTopicMapSystem(getTopicMapSystem());
		/*
		 * initialize queue
		 */
		queue = new TopicMapStoreQueue(jdbcTopicMapStore);
		queue.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect() throws TopicMapStoreException {		
		super.connect();
		inMemoryTopicMapStore.connect();
		jdbcTopicMapStore.connect();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		inMemoryTopicMapStore.close();
		jdbcTopicMapStore.close();
		queue.interrupt();
		super.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <I extends Index> I getIndex(Class<I> clazz) {
		throw new UnsupportedOperationException("Currently not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCachingEnabled() {
		return jdbcTopicMapStore.isCachingEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableCaching(boolean enable) {
		jdbcTopicMapStore.enableCaching(enable);
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateItemIdentifier(ITopicMap topicMap) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type,
			Collection<ITopic> themes) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, Collection<ITopic> themes)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			ILocator value) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			ILocator value, Collection<ITopic> themes)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, ILocator datatype) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociationRole doCreateRole(IAssociation association,
			ITopic type, ITopic player) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap,
			ILocator itemIdentifier) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap,
			ILocator subjectLocator) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateLocator(ITopicMap topicMap, String reference)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, ILocator value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value,
			ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyPlayer(IAssociationRole role, ITopic player)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyReifier(IReifiable r, ITopic reifier)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyScope(IScopable s, ITopic theme)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectIdentifier(ITopic t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectLocator(ITopic t, ILocator subjectLocator)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySupertype(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag, Calendar timestamp)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITypeable t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTopicType(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IName n, String value)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, String value)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, String value,
			ILocator datatype) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, Object value)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyMetaData(IRevision revision, String key, String value)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopics(ITopic context, ITopic other)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopicMaps(TopicMap context, TopicMap other)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveScope(IScopable s, ITopic theme)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectIdentifier(ITopic t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSupertype(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveType(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopicMap(ITopicMap topicMap, boolean cascade)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopic(ITopic topic, boolean cascade)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveName(IName name, boolean cascade)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveAssociation(IAssociation association, boolean cascade)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveRole(IAssociationRole role, boolean cascade)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveVariant(IVariant variant, boolean cascade)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void storeRevision(IRevision revision, TopicMapEventType type,
			IConstruct context, Object newValue, Object oldValue) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected IRevision createRevision(TopicMapEventType type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type,
			IScope scope) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, String id)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadDataType(IDatatypeAware d)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadId(IConstruct c) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadItemIdentifiers(IConstruct c)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadLocator(ITopicMap t) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type, IScope scope)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, IScope scope)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadFutureRevision(IRevision r)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadPlayer(IAssociationRole role)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadPastRevision(IRevision r)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable doReadReification(ITopic t) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadReification(IReifiable r) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadRevisionTimestamp(IRevision r)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public TopicMapEventType doReadChangeSetType(IRevision revision)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association,
			ITopic type) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type,
			ITopic assocType) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadRoleTypes(IAssociation association)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectIdentifiers(ITopic t)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectLocators(ITopic t)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadSuptertypes(ITopic t)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectLocator(ITopicMap t,
			ILocator subjectLocator) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadType(ITypeable typed) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doReadScope(IScopable s) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IName n) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IDatatypeAware t) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T doReadValue(IDatatypeAware t, Class<T> type)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n, IScope scope)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> doReadMetaData(IRevision revision)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadMetaData(IRevision revision, String key)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic) throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic, ITopic theme, boolean strict)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
	}

}

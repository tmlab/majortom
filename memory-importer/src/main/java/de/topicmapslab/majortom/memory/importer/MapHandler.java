package de.topicmapslab.majortom.memory.importer;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryIdentity;
import de.topicmapslab.majortom.inmemory.store.InMemoryMergeUtils;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.internal.AssociationStore;
import de.topicmapslab.majortom.inmemory.store.internal.CharacteristicsStore;
import de.topicmapslab.majortom.inmemory.store.internal.IdentityStore;
import de.topicmapslab.majortom.inmemory.store.internal.ReificationStore;
import de.topicmapslab.majortom.inmemory.store.internal.ScopeStore;
import de.topicmapslab.majortom.inmemory.store.internal.TopicTypeStore;
import de.topicmapslab.majortom.inmemory.store.internal.TypedStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IConstructFactory;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;

public class MapHandler implements IMapHandler {

	private static Logger logger = LoggerFactory.getLogger(MapHandler.class);
	
	private final InMemoryTopicMapStore store;

	private ITopicMap currentTopicMap;
	private ITopic currentTopic;
	private IAssociation currentAssociation;
	private IAssociationRole currentRole;
	private ITopic currentPlayer;
	
	private IName currentName;
	private IOccurrence currentOccurrence;
	private IVariant currentVariant;

	private Set<ITopic> currentScope;
	
	private IConstructFactory constructFactory;
	
	private boolean hadEndTopicEvent = false;
	
	private enum State {
		SCOPE, NAME, OCCURRENCE, ISA, AKO, ROLE, PLAYER, TOPICMAP, TYPE, TOPIC, ASSOCIATION, THEME, VARIANT, REIFIER
	}

	private Stack<State> state;
	
	public MapHandler(InMemoryTopicMapStore store) {
		this.store = store;
		this.state = new Stack<MapHandler.State>();
		this.constructFactory = store.getConstructFactory();
	}
	
	@Override
	public void startAssociation() throws MIOException {
		logger.debug("startAssociation");
		
		if(!this.hadEndTopicEvent)
			endTopic();
		
		state.push(State.ASSOCIATION);
		long id = this.store.generateId();
		this.currentAssociation = this.constructFactory.newAssociation(new InMemoryIdentity(id) , this.currentTopicMap);
		
		AssociationStore as = this.store.getAssociationStore();
		as.addAssociation(currentAssociation);
		
	}
	
	@Override
	public void endAssociation() throws MIOException {
		logger.debug("endAssociation");
		
		this.state.pop();
		this.currentAssociation = null;
	}
	
	@Override
	public void startIsa() throws MIOException {
		logger.debug("startIsa");
		
		this.state.push(State.ISA);
	}
	
	@Override
	public void endIsa() throws MIOException {
		logger.debug("endIsa");
		
		this.state.pop();
	}

	@Override
	public void startName() throws MIOException {
		logger.debug("startName");
		
		this.state.push(State.NAME);
		long id = this.store.generateId();
		this.currentName = this.constructFactory.newName(new InMemoryIdentity(id), this.currentTopic);
	}
	
	@Override
	public void endName() throws MIOException {
		logger.debug("endName");
		this.state.pop();
		
		CharacteristicsStore cs = this.store.getCharacteristicsStore();
		cs.addName(currentTopic, currentName);
				
		this.currentName = null;
	}

	@Override
	public void startOccurrence() throws MIOException {
		logger.debug("startOccurrence");
		
		this.state.push(State.OCCURRENCE);
		long id = this.store.generateId();
		this.currentOccurrence = this.constructFactory.newOccurrence(new InMemoryIdentity(id), this.currentTopic);
		
		CharacteristicsStore cs = this.store.getCharacteristicsStore();
		cs.addOccurrence(this.currentTopic, this.currentOccurrence);
		
	}
	
	@Override
	public void endOccurrence() throws MIOException {
		logger.debug("endOccurrence");
		
		this.state.pop();
		this.currentOccurrence = null;
	}

	@Override
	public void startPlayer() throws MIOException {
		logger.debug("startPlayer");
		this.state.push(State.PLAYER);
	}
	
	@Override
	public void endPlayer() throws MIOException {
		logger.debug("endPlayer");
		
		this.state.pop();
	}

	@Override
	public void startReifier() throws MIOException {
		logger.debug("startReifier");
		
		this.state.push(State.REIFIER);
	}
	
	@Override
	public void endReifier() throws MIOException {
		logger.debug("endReifier");
		
		this.state.pop();
	}

	@Override
	public void startRole() throws MIOException {
		logger.debug("startRole");
		
		this.state.push(State.ROLE);
		long id = this.store.generateId();
		this.currentRole = this.constructFactory.newAssociationRole(new InMemoryIdentity(id), this.currentAssociation);
	}
	
	@Override
	public void endRole() throws MIOException {
		logger.debug("endRole");
		
		this.state.pop();
		
		AssociationStore as = this.store.getAssociationStore();
		as.addRole(this.currentAssociation, this.currentRole, this.currentPlayer);
		
		this.currentRole = null;
		this.currentPlayer = null;
	}

	@Override
	public void startScope() throws MIOException {
		logger.debug("startScope");
		this.state.push(State.SCOPE);
	}
	
	@Override
	public void endScope() throws MIOException {
		logger.debug("endScope");
		
		ScopeStore ss = this.store.getScopeStore();
		IScope scope = ss.getScope(this.currentScope);
		
		if(this.currentVariant != null){
			ss.setScope(this.currentVariant, scope);
		}else if(this.currentName != null){
			ss.setScope(this.currentName, scope);
		}else if(this.currentOccurrence != null){
			ss.setScope(this.currentOccurrence, scope);
		}else if(this.currentAssociation != null){
			ss.setScope(this.currentAssociation, scope);
		}

		this.state.pop();
		this.currentScope = null;
	}

	@Override
	public void startTheme() throws MIOException {
		logger.debug("startTheme");
		
		this.state.push(State.THEME);
	}
	
	@Override
	public void endTheme() throws MIOException {
		logger.debug("endTheme");
		this.state.pop();
	}

	@Override
	public void startTopic(IRef arg0) throws MIOException {
		logger.debug("startTopic");
		
		if(!this.hadEndTopicEvent)
			endTopic();

		this.state.push(State.TOPIC);
		setCurrentTopic(createTopicByRef(arg0));
		
		this.hadEndTopicEvent = false;
	}
	
	@Override
	public void endTopic() throws MIOException {
		logger.debug("endTopic");
		
		if(getCurrentTopic() == null)
			return;
		
		InMemoryMergeUtils.removeDuplicates(this.store, getCurrentTopic(),false);
		
		this.state.pop();
		clearCurrentTopic();
		
		this.hadEndTopicEvent = true;
	}

	@Override
	public void startTopicMap() throws MIOException {
		logger.debug("startTopicMap");
		
		this.state.push(State.TOPICMAP);
		this.currentTopicMap = store.getTopicMap();
	}
	
	@Override
	public void endTopicMap() throws MIOException {
		logger.debug("endTopicMap");
		
		this.state.pop();
	}

	@Override
	public void startType() throws MIOException {
		logger.debug("startType");
		this.state.push(State.TYPE);
	}
	
	@Override
	public void endType() throws MIOException {
		logger.debug("endType");
		this.state.pop();
	}

	@Override
	public void startVariant() throws MIOException {
		logger.debug("startVariant");
			
		this.state.push(State.VARIANT);
		long id = this.store.generateId();
		this.currentVariant = this.constructFactory.newVariant(new InMemoryIdentity(id), this.currentName);
		CharacteristicsStore cs = this.store.getCharacteristicsStore();
		cs.addVariant(this.currentName, this.currentVariant);
	}
	
	@Override
	public void endVariant() throws MIOException {
		logger.debug("endVariant");
		
		this.state.pop();
		this.currentVariant = null;
	}
	
	@Override
	public void itemIdentifier(String arg0) throws MIOException {
		logger.debug("itemIdentifier");
		
		/// TODO check if the item identifier belongs to an other construct, though it is not very likely
		IdentityStore is = this.store.getIdentityStore();
		
		if(this.currentAssociation != null){
			
			if(this.currentRole != null){
				is.addItemIdentifer(this.currentRole,new LocatorImpl(arg0));
			}else{
				is.addItemIdentifer(this.currentAssociation,new LocatorImpl(arg0));
			}
		}else if(this.currentName != null){
			
			if(this.currentVariant != null){
				is.addItemIdentifer(this.currentVariant,new LocatorImpl(arg0));
			}else{
				is.addItemIdentifer(this.currentName,new LocatorImpl(arg0));
			}
		}else if(this.currentOccurrence != null){
			is.addItemIdentifer(this.currentOccurrence,new LocatorImpl(arg0));
		}else if(getCurrentTopic() != null){
			is.addItemIdentifer(getCurrentTopic(),new LocatorImpl(arg0));
		}else{
			is.addItemIdentifer(this.currentTopicMap,new LocatorImpl(arg0));
		}
	}
	
	@Override
	public void subjectIdentifier(String arg0) throws MIOException {
		logger.debug("subjectIdentifier");
		
		IdentityStore is = this.store.getIdentityStore();
		is.addSubjectIdentifier(getCurrentTopic(),new LocatorImpl(arg0));
	}
	
	@Override
	public void subjectLocator(String arg0) throws MIOException {
		logger.debug("subjectLocator");
		
		IdentityStore is = this.store.getIdentityStore();
		is.addSubjectLocator(getCurrentTopic(),new LocatorImpl(arg0));
	}
	
	@Override
	public void topicRef(IRef arg0) throws MIOException {
		logger.debug("topicRef");
		
		
		if (this.currentAssociation != null) {
			switch (this.state.peek()) {
			case TYPE:
				
				ITopic type = createTopicByRef(arg0);
				TypedStore ts = this.store.getTypedStore();
				
				if(this.currentRole != null){
					ts.setType(this.currentRole, type);
				}else{
					ts.setType(this.currentAssociation, type);
				}
				
				break;
			case PLAYER:
				
				this.currentPlayer = createTopicByRef(arg0);
				this.currentRole.setPlayer(this.currentPlayer);
				break;
				
			case THEME:
				
				if(this.currentScope == null)
					this.currentScope = new HashSet<ITopic>();
				
				this.currentScope.add(createTopicByRef(arg0));
				
				break;
			case REIFIER:
				
				ITopic reifier = createTopicByRef(arg0);
				
				ReificationStore rs = this.store.getReificationStore();
				
				if(this.currentRole != null){
					
					rs.setReifier(currentRole, reifier);
					
				}else{
					rs.setReifier(currentAssociation, reifier);
				}
				
				break;
			}
			return;
		}else if ((this.currentName != null)) {

			switch (this.state.peek()) {
			case TYPE:
				
				ITopic type = createTopicByRef(arg0);
				TypedStore ts = this.store.getTypedStore();
				ts.setType(this.currentName, type);
	
				break;
			case THEME:
				
				if(this.currentScope == null)
					this.currentScope = new HashSet<ITopic>();
				
				this.currentScope.add(createTopicByRef(arg0));
				
				break;
			case REIFIER:
				
				if(this.currentVariant != null){
					
					ReificationStore rs = this.store.getReificationStore();
					ITopic reifier = createTopicByRef(arg0);
					rs.setReifier(this.currentVariant, reifier);
					
				}else{
				
					ReificationStore rs = this.store.getReificationStore();
					ITopic reifier = createTopicByRef(arg0);
					rs.setReifier(this.currentName, reifier);
				}
				
				break;
			}
		}else if ((this.currentOccurrence != null)) {

			switch (this.state.peek()) {
			case TYPE:
				
				ITopic type = createTopicByRef(arg0);
				TypedStore ts = this.store.getTypedStore();
				ts.setType(this.currentOccurrence, type);
	
				break;
			case THEME:
				
				if(this.currentScope == null)
				this.currentScope = new HashSet<ITopic>();
			
				this.currentScope.add(createTopicByRef(arg0));
				
				if(this.currentScope == null)
					this.currentScope = new HashSet<ITopic>();
				
				this.currentScope.add(createTopicByRef(arg0));

				
				break;
			case REIFIER:

				ReificationStore rs = this.store.getReificationStore();
				ITopic reifier = createTopicByRef(arg0);
				rs.setReifier(this.currentOccurrence, reifier);

				break;
			}
			
		}else {
			switch (state.peek()) {
			case ISA:
				
				TopicTypeStore tts = this.store.getTopicTypeStore();
				ITopic type = createTopicByRef(arg0);
				tts.addType(getCurrentTopic(), type);
				break;
		
			case REIFIER:
			
				ReificationStore rs = this.store.getReificationStore();
				ITopic reifier = createTopicByRef(arg0);
				rs.setReifier(this.currentTopicMap, reifier);
			
			break;
		}
		}
	}
	
	@Override
	public void value(String arg0) throws MIOException {
		logger.debug("value");

		if (this.currentName != null) {
			this.currentName.setValue(arg0);	
		}else if (this.currentOccurrence != null) {
			this.currentOccurrence.setValue(arg0);	
		}
	}
	
	@Override
	public void value(String arg0, String arg1) throws MIOException {
		logger.debug("value2");
		
		ILocator l = new LocatorImpl(arg1);
		
		CharacteristicsStore cs = this.store.getCharacteristicsStore();

		if (this.currentOccurrence != null) {
			this.currentOccurrence.setValue(arg0);
			cs.setDatatype(this.currentOccurrence, l);
				
		}else if(this.currentVariant != null){
			this.currentVariant.setValue(arg0);
			cs.setDatatype(this.currentVariant, l);
		}
	}

	private ITopic createTopicByRef(IRef ref){
		
		IdentityStore is = this.store.getIdentityStore();
		ILocator l = new LocatorImpl(ref.getIRI());
		
		ITopic topic = null;
		
		if(ref.getType() == 1){ // ITEM_IDENTIFIER
			topic = (ITopic)is.byItemIdentifier(l);
			if(topic == null)
				topic = is.bySubjectIdentifier(l);
			
		}else if(ref.getType() == 2){ // SUBJECT_IDENTIFIER
			topic = is.bySubjectIdentifier(l);
			if(topic == null)
				topic = (ITopic)is.byItemIdentifier(l);
			
		}else if(ref.getType() == 3){ // SUBJECT_LOCATOR
			topic = is.bySubjectLocator(l);
		}
		
		if(topic != null)
			return topic;
				
		// create new topic
		
		long id = this.store.generateId(); 
		topic = this.constructFactory.newTopic(new InMemoryIdentity(id), this.currentTopicMap);
		is.setId(topic, Long.toString(id));
		
		if(ref.getType() == 1){ // ITEM_IDENTIFIER
			is.addItemIdentifer(topic, l);
		}else if(ref.getType() == 2){ // SUBJECT_IDENTIFIER
			is.addSubjectIdentifier(topic, l);
		}else if(ref.getType() == 3){ // SUBJECT_LOCATOR
			is.addSubjectLocator(topic, l);
		}
		
		return topic;
	}

	private ITopic getCurrentTopic(){
		return this.currentTopic;
	}
	
	private void setCurrentTopic(ITopic topic){
		this.currentTopic = topic;
	}
	
	private void clearCurrentTopic(){
		this.currentTopic = null;
	}

	
}

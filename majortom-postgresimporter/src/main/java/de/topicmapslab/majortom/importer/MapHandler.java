/**
 * 
 */
package de.topicmapslab.majortom.importer;

import java.sql.SQLException;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

import de.topicmapslab.majortom.importer.helper.Characteristic;
import de.topicmapslab.majortom.importer.helper.Name;

/**
 * @author Hannes Niederhausen
 *
 */
public class MapHandler implements IMapHandler {

	private static Logger logger = LoggerFactory.getLogger(MapHandler.class);
	
	private long topicMapId;
	
	private long currTopicId = -1;
	
	private PostgresMapHandler handler;
	
	private Characteristic currentName;
	
	private Stack<State> state;
	
	/**
	 * Constructor
	 * @throws SQLException
	 */
	public MapHandler() throws SQLException {
		handler = new PostgresMapHandler();
		state = new Stack<MapHandler.State>();
	}
	
	public void endAssociation() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endIsa() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endName() throws MIOException {
		
		handler.addName(currentName);
		currentName = null;
		logger.debug("End Name");
		state.pop();
	}

	public void endOccurrence() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endPlayer() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endReifier() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endRole() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endScope() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endTheme() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void endTopic() throws MIOException {
		currTopicId = -1;
		state.pop();
	}

	public void endTopicMap() throws MIOException {
		handler.end();
		state.pop();
	}

	public void endType() throws MIOException {
		logger.debug("End type");
		state.pop();
	}

	public void endVariant() throws MIOException {
		// TODO Auto-generated method stub
		state.pop();
	}

	public void startAssociation() throws MIOException {
		state.push(State.ASSOCIATION);
		// TODO Auto-generated method stub
		
	}

	public void startIsa() throws MIOException {
		state.push(State.ISA);
	}

	public void startName() throws MIOException {
		state.push(State.NAME);
		currentName = new Name();
		currentName.setTopicmapId(topicMapId);
		currentName.setParentId(currTopicId);
		
		logger.debug("Start name");
		
	}

	public void startOccurrence() throws MIOException {
		state.push(State.OCCURRENCE);
		
	}

	public void startPlayer() throws MIOException {
		state.push(State.PLAYER);
		
	}

	public void startReifier() throws MIOException {
		state.push(State.REIFIER);
		
	}

	public void startRole() throws MIOException {
		state.push(State.ROLE);
		
	}

	public void startScope() throws MIOException {
		state.push(State.SCOPE);
		
	}

	public void startTheme() throws MIOException {
		state.push(State.THEME);
		
	}

	public void startTopic(IRef arg0) throws MIOException {
		state.push(State.TOPIC);
		logger.debug("Starting Topic with "+arg0.getIRI());
		
		currTopicId = handler.getTopic(arg0);
		
	}

	public void startTopicMap() throws MIOException {
		state.push(State.TOPICMAP);
		handler.start();
		topicMapId = handler.getTopicMapId("http://test.de");
		logger.debug("Found Topic Map with id: "+topicMapId);
		
	}

	public void startType() throws MIOException {
		state.push(State.TYPE);
		logger.debug("Start type");
		
	}

	public void startVariant() throws MIOException {
		state.push(State.VARIANT);
		// TODO Auto-generated method stub
		
	}

	public void itemIdentifier(String arg0) throws MIOException {
		handler.addIdentifier(currTopicId, arg0, IRef.ITEM_IDENTIFIER);
		logger.debug("Item Identifier: "+arg0);
	}

	public void subjectIdentifier(String arg0) throws MIOException {
		handler.addIdentifier(currTopicId, arg0, IRef.SUBJECT_IDENTIFIER);
		logger.debug("Subject Identifier: "+arg0);
	}

	public void subjectLocator(String arg0) throws MIOException {
		handler.addIdentifier(currTopicId, arg0, IRef.SUBJECT_LOCATOR);
		logger.debug("Subject Locator: "+arg0);
		
	}

	public void topicRef(IRef arg0) throws MIOException {
		if (currentName!=null) {

			switch(state.peek()) {
			case TYPE:
				currentName.setTypeRef(arg0);
				break;
			case THEME:
				currentName.addTheme(arg0);
				break;
			case REIFIER:
				currentName.setReifier(arg0);
			}
		}
		
		// TODO do something else
		logger.debug("Topic Ref:"+arg0.getIRI());
		
	}

	public void value(String arg0) throws MIOException {
		if (currentName!=null) {
			// we have a name...
			currentName.setValue(arg0);
		}
		logger.debug("Set value:"+arg0);
	}

	public void value(String arg0, String arg1) throws MIOException {
		// TODO Auto-generated method stub
		
	}

	
	private enum State {
		SCOPE,
		NAME,
		OCCURRENCE,
		ISA,
		AKO,
		ROLE,
		PLAYER,
		TOPICMAP,
		TYPE,
		TOPIC,
		ASSOCIATION,
		THEME,
		VARIANT,
		REIFIER
	}
}

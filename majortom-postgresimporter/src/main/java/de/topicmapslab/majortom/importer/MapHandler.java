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

import de.topicmapslab.majortom.importer.helper.Association;
import de.topicmapslab.majortom.importer.helper.Characteristic;
import de.topicmapslab.majortom.importer.helper.Name;
import de.topicmapslab.majortom.importer.helper.Occurrence;
import de.topicmapslab.majortom.importer.helper.Role;
import de.topicmapslab.majortom.importer.helper.Variant;
import de.topicmapslab.majortom.importer.model.IHandler;

/**
 * @author Hannes Niederhausen
 * 
 */
public class MapHandler implements IMapHandler {

	private static Logger logger = LoggerFactory.getLogger(MapHandler.class);

	private long topicMapId;

	private long currTopicId = -1;

	private final String baseIRI;

	// private PostgresMapHandler handler;

	private Characteristic currentCharacteristic;

	private Variant currentVariant = null;

	private Association currentAssociation;

	private Role currentRole;

	private Stack<State> state;

	private IHandler handler;

	/**
	 * Constructor
	 * 
	 * @throws SQLException
	 */
	public MapHandler(IHandler handler, String baseIRI) throws MIOException {
		this.handler = handler;
		this.state = new Stack<MapHandler.State>();
		this.baseIRI = baseIRI;
	}

	public void endAssociation() throws MIOException {
		handler.addAssociation(currentAssociation);
		currentAssociation = null;
		state.pop();
	}

	public void endIsa() throws MIOException {
		state.pop();
	}

	public void endName() throws MIOException {

		handler.addName((Name) currentCharacteristic);
		currentCharacteristic = null;
		logger.debug("End Name");
		state.pop();
	}

	public void endOccurrence() throws MIOException {
		state.pop();
		logger.debug("End Occurrence");
		handler.addOccurrence((Occurrence) currentCharacteristic);
	}

	public void endPlayer() throws MIOException {
		state.pop();
	}

	public void endReifier() throws MIOException {
		state.pop();
	}

	public void endRole() throws MIOException {

		currentAssociation.getRoles().add(currentRole);
		currentRole = null;

		state.pop();
	}

	public void endScope() throws MIOException {
		state.pop();
	}

	public void endTheme() throws MIOException {
		state.pop();
	}

	public void endTopic() throws MIOException {
		currTopicId = -1;
		state.pop();
		handler.commit();
	}

	public void endTopicMap() throws MIOException {
		handler.end();
		state.pop();
		handler.commit();
	}

	public void endType() throws MIOException {
		logger.debug("End type");
		state.pop();
	}

	public void endVariant() throws MIOException {

		((Name) currentCharacteristic).getVariants().add(currentVariant);
		currentVariant = null;
		state.pop();
	}

	public void startAssociation() throws MIOException {
		state.push(State.ASSOCIATION);

		currentAssociation = new Association();
		currentAssociation.setTopicmapId(topicMapId);

	}

	public void startIsa() throws MIOException {
		state.push(State.ISA);
	}

	public void startName() throws MIOException {
		state.push(State.NAME);
		currentCharacteristic = new Name();
		currentCharacteristic.setTopicmapId(topicMapId);
		currentCharacteristic.setParentId(currTopicId);

		logger.debug("Start name");

	}

	public void startOccurrence() throws MIOException {
		state.push(State.OCCURRENCE);
		currentCharacteristic = new Occurrence();
		currentCharacteristic.setParentId(currTopicId);
		currentCharacteristic.setTopicmapId(topicMapId);
	}

	public void startPlayer() throws MIOException {
		state.push(State.PLAYER);

	}

	public void startReifier() throws MIOException {
		state.push(State.REIFIER);

	}

	public void startRole() throws MIOException {
		state.push(State.ROLE);
		currentRole = new Role();
	}

	public void startScope() throws MIOException {
		state.push(State.SCOPE);

	}

	public void startTheme() throws MIOException {
		state.push(State.THEME);

	}

	public void startTopic(IRef arg0) throws MIOException {
		state.push(State.TOPIC);
		logger.debug("Starting Topic with " + arg0.getIRI());

		currTopicId = handler.getTopic(arg0);

	}

	public void startTopicMap() throws MIOException {
		state.push(State.TOPICMAP);
		handler.start();
		topicMapId = handler.getTopicMapId(baseIRI);
		logger.debug("Found Topic Map with id: " + topicMapId);

	}

	public void startType() throws MIOException {
		state.push(State.TYPE);
		logger.debug("Start type");

	}

	public void startVariant() throws MIOException {
		state.push(State.VARIANT);
		currentVariant = new Variant();

	}

	public void itemIdentifier(String arg0) throws MIOException {
		handler.addIdentifier(currTopicId, arg0, IRef.ITEM_IDENTIFIER);
		logger.debug("Item Identifier: " + arg0);
	}

	public void subjectIdentifier(String arg0) throws MIOException {
		handler.addIdentifier(currTopicId, arg0, IRef.SUBJECT_IDENTIFIER);
		logger.debug("Subject Identifier: " + arg0);
	}

	public void subjectLocator(String arg0) throws MIOException {
		handler.addIdentifier(currTopicId, arg0, IRef.SUBJECT_LOCATOR);
		logger.debug("Subject Locator: " + arg0);

	}

	public void topicRef(IRef arg0) throws MIOException {

		if (currentAssociation != null) {
			switch (state.peek()) {
			case TYPE:
				if (currentRole != null)
					currentRole.setRoleType(arg0);
				else
					currentAssociation.setType(arg0);
				break;
			case PLAYER:
				currentRole.setRolePlayer(arg0);
				break;
			case THEME:
				currentAssociation.getThemes().add(arg0);
				break;
			case REIFIER:
				currentAssociation.setReifier(arg0);
				break;
			}
			return;
		} else if ((currentCharacteristic != null)) {

			switch (state.peek()) {
			case TYPE:
				currentCharacteristic.setTypeRef(arg0);
				break;
			case THEME:
				if (currentVariant == null)
					currentCharacteristic.addTheme(arg0);
				else
					currentVariant.addTheme(arg0);
				break;
			case REIFIER:
				if (currentVariant == null)
					currentCharacteristic.setReifier(arg0);
				else
					currentVariant.setReifier(arg0);
				break;
			}
		} else {
			if (state.peek() == State.ISA) {
				handler.addType(currTopicId, arg0);
			}
		}

		// TODO do something else
		logger.debug("Topic Ref:" + arg0.getIRI());

	}

	public void value(String arg0) throws MIOException {
		if (currentCharacteristic != null) {
			// we have a name...
			currentCharacteristic.setValue(arg0);
		}
		logger.debug("Set value:" + arg0);
	}

	public void value(String arg0, String arg1) throws MIOException {
		if (currentCharacteristic instanceof Occurrence) {
			((Occurrence) currentCharacteristic).setDatatype(arg1);
			currentCharacteristic.setValue(arg0);
		} else if (currentVariant != null) {
			currentVariant.setDatatype(arg1);
			currentVariant.setValue(arg0);
		}
	}

	private enum State {
		SCOPE, NAME, OCCURRENCE, ISA, AKO, ROLE, PLAYER, TOPICMAP, TYPE, TOPIC, ASSOCIATION, THEME, VARIANT, REIFIER
	}
}

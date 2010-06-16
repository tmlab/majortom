package de.topicmapslab.majortom.model.core;

import org.tmapi.core.Reifiable;

/**
 * Interface definition representing construct which can be reified by a topic
 * item.
 * 
 * <p>
 * The act of reification is the act of making a topic represent the subject of
 * another topic map construct in the same topic map. For example, creating a
 * topic that represents the relationship represented by an association is
 * reification.
 * </p>
 * <p>
 * In many cases it is desirable to be able to attach additional information to
 * topic map constructs, for example by giving an association occurrences, or to
 * give an occurrence a name. The model does not allow this, except through
 * reification; that is, creating a topic that reifies the topic map construct.
 * The necessary information can then be attached to the reifying topic, and the
 * reification relationship is explicitly present in the topic map.
 * </p>
 * 
 * @author Sven Krosse
 * 
 */
public interface IReifiable extends Reifiable, IConstruct {

}

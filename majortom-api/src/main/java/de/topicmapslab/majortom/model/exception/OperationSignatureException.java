package de.topicmapslab.majortom.model.exception;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.store.TopicMapStoreParamterType;

/**
 * Topic map store exception thrown if the calling signature does not matching to any internal operation.
 * 
 * @author Sven Krosse
 * 
 */
public class OperationSignatureException extends TopicMapStoreException {

	private static final long serialVersionUID = 1L;

	/**
	 * the class of the given context
	 */
	private final Class<?> context;
	/**
	 * the class of the given parameters
	 */
	private final Class<?>[] params;
	/**
	 * the parameter type of the operation
	 */
	private final TopicMapStoreParamterType paramType;

	/**
	 * constructor
	 * 
	 * @param context
	 *            the context given to the topic map store
	 * @param paramType
	 *            the parameter type given to the topic map store
	 * @param params
	 *            an array of arguments given to the topic map store
	 */
	public OperationSignatureException(Construct context, TopicMapStoreParamterType paramType, Object... params) {
		super("");
		this.context = context.getClass();
		this.paramType = paramType;
		this.params = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			this.params[i] = params[i].getClass();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMessage() {
		StringBuilder builder = new StringBuilder("Invalid operation signature for topic map store implementation.\r\n");
		builder.append("Context type: " + context.getSimpleName() + "\r\n");
		builder.append("Parameter type: " + paramType.name() + "\r\n");
		builder.append("Arguments: ");
		boolean first = true;
		for (Class<?> c : params) {
			builder.append((first ? "" : ",") + c.getSimpleName());
			first = false;
		}
		builder.append("\r\n");
		return builder.toString();
	}

}

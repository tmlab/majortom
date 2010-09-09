package de.topicmapslab.majortom.model.exception;

/**
 * Base exception thrown by the topic maps engine in the context of transactions.
 * 
 * @author Sven Krosse
 * 
 */
public class TransactionException extends TopicMapStoreException {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 * 
	 * @param msg
	 *            the message containing some additional information about the
	 *            cause
	 */
	public TransactionException(String msg) {
		super(msg);
	}

	/**
	 * constructor
	 * 
	 * @param cause
	 *            the cause of this exception
	 */
	public TransactionException(Throwable cause) {
		super(cause);
	}

	/**
	 * constructor
	 * 
	 * @param msg
	 *            the message containing some additional information about the
	 *            cause
	 * @param cause
	 *            the cause of this exception
	 */
	public TransactionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
